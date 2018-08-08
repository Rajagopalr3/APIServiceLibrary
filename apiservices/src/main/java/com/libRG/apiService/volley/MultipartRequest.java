package com.libRG.apiService.volley;

import android.os.AsyncTask;

import com.libRG.apiService.raja.ApiService;
import com.libRG.apiService.raja.ErrorListener;
import com.libRG.apiService.raja.ResponseListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ${Raja} on 09-Oct-17.
 */

public class MultipartRequest extends AsyncTask<String, String, String> {

    private String charset = "UTF-8";
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private OutputStream outputStream;
    private PrintWriter writer;
    private HashMap<String, String> params;
    private HashMap<String, File> fileLists;
    private File uploadFile;
    private String key;
    private int method;
    private ResponseListener<String> listener;
    private ErrorListener errorListener;
    public Map<String, String> headers;

    public MultipartRequest(int method, HashMap<String, String> params, File file, String key,
                            ResponseListener<String> listener, ErrorListener errorListener) {
        this.method = method;
        this.params = params;
        this.uploadFile = file;
        this.key = key;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    public MultipartRequest(int method, HashMap<String, String> params, HashMap<String, File> fileList,
                            ResponseListener<String> listener, ErrorListener errorListener) {
        this.method = method;
        this.params = params;
        this.fileLists = fileList;
        this.listener = listener;
        this.errorListener = errorListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuilder response = new StringBuilder();

        try {
            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(params[0]);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setReadTimeout(ApiService.SOCKET_TIMEOUT);
            httpConn.setRequestMethod(method == 1 ? "POST" : "GET");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            if (ApiService.getHeaders() != null && ApiService.getHeaders().size() > 0) {
                for (Map.Entry<String, String> entry : ApiService.getHeaders().entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        if (!entry.getKey().equals("Content-Type"))
                            httpConn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
            }
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);

            for (Map.Entry<String, String> entry : this.params.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    addFormField(entry.getKey(), entry.getValue());
                }
            }
            if (fileLists != null && fileLists.size() > 0) {
                for (Map.Entry<String, File> entry : fileLists.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        addFilePart(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                if (key != null && uploadFile != null)
                    addFilePart(key, uploadFile);
            }
            writer.append(LINE_FEED).flush();
            writer.append("--").append(boundary).append("--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                if (httpConn.getHeaderFields() != null) {
                    headers = new HashMap<>();
                    for (Map.Entry<String, List<String>> entries : httpConn.getHeaderFields().entrySet()) {
                        if (entries.getKey() != null && entries.getValue() != null)
                            headers.put(entries.getKey(), entries.getValue().get(0));
                    }
                }
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);

            }
        } catch (IOException e) {
            try {
                if (errorListener != null) {
                    errorListener.onErrorResponse(new VolleyError(e.getMessage()));
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        return response.toString();
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */

    private void addFormField(String name, String value) {
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(name).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=").append(charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }


    private void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--").append(boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"").append(fieldName).append("\"; filename=\"").append(fileName).append("\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        int bytesAvailable = inputStream.available();
        byte[] buffer = new byte[4096];
        int sentBytes = 0;
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            sentBytes += bytesRead;
            //publishProgress(String.valueOf(sentBytes * 100 / bytesAvailable));
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }


    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecute(String result) {
        if (listener != null) {
            listener.onResponse(result, headers != null ? new JSONObject(headers) : new JSONObject());
        }
        super.onPostExecute(result);
    }

}
