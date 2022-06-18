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

public class TravelFragment extends Fragment {
    public WebView webTravelView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressDialog progressBar;
    private WebView webTravelView1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View travelView=inflater.inflate(R.layout.travel_fragment, container, false);
        webTravelView = (WebView) travelView.findViewById(R.id.web1);
        webTravelView.loadUrl("http://www.firstcrush.co/travel/");

        // Enable Javascript
        WebSettings webSettings = webTravelView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // Enable Javascript
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSaveFormData(true);
        //webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.supportMultipleWindows();
        String ua ="Chrome";
        webTravelView.getSettings().setUserAgentString(ua);
        // Force links and redirects to open in the WebView instead of in a browser
        webTravelView.setWebViewClient(new WebViewClient());
        webTravelView.loadUrl("http://www.firstcrush.co/travel/");
        return travelView;
    }

    public boolean onBackPressed() {
        if (webTravelView.canGoBack()) {
            webTravelView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webTravelView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webTravelView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webTravelView = null;
    }
}