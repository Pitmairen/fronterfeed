package net.myr1.fronterfeed;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;


public class SettingsTest extends AndroidTestCase {

    AesCbcWithIntegrity.SecretKeys mKeys;
    Settings mSettings;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mKeys = Values.getKeys();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().clear().commit();

        mSettings = new Settings(getContext(), mKeys);
    }

    @Override
    protected void tearDown() throws Exception {
        super.setUp();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().clear().commit();

    }


    public void testUsernameAndPassword(){

        String username = "hello";
        String password = "12345";

        mSettings.startEdit();
        mSettings.setUsername(username);
        mSettings.setPassword(password);
        mSettings.commitEdit();
        mSettings.stopEdit();

        String gotUsername = mSettings.getUsername();
        String gotPassword = mSettings.getPassword();


        assertEquals(username, gotUsername);
        assertEquals(password, gotPassword);



        // Cached version
        gotUsername = mSettings.getUsername();
        gotPassword = mSettings.getPassword();


        assertEquals(username, gotUsername);
        assertEquals(password, gotPassword);



        mSettings.startEdit();
        mSettings.setUsername("u1");
        mSettings.setPassword("p1");
        mSettings.commitEdit();
        mSettings.stopEdit();

        // Check if cache was removed
        // Cached version
        gotUsername = mSettings.getUsername();
        gotPassword = mSettings.getPassword();

        assertEquals("u1", gotUsername);
        assertEquals("p1", gotPassword);

    }


    public void testEncryption() throws UnsupportedEncodingException, GeneralSecurityException {

        String username = "hello";
        String password = "12345";


        mSettings.startEdit();
        mSettings.setUsername(username);
        mSettings.setPassword(password);
        mSettings.commitEdit();
        mSettings.stopEdit();

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());


        String plainUsername = decryptString(prefs.getString("user2", ""));
        String plainPassword = decryptString(prefs.getString("pass2", ""));

        assertEquals(username, plainUsername);
        assertEquals(password, plainPassword);


    }




    public void testUpgrade_1_2() throws UnsupportedEncodingException, GeneralSecurityException {

        String username = "user1";
        String password = "pass1";


        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putString("fronter_username", username)
                .putString("fronter_password", password)
                .commit();

        mSettings.doStorageUpgrade();


        assertEquals(false, prefs.contains("fronter_username"));
        assertEquals(false, prefs.contains("fronter_password"));

        assertEquals(true, prefs.contains("user2"));
        assertEquals(true, prefs.contains("pass2"));


        String plainUsername = decryptString(prefs.getString("user2", ""));
        String plainPassword = decryptString(prefs.getString("pass2", ""));

        assertEquals(username, plainUsername);
        assertEquals(password, plainPassword);

        assertEquals(2, prefs.getInt("storage_version", -1));

    }


    // No data before upgrade
    public void testUpgrade_1_2_empty() throws UnsupportedEncodingException, GeneralSecurityException {

        String username = "user1";
        String password = "pass1";

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());

        mSettings.doStorageUpgrade();


        assertEquals(false, prefs.contains("fronter_username"));
        assertEquals(false, prefs.contains("fronter_password"));

        assertEquals(false, prefs.contains("user2"));
        assertEquals(false, prefs.contains("pass2"));

        assertEquals(2, prefs.getInt("storage_version", -1));

    }


    public void testUpgrade_2_already_at_2() throws UnsupportedEncodingException, GeneralSecurityException {

        String username = "user1";
        String password = "pass1";

        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit()
                .putInt("storage_version", 2)
                .commit();

        mSettings.startEdit();
        mSettings.setUsername(username);
        mSettings.setPassword(password);
        mSettings.commitEdit();
        mSettings.stopEdit();


        mSettings.doStorageUpgrade();

        assertEquals(false, prefs.contains("fronter_username"));
        assertEquals(false, prefs.contains("fronter_password"));


        assertEquals(true, prefs.contains("user2"));
        assertEquals(true, prefs.contains("pass2"));

        assertEquals(2, prefs.getInt("storage_version", -1));


        String plainUsername = decryptString(prefs.getString("user2", ""));
        String plainPassword = decryptString(prefs.getString("pass2", ""));

        assertEquals(username, plainUsername);
        assertEquals(password, plainPassword);

    }


    private String encryptString(String input) throws UnsupportedEncodingException, GeneralSecurityException {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                null;
        cipherTextIvMac = AesCbcWithIntegrity.encrypt(input, mKeys);
        return cipherTextIvMac.toString();


    }

    private String decryptString(String input) throws UnsupportedEncodingException, GeneralSecurityException {

        AesCbcWithIntegrity.CipherTextIvMac cipherTextIvMac =
                new AesCbcWithIntegrity.CipherTextIvMac(input);

        return AesCbcWithIntegrity.decryptString(cipherTextIvMac, mKeys);


    }

}
