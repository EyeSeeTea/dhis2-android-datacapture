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
import android.net.Uri;

import org.dhis2.mobile.sdk.controllers.IController;
import org.dhis2.mobile.sdk.controllers.InvalidateUserController;
import org.dhis2.mobile.sdk.controllers.LogInUserController;
import org.dhis2.mobile.sdk.controllers.LogOutUserController;
import org.dhis2.mobile.sdk.controllers.MetaDataController;
import org.dhis2.mobile.sdk.entities.UserAccount;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.http.Response;
import org.dhis2.mobile.sdk.network.managers.NetworkManager;
import org.dhis2.mobile.sdk.network.models.Credentials;
import org.dhis2.mobile.sdk.persistence.preferences.SessionHandler;
import org.dhis2.mobile.sdk.persistence.preferences.UserAccountHandler;
import org.dhis2.mobile.sdk.persistence.models.Session;

import java.net.HttpURLConnection;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public class DhisManager extends NetworkManager {
    private final Context mContext;
    private SessionHandler mSessionHandler;
    private UserAccountHandler mUserAccountHandler;

    public DhisManager(Context context) {
        mContext = isNull(context, "Context object must not be null");
        mSessionHandler = new SessionHandler(context);
        mUserAccountHandler = new UserAccountHandler(context);
        // fetch meta data from disk
        readMetaData();
    }

    public UserAccount logInUser(Uri serverUri,
                                 Credentials credentials) throws APIException {
        return signInUser(serverUri, credentials);
    }

    public UserAccount confirmUser(Credentials credentials) {
        return signInUser(getServerUri(), credentials);
    }

    public void logOutUser() throws APIException {
        IController<Object> controller =
                new LogOutUserController(mSessionHandler, mUserAccountHandler);
        controller.run();

        // fetch meta data from disk
        readMetaData();
    }

    private UserAccount signInUser(Uri serverUri,
                                   Credentials credentials) throws APIException {
        IController<UserAccount> controller = new LogInUserController(
                this, mSessionHandler, mUserAccountHandler, serverUri, credentials);
        UserAccount userAccount = controller.run();

        // fetch meta data from disk
        readMetaData();
        return userAccount;
    }

    public void invalidateMetaData() {
        InvalidateUserController invalidateController
                = new InvalidateUserController(mSessionHandler);
        invalidateController.run();

        // fetch meta data from disk
        readMetaData();
    }

    public boolean isUserLoggedIn() {
        return getServerUri() != null &&
                getCredentials() != null;
    }

    public boolean isUserInvalidated() {
        return getServerUri() != null &&
                getCredentials() == null;
    }

    private void readMetaData() {
        Session session = mSessionHandler.get();
        if (session != null) {
            setServerUri(session.getServerUri());
            setCredentials(session.getCredentials());
        }
    }

    public void syncMetaData() throws APIException {
        IController<Object> metaDataController = new MetaDataController(
                this, null, null, mSessionHandler
        );
        runController(metaDataController);
    }

    // we need this method in order to catch certain types of exceptions.
    // For example: UnauthorizedException (HTTP 401)
    // NOTE!: this method should be used fo controllers except LogInUserController
    private <T> T runController(IController<T> controller) throws APIException {
        try {
            return controller.run();
        } catch (APIException apiException) {
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
