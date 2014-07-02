package com.rebot.roomme;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Toshiba on 3/05/14.
 */
public class WebViewFacebook extends SherlockActivity {
    private WebView webView;
    private Roome app;

    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.webview_facebook_layout);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        app = (Roome) getApplication();
        JSONObject profile = app.roomieSeleccionado.getUser().getJSONObject("profile");

        webView = (WebView) findViewById(R.id.webview);
        try {
            startWebView("https://facebook.com/"+ profile.getString("facebookId"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startWebView(String url) {
        //Create new webview Client to show progress dialog
        //When opening a url or click on link
        webView.setWebViewClient(new WebViewClient() {
            ProgressDialog progressDialog;
            //If you will not use this method url links are opeen in new brower not in webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progressDialog = new ProgressDialog(WebViewFacebook.this);
                progressDialog.setMessage("Cargando...");
                progressDialog.show();
                view.loadUrl(url);
                return true;
            }
            //Show loader on url load
            public void onLoadResource (WebView view, String url) {
                if (progressDialog == null) {
                    // in standard case YourActivity.this
                /*progressDialog = new ProgressDialog(News2Web.this);
                if(!progressDialog.isShowing()){
                    progressDialog.setMessage("Loading...");
                    progressDialog.show();
                } */
                }
            }
            public void onPageFinished(WebView view, String url) {
                try{
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        progressDialog = null;
                    }
                }catch(Exception exception){
                    exception.printStackTrace();
                }
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setBuiltInZoomControls(true);
        //Load url in webview
        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == android.R.id.home){
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
