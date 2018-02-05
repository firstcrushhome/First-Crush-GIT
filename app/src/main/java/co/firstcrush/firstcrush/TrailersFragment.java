package co.firstcrush.firstcrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class TrailersFragment extends Fragment {
    public WebView webTrailerView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ViewGroup mContentViewContainer;
    private MyWebChromeClient mWebChromeClient = null;
    private ProgressDialog progressBar;
    View decorView;
    AudioManager audioManager;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:{
                    webViewGoBack();
                }break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View trailersView=inflater.inflate(R.layout.trailers_fragment, container, false);
        webTrailerView = (WebView) trailersView.findViewById(R.id.web1);


        // Enable Javascript
        WebSettings webSettings = webTrailerView.getSettings();
        // Enable Javascript
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        //webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.supportMultipleWindows();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        if (android.os.Build.VERSION.SDK_INT >= 20) {
            String ua = "Chrome";
            webTrailerView.getSettings().setUserAgentString(ua);
        }
        mWebChromeClient = new MyWebChromeClient();
        webTrailerView.setWebChromeClient(mWebChromeClient);
        // Force links and redirects to open in the WebView instead of in a browser
        webTrailerView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (progressBar != null)
                    progressBar.dismiss();
            }
        });
        progressBar = ProgressDialog.show(getActivity(), "", "Loading...");
        webTrailerView.loadUrl("http://www.firstcrush.co/trailers/");

        webTrailerView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP) {
                    if(webTrailerView.canGoBack()&& mCustomView == null) {
                        handler.sendEmptyMessage(1);
                        webTrailerView.goBack();
                        return true;
                    }
                    else
                    {
                        decorView = getActivity().getWindow().getDecorView();
                        decorView.setSystemUiVisibility(
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
                    }
                }

                if ((keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)) {
                    handler.sendEmptyMessage(2);
                    return true;
                }
                if ((keyCode == KeyEvent.KEYCODE_VOLUME_UP)) {
                    handler.sendEmptyMessage(3);
                    return true;
                }
                if ((keyCode == KeyEvent.KEYCODE_HOME)) {
                    handler.sendEmptyMessage(4);
                    return true;
                }
                return false;
            }
        });
        return trailersView;
    }
    private void webViewGoBack(){
        webTrailerView.goBack();
    }
    public boolean onBackPressed() {
        if (webTrailerView.canGoBack()) {
            webTrailerView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webTrailerView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webTrailerView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webTrailerView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webTrailerView.saveState(outState);
    }





    public class MyWebChromeClient extends WebChromeClient {
        private int mOriginalOrientation;
        private Context mContext;
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mOriginalOrientation = getActivity().getRequestedOrientation();
            //mContentView = (RelativeLayout) getView();
            mContentView = getActivity().findViewById(R.id.activity_main);
            if (mContentView != null)
            {
                mContentView.setVisibility(View.GONE);
                mContentViewContainer=(ViewGroup) mContentView.getParent();
                mContentViewContainer.removeView(mContentView);
            }
            mCustomViewContainer = new FrameLayout(getActivity());
            mCustomViewContainer.setLayoutParams(LayoutParameters);
            mCustomViewContainer.setBackgroundResource(android.R.color.black);
            view.setLayoutParams(LayoutParameters);
            mCustomViewContainer.addView(view);
            mCustomView = view;
            mCustomViewCallback = callback;
            mCustomViewContainer.setVisibility(View.VISIBLE);
            getActivity().setContentView(mCustomViewContainer);
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (mCustomView != null) {
                // Hide the custom view.
                mCustomView.setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer = (FrameLayout) mCustomView.getParent();
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();

                // Show the content view.
                mContentView.setVisibility(View.VISIBLE);
                getActivity().setContentView(mContentView);

            }
        }
    }
}