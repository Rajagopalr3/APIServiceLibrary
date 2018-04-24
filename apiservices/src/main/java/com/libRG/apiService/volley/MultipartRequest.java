package com.libRG.apiService.volley;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
import java.util.Map;

/**
 * Created by ${Raja} on 09-Oct-17.
 */

public class MultipartRequest extends AsyncTask<String, String, String> {

    private String charset = "UTF-8";
    private String boundary;
    private static final String LINE_FEED = "\r\n";
    private HttpURLConnection httpConn;
    private OutputStream outputStream;
    private PrintWriter writer;
    private int bytesAvailable;
    private HashMap<String, String> inputData;
    private File uploadFile;
    private Context mContext;
    private Dialog dialog;
    private static UploadReceiver uploadReceiver;

    public MultipartRequest(Context context, HashMap<String, String> input, File fileName) {
        this.mContext = context;
        this.inputData = input;
        this.uploadFile = fileName;
    }


    @Override
    protected void onPreExecute() {
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        StringBuffer response = new StringBuffer();

        try {
            // creates a unique boundary based on time stamp
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(params[0]);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            httpConn.setRequestProperty("x-auth-token", "");
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            outputStream = httpConn.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
            for (Map.Entry<String, String> entry : inputData.entrySet()) {
                if (entry.getKey() != null && entry.getValue() != null) {
                    addFormField(entry.getKey(), entry.getValue());
                }
            }
            addFilePart("file1", uploadFile);
            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = httpConn.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        httpConn.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                httpConn.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    /**
     * Adds a form field to the request
     *
     * @param name  field name
     * @param value field value
     */

    public void addFormField(String name, String value) {
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                .append(LINE_FEED);
        writer.append("Content-Type: text/plain; charset=" + charset).append(
                LINE_FEED);
        writer.append(LINE_FEED);
        writer.append(value).append(LINE_FEED);
        writer.flush();
    }


    public void addFilePart(String fieldName, File uploadFile)
            throws IOException {
        String fileName = uploadFile.getName();
        writer.append("--" + boundary).append(LINE_FEED);
        writer.append(
                "Content-Disposition: form-data; name=\"" + fieldName
                        + "\"; filename=\"" + fileName + "\"")
                .append(LINE_FEED);
        writer.append(
                "Content-Type: "
                        + URLConnection.guessContentTypeFromName(fileName))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        FileInputStream inputStream = new FileInputStream(uploadFile);
        bytesAvailable = inputStream.available();
        Log.e("bytesAvailable", String.valueOf(bytesAvailable));
        byte[] buffer = new byte[4096];
        int sentBytes = 0;
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            sentBytes += bytesRead;
            //  publishProgress(String.valueOf(sentBytes * 100 / bytesAvailable));
        }
        outputStream.flush();
        inputStream.close();

        writer.append(LINE_FEED);
        writer.flush();
    }

    /**
     * Adds a header field to the request.
     *
     * @param name  - name of the header field
     * @param value - value of the header field
     */
    public void addHeaderField(String name, String value) {
        writer.append(name + ": " + value).append(LINE_FEED);
        writer.flush();
    }


    @Override
    protected void onProgressUpdate(String... progress) {
        super.onProgressUpdate();
    }

    @Override
    protected void onPostExecute(String result) {
        dialog.dismiss();
        uploadReceiver.onReceive(result);
        super.onPostExecute(result);
    }


    public static void setUploadReceiver(UploadReceiver receiver) {
        uploadReceiver = receiver;
    }

    public interface UploadReceiver {
        void onReceive(String result);
    }

}
