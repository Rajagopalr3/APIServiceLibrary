/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.libRG.apiService.volley.toolbox;


import com.libRG.apiService.volley.NetworkResponse;
import com.libRG.apiService.volley.Request;
import com.libRG.apiService.volley.Response;
import com.libRG.apiService.volley.Response.ErrorListener;
import com.libRG.apiService.volley.Response.Listener;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * A canned request for retrieving the response body at a given URL as a String.
 */
public class StringRequest extends Request<String> {

    /**
     * Lock to guard mListener as it is cleared on cancel() and read on delivery.
     */
    private final Object mLock = new Object();
    private JSONObject responseHeaders;

    // @GuardedBy("mLock")
    private Listener<String> mListener;


    /**
     * Creates a new request with the given method.
     *
     * @param method        the request {@link Method} to use
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(int method, String url, Listener<String> listener,
                         ErrorListener errorListener) {
        super(method, url, errorListener);
        mListener = listener;

    }


    /**
     * Creates a new GET request.
     *
     * @param url           URL to fetch the string at
     * @param listener      Listener to receive the String response
     * @param errorListener Error listener, or null to ignore errors
     */
    public StringRequest(String url, Listener<String> listener, ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    @Override
    public void cancel() {
        super.cancel();
        synchronized (mLock) {
            mListener = null;
        }
    }

    @Override
    protected void deliverResponse(String response, NetworkResponse networkResponse) {
        Listener<String> listener;
        synchronized (mLock) {
            listener = mListener;
        }
        try {
            responseHeaders = new JSONObject(networkResponse.headers);
        } catch (Exception ignored) {
        }
        if (listener != null) {
            listener.onResponse(response, responseHeaders);
        }
    }


    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            responseHeaders = new JSONObject(response.headers);
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }
}
