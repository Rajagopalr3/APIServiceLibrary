package com.libRG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;

import com.libRG.apiService.R;
import com.libRG.volley.AuthFailureError;
import com.libRG.volley.DefaultRetryPolicy;
import com.libRG.volley.Request;
import com.libRG.volley.RequestQueue;
import com.libRG.volley.RetryPolicy;
import com.libRG.volley.toolbox.ImageLoader;
import com.libRG.volley.toolbox.JsonObjectRequest;
import com.libRG.volley.toolbox.StringRequest;
import com.libRG.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for communicating with the server using api call
 */
public class ApiService {

    @SuppressLint("StaticFieldLeak")
    private volatile static RequestManager requestSession;
    public static int SOCKET_TIMEOUT = 20000; //20 seconds
    private static ActivityResponseListener nListener;
    private static HashMap<String, String> params;
    public static Dialog dialog = null;
    private static ImageLoader mImageLoader;
    private static RequestQueue requestQueue;

    /**
     * Constructor with class specified tag name
     **/
    private ApiService(Context mContext) {
        nListener = (ActivityResponseListener) mContext;
        initialize(mContext);
        dialog = showCustomDialog(mContext);
    }

    public static ImageLoader getImageLoader(Context context) {
        //new ApiService(context);
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        }
        return mImageLoader;
    }

    /**
     * Initialize the RequestManager instance
     */

    private void initialize(Context mContext) {
        requestSession = RequestManager.getInstance(mContext.getApplicationContext());
    }

    public static void JSONObjectRequest(Context mContext, int method, String url, JSONObject jsonInput,
                                         String tag, boolean showProgress) {
        new ApiService(mContext);
        if (showProgress && !((Activity) mContext).isFinishing())
            dialog.show();

        String log = "URL : " + url + "\n" + " : Input : " + (jsonInput != null ? jsonInput : "");
        Log.i(tag != null ? tag : "", " : " + log);

        ResponseListener<JSONObject> listener = new ResponseListener<>(nListener, tag != null ? tag : "", dialog);
        ErrorListener errorListener = new ErrorListener(nListener, tag != null ? tag : "", dialog);
        JsonObjectRequest jObjReq = new JsonObjectRequest(method == 1 ? Request.Method.POST : Request.Method.GET, url, jsonInput, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (params == null)
                    params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }


        };
        jObjReq.setRetryPolicy(getRetryPolicy());
        jObjReq.setShouldCache(false);
        requestSession.addToRequestQueue(jObjReq, ((Activity) mContext).getLocalClassName());
    }

    public static void StringRequest(Context mContext, int method, String url, final HashMap<String, String> input,
                                     String tag, boolean showProgress) {
        new ApiService(mContext);
        if (showProgress && !((Activity) mContext).isFinishing())
            dialog.show();

        String log = "URL : " + url + "\n" + " : Input : " + (input != null ? input : "");
        Log.i(tag != null ? tag : "", " : " + log);
        ResponseListener<String> listener = new ResponseListener<>(nListener, tag != null ? tag : "", dialog);
        ErrorListener errorListener = new ErrorListener(nListener, tag != null ? tag : "", dialog);
        StringRequest request = new StringRequest(method == 1 ? Request.Method.POST : Request.Method.GET, url, listener, errorListener) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                if (input != null && input.size() > 0) {
                    return encodeParameters(input, getParamsEncoding());
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                if (params == null)
                    params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return params;
            }
        };

        request.setRetryPolicy(getRetryPolicy());
        request.setShouldCache(false);
        requestSession.addToRequestQueue(request, ((Activity) mContext).getLocalClassName());
    }

    public static void setHeaders(HashMap<String, String> param) {
        params = param;
    }

    public HashMap<String, String> getHeaders() {
        return params;
    }

    /**
     * Method give the default retryPolicy
     */
    private static RetryPolicy getRetryPolicy() {
        return new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    }

    /**
     * Method give the default retryPolicy
     */
    public static void setRetryTimeOut(int milliseconds) {
        SOCKET_TIMEOUT = milliseconds;
    }

    private static byte[] encodeParameters(Map<String, String> params, String paramsEncoding) {
        StringBuilder encodedParams = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                encodedParams.append(URLEncoder.encode(entry.getKey(), paramsEncoding));
                encodedParams.append('=');
                encodedParams.append(URLEncoder.encode(entry.getValue(), paramsEncoding));
                encodedParams.append('&');
            }
            return encodedParams.toString().getBytes(paramsEncoding);
        } catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("Encoding not supported: " + paramsEncoding, uee);
        }
    }

    private Dialog showCustomDialog(Context context) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.progress_dialog);

        return dialog;
    }
}
