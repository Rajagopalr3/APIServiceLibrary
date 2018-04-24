package com.libRG.apiService.raja;

public interface ActivityResponseListener {

    <T> void onResponse(T response, String tagName);

    void onError(Object error, String tagName);

}
