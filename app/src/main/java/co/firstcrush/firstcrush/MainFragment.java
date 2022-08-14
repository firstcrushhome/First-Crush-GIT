package co.firstcrush.firstcrush;



import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import com.onesignal.OneSignal;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainFragment extends Fragment{
    public WebView webMainView;
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
    SwipeRefreshLayout mySwipeRefreshLayout;
    ViewTreeObserver.OnScrollChangedListener mOnScrollChangedListener;

    private final Handler handler = new Handler(Looper.getMainLooper()){
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
            if (message.what == 5) {
                onPause();
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
            view = inflater.inflate(R.layout.main_fragment, container, false);
            webMainView = view.findViewById(R.id.web1);
            mySwipeRefreshLayout = view.findViewById(R.id.swipeContainer);
            mySwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
            mySwipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.cardview_dark_background);
            progressBar = view.findViewById(R.id.progressbar);

            WebSettings webSettings = webMainView.getSettings();
            webSettings.setBuiltInZoomControls(false);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAppCacheEnabled(true);
            webSettings.setUseWideViewPort(true);
            webSettings.setLoadWithOverviewMode(false);
            webSettings.supportMultipleWindows();
            webSettings.setAllowFileAccess(true);
            webSettings.setDomStorageEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setAllowContentAccess(true);

            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

            String ua = "Chrome";

            webMainView.getSettings().setUserAgentString(ua);
            // Force links and redirects to open in the WebView instead of in a browser
            webMainView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            // Force links and redirects to open in the WebView instead of in a browser
            mWebChromeClient = new MyWebChromeClient();
            webMainView.setWebChromeClient(mWebChromeClient);
            webMainView.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                    if((String.valueOf(request.getUrl().getHost())).contains("firstcrush.co")) {
                        webMainView.loadUrl(String.valueOf(request.getUrl()));
                        Log.w("App Link","Internal Link");
                        progressBar.setVisibility(View.VISIBLE);
                        return false;
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW, request.getUrl());
                        startActivity(intent);
                        return true;
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                    mySwipeRefreshLayout.clearAnimation();
                        mySwipeRefreshLayout.setRefreshing(false);
                    super.onPageFinished(view, url);

                }
            });
            Intent appLinkIntent = getActivity().getIntent();
            String appLinkAction = appLinkIntent.getAction();
            Uri appLinkData = appLinkIntent.getData();
            if (appLinkData == null) {
                webMainView.loadUrl("https://www.firstcrush.co");
                Log.w("App Link","Home Page Link");
            }
            else {
                webMainView.loadUrl(appLinkData.toString());
                Log.w("App Link",appLinkData.toString());
            }

            mySwipeRefreshLayout.setOnRefreshListener(
                    new SwipeRefreshLayout.OnRefreshListener() {
                        @Override
                        public void onRefresh() {

                            webMainView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
                            webMainView.reload();
// This is important as it forces webview to load from the instead of reloading from cache
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    }
            );
            webMainView.setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP) {
                    if(webMainView.canGoBack()&& mCustomView == null) {
                        handler.sendEmptyMessage(1);
                        webMainView.goBack();
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
                if ((keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE)) {
                    handler.sendEmptyMessage(5);
                    return true;
                }
                    return false;
            });


            return view;
        }

    private void webViewGoBack(){
        webMainView.goBack();
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode);
        if (isInPictureInPictureMode) {
            //Toast.makeText(getContext(), "Entering Pip", Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
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


    public void onPause() {
        super.onPause();

             }

    @Override
    public void onStart() {
        super.onStart();


        mySwipeRefreshLayout.getViewTreeObserver()
                .addOnScrollChangedListener(mOnScrollChangedListener =
                        () -> {
                            if (webMainView.getScrollY() == 0)
                                mySwipeRefreshLayout.setEnabled(true);
                            else
                                mySwipeRefreshLayout.setEnabled(false);

                        });
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
        if (mCustomView != null) {
            getActivity().setContentView(mContentView);
        }
        mySwipeRefreshLayout.getViewTreeObserver()
                .removeOnScrollChangedListener(mOnScrollChangedListener);
    }



    @Override
    public void onDestroy() {
        webMainView.destroy();
        webMainView = null;
        super.onDestroy();
    }


    public class MyWebChromeClient extends WebChromeClient {
        private int mOriginalOrientation;
        private Context mContext;
        FrameLayout.LayoutParams LayoutParameters = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            super.onShowCustomView(view, callback);
            decorView = getActivity().getWindow().getDecorView();
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