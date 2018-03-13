package com.libRG.library;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.libRG.ActivityResponseListener;
import com.libRG.ApiService;
import com.libRG.volley.toolbox.ImageLoader;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ActivityResponseListener {

    ImageView img1;
    String imgURL = "http://www.wallpapereast.com/static/images/nature-hd-wallpapers-super-hd_54OVdsW.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img1 = findViewById(R.id.img1);


        //Setting authentication tokes for request - set this code into base activity

        //HashMap<String, String> headerParams = new HashMap<>();
        //headerParams.put("key", "value");
        //ApiService.setHeaders(headerParams);

        setImageFromVolley(imgURL, img1);
    }

    public void setImageFromVolley(String imageURL, ImageView imageView) {
        ApiService.getImageLoader(this).get(imageURL, ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher_round, R.mipmap.ic_launcher));
    }


    private void sendRequest() { // tag is used to identify the API requests - when multiple requests are used.
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=560078";
        ApiService.StringRequest(this, 1, url, null, "GET_ADDRESS", true);
    }

    @Override
    public <T> void onResponse(T response, String tagName) {
        if (tagName.equals("GET_ADDRESS")) {
            validateResponse(response.toString());
        }

    }

    @Override
    public void onError(Object error, String tagName) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }


    private void validateResponse(String response) {
        try {
            JSONObject json = new JSONObject(response);
            showDialog(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
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
        sendRequest();
    }
}
