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

import org.dhis2.mobile.sdk.DhisManager;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.retrofit.DhisService;
import org.dhis2.mobile.sdk.network.retrofit.RetrofitManager;
import org.dhis2.mobile.sdk.persistence.models.UserAccount;
import org.dhis2.mobile.sdk.persistence.preferences.UserAccountHandler;

import java.util.HashMap;
import java.util.Map;

public final class GetUserAccountController implements IController<UserAccount> {
    private final UserAccountHandler mUserAccountHandler;
    private final DhisService mService;

    public GetUserAccountController(UserAccountHandler userAccountHandler) {
        mUserAccountHandler = userAccountHandler;
        mService = RetrofitManager.createService(
                DhisManager.getInstance().getServerUrl(),
                DhisManager.getInstance().getUserCredentials()
        );
    }

    @Override
    public UserAccount run() throws APIException {
        UserAccount userAccount = getUserAccount();
        // update it in db
        saveUserAccount(userAccount);
        return userAccount;
    }

    private UserAccount getUserAccount() {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("fields", UserAccount.ALL_USER_ACCOUNT_FIELDS);
        return mService.getCurrentUserAccount(queryParams);
    }

    private void saveUserAccount(UserAccount userAccount) {
        mUserAccountHandler.put(userAccount);
    }
}
