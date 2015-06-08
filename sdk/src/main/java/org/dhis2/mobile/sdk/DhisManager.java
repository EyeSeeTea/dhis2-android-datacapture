/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.sdk;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;

import org.dhis2.mobile.sdk.controllers.IController;
import org.dhis2.mobile.sdk.controllers.InvalidateUserController;
import org.dhis2.mobile.sdk.controllers.LogInUserController;
import org.dhis2.mobile.sdk.controllers.LogOutUserController;
import org.dhis2.mobile.sdk.controllers.MetaDataController2;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.models.Credentials;
import org.dhis2.mobile.sdk.network.models.Session;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;
import org.dhis2.mobile.sdk.persistence.preferences.LastUpdatedPreferences;
import org.dhis2.mobile.sdk.persistence.preferences.SessionManager;
import org.dhis2.mobile.sdk.persistence.preferences.UserAccountHandler;

import java.net.HttpURLConnection;

import retrofit.client.Response;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class DhisManager {
    private static DhisManager mDhisManager;
    private final UserAccountHandler mUserAccountHandler;
    private final LastUpdatedPreferences mLastUpdatedPreferences;
    private Session mSession;

    public static void init(Context context) {
        isNull(context, "Context object must not be null");
        if (mDhisManager == null) {
            mDhisManager = new DhisManager(context);
        }
    }

    public static DhisManager getInstance() {
        if (mDhisManager == null) {
            throw new IllegalArgumentException("You need to call init() first");
        }

        return mDhisManager;
    }

    private DhisManager(Context context) {
        SessionManager.init(context);
        mUserAccountHandler = new UserAccountHandler(context);
        mLastUpdatedPreferences = new LastUpdatedPreferences(context);

        // fetch meta data from disk
        readSession();
    }

    public UserAccount logInUser(HttpUrl serverUrl,
                                 Credentials credentials) throws APIException {
        return signInUser(serverUrl, credentials);
    }

    public UserAccount confirmUser(Credentials credentials) throws APIException {
        // Session session = SessionManager.getInstance().get();
        return signInUser(mSession.getServerUrl(), credentials);
    }

    public void logOutUser() throws APIException {
        IController<Object> controller =
                new LogOutUserController(mUserAccountHandler);
        controller.run();

        // fetch meta data from disk
        readSession();
    }

    private UserAccount signInUser(HttpUrl serverUrl,
                                   Credentials credentials) throws APIException {
        IController<UserAccount> controller = new LogInUserController(
                mUserAccountHandler, mLastUpdatedPreferences, serverUrl, credentials);
        UserAccount userAccount = controller.run();

        // fetch meta data from disk
        readSession();
        return userAccount;
    }

    public void invalidateMetaData() {
        InvalidateUserController invalidateController
                = new InvalidateUserController();
        invalidateController.run();

        // fetch meta data from disk
        readSession();
    }

    public boolean isUserLoggedIn() {
        return mSession.getServerUrl() != null &&
                mSession.getCredentials() != null;
    }

    public boolean isUserInvalidated() {
        return mSession.getServerUrl() != null &&
                mSession.getCredentials() == null;
    }

    private void readSession() {
        mSession = SessionManager.getInstance().get();
    }

    public void syncMetaData() throws APIException {
        runController(new MetaDataController2(mLastUpdatedPreferences));
    }

    public HttpUrl getServerUrl() {
        return mSession.getServerUrl();
    }

    public Credentials getUserCredentials() {
        return mSession.getCredentials();
    }

    // we need this method in order to catch certain types of exceptions.
    // For example: UnauthorizedException (HTTP 401)
    // NOTE!: this method should be used for controllers except LogInUserController
    private <T> T runController(IController<T> controller) throws APIException {
        try {
            return controller.run();
        } catch (APIException apiException) {
            apiException.printStackTrace();

            if (apiException.isHttpError()) {
                Response response = apiException.getResponse();
                if (response != null &&
                        response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    // invalidate the metadata in application
                    invalidateMetaData();
                }
            }
            throw apiException;
        }
    }
}
