package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityCreateAccountBinding;

import java.util.ArrayList;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding;

    private EditText mCreateAccountUsernameTextEdit;
    private EditText mCreateAccountPasswordTextEdit;

    private Button mCreateAccountButton;
    private Button mBackButton;

    private UserDAO mUserDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mCreateAccountUsernameTextEdit = binding.createAccountPageUsernameEditText;
        mCreateAccountPasswordTextEdit = binding.createAccountPagePasswordEditText;

        mCreateAccountButton = binding.createAccountPageCreateButton;
        mBackButton = binding.createAccountPageBackButton;

        setupDatabase();

        /**
         * Back button.
         * Goes back to the sign in page.
         */
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });

        /**
         * Create account button.
         */

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCreateAccountUsernameTextEdit.getText().toString().length() > 0
                        && mCreateAccountPasswordTextEdit.getText().toString().length() > 0) {

                    User user = mUserDAO.getUserInfo(mCreateAccountUsernameTextEdit.getText().toString().toLowerCase());

                    if (user == null) {
                        user = new User(mCreateAccountUsernameTextEdit.getText().toString().toLowerCase(),
                                mCreateAccountPasswordTextEdit.getText().toString(), 0, new ArrayList<>());
                        mUserDAO.insert(user);
                        Toast.makeText(CreateAccountActivity.this, "Account created!",
                                Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(CreateAccountActivity.this, "Account already exists!",
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(CreateAccountActivity.this, "Fill out all fields.",
                            Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void setupDatabase() {
        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .build().UserDAO();
    }
}