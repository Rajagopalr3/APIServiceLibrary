package com.libRG;

import android.app.Dialog;
import android.util.Log;

import com.libRG.volley.AuthFailureError;
import com.libRG.volley.NetworkError;
import com.libRG.volley.ParseError;
import com.libRG.volley.Response;
import com.libRG.volley.ServerError;
import com.libRG.volley.TimeoutError;
import com.libRG.volley.VolleyError;


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
            }
            Log.e(requestTag, errorMessage);
            activityReference.onError(errorMessage, requestTag);
        }
        if (dialog != null && dialog.isShowing())
            dialog.dismiss();

    }


}
