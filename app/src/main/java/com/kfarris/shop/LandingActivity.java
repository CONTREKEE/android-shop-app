package com.kfarris.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

                if (MainActivity.isAdmin(mUser)) {

                    Intent intent = AdminActivity.intentFactory(getApplicationContext(), mUsername);
                    startActivity(intent);

                }

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.sign_out_userMenu) {
            signOutUser();
        }else if (item.getItemId() == R.id.delete_account_userMenu) {
            deleteAccount();
            forceSignOutUser();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user_menu, menu);
        return true;
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

    private void forceSignOutUser() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.commit();

        Intent intent = new Intent(LandingActivity.this, MainActivity.class);
        startActivity(intent);
    }
    
    private void deleteAccount() {
        if (mUser != null) {
            mUserDAO.delete(mUser);
            Toast.makeText(this, "Account deleted!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "An error occurred while deleting account.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up logged in user for the landing page.
     */
    private void setupUser() {
        mSharedPreferences = getSharedPreferences(MainActivity.LOGIN_INFO, Context.MODE_PRIVATE);

        mUsername = getIntent().getStringExtra(LOGGED_IN_USERNAME);

        if (mUsername != null) {

            mUser = mUserDAO.getUserInfo(mUsername);

            if (!MainActivity.isAdmin(mUser)) {
                mAdminButton.setVisibility(View.INVISIBLE);
            }

            String welcomeText = "Welcome, " +
                    mUser.getUsername() +
                    "!";
            mLandingPageWelcomeUserTextView.setText(welcomeText);

        }
    }

}