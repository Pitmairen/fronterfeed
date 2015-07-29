package net.myr1.fronterfeed;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


/**
 * The login screen
 */
public class LoginActivity extends Activity {


    public static final String ERROR_MESSAGE_EXTRA = "errorMessage";


    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mBaseUrlView;
    private CheckBox mStoreCredentialsCheckbox;

    private Settings mSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mSettings = new Settings(this);

        mBaseUrlView = (EditText) findViewById(R.id.baseurl);
        mUsernameView = (EditText) findViewById(R.id.username);
        mPasswordView = (EditText) findViewById(R.id.password);

        // Set old values
        mBaseUrlView.setText(mSettings.getBaseFeedUrl());
        mUsernameView.setText(mSettings.getUsername());
        mPasswordView.setText(mSettings.getPassword());



        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView urlHint = (TextView) findViewById(R.id.baseurl_hint);
        urlHint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showUrlHint();
            }
        });


        mStoreCredentialsCheckbox = (CheckBox) findViewById(R.id.store_password);

        mStoreCredentialsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Utils.showMessageDialog(LoginActivity.this, getString(R.string.store_credentials_title),
                            getString(R.string.store_credentials_description));
                }
            }
        });


        // Show the error message if one is provided.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString(ERROR_MESSAGE_EXTRA, "");
            if(!TextUtils.isEmpty(value))
                showErrorMessage(value);
        }

    }


    private void showUrlHint()
    {

        Utils.showMessageDialog(this, getString(R.string.feed_url_hint_title),
                getString(R.string.feed_url_hint_description));

    }


    private void showErrorMessage(String message)
    {

        Utils.showMessageDialog(this, getString(R.string.generic_error_title), message);

    }


    private void showFeedList()
    {

        Intent intent = new Intent(this, FeedActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

        startActivity(intent);
    }


    private void updateCredentials(String username, String password, String baseUrl)
    {
        mSettings.startEdit();
        mSettings.setUsername(username);
        mSettings.setPassword(password);
        mSettings.setBaseFeedUrl(baseUrl);
        mSettings.commitEdit();
        mSettings.stopEdit();
    }


    private void attemptLogin() {

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mBaseUrlView.setError(null);
        mStoreCredentialsCheckbox.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String url = mBaseUrlView.getText().toString();
        boolean storeCredentialsConfirm = mStoreCredentialsCheckbox.isChecked();

        boolean cancel = false;
        View focusView = null;


        if(!storeCredentialsConfirm){
            mStoreCredentialsCheckbox.setError(getString(R.string.error_credentials_must_be_stored));
            focusView = mStoreCredentialsCheckbox;
            cancel = true;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if(TextUtils.isEmpty(url)){
            mBaseUrlView.setError(getString(R.string.error_field_required));
            focusView = mBaseUrlView;
            cancel = true;
        }




        if (cancel) {
            focusView.requestFocus();

        } else {

            updateCredentials(username, password, Utils.fixURL(url));

            showFeedList();

        }
    }




}


