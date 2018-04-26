package sergiojuliogu.myapplication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by joseantoniocarbonell on 26/4/18.
 */

public class APIrequest extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... params) {

        try {
            URL url = new URL(params[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "*/*");

            connection.setDoOutput(true);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            writer.write(params[1]);
            writer.close();

            connection.connect();

            // Response: 400
            Log.e("Response", connection.getResponseMessage() + "");

        } catch (Exception e) {
            Log.e(e.toString(), "Something with request");
        }

        return null;
    }
    protected void onPostExecute(String result){
        super.onPostExecute(result);
    }
}
