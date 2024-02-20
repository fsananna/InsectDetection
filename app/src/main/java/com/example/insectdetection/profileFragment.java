package com.example.insectdetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class profileFragment extends Fragment {
    private FirebaseAuth auth;
    private Button button;
    private TextView emailTextView, countryTextView, dobTextView;
    private FirebaseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        button = view.findViewById(R.id.logout);
        emailTextView = view.findViewById(R.id.user_details);
        countryTextView = view.findViewById(R.id.countryId);
        dobTextView = view.findViewById(R.id.dobId);
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(requireContext(), Login.class);
            startActivity(intent);
            requireActivity().finish();
        } else {
            emailTextView.setText(user.getEmail());
            loadUserData(user.getUid());
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(requireContext(), Login.class);
                startActivity(intent);
                requireActivity().finish();
            }
        });

        return view;
    }

    private void loadUserData(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String country = dataSnapshot.child("country").getValue(String.class);
                    String dob = dataSnapshot.child("dob").getValue(String.class);
                    if (country != null) {
                        countryTextView.setText(country);
                    }
                    if (dob != null) {
                        dobTextView.setText(dob);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors here
            }
        });
    }
}
