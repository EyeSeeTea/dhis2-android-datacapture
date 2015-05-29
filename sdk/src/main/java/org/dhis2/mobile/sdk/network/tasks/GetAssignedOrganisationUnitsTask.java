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

package org.dhis2.mobile.sdk.network.tasks;

import org.dhis2.mobile.sdk.entities.OrganisationUnit;
import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.http.ApiRequest;
import org.dhis2.mobile.sdk.network.http.Request;
import org.dhis2.mobile.sdk.network.http.RequestBuilder;

import java.util.List;

final class GetAssignedOrganisationUnitsTask implements ITask<List<OrganisationUnit>> {
    private final ApiRequest<String, List<OrganisationUnit>> mRequest;

    public GetAssignedOrganisationUnitsTask(INetworkManager dhisManager) {
        String base64Credentials = dhisManager.getBase64Manager()
                .toBase64(dhisManager.getCredentials());
        String url = dhisManager.getServerUri().buildUpon()
                .appendEncodedPath("api/me/")
                .appendQueryParameter("fields", buildQueryParams())
                .build().toString();
        Request request = RequestBuilder.forUri(url)
                .header("Authorization", base64Credentials)
                .header("Accept", "application/json")
                .build();

        mRequest = new ApiRequest<>(request,
                dhisManager.getHttpManager(),
                dhisManager.getLogManager(),
                dhisManager.getJsonManager().getOrgUnitsConverter());
    }

    private static String buildQueryParams() {
        return "organisationUnits" + "[" +
                "id,created,lastUpdated,name,displayName,level" +
                "]";
    }

    @Override
    public List<OrganisationUnit> run() throws APIException {
        return mRequest.request();
    }
}
