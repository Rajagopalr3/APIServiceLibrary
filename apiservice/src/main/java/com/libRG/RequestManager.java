package com.libRG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.libRG.volley.Request;
import com.libRG.volley.RequestQueue;
import com.libRG.volley.toolbox.Volley;

public class RequestManager {

    // An Singleton instance of the class for accessing in other places
    @SuppressLint("StaticFieldLeak")
    private static RequestManager reqInstance;

    private String TAG_NAME = RequestManager.class.getSimpleName();

    private Context reqContext;

    // Request Queue  of volley
    private RequestQueue requestQueue;

    private RequestManager(Context context) {
        this.reqContext = context;
    }

    /**
     * @return Application instance
     */
    public static synchronized RequestManager getInstance(Context context) {
        if (reqInstance == null) {
            reqInstance = new RequestManager(context);
        }
        return reqInstance;
    }

    /**
     * @return An instance of Volley RequestQueue, the queue will be created if it is null
     */
    private RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(reqContext.getApplicationContext());

        return requestQueue;
    }

    /**
     * Add a specified request to the volley RequestQueue if tag is specified,then it is used else Default TAG will be used
     */
   public  <T> void addToRequestQueue(Request<T> req, String tag) {
        if (TextUtils.isEmpty(tag))
            tag = TAG_NAME;
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    /**
     * Add a request to the global queue with the default tag
     */
    public  <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG_NAME);
        getRequestQueue().add(req);
    }

    /**
     * Cancel all pending request by the specified tag
     */
    public  void cancelPendingRequest(Object tag) {
        if (requestQueue != null)
            requestQueue.cancelAll(tag);
    }
}
