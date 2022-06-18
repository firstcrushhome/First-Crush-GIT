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
import android.widget.RelativeLayout;

public class NewsFragment extends Fragment {
    public WebView webNewsView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressDialog progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newsView=inflater.inflate(R.layout.news_fragment, container, false);
        webNewsView = (WebView) newsView.findViewById(R.id.web1);
        webNewsView.loadUrl("https://www.firstcrush.co/news/");

        // Enable Javascript
        WebSettings webSettings = webNewsView.getSettings();

        // Force links and redirects to open in the WebView instead of in a browser
        webNewsView.setWebViewClient(new WebViewClient());
        // Enable Javascript

        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        //webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.supportMultipleWindows();
        webSettings.setJavaScriptEnabled(true);
        String ua ="Chrome";

        webNewsView.getSettings().setUserAgentString(ua);
        // Force links and redirects to open in the WebView instead of in a browser
        webNewsView.setWebViewClient(new WebViewClient());
        return newsView;
    }

    public boolean onBackPressed() {
        if (webNewsView.canGoBack()) {
            webNewsView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webNewsView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webNewsView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webNewsView = null;
    }
}