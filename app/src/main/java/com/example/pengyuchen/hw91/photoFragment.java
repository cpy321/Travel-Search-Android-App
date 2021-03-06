package com.example.pengyuchen.hw91;


import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;



/**
 * Created by pengyuchen on 4/21/18.
 */

public class photoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.photo, container, false);
        final String placeId = ((detailActivity)getActivity()).getPlaceId();

        String url ="file:///android_asset/www/photo.html";
        WebView view =(WebView) rootView.findViewById(R.id.photoWeb);
        view.getSettings().setJavaScriptEnabled(true);
        view.loadUrl(url);

        view.addJavascriptInterface(new Object() {
            @JavascriptInterface
            public String placeIdFunction() {
                return placeId;
            }
        }, "placeID");

        view.setWebChromeClient(new WebChromeClient());


        return rootView;
    }


}
