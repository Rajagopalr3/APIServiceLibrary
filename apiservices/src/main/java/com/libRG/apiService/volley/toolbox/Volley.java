/*
 * Copyright (C) 2012 The Android Open Source Project
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

import android.content.Context;


import com.libRG.apiService.volley.Network;
import com.libRG.apiService.volley.RequestQueue;

import java.io.File;

public class Volley {

    /**
     * Default on-disk cache directory.
     */
    private static final String DEFAULT_CACHE_DIR = "volley";

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   A {@link BaseHttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context, BaseHttpStack stack) {
        BasicNetwork network;
        if (stack == null) {
            network = new BasicNetwork(new HurlStack());
        } else {
            network = new BasicNetwork(stack);
        }

        return newRequestQueue(context, network);
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @param stack   An {@link HttpStack} to use for the network, or null for default.
     * @return A started {@link RequestQueue} instance.
     * @deprecated Use {@link #newRequestQueue(Context, BaseHttpStack)} instead to avoid depending
     * on Apache HTTP. This method may be removed in a future release of Volley.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public static RequestQueue newRequestQueue(Context context, HttpStack stack) {
        if (stack == null) {
            return newRequestQueue(context, (BaseHttpStack) null);
        }
        return newRequestQueue(context, new BasicNetwork(stack));
    }

    private static RequestQueue newRequestQueue(Context context, Network network) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
        RequestQueue queue = new RequestQueue(new DiskBasedCache(cacheDir), network);
        queue.start();
        return queue;
    }

    /**
     * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
     *
     * @param context A {@link Context} to use for creating the cache dir.
     * @return A started {@link RequestQueue} instance.
     */
    public static RequestQueue newRequestQueue(Context context) {
        return newRequestQueue(context, (BaseHttpStack) null);
    }
}
