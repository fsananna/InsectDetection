package com.example.insectdetection;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class ratingFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rating, container, false);
        WebView webView = view.findViewById(R.id.webView);
        String video ="<iframe width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/Oo_IZysj4F4?si=XxLiqqxWi1GgYrtn\" title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share\" allowfullscreen></iframe>";
        webView.loadData(video,"text/html","utf-8");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());

        // Rating functionality
        RatingBar mRating = view.findViewById(R.id.rating);
        Button mSubmit = view.findViewById(R.id.submit);
        final TextView mThank = view.findViewById(R.id.thank);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float rating = mRating.getRating();

                mThank.setVisibility(View.VISIBLE);
                mSubmit.setVisibility(View.INVISIBLE);

                if (rating == 5) {
                    mThank.setText(R.string.thank_you);
                } else if (rating == 0) {
                    mThank.setText(R.string.very_disappointing);
                } else {
                    mThank.setText(R.string.thank_you_for_your_feedback);
                }
            }
        });

        return view;
    }
}
