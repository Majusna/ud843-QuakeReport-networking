package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    /** Sample JSON response for a USGS query */
    /** URL for earthquake data from the USGS dataset */
    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=6&limit=10";
    public static final String LOG_TAG = EarthquakeActivity.class.getSimpleName();
    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }
    // 1.kreiranje url objekta : url = new URL(stringUrl);
    //url klasa - pokazivac resursa na www, input je string "https://..."

    private static URL createURL (String stringUrl){
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    //2. salje zahtev za konekciju ,ulazni parametar je url, vraca string

    private static String makeHttpRequest (URL url)throws IOException {

        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;

        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // if the request was successful ,then read the input stream and parse the response
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());

        } finally {
            //if the url is null, we shouldn’t try to make the HTTP request
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            //if the JSON response is null or empty string, we shouldn’t try to continue with parsing it
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;

        }

    // 3 - procitaj inputStream podatke pomocu InputStreamReader i BufferedReader
    //prvedi sve u string, koji sadrzi ceo json odgovor sa servera

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //4 - koriguj JSON parsing metod da ekstrahuje listu zemljotresa sa web servera


    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */

    private static List<Earthquake> extractFeatureFromJson(String earthquakeJSON) {

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }


        // Create an empty List that we can start adding earthquakes to
        List<Earthquake> earthquakes = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            //Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.

            //Convert SAMPLE_JSON_RESPONSE String into a JSONObject
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            //Extract “features” JSONArray
            JSONArray earthquakeArray = baseJsonResponse.getJSONArray("features");

            //Loop through each feature in the array
                for (int i=0 ; i<earthquakeArray.length(); i++){
                    //Get earthquake JSONObject at position i
                    JSONObject baseJsonOb = earthquakeArray.getJSONObject(i);
                    //Get “properties” JSONObject
                    JSONObject properties = baseJsonOb.getJSONObject("properties");
                    //Extract “mag” for magnitude
                    double magnitude = properties.getDouble("mag");
                    //Extract “place” for location
                    String location = properties.getString("place");
                    //Extract “time” for time
                    long time = properties.getLong("time");

                    // Extract the value for the key called "url"
                    String url = properties.getString("url");

                    // Create a new {@link Earthquake} object with the magnitude, location, time,
                    // and url from the JSON response.
                    Earthquake earthquake = new Earthquake(magnitude, location, time, url);
                    earthquakes.add(earthquake);

                }


                
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }



}