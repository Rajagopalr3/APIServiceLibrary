package com.libRG;

public interface ActivityResponseListener {

    <T> void onResponse(T response, String tagName);

    void onError(Object error, String tagName);

}
