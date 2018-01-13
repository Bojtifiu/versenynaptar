package com.example.kozma.versenynaptar;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;

/**
 * Created by kozma on 2017. 10. 29..
 */

public class HttpAsyncTask extends AsyncTask<String, Void, String>
{
    String result;

    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        String prop = params[1];
        this.result = POST(url,prop);
        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        this.result=result;
    }

    private String POST(String url, String prop){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();

            HttpPost httpPost = new HttpPost(url);
            StringEntity se = new StringEntity(prop,"UTF-8");

            se.setContentType("application/x-www-form-urlencoded");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
            {
                result = convertInputStreamToString(inputStream);
                System.out.println(result);

            }
            else
                result = "Reply is empty!";

        } catch (Exception e) {
            System.out.println("Unable to connect.");
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream, "UTF-8"));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
