package com.example.insectdetection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

public class ratingFragment extends Fragment {

    private int likeCount = 0;
    private int dislikeCount = 0;
    private SharedPreferences sharedPreferences;

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

        // Initialize SharedPreferences
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // Retrieve the like count from SharedPreferences
        likeCount = sharedPreferences.getInt("likeCount", 0);
        // Retrieve the dislike count from SharedPreferences
        dislikeCount = sharedPreferences.getInt("dislikeCount", 0);

        // Update the like count TextView
        TextView likeCountTextView = view.findViewById(R.id.like_count);
        likeCountTextView.setText(String.valueOf(likeCount));

        // Update the dislike count TextView
        TextView dislikeCountTextView = view.findViewById(R.id.dislike_count);
        dislikeCountTextView.setText(String.valueOf(dislikeCount));

        // Rating functionality
        RatingBar mRating = view.findViewById(R.id.rating);
        Button mSubmit = view.findViewById(R.id.submit);
        final TextView mThank = view.findViewById(R.id.thank);

        // Like and Dislike functionality
        final ImageButton likeButton = view.findViewById(R.id.like_button);
        final ImageButton dislikeButton = view.findViewById(R.id.dislike_button);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likeCount++;
                likeCountTextView.setText(String.valueOf(likeCount));
                // Save the updated like count to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("likeCount", likeCount);
                editor.apply();
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dislikeCount++;
                dislikeCountTextView.setText(String.valueOf(dislikeCount));
                // Save the updated dislike count to SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("dislikeCount", dislikeCount);
                editor.apply();
            }
        });

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
