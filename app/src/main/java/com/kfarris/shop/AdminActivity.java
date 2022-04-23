package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    Button mXButton;

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
         * Show add product dialog
         */
        mAddAnItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductDialog();
            }
        });

        /**
         * Shows all existing users.
         * Pops up another dialog.
         */

        mViewExistingUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewUsersDialog();
            }
        });

    }

    public void createNewUsersDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View userDialogView = getLayoutInflater().inflate(R.layout.view_users_admin_dialog, null);
        mAllUsersTextView = (TextView) userDialogView.findViewById(R.id.user_popup_allUsersScroll_textView);
        mXButton = (Button) userDialogView.findViewById(R.id.popup_x_button);
        mDialogBuilder.setView(userDialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        mXButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        mAllUsersTextView.setMovementMethod(new ScrollingMovementMethod());

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

    public void addProductDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View userDialogView = getLayoutInflater().inflate(R.layout.add_product_admin_dialog, null);
        EditText mProductName = (EditText) userDialogView.findViewById(R.id.add_product_page_productName_editText);
        EditText mProductPrice = (EditText) userDialogView.findViewById(R.id.add_product_page_productPrice_editText);
        EditText mProductLocation = (EditText) userDialogView.findViewById(R.id.add_product_page_productLocation_editText);
        EditText mProductQuantity = (EditText) userDialogView.findViewById(R.id.add_product_page_productQuantity_editText);
        EditText mProductDescription = (EditText) userDialogView.findViewById(R.id.add_product_page_productDescription_editText);
        Button mAddProductButton = (Button) userDialogView.findViewById(R.id.add_product_page_addProduct_button);
        Button mCancelProductButton = (Button) userDialogView.findViewById(R.id.add_product_page_cancelProduct_button);
        mDialogBuilder.setView(userDialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mProductName.getText().toString().length() > 1 &&
                        mProductPrice.getText().toString().length() > 1 &&
                        mProductLocation.getText().toString().length() > 1 &&
                        mProductQuantity.getText().toString().length() > 1 &&
                        mProductDescription.getText().toString().length() > 1) {

                    String name = mProductName.getText().toString();
                    Double price = Double.valueOf(mProductPrice.getText().toString());
                    String location = mProductLocation.getText().toString();
                    int quantity = Integer.valueOf(mProductQuantity.getText().toString());
                    String description = mProductDescription.getText().toString();


                }else {
                    Toast.makeText(AdminActivity.this, "Please fill out all fields.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });


    }

}