# Minimized API Service Library
  This is a optimized custom library for server communication. References taken from Official VOLLEY library
  
  
# Intro
  We can reduce the code for making api calls when using this library. All references are taken from Google's Official Volley.
  thanks to Volley
  
# Usage

import apiservice module into your project and add the following code for api communication.

Implement ActivityResponseListener on activity for getting success & failure response from server

```

public class MainActivity extends AppCompatActivity implements ActivityResponseListener{

  @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Setting authentication tokes for request - set this code into base activity

        //HashMap<String, String> headerParams = new HashMap<>();
        //headerParams.put("key", "value");
        //ApiService.setHeaders(headerParams);
        
        sendRequest();
    }

 private void sendRequest() { // tag is used to identify the API requests - when multiple requests are used.
        String url = "http://maps.googleapis.com/maps/api/geocode/json?address=560078";
        ApiService.StringRequest(this, 1, url, null, "GET_ADDRESS", true);
    }
    
    
  @Override
    public <T> void onResponse(T response, String tagName) {
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

  
  
 
  
