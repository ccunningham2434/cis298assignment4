package edu.kvcc.cis298.cis298assignment4.database;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.kvcc.cis298.cis298assignment4.Beverage;

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

    public List<Beverage> fetchBeverages() {
        List<Beverage> beverageList = new ArrayList<>();// >List to hold the beverages from the json string.

        try {
            String url = Uri.parse("http://barnesbrothers.homeserver.com/beverageapi")
                    .buildUpon()
                    //.appendQueryParameter("param", "value")
                    .build().toString();

            String jsonString = getUrlString(url);
            Log.i("Beverage Fetcher", jsonString);
            JSONArray jsonArray = new JSONArray(jsonString);

            parseBeverages(beverageList, jsonArray);

        } catch (JSONException jse) {
            Log.e("Beverage Fetcher", "Failed to parse JSON", jse);
        }catch (IOException ioe) {
            Log.e("Beverage Fetcher", "Failed to load data", ioe);
        }

        return beverageList;
    }

    private void parseBeverages(List<Beverage> beverageList, JSONArray jsonArray) throws IOException, JSONException {
        for (int i = 0; i <jsonArray.length(); i++) {
            // >Get a json object out of the array.
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            // >Create a new beverage and give it a uuid.
            Beverage beverage = new Beverage(UUID.randomUUID());
            // >Use the json object to set the rest of the properties.
            beverage.setId(jsonObject.getString("id"));
            beverage.setName(jsonObject.getString("name"));
            beverage.setPack(jsonObject.getString("pack"));
            beverage.setPrice(jsonObject.getDouble("price"));
            beverage.setActive((jsonObject.getString("isActive") == "1") ? true : false);

            beverageList.add(beverage);
        }
    }

}
