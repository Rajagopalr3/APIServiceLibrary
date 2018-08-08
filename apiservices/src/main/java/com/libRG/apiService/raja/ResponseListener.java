package com.libRG.apiService.raja;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.libRG.apiService.BuildConfig;
import com.libRG.apiService.volley.Response;

import org.json.JSONObject;

public class ResponseListener<T> implements Response.Listener<T> {

    private ActivityResponseListener activityReference;
    private String requestTag = "";
    private Dialog dialog = null;

    ResponseListener(ActivityResponseListener rhelper, String tag, Dialog pd) {
        this.activityReference = rhelper;
        this.requestTag = tag;
        this.dialog = pd;
    }

    @Override
    public void onResponse(T result, JSONObject responseHeaders) {
        if (dialog != null && dialog.isShowing() && !((Activity) activityReference).isFinishing())
            dialog.dismiss();
        if (BuildConfig.DEBUG)
            Log.i(requestTag, result != null ? result.toString() : "null");
        if (activityReference != null)
            activityReference.onResponse(result, requestTag, responseHeaders);
    }

}
