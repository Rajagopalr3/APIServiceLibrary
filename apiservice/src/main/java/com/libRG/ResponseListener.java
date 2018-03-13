package com.libRG;

import android.app.Dialog;
import android.util.Log;

import com.libRG.volley.Response;

public class ResponseListener<T> implements Response.Listener<T> {

    private ActivityResponseListener activityReference;
    private String requestTag;
    private Dialog dialog = null;

    ResponseListener(ActivityResponseListener rhelper, String tag, Dialog pd) {
        this.activityReference = rhelper;
        this.requestTag = tag;
        this.dialog = pd;
    }

    @Override
    public void onResponse(T result) {
        Log.i(requestTag, result != null ? result.toString() : "null");
        if (activityReference != null)
            activityReference.onResponse(result, requestTag);
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

    }
}
