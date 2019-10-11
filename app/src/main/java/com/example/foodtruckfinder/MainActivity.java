package com.example.foodtruckfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private Button button_register;
    private Button button_login;
    private View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_register:
                    register_button_clicked();
                    break;
                case R.id.button_login:
                    login_button_clicked();
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.login_email_input);
        password = (EditText) findViewById(R.id.login_password_input);
        button_register = (Button) findViewById(R.id.button_register);
        button_login = (Button) findViewById(R.id.button_login);
        mAuth = FirebaseAuth.getInstance();

        button_register.setOnClickListener(button_listener);
        button_login.setOnClickListener(button_listener);

    }

    private void register_button_clicked() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void login_button_clicked()
    {
        String Email = email.getText().toString().trim();
        String Password = password.getText().toString().trim();

        if (Email.isEmpty()){
            Toast.makeText(this, "Email is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.isEmpty()){
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            //startActivity(new Intent(getApplicationContext(),
                            //        ProfileActivity.class));
                            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                            finish();

                        }
                        else
                            {
                            Toast.makeText(MainActivity.this, "Wrong email or password (login)",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                });
    }
}
