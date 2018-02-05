package co.firstcrush.firstcrush;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.vision.Frame;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment{
    public WebView webMainView;
    View view;
    MainActivity mainActivity;
    private BottomNavigationViewHelper bottomNavigationViewHelper;
    private static boolean activityStarted;
    private View mCustomView;
    private View mContentView;
    private ViewGroup mContentViewContainer;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private MyWebChromeClient mWebChromeClient = null;
    private ProgressDialog progressBar;
    View decorView;
    ViewGroup parentView;
    private android.app.FragmentManager fragmentManager;
    AudioManager audioManager;
    String ua;

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

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            view = inflater.inflate(R.layout.main_fragment, container, false);
            parentView=container;
            webMainView = (WebView) view.findViewById(R.id.web1);
            WebSettings webSettings = webMainView.getSettings();
            // Enable Javascript
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setSaveFormData(true);
            webSettings.supportMultipleWindows();
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

            if (android.os.Build.VERSION.SDK_INT >= 20) {
                ua = "Chrome";
                webMainView.getSettings().setUserAgentString(ua);
            }

            //webMainView.getSettings().setUserAgentString("Mozilla/57.0 (compatible; MSIE 5.01; Windows NT 5.0)");
            webMainView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            // Force links and redirects to open in the WebView instead of in a browser
            mWebChromeClient = new MyWebChromeClient();
            webMainView.setWebChromeClient(mWebChromeClient);
            webMainView.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {
                    if (progressBar != null)
                        progressBar.dismiss();
                }
            });
            progressBar = ProgressDialog.show(getActivity(), "", "Loading...");
            webMainView.loadUrl("http://www.firstcrush.co");
            webMainView.setOnKeyListener(new View.OnKeyListener() {

                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == MotionEvent.ACTION_UP) {
                        if(webMainView.canGoBack()&& mCustomView == null) {
                            handler.sendEmptyMessage(1);
                            webMainView.goBack();
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


            return view;
        }

    private void webViewGoBack(){

        webMainView.goBack();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        view.onWindowFocusChanged(hasFocus);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webMainView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        webMainView.saveState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webMainView.onResume();
        decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView != null) {
            getActivity().setContentView(mContentView);;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webMainView = null;
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

    private class ExampleNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");
                    if (activityStarted
                            && getActivity().getIntent() != null
                            && (getActivity().getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
                        getActivity().finish();
                        return;
                    }
                }
                Log.d("OneSignalExample", "Full additionalData:\n" + additionalData.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}