package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityAdminBinding;

import java.util.List;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    TextView mUserTextView;

    Button mAddAnItemButton;
    Button mViewExistingItemsButton;
    Button mRemoveAnItemButton;
    Button mViewExistingUsersButton;
    Button mBackButton;

    UserDAO mUserDAO;

    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog dialog;
    TextView mAllUsersTextView;

    public static Intent intentFactory(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, AdminActivity.class);
        intent.putExtra(LandingActivity.LOGGED_IN_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mUserTextView = binding.adminPageWelcomeTextView;

        mAddAnItemButton = binding.adminPageAddAnItemButton;
        mViewExistingItemsButton = binding.adminPageViewExistingItemsButton;
        mRemoveAnItemButton = binding.adminPageRemoveAnItemButton;
        mViewExistingUsersButton = binding.adminPageViewExistingUsersButton;
        mBackButton = binding.adminPageBackButton;

        String username = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        mUserTextView.setText("Welcome, " + username + "!");

        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build().UserDAO();

        /**
         * Back button.
         * Goes back to the landing page.
         */

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = LandingActivity.intentFactory(getApplicationContext(), username);
                startActivity(intent);

            }
        });

        /**
         * Shows all existing users.
         * Pops up another dialog.
         */

        mViewExistingUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUsersPopUp();
            }
        });

    }

    public void createNewUsersPopUp() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View userPopUpView = getLayoutInflater().inflate(R.layout.popup, null);
        mAllUsersTextView = (TextView) userPopUpView.findViewById(R.id.user_popup_allUsersScroll_textView);
        mDialogBuilder.setView(userPopUpView);
        dialog = mDialogBuilder.create();
        dialog.show();

        StringBuilder sb = new StringBuilder();

        List<User> users = mUserDAO.getUsersInfo();

        for (User user : users) {

            sb.append("Username : ");
            sb.append(user.getUsername() + "\n");
            sb.append("Password : ");
            sb.append(user.getPassword() + "\n");
            sb.append("Products Owned : " + "\n");
            sb.append("- - - - - - - - - - - - - -" + "\n");

        }

        mAllUsersTextView.setText(sb.toString());


    }

}