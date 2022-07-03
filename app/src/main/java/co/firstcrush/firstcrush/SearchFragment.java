package co.firstcrush.firstcrush;

import android.os.Bundle;
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

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class SearchFragment extends Fragment {
    public WebView webSearchView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressBar progressBar;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View searchView=inflater.inflate(R.layout.search_fragment, container, false);
        webSearchView = (WebView) searchView.findViewById(R.id.web1);
        webSearchView.loadUrl("https://www.firstcrush.co/search-content/");

        // Enable Javascript
        WebSettings webSettings = webSearchView.getSettings();

        // Force links and redirects to open in the WebView instead of in a browser
        webSearchView.setWebViewClient(new WebViewClient());
        // Enable Javascript
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        //webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.supportMultipleWindows();
        String ua ="Chrome";

        webSearchView.getSettings().setUserAgentString(ua);
        // Force links and redirects to open in the WebView instead of in a browser
        webSearchView.setWebViewClient(new WebViewClient());
        return searchView;
    }

    public boolean onBackPressed() {
        if (webSearchView.canGoBack()) {
            webSearchView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webSearchView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webSearchView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webSearchView = null;
    }
}