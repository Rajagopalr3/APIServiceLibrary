package com.libRG.library;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.libRG.apiService.raja.ActivityResponseListener;
import com.libRG.apiService.raja.ApiService;
import com.libRG.apiService.volley.toolbox.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements ActivityResponseListener {

    ImageView img, img1;
    String imgURL = "http://www.wallpapereast.com/static/images/nature-hd-wallpapers-super-hd_54OVdsW.jpg";
    String url = "http://maps.googleapis.com/maps/api/geocode/json?address=560078";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img);
        img1 = findViewById(R.id.img1);


        //Setting authentication tokes for request - set this code into base activity

        //HashMap<String, String> headerParams = new HashMap<>();
        //headerParams.put("key", "value");
        //ApiService.setHeaders(headerParams);

        setImageFromVolley(imgURL, img);
        setImageFromVolleyN(imgURL, img1);
    }

    public void setImageFromVolley(String imageURL, ImageView imageView) {
        ApiService.getImageLoader(this).get(imageURL, ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher));
    }

    public void setImageFromVolleyN(String imageURL, ImageView imageView) {

        ApiService.getImageLoader(this).get(imageURL, ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher));
    }


    private void sendRequest(int i) { // tag is used to identify the API requests - when multiple requests are used.
        switch (i) {
            case 1:
                ApiService.JSONObjectRequest(this, 1, url, new JSONObject(), "GET_ADDRESS", true);
                break;
            case 3:
                ApiService.StringRequest(this, 1, url, null, "GET_ADDRESS", true);
                break;

        }
    }

    @Override
    public <T> void onResponse(T response, String tagName, JSONObject responseHeaders) {
        Log.e("Response Headers", responseHeaders.toString());

        if (tagName.equals("GET_ADDRESS")) {
            validateResponse(response.toString());
        } else if (tagName.equalsIgnoreCase("validate")) {
            String authKey = responseHeaders.optString("x-auth-token");
            HashMap<String, String> headers = new HashMap<>();
            headers.put("x-auth-token", authKey);
            ApiService.setHeaders(headers);
        } else if (tagName.equalsIgnoreCase("UploadDoc")) {
            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onError(Object error, String tagName) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }


    private void validateResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            showDialog(response);
        } catch (Exception e) {
            e.printStackTrace();
            showDialog(response);
        }
    }


    private void showDialog(String msg) {
        new AlertDialog.Builder(this)
                .setTitle("Message")
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void getAddress(View view) {
        switch (view.getId()) {
            case R.id.btn1:
                sendRequest(1);
                break;
            case R.id.btn2:
                uploadFile();
                break;
            case R.id.btn3:
                sendRequest(3);
                break;
        }
    }

    private void uploadFile() {

        HashMap<String, String> input = new HashMap<>();
        input.put("key", "value");
        File ff = new File("/storage/emulated/0/DCIM/Facebook/FB_IMG_1533051602721.jpg");
        if (ff.isFile()) {
            Log.e("value", "true");
        }
        ApiService.UploadFile(this, 1, url, input, ff, "file1", "UploadDoc", true);

    }
}

