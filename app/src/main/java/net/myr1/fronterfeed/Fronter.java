package net.myr1.fronterfeed;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Connects to fronter and downloads the feed
 */
public class Fronter {


    private String mUsername;
    private String mPassword;


    public class AuthenticationFailure extends Exception{}


    public Fronter(String username, String password)
    {
        this.mUsername = username;
        this.mPassword = password;
    }


    /**
     * Returns the content at the given url
     *
     * @param url the url to download
     * @return the content as a string
     * @throws AuthenticationFailure
     * @throws IOException
     */
    public String get(String url)
            throws AuthenticationFailure,
            IOException
    {
        return get(new URL(url));

    }

    /**
     *  Returns the content at the given url
     * @param url the url to download
     * @return the content as a string
     * @throws AuthenticationFailure
     * @throws IOException
     */
    public String get(URL url)
            throws AuthenticationFailure,
                   IOException
    {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {

            return doGetRequest(connection);

        } finally {
            connection.disconnect();
        }

    }



    private String doGetRequest(HttpURLConnection connection) throws IOException,
                                                                     AuthenticationFailure
    {

        try{

            addHeaders(connection);

            return readResponse(connection);


        }catch(IOException e) {
            int code = connection.getResponseCode();

            if(code == 401){
                throw new AuthenticationFailure();
            }

            throw e;
        }

    }

    // Adds authentication headers
    private void addHeaders(URLConnection connection)
    {
        String credentials = this.mUsername + ":" + this.mPassword;

        credentials = Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);

        connection.setRequestProperty("Authorization", "Basic " + credentials);
    }



    private String readResponse(URLConnection connection) throws IOException
    {

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "ISO-8859-1"));
        StringBuilder builder = new StringBuilder();

        try{

            String line;

            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

        } finally {
            reader.close();
        }


        return builder.toString();

    }





}
