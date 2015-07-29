package net.myr1.fronterfeed;

import android.test.AndroidTestCase;

import junit.framework.Assert;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;


public class FronterTest extends AndroidTestCase {


    public void testInvalidURL() throws IOException, Fronter.AuthenticationFailure
    {

        Fronter f = new Fronter("test123", "123");

        try {
            f.get("invalid url");
        }catch(MalformedURLException e){

        }


    }



    public void testAuth() throws IOException
    {

        Fronter f = new Fronter("test123", "123");

        try {
            f.get("https://fronter.com/hials/rss/get_today_rss.php");

            Assert.fail("Should fail auth");
        }catch(Fronter.AuthenticationFailure e){

        }


    }



    public void testUrlNotFound() throws IOException, Fronter.AuthenticationFailure
    {

        Fronter f = new Fronter("test123", "123");

        try {
            f.get("https://fronter.com/hials/rss_123/get_today_rss.php");

            Assert.fail("Should fail url not found");
        }catch(FileNotFoundException e){

        }


    }

}
