package net.myr1.fronterfeed;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Displays a FeedItem's description
 */
public class MessageActivity extends Activity {

    private WebView textView;
    private View progressView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_message);

        Intent intent = getIntent();

        String description = intent.getStringExtra("description");

        this.progressView = findViewById(R.id.message_view_progress);

        textView = (WebView) findViewById(R.id.view_message);
        textView.setWebViewClient(new MyWebViewClient());


        showProgress(true);

        // Just set fronter.com as base url so that shouldOverrideUrlLoading will get called
        // for relative urls.
        textView.loadDataWithBaseURL("http://fronter.com/", createHtmlDocument(description),
                "text/html", "utf-8", null);


    }


    public void showProgress(final boolean show) {

        this.progressView.setVisibility(show ? View.VISIBLE : View.GONE);
        this.textView.setVisibility(show ? View.GONE : View.VISIBLE);
    }


    private String createHtmlDocument(String description)
    {

        return "<!DOCTYPE html><html><head><title>Message</title></head><body>"
                + description + "</body></html>";
    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // Disable links
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            showProgress(false);

        }
    }

}
