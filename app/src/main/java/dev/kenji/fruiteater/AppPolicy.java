package dev.kenji.fruiteater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;


public class AppPolicy extends AppCompatActivity {

    private static final String TAG = "Policy";
    private static final String AGREED_TO_POLICY_KEY = "agreedToPolicy";
    private static final String LOAD_URL = "file:///android_asset/userconsent.html";

    private SharedPreferences MyPrefs;
    private WebView webView;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Intent data = result.getData();
                int resultCode = result.getResultCode();
                if (resultCode == RESULT_OK && data != null) {
                    handleFileChooseResult(data);
                } else {
                    clearUploadMessage();
                }
            }
    );

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_app_policy);

        MyPrefs = getSharedPreferences("Fruit Eater", MODE_PRIVATE);

        boolean agreedToPolicy = MyPrefs.getBoolean(AGREED_TO_POLICY_KEY, false);

        if (agreedToPolicy) {
            redirectToMainMenu();
            return;
        }

        if (TextUtils.isEmpty(LOAD_URL)) {
            finish();
            return;
        }

        setupWebView();
    }

    private void setupWebView() {
        webView = new WebView(this);
        setWebViewSettings();
        setWebViewClients();
        loadWebViewUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", ""));
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setSupportZoom(false);
        enableUniversalAccessFromFileURLs(settings);
    }

    private void enableUniversalAccessFromFileURLs(WebSettings settings) {
        try {
            Class<?> clazz = settings.getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(settings, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setWebViewClients() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });

        webView.addJavascriptInterface(new JsInterface(), "jsBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                AppPolicy.this.mUploadCallbackAboveL = filePathCallback;
                openFileChooseProcess();
                return true;
            }
        });
    }

    private void loadWebViewUrl() {
        webView.loadUrl(LOAD_URL);
        setContentView(webView);
    }

    private void openFileChooseProcess() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void handleFileChooseResult(Intent data) {
        Uri result = data.getData();
        if (result != null && mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(RESULT_OK, data));
            mUploadCallbackAboveL = null;
        } else {
            clearUploadMessage();
        }
    }

    private void clearUploadMessage() {
        if (mUploadCallbackAboveL != null) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }
    }

    private void redirectToMainMenu() {
        Intent intent = new Intent(AppPolicy.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public class JsInterface {
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void postMessage(String eventType, String data) {
            if (!TextUtils.isEmpty(eventType) && !TextUtils.isEmpty(data)) {
                if (eventType.equals("userconsent") && data.equals("Accepted")) {
                    MyPrefs.edit().putBoolean(AGREED_TO_POLICY_KEY, true).apply();
                    redirectToMainMenu();
                }
            }
            if ("CloseButtonClicked".equals(eventType)) {
                Log.d("WebView", "Close button clicked");
                // Handle the app exit here
                finish();
            }
        }

        private void redirectToMainMenu() {
            Intent intent = new Intent(AppPolicy.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
