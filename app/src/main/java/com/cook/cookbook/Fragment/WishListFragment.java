package com.cook.cookbook.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cook.cookbook.R;
import com.facebook.shimmer.ShimmerFrameLayout;


public class WishListFragment extends Fragment {

    private WebView webView;

    ShimmerFrameLayout blogShimmer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_wish_list, container, false);

        webView = view.findViewById(R.id.blog_webview);

        blogShimmer = view.findViewById(R.id.blog_shimmer);
        blogShimmer.startShimmer();

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                // Inject JavaScript to hide the unwanted elements
                webView.evaluateJavascript(
                        "(function() { " +
                                "   document.querySelector('#masthead').style.display='none'; " + // Hide header Logo
                                "   document.querySelector('nav').style.display='none'; " + // Navbar
                                "   document.querySelector('.page-header').style.display='none'; " + // Hide header Logo
                                "   document.querySelector('footer').style.display='none'; " + // Hide footer
                                "   document.querySelector('#right-sidebar').style.display='none'; " + // Hide sidebar (if exists)
                                "   document.querySelector('.site-footer').style.display='none'; " + // Hide any ad sections
                                "})()", null);

                blogShimmer.stopShimmer();
                blogShimmer.setVisibility(View.GONE);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }
        });

        webView.loadUrl("https://blog.princesahni.com/category/food/");

        webView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    WebView webView = (WebView) v;

                    switch(keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (webView.canGoBack()) {
                                webView.goBack();
                                return true;
                            }
                            break;
                    }
                }
                return false;
            }
        });

        return  view;
    }


}