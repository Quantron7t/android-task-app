package com.example.todoapp.quill;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class QuillViewer extends WebView {

    private static final String SETUP_HTML = "file:///android_asset/quill_viewer.html";
    private boolean isReady = false;
    private String mContents;
    public QuillViewer(Context context) {
        this(context, null);
    }

    public QuillViewer(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public QuillViewer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebViewClient());
        loadUrl(SETUP_HTML);
    }

    protected QuillViewer.EditorWebViewClient createWebViewClient() {
        return new QuillViewer.EditorWebViewClient();
    }

    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            exec("javascript:QE.setHTML('" + contents + "');");
        } catch (Exception e) {
            // No handling
        }
        mContents = contents;
    }

    public String getHtml() {
        return mContents;
    }

    protected void exec(final String trigger) {
        if (isReady) {
            load(trigger);
        } else {
            postDelayed(() -> exec(trigger), 100);
        }
    }

    private void load(String trigger) {
        evaluateJavascript(trigger, null);
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
        }
    }
}
