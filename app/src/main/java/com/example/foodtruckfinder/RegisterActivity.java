package com.example.foodtruckfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private EditText password;
    private EditText email;
    private EditText firstname;
    private EditText lastname;
    private EditText phone;

    private String role;

    private Button button_register;

    private RadioButton customer;
    private RadioButton owner;


    private View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_register_activity:
                    register_button_clicked();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = (EditText) findViewById(R.id.register_email_input);
        password = (EditText) findViewById(R.id.register_password_input);
        firstname = (EditText) findViewById(R.id.register_firstname_input);
        lastname = (EditText) findViewById(R.id.register_lastname_input);
        phone = (EditText) findViewById(R.id.register_phonenumber_input);

        customer = (RadioButton) findViewById(R.id.customer_radio_button);
        owner = (RadioButton) findViewById(R.id.owner_radio_button);

        button_register = (Button) findViewById(R.id.button_register_activity);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        button_register.setOnClickListener(button_listener);
    }

    private void register_button_clicked()
    {
        final String Email = email.getText().toString().trim();
        final String Password = password.getText().toString().trim();
        final String FirstName = firstname.getText().toString().trim();
        final String LastName = lastname.getText().toString().trim();
        final String Phone = phone.getText().toString().trim();

        if (Email.isEmpty()){
            Toast.makeText(this, "Email is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Password.isEmpty()){
            Toast.makeText(this, "Password is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (FirstName.isEmpty()){
            Toast.makeText(this, "First Name is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (LastName.isEmpty()){
            Toast.makeText(this, "Last Name is Empty", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Phone.isEmpty()){
            Toast.makeText(this, "Phone is Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        //check if successful
                        if (task.isSuccessful()) {
                            // Check role
                            if (customer.isChecked())
                            {
                                role = "Customer";
                            }
                            else
                            {
                                role = "Food Truck Owner";
                            }

                            // make a User object
                            User user = new User(Email, FirstName, LastName, Phone, role);

                            // add to Database
                            database.getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>()
                                    {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(RegisterActivity.this, "Added to database",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(RegisterActivity.this, "Couldn't add to database, try again",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            //User is successfully registered and logged in
                            //start Profile Activity here
                            Toast.makeText(RegisterActivity.this, "Registration successful",
                                    Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, ProfileActivity.class));
                            finish();
                        }

                        else
                            {
                            Toast.makeText(RegisterActivity.this, "Couldn't register, try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
