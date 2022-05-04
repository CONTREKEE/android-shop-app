package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.ProductDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    private TextView mUserTextView;

    private Button mAddAProductButton;
    private Button mViewExistingProductsButton;
    private Button mRemoveAProductButton;
    private Button mViewExistingUsersButton;
    private Button mBackButton;
    private Button mXButton;

    private UserDAO mUserDAO;
    private ProductDAO mProductDAO;

    private AlertDialog.Builder mDialogBuilder;
    private AlertDialog dialog;
    private TextView mAllUsersTextView;

    private String mUsername;

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

        mAddAProductButton = binding.adminPageAddAnItemButton;
        mViewExistingProductsButton = binding.adminPageViewExistingItemsButton;
        mRemoveAProductButton = binding.adminPageRemoveAnItemButton;
        mViewExistingUsersButton = binding.adminPageViewExistingUsersButton;
        mBackButton = binding.adminPageBackButton;

        mUsername = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        mUserTextView.setText("Welcome, " + mUsername + "!");

        setupDatabase();

        /**
         * Back button.
         * Goes back to the landing page.
         */
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LandingActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });

        /**
         * Show add product dialog
         */
        mAddAProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        /**
         * View existing items dialog.
         */

        mViewExistingProductsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAllItemsDialog();
            }
        });

        /**
         * Remove items dialog.
         */

        mRemoveAProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItemDialog();
            }
        });

        /**
         * Shows all existing users.
         * Pops up another dialog.
         */

        mViewExistingUsersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewExistingUsers();
            }
        });

    }

    /**
     * Sets up the user and product table.
     */
    private void setupDatabase() {
        mUserDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build().UserDAO();

        mProductDAO = Room.databaseBuilder(this, AppDatabase.class,
                AppDatabase.DATABASE_NAME)
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
                .ProductDAO();
    }

    /**
     * Shows all existing users username, password, and items purchases
     * in a dialog.
     */
    public void viewExistingUsers() {
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
            sb.append(user.getProductsOwned().toString().replace("[", "").replace("]", "") + "\n");
            sb.append("- - - - - - - - - - - - - -" + "\n");

        }

        mAllUsersTextView.setText(sb.toString());


    }

    /**
     * Item add dialog.
     * An item can be added to the product table.
     */
    public void addItemDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.add_product_admin_dialog, null);
        EditText mProductName = (EditText) dialogView.findViewById(R.id.add_product_page_productName_editText);
        EditText mProductPrice = (EditText) dialogView.findViewById(R.id.add_product_page_productPrice_editText);
        EditText mProductLocation = (EditText) dialogView.findViewById(R.id.add_product_page_productLocation_editText);
        EditText mProductQuantity = (EditText) dialogView.findViewById(R.id.add_product_page_productQuantity_editText);
        EditText mProductDescription = (EditText) dialogView.findViewById(R.id.add_product_page_productDescription_editText);
        Button mAddProductButton = (Button) dialogView.findViewById(R.id.add_product_page_addProduct_button);
        Button mCancelProductButton = (Button) dialogView.findViewById(R.id.add_product_page_cancelProduct_button);
        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        mAddProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mProductName.getText().toString().length() > 0 &&
                        mProductPrice.getText().toString().length() > 0 &&
                        mProductLocation.getText().toString().length() > 0 &&
                        mProductQuantity.getText().toString().length() > 0 &&
                        mProductDescription.getText().toString().length() > 0) {

                    String name = mProductName.getText().toString();
                    Double price = Double.valueOf(mProductPrice.getText().toString());
                    String location = mProductLocation.getText().toString();
                    int quantity = Integer.valueOf(mProductQuantity.getText().toString());
                    String description = mProductDescription.getText().toString();

                    if (mProductDAO.getProduct(name.toLowerCase()) == null) {

                        Product product = new Product(name, name.toLowerCase(), price, location, quantity, description);
                        mProductDAO.insert(product);

                        Toast.makeText(AdminActivity.this, "Product [ " + name + " ] added.",
                                Toast.LENGTH_LONG).show();

                        dialog.dismiss();

                    } else {
                        Toast.makeText(AdminActivity.this, "Product [ " + name + " ] already added.",
                                Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(AdminActivity.this, "Please fill out all fields.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        mCancelProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    /**
     * Item dialog.
     * Shows all items in the product table.
     */
    public void viewAllItemsDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.view_items_admin_dialog, null);
        TextView mAllProductsTextView = (TextView) dialogView.findViewById(R.id.all_products_page_allProducts_textView);
        Button mDismissButton = (Button) dialogView.findViewById(R.id.all_products_page_dismiss_button);
        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        StringBuilder sb = new StringBuilder();

        List<Product> products = mProductDAO.getProductsInfo();

        for (Product product : products) {
            sb.append(product.toString());
            sb.append("\n- - - - - - - - - - - - - - - - - - - - - - - -\n");
        }
        mAllProductsTextView.setText(sb.toString());

        mAllProductsTextView.setMovementMethod(new ScrollingMovementMethod());


        mDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * Remove item dialog.
     * Item can be selected to be deleted from the product table.
     */
    public void removeItemDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.remove_product_admin_dialog, null);

        AutoCompleteTextView mRemoveProductDropDownMenu = (AutoCompleteTextView) dialogView.findViewById(R.id.remove_product_page_product_dropDownMenu);
        ;

        Button mRemoveProductButton = (Button) dialogView.findViewById(R.id.remove_product_page_remove_button);
        Button mRemoveCancelButton = (Button) dialogView.findViewById(R.id.remove_product_page_cancel_button);

        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        ArrayList<String> names = new ArrayList<>();

        for (Product product : mProductDAO.getProductsInfo()) {
            names.add(product.getProductName());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getProductNames());
        mRemoveProductDropDownMenu.setAdapter(adapter);

        mRemoveProductDropDownMenu.setAdapter(adapter);

        mRemoveProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentProduct = mRemoveProductDropDownMenu.getText().toString();

                Product product = mProductDAO.getProduct(currentProduct.toLowerCase());

                if (product != null) {

                    mProductDAO.delete(product);

                    names.remove(product.getProductName());

                    adapter.notifyDataSetChanged();

                    dialog.dismiss();

                    Toast.makeText(AdminActivity.this, "Product [ " + currentProduct + " ] removed.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(AdminActivity.this, "An error occurred. Please try again later.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        mRemoveCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * Returns a list of all item names in the product table.
     *
     * @return
     */
    public ArrayList<String> getProductNames() {

        ArrayList<String> names = new ArrayList<>();

        for (Product product : mProductDAO.getProductsInfo()) {
            names.add(product.getProductName());
        }
        return names;
    }
}