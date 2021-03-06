# Minimized API Service Library
   This is a optimized custom library for server communication. References taken from VOLLEY library.
   We can reduce the code for making api calls when using this library. All references are taken from Google's Official Volley.
   
  
# Features:

   1. Added custom listeners to handle the responses in easy way
   2. Added Progress bar when getting response from server.(can disable by passing false in request)  
   3. Success and Failure response are captured in Logcats(Log.Info)  
   4. Circular imageview
   5. Used API TAGS to identify multiple requests on single activity.
   6. Added ResponseHeaders in response
   7. File upload 
   
   <a href='https://bintray.com/rajagopalr3/ApiService/apiservices/_latestVersion'><img src='https://api.bintray.com/packages/rajagopalr3/ApiService/apiservices/images/download.svg'></a>
  
  
  ![Screenshot](screenshot_three.png)                  ![Screenshot](screenshot_one.png)   
  
 <a href='https://bintray.com/rajagopalr3/ApiService/apiservices?source=watch' alt='Get automatic notifications about new "apiservices" versions'><img src='https://www.bintray.com/docs/images/bintray_badge_color.png'></a>
  
# HOW TO USE


Step 1:

Add this to app gradle

```

dependencies {
    implementation 'com.libRG.volley:apiservices:1.7'
}


```
Step 2:
 Implement ActivityResponseListener on activity for getting success & failure response from server


# Implementation Steps in Activity

```

public class MainActivity extends AppCompatActivity implements ActivityResponseListener{

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Setting authentication tokens for request - set this code into base activity

        //HashMap<String, String> headerParams = new HashMap<>();
        //headerParams.put("key", "value");
        //ApiService.setHeaders(headerParams);
        
        sendRequest();
    }

 private void sendRequest() { // tag is used to identify the API requests - when multiple requests are used.
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=560078";
        ApiService.StringRequest(this, 1, url, null, "GET_ADDRESS", true);
    }
    
  public void setImage(String imageURL, ImageView imageView) {
  
        ApiService.getImageLoader(this).get(imageURL, ImageLoader.getImageListener(imageView, R.mipmap.ic_launcher_round,R.mipmap.ic_launcher));
 }
    
    
  @Override
    public <T> void onResponse(T response, String tagName, JSONObject responseHeaders) {//responseHeaders is used to catch the network header params like auth key and value
        
        if (tagName.equals("GET_ADDRESS")) {
        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public void onError(Object error, String tagName) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
    }
    
}



```

APIServiceLibrary Provides variety of implementations of Request.

1. StringRequest  
2. JsonObjectRequest  
3. JsonArrayRequest
4. File Upload
5. ImageRequest  

# Explanation of code:

```
   HashMap<String, String> input  =new HashMap<String, String>() 
   input.put("key", "value");
   
   ApiService.StringRequest(this, 1, url, input, "GET_ADDRESS", true);
    
   StringRequest & JSONObjectRequest method Params :
    
   1. this           -->   It is used receive the callback from server response.(passing context to intialize the request)
   2. 0 or 1         -->   This is request type either POST or GET etc.(should pass integer values)
   3. url            -->   This is request url of server
   4. input          -->   Pass request input parameters based on method types(GET or POST, if GET method pass null)
                           (if JSON request pass json input else pass formdata)
   5. GET_ADDRESS    -->   This is Request TAG to identify and parse the specific response from server
   6. true           -->   This is used to show progress bar when getting data from server.(pass false if not required)
   
 **File upload:**
 
   ApiService.UploadFile(this, 1, url, input_params, file, "file_key", "tag_name", true);
   
   ApiService.UploadFile(this, 1, url, input_params, HashMap<String, File> fileList, "file_key", "tag_name", true);
   
   1. this           -->   It is used receive the callback from server response.(passing context to intialize the request)
   2. 0 or 1         -->   This is request type either POST or GET etc.(should pass integer values)
   3. url            -->   This is request url of server
   4.input_params    -->   hashmap input params
   5.file            -->   File to upload (if multiple files use Hashmap<String,File> fileList)
   6 file_key        -->   Filekey param
   7.tag_name        -->   This is Request TAG to identify and validate the specific response from server
   8.true            -->   This is used to show progress bar when getting data from server.(pass false if not required)
   
   

   
 # Setting authentication tokens for request
 
        HashMap<String, String> headerParams = new HashMap<>();
        headerParams.put("key", "value");
        ApiService.setHeaders(headerParams);
        
   Note - set these headers to appcontrollers whenever lauch the app.  
   
 # Set custom progress dialog 
   
    ApiService.setCustomDialogView(R.layout.progress_bar_dialog);
    ApiService.setCancellable(true);
 
        
```

# Proguard Rules

Use the following suggested ProGuard settings

```

-keep class android.net.http.** { *; }
-dontwarn android.net.http.**

-dontwarn org.apache.http.**
-dontwarn android.net.http.AndroidHttpClient
-dontwarn com.google.android.gms.**
-keep class com.libRG.** {*;}

```

# References taken from Google's Volley Library
   I have optimized the API request code to reduce the code implementation for api call. I used volley for server commmunication.
   Thanks to Volley*__*

 # License
 
 ```
 
Copyright 2017 Rajagopal

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 
 
```  


  
  
 
  
