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

public class ProfileFragment extends Fragment {
    public WebView webProfileView;
    private BottomNavigationView navigation;
    private static boolean activityStarted;
    private View mCustomView;
    private RelativeLayout mContentView;
    private FrameLayout mCustomViewContainer;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    private ProgressDialog progressBar;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View newsView=inflater.inflate(R.layout.profile_fragment, container, false);
        webProfileView = (WebView) newsView.findViewById(R.id.web1);
        webProfileView.loadUrl("http://www.firstcrush.co/your-profile");

        // Enable Javascript
        WebSettings webSettings = webProfileView.getSettings();

        // Force links and redirects to open in the WebView instead of in a browser
        webProfileView.setWebViewClient(new WebViewClient());
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

        webProfileView.getSettings().setUserAgentString(ua);
        // Force links and redirects to open in the WebView instead of in a browser
        webProfileView.setWebViewClient(new WebViewClient());
        return newsView;
    }

    public boolean onBackPressed() {
        if (webProfileView.canGoBack()) {
            webProfileView.goBack();
            return true;
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        webProfileView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        webProfileView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        webProfileView = null;
    }
}