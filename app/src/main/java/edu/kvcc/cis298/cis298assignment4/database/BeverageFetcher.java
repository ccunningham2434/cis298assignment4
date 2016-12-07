package edu.kvcc.cis298.cis298assignment4.database;

import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ccunn on 06-Dec-16.
 */

public class BeverageFetcher {

    private byte[] getUrlBytes(String urlSpec) throws IOException {
        // >Create a URL object from the passed in url.
        URL url = new URL(urlSpec);
        // >Create new http connection from the url.
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // >Create an output stream to hold data read from the url source.
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // >Create an input stream from the http connection.
            InputStream in = connection.getInputStream();

            // >Throw an exception if the response was not ok.
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead = 0;// >The number of bytes we are reading in.
            byte[] buffer = new byte[1024];// >Buffer size is 1024 bytes.

            while ((bytesRead = in.read(buffer)) > 0) {
                // >Write the bytes to the input stream.
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            in.close();

            // >Convert the output stream to a byte array.
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    private String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public void fetchBeverages() {
        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi")
                    .buildUpon()
                    //.appendQueryParameter("param", "value")
                    .build().toString();

            String jsonString = getUrlString(url);
            // Log.i("Beverage Fetcher", jsonString);
        } catch (IOException ioe) {
            Log.e("Beverage Fetcher", "Failed to load data", ioe);
        }
    }

}
