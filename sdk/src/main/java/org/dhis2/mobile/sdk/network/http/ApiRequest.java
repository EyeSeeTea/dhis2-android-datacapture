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

package org.dhis2.mobile.sdk.network.http;

import org.dhis2.mobile.sdk.network.APIException;
import org.dhis2.mobile.sdk.network.converters.IJsonConverter;
import org.dhis2.mobile.sdk.utils.ILogManager;

import java.io.IOException;

import static org.dhis2.mobile.sdk.utils.Preconditions.isNull;

public final class ApiRequest<I, T> {
    private static final String TAG = ApiRequest.class.getSimpleName();

    private final Request mRequest;
    private final IHttpManager mHttpManager;
    private final ILogManager mLogManager;
    private final IJsonConverter<I, T> mJsonConverter;

    public ApiRequest(Request request,
                      IHttpManager httpManager,
                      ILogManager logManager,
                      IJsonConverter<I, T> jsonConverter) {
        mRequest = isNull(request, "Request object must not be null");
        mHttpManager = isNull(httpManager, "IHttpManager must not be null");
        mLogManager = isNull(logManager, "ILogManager must not be null");
        mJsonConverter = isNull(jsonConverter, "IJsonConverter must not be null");
    }

    private static boolean isSuccessful(int code) {
        return code >= 200 && code < 300;
    }

    public IJsonConverter<I, T> getJsonConverter() {
        return mJsonConverter;
    }

    public IHttpManager getNetworkManager() {
        return mHttpManager;
    }

    public Request getRequest() {
        return mRequest;
    }

    public T request() {
        Response response = null;
        T data = null;

        try {
            mLogManager.LOGD(TAG, "Request URL " + mRequest.getUrl());
            response = mHttpManager.request(mRequest);
        } catch (IOException networkException) {
            throw APIException.networkError(mRequest.getUrl(), networkException);
        } catch (Exception unknownException) {
            throw APIException.unexpectedError(mRequest.getUrl(), unknownException);
        }

        if (response == null) {
            throw APIException.unexpectedError(mRequest.getUrl(),
                    new RuntimeException("Response cannot be null"));
        }

        if (!isSuccessful(response.getStatus())) {
            throw APIException.httpError(mRequest.getUrl(), response);
        }

        try {
            String responseBody = new String(response.getBody());
            data = mJsonConverter.deserialize(responseBody);
        } catch (Throwable conversionException) {
            conversionException.printStackTrace();
            throw APIException.conversionError(mRequest.getUrl(),
                    response, conversionException);
        }

        return data;
    }
}
