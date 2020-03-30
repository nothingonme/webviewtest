package com.koubei.webviewtest;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebPageFragment extends Fragment {

    private static final String DEFAULT_TAG = "WebPageFragment";

    private static final String KEY_PRESET_URL = "preset_url";
    private static final String KEY_TAG = "tag";
    private static final String KEY_SHOW_IMMEDIATELY = "show_immediately";

    public static WebPageFragment newInstance(@Nullable final String presetUrl,
                                              @Nullable final String tag,
                                              boolean showImmediately) {
        WebPageFragment fragment = new WebPageFragment();
        Bundle argument = new Bundle();
        argument.putString(KEY_PRESET_URL, presetUrl);
        argument.putString(KEY_TAG, tag);
        argument.putBoolean(KEY_SHOW_IMMEDIATELY, showImmediately);
        fragment.setArguments(argument);
        return fragment;
    }

    public static WebPageFragment newInstance(@Nullable final String presetUrl,
                                              @Nullable final String tag) {
        return newInstance(presetUrl, tag, false);
    }

    private String presetUrl;

    private Logger logger;

    private boolean showImmediately;

    private long processStartTime;

    private WebView webView;

    private String requestPage;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle bundle = getArguments();
        if (bundle != null) {
            presetUrl = bundle.getString(KEY_PRESET_URL, null);
        }
        // create logger
        if (bundle != null) {
            logger = Logger.withTag(bundle.getString(KEY_TAG, null));
        } else {
            logger = Logger.withTag(null);
        }
        // parse show behavior
        if (bundle != null) {
            showImmediately = bundle.getBoolean(KEY_SHOW_IMMEDIATELY, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        logger.d("fragment onCreateView, time is: " + System.currentTimeMillis());
        return inflater.inflate(R.layout.fragment_web_page, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        logger.d("fragment init webview, time is: " + System.currentTimeMillis());
        initWebView(view);
    }

    public void loadPage(final String url, boolean showImmediately) {
        requestPage = url;
        processStartTime = System.currentTimeMillis();
        logger.d("process start [" + url + "], time is: " + processStartTime);
        webView.loadUrl(url);
        if (showImmediately) {
            showSelf();
        }
    }

    private void initWebView(View rootView) {
        webView = rootView.findViewById(R.id.webview);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings settings = webView.getSettings();
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setJavaScriptEnabled(true);
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new BaseWebViewClient());
        // load empty page
        if (presetUrl != null) {
            loadPage(presetUrl, showImmediately);
        } else {
            webView.loadUrl("about:blank");
        }
    }

    private void showSelf() {
        if (getActivity() != null && getActivity().getSupportFragmentManager() != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .show(this)
                    .commit();
        }
    }

    private class BaseWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            logger.d("onPageStart [" + url + "], delta time is: " + (System.currentTimeMillis() - processStartTime));
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            logger.d("onPageFinished [" + url + "], delta time is: " + (System.currentTimeMillis() - processStartTime));
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
            logger.d("onPageCommitVisible [" + url + "], delta time is: " + (System.currentTimeMillis() - processStartTime));
            if (requestPage != null && TextUtils.equals(requestPage, url)) {
                showSelf();
            }
        }
    }
}
