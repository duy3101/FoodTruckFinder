package com.example.foodtruckfinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserSettingsActivity extends AppCompatActivity {


    private DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseUser user;


    private EditText firstname;
    private EditText lastname;
    private EditText phone;

    private Button back;
    private Button confirm;

    private View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_back:
                    startActivity(new Intent(UserSettingsActivity.this, ProfileActivity.class));
                    finish();
                    break;
                case R.id.button_confirm:
                    confirm_button_clicked();
                    break;
                default:
                    break;
            }

        }
    };

    private void confirm_button_clicked()
    {
        final String FirstName = firstname.getText().toString().trim();
        final String LastName = lastname.getText().toString().trim();
        final String Phone = phone.getText().toString().trim();

        if (FirstName.isEmpty() || LastName.isEmpty() || Phone.isEmpty())
        {
            Toast.makeText(this, "Please enter new information", Toast.LENGTH_SHORT).show();
        }
        else
        {

            users_ref.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User userinfo = dataSnapshot.getValue(User.class);
                    userinfo.setInfo(FirstName, LastName, Phone);
                    users_ref.child(user.getUid()).setValue(userinfo);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        firstname = findViewById(R.id.new_first_name);
        lastname = findViewById(R.id.new_last_name);
        phone = findViewById(R.id.new_phone);


        back = (Button)findViewById(R.id.button_back);
        confirm = (Button)findViewById(R.id.button_confirm);

        back.setOnClickListener(button_listener);
        confirm.setOnClickListener(button_listener);

        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
