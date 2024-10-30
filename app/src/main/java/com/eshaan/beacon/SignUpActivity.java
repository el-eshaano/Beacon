package com.eshaan.beacon;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();

        Button signupButton = findViewById(R.id.Signup_Button);
        EditText emailEditText = findViewById(R.id.EmailTextbox);
        EditText passwordEditText = findViewById(R.id.PasswordTextbox);
        EditText usernameEditText = findViewById(R.id.UsernameTextbox);

        signupButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String username = usernameEditText.getText().toString();

            if (!Utils.isEmailValid(email)) {
                Drawable errorBox = ContextCompat.getDrawable(this, R.drawable.error_box);
                emailEditText.setBackground(errorBox);
                return;
            }
            else {
                Drawable thickBox = ContextCompat.getDrawable(this, R.drawable.box);
                emailEditText.setBackground(thickBox);
            }

            if (!Utils.isPasswordValid(password)) {
                Drawable errorBox = ContextCompat.getDrawable(this, R.drawable.error_box);
                passwordEditText.setBackground(errorBox);
                return;
            }
            else {
                Drawable thickBox = ContextCompat.getDrawable(this, R.drawable.box);
                passwordEditText.setBackground(thickBox);
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Log.d("Signup", "createUser:success");
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(username)
                                        .build();
                                currentUser.updateProfile(profileUpdates);

                                // Redirect to login
                                finish();
                            }
                            else {
                                Log.w("Signup", "createUser:failure", task.getException());
                            }
                        }
                    });
        });
    }
}