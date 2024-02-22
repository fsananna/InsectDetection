package com.example.insectdetection;
import com.google.android.material.textfield.TextInputLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText editTextEmail, editTextPassword, editTextDob, editTextCountry;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView;
    FirebaseUser currentUser;

    private TextView registerDate;
    private DatePickerDialog datePickerDialog;

    DatabaseReference usersRef;

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        editTextCountry = findViewById(R.id.inputTV);
        buttonReg = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        registerDate = findViewById(R.id.idBtnPickDate);

        TextInputLayout textInputLayout = findViewById(R.id.inputLayout);
        MaterialAutoCompleteTextView autoCompleteTextView = findViewById(R.id.inputTV);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        // Inside onCreate method after initializing autoCompleteTextView
        autoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedOption = (String) parent.getItemAtPosition(position);
            if (!TextUtils.isEmpty(selectedOption)) {
                checkUsernameAvailability(selectedOption);
            }
        });

        registerDate.setOnClickListener(this);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        });

        buttonReg.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String email, password, country, Dob;
            email = String.valueOf(editTextEmail.getText());
            password = String.valueOf(editTextPassword.getText());
            country = String.valueOf(editTextCountry.getText());
            Dob = String.valueOf(registerDate.getText());

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(country) || TextUtils.isEmpty(Dob)) {
                Toast.makeText(Register.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                return;
            }

            // Create user in Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String userId = currentUser.getUid();

                            User user = new User(email, password, country, Dob);

                            // Add the user object to the "users" node in the Firebase database
                            usersRef.child(userId).setValue(user).addOnCompleteListener(task1 -> {
                                progressBar.setVisibility(View.GONE);
                                if (task1.isSuccessful()) {
                                    Toast.makeText(Register.this, "Account Created.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Register.this, "Failed to create account.", Toast.LENGTH_SHORT).show();
                                    Log.e("FirebaseDatabase", "Failed to add user data: " + task1.getException().getMessage());
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(Register.this, "Failed to create account.", Toast.LENGTH_SHORT).show();
                            Log.e("AuthenticationError", "Authentication failed: " + task.getException().getMessage());
                        }
                    });
        });
    }

    private void checkUsernameAvailability(String username) {
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    editTextCountry.setError("Username already exists");
                } else {
                    editTextCountry.setError(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseDatabase", "Error checking username availability: " + error.getMessage());
            }
        });
    }

    @Override
    public void onClick(View v) {
        DatePicker datePicker = new DatePicker(this);
        int currentDay = datePicker.getDayOfMonth();
        int currentMonth = (datePicker.getMonth()) + 1;
        int currentYear = datePicker.getYear();

        datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> registerDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year), currentYear, currentMonth, currentDay);
        datePickerDialog.show();
    }
}
