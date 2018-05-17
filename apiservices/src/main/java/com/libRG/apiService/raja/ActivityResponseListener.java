package com.libRG.apiService.raja;

import org.json.JSONObject;

public interface ActivityResponseListener {

    <T> void onResponse(T response, String tagName, JSONObject responseHeaders);

    void onError(Object error, String tagName);

}
