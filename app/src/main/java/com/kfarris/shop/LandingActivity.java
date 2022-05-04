package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityLandingBinding;

public class LandingActivity extends AppCompatActivity {

    public static final String LOGGED_IN_USERNAME = "com.fkeegan.online_store_username";

    ActivityLandingBinding binding;

    TextView mLandingPageWelcomeUserTextView;

    Button mSearchButton;
    Button mOrderHistoryButton;
    Button mCancelOrderButton;
    Button mAdminButton;
    Button mSignOutButton;

    String mUsername;

    UserDAO mUserDAO;

    SharedPreferences mSharedPreferences;

    User mUser;

    public static Intent intentFactory(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, LandingActivity.class);
        intent.putExtra(LOGGED_IN_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        binding = ActivityLandingBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mLandingPageWelcomeUserTextView = binding.landingPageWelcomeUserTextView;
        mSearchButton = binding.landingPageSearchButton;
        mOrderHistoryButton = binding.landingPageOrderHistoryButton;
        mCancelOrderButton = binding.landingPageCancelOrderButton;
        mAdminButton = binding.landingPageAdminButton;
        mSignOutButton = binding.landingPageSignOutButton;

        setupDatabase();
        setupUser();


        /**
         * Search button.
         */
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = SearchActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });

        /**
         * Order history button.
         * Shows order history of logged in user.
         */
        mOrderHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = OrderHistoryActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });

        /**
         * Sign out button.
         * Clears sharedPreferences of the username login info.
         */
        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOutUser();
            }
        });

        /**
         * Cancel order button.
         * Opens page to cancel orders.
         */
        mCancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = CancelOrderActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });

        /**
         * Admin button.
         * Opens admin page.
         */

        mAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mUser.getIsAdmin() == 1) {

                    Intent intent = AdminActivity.intentFactory(getApplicationContext(), mUsername);
                    startActivity(intent);

                }

            }
        });
    }

    /**
     * Sets up the user table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
    }

    /**
     * Signs out user and removes the user information from the SharedPreferences.
     * Asks user to confirm if they want to sign out.
     */
    private void signOutUser() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage(R.string.sign_out_question);

        alertBuilder.setPositiveButton(getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.clear();
                        editor.commit();

                        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        alertBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        alertBuilder.create().show();
    }

    /**
     * Sets up logged in user for the landing page.
     */
    private void setupUser() {
        mSharedPreferences = getSharedPreferences(MainActivity.LOGIN_INFO, Context.MODE_PRIVATE);

        mUsername = getIntent().getStringExtra(LOGGED_IN_USERNAME);

        if (mUsername != null) {

            mUser = mUserDAO.getUserInfo(mUsername);

            if (mUser.getIsAdmin() != 1) {
                mAdminButton.setVisibility(View.INVISIBLE);
            }

            String welcomeText = "Welcome, " +
                    mUser.getUsername() +
                    "!";
            mLandingPageWelcomeUserTextView.setText(welcomeText);

        }
    }

}