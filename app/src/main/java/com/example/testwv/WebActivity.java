package com.example.testwv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WebActivity extends AppCompatActivity {
    WebView webView;
    JSInterface api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        getSupportActionBar().hide();

        webView = (WebView) findViewById(R.id.ui_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        //webView.loadUrl("https://dantri.com.vn/");

        api = new JSInterface();
        webView.addJavascriptInterface(api, "api");

       loadHtml();
    }

    void loadHtml(){
        String html = "";
        //readHtml("http://192.168.1.50:8080/");
        //html = "<h1>12345</h1>";
        OkHttpHandler handler = new OkHttpHandler();
        byte[] buf;
        try {
            buf = handler.execute("http://192.168.1.50:8080/test.html").get();
            if (buf != null && buf.length > 0) {
                html = new String(buf, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Connect Failed", Toast.LENGTH_SHORT).show();
        }

        //webView.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "utf-8", "");
        webView.loadData(html, "text/html", "UTF-8");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class OkHttpHandler extends AsyncTask<String, Void, byte[]> {

        OkHttpClient client = new OkHttpClient();
        Context c;

        @Override
        protected byte[] doInBackground(String... params) {

            Request.Builder builder = new Request.Builder();
            builder.url(params[0]);

            Request request = builder.build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().bytes();
            } catch (Exception e) {
                Toast.makeText(c, "Connect failed", Toast.LENGTH_SHORT).show();
            }
            return null;
        }
    }

    private class JSInterface {
        @JavascriptInterface
        public void refresh() {
            loadHtml();
        }
        @JavascriptInterface
        public void toastShow(String str) {
            Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
        }
    }

}