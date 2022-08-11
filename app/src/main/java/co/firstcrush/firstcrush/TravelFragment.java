package co.firstcrush.firstcrush;


import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TravelFragment extends Fragment{
    public WebView webTravelView;
    View view;
    private BottomNavigationViewHelper bottomNavigationViewHelper;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressBar progressBar;
    private ViewGroup mContentViewContainer;
    private MyWebChromeClient mWebChromeClient = null;
    AudioManager audioManager;
    View decorView;
    SwipeRefreshLayout mySwipeRefreshLayoutTravel;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    private Handler handler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message message) {
            if (message.what == 1) {
                webViewGoBack();
            }
            if (message.what == 2) {
                onKeyDown();
            }
            if (message.what == 3) {
                onKeyUp();
            }
        }
    };
    public static TravelFragment newInstance() {
        TravelFragment fragment = new TravelFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.travel_fragment, container, false);
        webTravelView = view.findViewById(R.id.web1);
        progressBar = (ProgressBar) view.findViewById(R.id.progressbar);

        mySwipeRefreshLayoutTravel = view.findViewById(R.id.swipeContainer);

        progressBar.setVisibility(View.VISIBLE);
        WebSettings webSettings = webTravelView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(false);
        webSettings.supportMultipleWindows();
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        String ua = "Chrome";

        webTravelView.getSettings().setUserAgentString(ua);
        // Force links and redirects to open in the WebView instead of in a browser
        webTravelView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Force links and redirects to open in the WebView instead of in a browser
        mWebChromeClient = new MyWebChromeClient();
        webTravelView.setWebChromeClient(mWebChromeClient);
        webTravelView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String urlNewString) {
                webView.loadUrl(urlNewString);
                progressBar.setVisibility(View.VISIBLE);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                mySwipeRefreshLayoutTravel.clearAnimation();
                mySwipeRefreshLayoutTravel.setRefreshing(false);
                super.onPageFinished(view, url);

            }
        });
        webTravelView.loadUrl("https://www.firstcrush.co/Travel/");
        mySwipeRefreshLayoutTravel.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        webTravelView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                        webTravelView.reload();
// This is important as it forces webview to load from the instead of reloading from cache

                    }
                }
        );

        webTravelView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == MotionEvent.ACTION_UP) {
                if(webTravelView.canGoBack()&& mCustomView == null) {
                    handler.sendEmptyMessage(1);
                    webTravelView.goBack();
                    return true;
                }
                else
                {
                    decorView = requireActivity().getWindow().getDecorView();
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
        });


        return view;
    }



    private void webViewGoBack(){
        webTravelView.goBack();
    }

    public boolean onKeyUp() {
        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        return true;
    }

    public boolean onKeyDown() {
        AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        am.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        return true;
    }


    public void onWindowFocusChanged(boolean hasFocus) {
        view.onWindowFocusChanged(hasFocus);
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webTravelView.saveState(outState);
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
        webTravelView.onResume();

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

    @Override
    public void onStart() {
        super.onStart();


        mySwipeRefreshLayoutTravel.getViewTreeObserver()
                .addOnScrollChangedListener(mOnScrollChangedListener =
                        () -> {
                            if (webTravelView.getScrollY() == 0)
                                mySwipeRefreshLayoutTravel.setEnabled(true);
                            else
                                mySwipeRefreshLayoutTravel.setEnabled(false);

                        });
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView != null) {
            getActivity().setContentView(mContentView);
        }
        mySwipeRefreshLayoutTravel.getViewTreeObserver()
                .removeOnScrollChangedListener(mOnScrollChangedListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webTravelView = null;
    }
    public class MyWebChromeClient extends WebChromeClient {
        private int mOriginalOrientation;
        private Context mContext;
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            decorView = requireActivity().getWindow().getDecorView();
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
            decorView = requireActivity().getWindow().getDecorView();
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
                requireActivity().setContentView(mContentView);

            }
        }
    }

    private abstract class ExampleNotificationOpenedHandler implements OneSignal.OSNotificationOpenedHandler {
        public void notificationOpened(String message, JSONObject additionalData, boolean isActive) {
            try {
                if (additionalData != null) {
                    if (additionalData.has("actionSelected"))
                        Log.d("OneSignalExample", "OneSignal notification button with id " + additionalData.getString("actionSelected") + " pressed");
                    if (activityStarted
                            && null != Objects.requireNonNull(requireActivity().getIntent())
                            && 0 != (requireActivity().getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)) {
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