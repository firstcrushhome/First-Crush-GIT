package co.firstcrush.firstcrush;

import android.app.ProgressDialog;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class TrailersFragment extends Fragment {
    public WebView webTrailerView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View trailersView=inflater.inflate(R.layout.trailers_fragment, container, false);
        webTrailerView = (WebView) trailersView.findViewById(R.id.web1);


        // Enable Javascript
        WebSettings webSettings = webTrailerView.getSettings();
        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.supportMultipleWindows();
        String ua ="Chrome";

        webTrailerView.getSettings().setUserAgentString(ua);

        // Force links and redirects to open in the WebView instead of in a browser
        webTrailerView.setWebViewClient(new WebViewClient() );
        webTrailerView.loadUrl("http://www.firstcrush.co/trailers/");
        return trailersView;
    }

    public boolean handleonBackPressed() {
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
}