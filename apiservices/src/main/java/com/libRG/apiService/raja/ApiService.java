package com.libRG.apiService.raja;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.Toast;

import com.libRG.apiService.BuildConfig;
import com.libRG.apiService.R;
import com.libRG.apiService.volley.DefaultRetryPolicy;
import com.libRG.apiService.volley.MultipartRequest;
import com.libRG.apiService.volley.Request;
import com.libRG.apiService.volley.RequestQueue;
import com.libRG.apiService.volley.RetryPolicy;
import com.libRG.apiService.volley.toolbox.ImageLoader;
import com.libRG.apiService.volley.toolbox.JsonArrayRequest;
import com.libRG.apiService.volley.toolbox.JsonObjectRequest;
import com.libRG.apiService.volley.toolbox.StringRequest;
import com.libRG.apiService.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for communicating with the server using api call
 */
public class ApiService<T> {

    public static int SOCKET_TIMEOUT = 20000; //20 seconds
    private static ActivityResponseListener nListener;
    private static HashMap<String, String> params;
    private static final int JSON = 0;
    private static final int JSON_ARRAY = 1;
    private static final int STRING = 2;
    private Dialog dialog = null;
    private static ImageLoader mImageLoader;
    private static RequestQueue requestQueue;
    private static ResponseListener<JSONObject> jsonListener;
    private static ResponseListener<JSONArray> jsonArrayListener;
    private static ResponseListener<String> stringListener;
    private static ErrorListener errorListener;

    /**
     * Constructor with class specified tag name
     **/
    private ApiService(Context mContext, String tag, boolean showProgress, int requestType) {
        try {
            dialog = showCustomDialog(mContext);
            nListener = (ActivityResponseListener) mContext;
            initListeners(mContext, tag, showProgress, requestType);
        } catch (Exception e) {
            Log.e("APIService", e.getMessage());
            if (e.getMessage().contains("com.libRG.apiService.raja.ActivityResponseListener")) {
                Toast.makeText(mContext, "Please implement the ActivityResponseListener on your activity", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * This method is used to make json object request in android
     */
    public static void JSONObjectRequest(Context mContext, int method, String url, JSONObject jsonInput, String tag, boolean showProgress) {
        new ApiService(mContext, tag, showProgress, JSON);
        if (nListener == null)
            return;
        String log = "URL : " + url + "\n" + " : Input : " + (jsonInput != null ? jsonInput : "");
        if (BuildConfig.DEBUG)
            Log.i((tag != null ? tag : ""), " : " + log);
        JsonObjectRequest jObjReq = new JsonObjectRequest(method == 1 ? Request.Method.POST : Request.Method.GET, url, jsonInput, jsonListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                if (params == null)
                    params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        jObjReq.setRetryPolicy(getRetryPolicy());
        jObjReq.setShouldCache(false);
        RequestManager.getInstance(mContext).addToRequestQueue(jObjReq, ((Activity) mContext).getLocalClassName());
    }

    /**
     * This is used to make json array request in android.
     */
    public static void JSONArrayRequest(Context mContext, int method, String url, JSONArray jsonArrayInput, String tag, boolean showProgress) {
        new ApiService(mContext, tag, showProgress, JSON_ARRAY);
        if (nListener == null)
            return;
        String log = "URL : " + url + "\n" + " : Input : " + (jsonArrayInput != null ? jsonArrayInput : "");
        if (BuildConfig.DEBUG)
            Log.i((tag != null ? tag : ""), " : " + log);
        JsonArrayRequest jObjReq = new JsonArrayRequest(method == 1 ? Request.Method.POST : Request.Method.GET, url, jsonArrayInput, jsonArrayListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() {
                if (params == null)
                    params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                return params;
            }
        };
        jObjReq.setRetryPolicy(getRetryPolicy());
        jObjReq.setShouldCache(false);
        RequestManager.getInstance(mContext).addToRequestQueue(jObjReq, ((Activity) mContext).getLocalClassName());
    }

    /**
     * This is used make String request in android
     */
    public static void StringRequest(Context mContext, int method, String url, final HashMap<String, String> input, String tag, boolean showProgress) {
        new ApiService(mContext, tag, showProgress, STRING);
        if (nListener == null)
            return;
        String log = "URL : " + url + "\n" + " : Input : " + (input != null ? input : "");
        if (BuildConfig.DEBUG)
            Log.i((tag != null ? tag : ""), " : " + log);
        StringRequest request = new StringRequest(method == 1 ? Request.Method.POST : Request.Method.GET, url, stringListener, errorListener) {
            @Override
            public byte[] getBody() {
                if (input != null && input.size() > 0) {
                    return encodeParameters(input, getParamsEncoding());
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() {
                if (params == null)
                    params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
                return params;
            }
        };

        request.setRetryPolicy(getRetryPolicy());
        request.setShouldCache(false);
        RequestManager.getInstance(mContext).addToRequestQueue(request, ((Activity) mContext).getLocalClassName());
    }

    /**
     * This method is used to upload the multiple files to the server
     */
    public static void UploadFile(Context context, int method, String url, final HashMap<String, String> input, final HashMap<String, File> fileList, String tag, boolean showProgress) {
        new ApiService(context, tag, showProgress, STRING);
        if (nListener == null)
            return;
        new MultipartRequest(method, input, fileList, stringListener, errorListener).execute(url);
    }

    /**
     * This method is used to upload the single file to the server
     */
    public static void UploadFile(Context context, int method, String url, final HashMap<String, String> input, File file, String fileKey, String tag, boolean showProgress) {
        new ApiService(context, tag, showProgress, STRING);
        if (nListener == null)
            return;
        new MultipartRequest(method, input, file, fileKey, stringListener, errorListener).execute(url);
    }

    /**
     * This method is used to set the header params
     */
    public static void setHeaders(HashMap<String, String> param) {
        params = param;
    }

    /**
     * This method is used to get the header params
     */
    public static HashMap<String, String> getHeaders() {
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

    /**
     * It is used to encode the input params
     */
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

    /**
     * This is used to show custom dialog when hitting the api.
     */
    private Dialog showCustomDialog(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.progress_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    RequestManager.getInstance(context).cancelPendingRequest(((Activity) context).getLocalClassName());
                }
                return true;
            }
        });
        return dialog;
    }

    /**
     * This is used to init the custom listeners like response and success listeners.
     */
    private void initListeners(Context mContext, String tag, boolean showProgress, int requestType) {
        switch (requestType) {
            case JSON:
                jsonListener = new ResponseListener<>(nListener, tag != null ? tag : "", dialog);
                break;
            case JSON_ARRAY:
                jsonArrayListener = new ResponseListener<>(nListener, tag != null ? tag : "", dialog);
                break;
            case STRING:
                stringListener = new ResponseListener<>(nListener, tag != null ? tag : "", dialog);
                break;
        }
        errorListener = new ErrorListener(nListener, tag != null ? tag : "", dialog);
        if (showProgress && !((Activity) mContext).isFinishing() && dialog != null)
            dialog.show();
    }

    /**
     * This is used to get the image loader instance to show the image.
     */
    public static ImageLoader getImageLoader(Context context) {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());

        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(requestQueue, new LruBitmapCache());
        }
        return mImageLoader;
    }
}
