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

    String username;

    UserDAO mUserDAO;

    SharedPreferences mSharedPreferences;

    User user;

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

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().UserDAO();

        mSharedPreferences = getSharedPreferences(MainActivity.mLoginFile, Context.MODE_PRIVATE);

        username = getIntent().getStringExtra(LOGGED_IN_USERNAME);

        if (username != null) {

            user = mUserDAO.getUserInfo(username);

            if (user.getIsAdmin() != 1) {
                mAdminButton.setVisibility(View.INVISIBLE);
            }

            String welcomeText = "Welcome, " +
                    user.getUsername() +
                    "!";
            mLandingPageWelcomeUserTextView.setText(welcomeText);

        }

        /**
         * Search button.
         */
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = SearchActivity.intentFactory(getApplicationContext(), username);
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
                Intent intent = OrderHistoryActivity.intentFactory(getApplicationContext(), username);
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
                Intent intent = CancelOrderActivity.intentFactory(getApplicationContext(), username);
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

                if (user.getIsAdmin() == 1) {

                    Intent intent = AdminActivity.intentFactory(getApplicationContext(), username);
                    startActivity(intent);

                }

            }
        });



    }

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

}