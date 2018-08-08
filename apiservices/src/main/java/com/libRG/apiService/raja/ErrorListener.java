package com.libRG.apiService.raja;

import android.app.Activity;
import android.app.Dialog;
import android.util.Log;

import com.libRG.apiService.BuildConfig;
import com.libRG.apiService.volley.AuthFailureError;
import com.libRG.apiService.volley.NetworkError;
import com.libRG.apiService.volley.ParseError;
import com.libRG.apiService.volley.Response;
import com.libRG.apiService.volley.ServerError;
import com.libRG.apiService.volley.TimeoutError;
import com.libRG.apiService.volley.VolleyError;


public class ErrorListener implements Response.ErrorListener {

    private ActivityResponseListener activityReference;
    private String requestTag;
    private Dialog dialog = null;

    ErrorListener(ActivityResponseListener context, String tag, Dialog pd) {
        this.activityReference = context;
        this.requestTag = tag;
        this.dialog = pd;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        if (dialog != null && dialog.isShowing() && !((Activity) activityReference).isFinishing())
            dialog.dismiss();
        if (activityReference != null) {
            String errorMessage = "";
            if (error instanceof NetworkError) {
                errorMessage += "Network Connection Error ";
            } else if (error instanceof ServerError) {
                errorMessage += "Server connection Error ";
            } else if (error instanceof AuthFailureError) {
                errorMessage += "AuthFailureError ";
            } else if (error instanceof ParseError) {
                errorMessage += "ParseError ";
            } else if (error instanceof TimeoutError) {
                errorMessage += "Request Timeout";
            } else {
                errorMessage += error.getMessage();
            }
            if (BuildConfig.DEBUG)
                Log.e(requestTag, errorMessage);
            activityReference.onError(errorMessage, requestTag);
        }
    }


}
