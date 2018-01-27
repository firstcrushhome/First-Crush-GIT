package co.firstcrush.firstcrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class NotificationsFragment extends Fragment {
    public WebView webNotificationsView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private MyWebChromeClient mWebChromeClient = null;
    private ProgressDialog progressBar;
    View decorView;

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

    public static NotificationsFragment newInstance() {
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View notificationsView=inflater.inflate(R.layout.notifications_fragment, container, false);
        webNotificationsView = (WebView) notificationsView.findViewById(R.id.web1);
        webNotificationsView.loadUrl("http://www.firstcrush.co/notifications");

        // Enable Javascript
        WebSettings webSettings = webNotificationsView.getSettings();

        // Force links and redirects to open in the WebView instead of in a browser
        webNotificationsView.setWebViewClient(new WebViewClient());
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
            webNotificationsView.getSettings().setUserAgentString(ua);
        }
        // Force links and redirects to open in the WebView instead of in a browser
        mWebChromeClient = new MyWebChromeClient();
        webNotificationsView.setWebChromeClient(mWebChromeClient);
        webNotificationsView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                if (progressBar != null)
                    progressBar.dismiss();
            }
        });
        progressBar = ProgressDialog.show(getActivity(), "", "Loading...");
        webNotificationsView.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP) {
                    if(webNotificationsView.canGoBack()&& mCustomView == null) {
                        handler.sendEmptyMessage(1);
                        webNotificationsView.goBack();
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
        return notificationsView;
    }
    private void webViewGoBack(){
        webNotificationsView.goBack();
    }
    public boolean onBackPressed() {
        if (webNotificationsView.canGoBack()) {
            webNotificationsView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webNotificationsView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webNotificationsView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webNotificationsView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webNotificationsView.saveState(outState);
    }



    public class MyWebChromeClient extends WebChromeClient {
        private Context mContext;
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Activity activity = getActivity();
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

            mContentView = (RelativeLayout) view.findViewWithTag(R.layout.notifications_fragment);
            getView().setVisibility(View.GONE);
            mCustomViewContainer = new FrameLayout(activity);
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
            decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            if (mCustomView == null) {
                mContentView.setVisibility(View.VISIBLE);
            } else {
                // Hide the custom view.
                getView().setVisibility(View.GONE);
                // Remove the custom view from its container.
                mCustomViewContainer.removeView(mCustomView);
                mCustomView = null;
                mCustomViewContainer.setVisibility(View.GONE);
                mCustomViewCallback.onCustomViewHidden();
                mContentView.setVisibility(View.VISIBLE);
                // Show the content view.
                getActivity().setContentView(mContentView);
            }


        }
    }
}