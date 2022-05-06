package com.kfarris.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ItemDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private Button mLoginButton;
    private Button mCreateAccountButton;

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;

    private UserDAO mUserDAO;

    private SharedPreferences mSharedPreferences;

    private User mUser;

    public static final String LOGIN_INFO = "login_status";
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

        setupDatabase();

        checkIfLoggedIn();

        /**
         * Login button.
         */
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        /**
         * Sends user to the create account page.
         */
        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = CreateAccountActivity.intentFactory(getApplicationContext());
                startActivity(intent);

            }
        });

        createDefaultUsers();

    }

    /**
     * Checks if a user was logged in when the
     * app was open previously.
     */
    private void checkIfLoggedIn() {
        mSharedPreferences = getSharedPreferences(LOGIN_INFO, Context.MODE_PRIVATE);

        if (mSharedPreferences.contains(mUsernamePreference) &&
                mSharedPreferences.contains(mPasswordPreference)) {

            String username = mSharedPreferences.getString(mUsernamePreference, null);

            if (username != null) {

                User user = mUserDAO.getUserInfo(username);

                if (user != null) {

                    if (user.getPassword().equals(mSharedPreferences.getString(mPasswordPreference, null))) {

                        Intent intent = LandingActivity.intentFactory(getApplicationContext(),
                                username);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    /**
     * Tries to log in user with entered information
     * in the username and password fields.
     */
    private void loginUser() {
        if (mUsernameEditText.getText().toString().length() > 0
                && mPasswordEditText.getText().toString().length() > 0) {

            mUser = mUserDAO.getUserInfo(mUsernameEditText.getText().toString().toLowerCase());

            if (mUser != null) {

                if (mUsernameEditText.getText().toString().equalsIgnoreCase(mUser.getUsername()) &&
                        mPasswordEditText.getText().toString().equals(mUser.getPassword())) {

                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(mUsernamePreference, mUser.getUsername());
                    editor.putString(mPasswordPreference, mUser.getPassword());
                    editor.commit();

                    Toast.makeText(MainActivity.this, "Login Success!",
                            Toast.LENGTH_LONG).show();

                    Intent intent = LandingActivity.intentFactory(getApplicationContext(), mUser.getUsername());
                    startActivity(intent);

                }

            } else {
                Toast.makeText(MainActivity.this, "Username or password is invalid.",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Sets up the user table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
    }

    /**
     * Creates two users [testuser1, admin2] if they
     * do not exist in the user table.
     */
    public void createDefaultUsers() {

        if (mUserDAO.getUsersInfo().size() < 1) {

            if (mUserDAO.getUserInfo("testuser1") == null) {
                User testUser = new User("testuser1", "testuser1", 0, new ArrayList<>());
                mUserDAO.insert(testUser);
            }

            if (mUserDAO.getUserInfo("admin2") == null) {
                User adminUser = new User("admin2", "admin2", 1, new ArrayList<>());
                mUserDAO.insert(adminUser);
            }
        }

    }

    /**
     * Returns true if a user is an Admin.
     * @param user
     * @return
     */
    public static boolean isAdmin(User user) {
        return (user.getIsAdmin() == 1);
    }

    /**
     * Returns a list of all item names in the item table.
     *
     * @return
     */
    public static ArrayList<String> getAllItemNames(ItemDAO itemDAO) {

        ArrayList<String> names = new ArrayList<>();

        for (Item item : itemDAO.getItemInfo()) {
            names.add(item.getItemName());
        }
        return names;
    }

}