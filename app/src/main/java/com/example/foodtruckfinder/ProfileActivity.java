package com.example.foodtruckfinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser user = mAuth.getCurrentUser();
    private DatabaseReference users_ref = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

    private TextView Email;
    private TextView Uid;
    private TextView Firstname;
    private TextView Lastname;
    private TextView Phone;

    private String roleuser;


    private Button usersettings;
    private Button logout;
    private Button map;

    private View.OnClickListener button_listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.button_user_settings:
                    startActivity(new Intent(ProfileActivity.this,
                            UserSettingsActivity.class));
                    finish();
                    break;
                case R.id.button_logout:
                    mAuth.signOut();
                    startActivity(new Intent(ProfileActivity.this,
                            MainActivity.class));
                    finish();
                    break;
                case R.id.button_map:
                    if (roleuser.equals("Food Truck Owner")) {
                        startActivity(new Intent(ProfileActivity.this,
                                OwnerMapsActivity.class));
                    }
                    else {
                        startActivity(new Intent(ProfileActivity.this,
                                CustomerMapsActivity.class));
                    }
                    finish();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Email = (TextView)findViewById(R.id.user_email);
        Uid = (TextView)findViewById(R.id.user_ID);
        Firstname = (TextView)findViewById(R.id.user_firstname);
        Lastname = (TextView)findViewById(R.id.user_lastname);
        Phone = (TextView)findViewById(R.id.user_phone);


        logout = (Button)findViewById(R.id.button_logout);
        map = (Button)findViewById(R.id.button_map);
        usersettings = (Button)findViewById(R.id.button_user_settings);

        logout.setOnClickListener(button_listener);
        map.setOnClickListener(button_listener);
        usersettings.setOnClickListener(button_listener);


        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){
            String email = user.getEmail();
            String uid = user.getUid();
            Email.setText(email);
            Uid.setText(uid);

            users_ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    String firstname = user.first_name;
                    String lastname = user.last_name;
                    String phone = user.phone;
                    roleuser = user.role;
                    Firstname.setText(firstname);
                    Lastname.setText(lastname);
                    Phone.setText(phone);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
