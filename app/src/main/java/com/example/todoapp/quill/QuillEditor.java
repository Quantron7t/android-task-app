package com.example.todoapp.quill;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;

public class QuillEditor extends WebView {

    private static final String SETUP_HTML = "file:///android_asset/quill_editor.html";
    private static final String CALLBACK_SCHEME = "re-callback://";
    private boolean isReady = false;
    private String mContents;
    private OnTextChangeListener mTextChangeListener;
    private AfterInitialLoadListener mLoadListener;

    public interface OnTextChangeListener {

        void onTextChange(String text);
    }
    public interface AfterInitialLoadListener {
        void onAfterInitialLoad(boolean isReady);
    }

    public QuillEditor(Context context) {
        this(context, null);
    }

    public QuillEditor(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.webViewStyle);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public QuillEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setVerticalScrollBarEnabled(false);
        setHorizontalScrollBarEnabled(false);
        getSettings().setJavaScriptEnabled(true);
        setWebChromeClient(new WebChromeClient());
        setWebViewClient(createWebViewClient());
        loadUrl(SETUP_HTML);
    }

    protected EditorWebViewClient createWebViewClient() {
        return new EditorWebViewClient();
    }

    public void setOnTextChangeListener(OnTextChangeListener listener) {
        mTextChangeListener = listener;
    }
    private void callback(String text) {
        mContents = text.replaceFirst(CALLBACK_SCHEME, "");
        if (mTextChangeListener != null) {
            mTextChangeListener.onTextChange(mContents);
        }
    }

    public void setHtml(String contents) {
        if (contents == null) {
            contents = "";
        }
        try {
            //exec("javascript:RE.setHtml('" + URLEncoder.encode(contents, "UTF-8") + "');");
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
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    exec(trigger);
                }
            }, 100);
        }
    }

    private void load(String trigger) {
        evaluateJavascript(trigger, null);
    }

    protected class EditorWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            isReady = url.equalsIgnoreCase(SETUP_HTML);
            if (mLoadListener != null) {
                mLoadListener.onAfterInitialLoad(isReady);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            final String url = request.getUrl().toString();
            String decode = Uri.decode(url);

            if (TextUtils.indexOf(url, CALLBACK_SCHEME) == 0) {
                callback(decode);
                return true;
            }

            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection ic = super.onCreateInputConnection(outAttrs);
        InputConnectionCompat.OnCommitContentListener callback = new InputConnectionCompat.OnCommitContentListener() {
            @Override
            public boolean onCommitContent(InputContentInfoCompat inputContentInfo, int flags, Bundle opts) {
                if (inputContentInfo.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                    String gifUrl = inputContentInfo.getContentUri().toString();

                    Log.d("TAGGED_GIF",gifUrl);
                    /*// Pass the GIF URL to the JavaScript interface
                    evaluateJavascript("javascript:insertGif('" + gifUrl + "')", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            // Handle the result, if needed
                        }
                    });*/
                    return true;
                }
                return false;
            }
        };
        return InputConnectionCompat.createWrapper(ic, outAttrs, callback);
    }

}