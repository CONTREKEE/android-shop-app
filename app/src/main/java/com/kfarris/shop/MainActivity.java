package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    Button mLoginButton;
    Button mCreateAccountButton;

    EditText mUsernameEditText;
    EditText mPasswordEditText;

    UserDAO mUserDAO;

    SharedPreferences mSharedPreferences;

    public static final String mLoginFile = "login_status";
    public static final String mUsernamePreference = "username";
    public static final String mPasswordPreference = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mLoginButton = binding.loginPageLoginButton;
        mCreateAccountButton = binding.loginPageCreateAccountButton;

        mUsernameEditText = binding.loginPageUsernameEditText;
        mPasswordEditText = binding.loginPagePasswordEditText;

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build().UserDAO();

        mSharedPreferences = getSharedPreferences(mLoginFile, Context.MODE_PRIVATE);

        if (mSharedPreferences.contains(mUsernamePreference) &&
                mSharedPreferences.contains(mPasswordPreference)) {

            String username = mSharedPreferences.getString(mUsernamePreference, null);

            if (username != null) {

                User user = mUserDAO.getUserInfo(username);

                if (user.getPassword().equals(mSharedPreferences.getString(mPasswordPreference, null))) {

                    Intent intent = LandingActivity.intentFactory(getApplicationContext(),
                            username);
                    startActivity(intent);
                }
            }
        }


        /**
         * Login button.
         * Checks if username is in database.
         * Then checks if username and password match.
         */
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUsernameEditText.getText().toString().length() > 0
                        && mPasswordEditText.getText().toString().length() > 0) {

                    User user = mUserDAO.getUserInfo(mUsernameEditText.getText().toString().toLowerCase());

                    if (user != null) {

                        if (mUsernameEditText.getText().toString().equalsIgnoreCase(user.getUsername()) &&
                                mPasswordEditText.getText().toString().equals(user.getPassword())) {

                            SharedPreferences.Editor editor = mSharedPreferences.edit();
                            editor.putString(mUsernamePreference, user.getUsername());
                            editor.putString(mPasswordPreference, user.getPassword());
                            editor.commit();

                            Toast.makeText(MainActivity.this, "Login Success!",
                                    Toast.LENGTH_LONG).show();

                            Intent intent = LandingActivity.intentFactory(getApplicationContext(), user.getUsername());
                            startActivity(intent);

                        }

                    }else {
                        Toast.makeText(MainActivity.this, "Username or password is invalid.",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, CreateAccountActivity.class);
                startActivity(intent);

            }
        });

        createDefaultUsers();

    }

    public void createDefaultUsers() {

        if (mUserDAO.getUserInfo("testuser1") == null) {
            User testUser = new User("testuser1", "testuser1", 0);
            mUserDAO.insert(testUser);
        }

        if (mUserDAO.getUserInfo("admin2") == null) {
            User adminUser = new User("admin2", "admin2", 1);
            mUserDAO.insert(adminUser);
        }

    }
}