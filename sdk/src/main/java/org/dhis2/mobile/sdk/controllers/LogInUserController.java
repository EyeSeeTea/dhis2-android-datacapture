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

package org.dhis2.mobile.sdk.controllers;

import com.squareup.okhttp.HttpUrl;

import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.models.Credentials;
import org.dhis2.mobile.sdk.network.models.Session;
import org.dhis2.mobile.sdk.network.repository.DhisService;
import org.dhis2.mobile.sdk.network.repository.RepoManager;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;
import org.dhis2.mobile.sdk.persistence.preferences.SessionManager;
import org.dhis2.mobile.sdk.persistence.preferences.UserAccountHandler;

import java.util.HashMap;
import java.util.Map;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class LogInUserController implements IController<UserAccount> {
    private final HttpUrl mServerUrl;
    private final Credentials mCredentials;
    private final UserAccountHandler mUserAccountHandler;
    private final DhisService mService;

    public LogInUserController(UserAccountHandler userAccountHandler,
                               HttpUrl serverUrl, Credentials credentials) {
        mUserAccountHandler = isNull(userAccountHandler, "UserAccountHandler must not be null");
        mServerUrl = isNull(serverUrl, "Server URI must not be null");
        mCredentials = isNull(credentials, "User credentials must not be null");
        mService = RepoManager.createService(mServerUrl, mCredentials);
    }

    @Override
    public UserAccount run() throws APIException {
        UserAccount userAccount = getUserAccount();
        // if we got here, it means http
        // request was executed successfully
        saveMetaData();
        saveUserAccount(userAccount);
        return userAccount;
    }

    private UserAccount getUserAccount() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fields", UserAccount.ALL_USER_ACCOUNT_FIELDS);
        return mService.getCurrentUserAccount(queryParams);
    }

    private void saveMetaData() {
        Session session = new Session(
                mServerUrl, mCredentials
        );
        SessionManager.getInstance()
                .put(session);
    }

    private void saveUserAccount(UserAccount userAccount) {
        mUserAccountHandler.put(userAccount);
    }
}
