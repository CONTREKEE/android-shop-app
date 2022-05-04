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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ProductDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivitySearchBinding;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    private AutoCompleteTextView mSearchForItemEditText;

    private Button mSearchButton;
    private Button mBuyButton;
    private Button mBackButton;

    private TextView mItemNameTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mLocationTextView;
    private TextView mProductDetailsLabelTextView;
    private TextView mProductDetailsTextTextView;

    private UserDAO mUserDAO;
    private ProductDAO mProductDAO;

    private Product mSelectedProduct;

    private String username;

    private User user;

    private ArrayAdapter mAdapter;

    public static Intent intentFactory(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, SearchActivity.class);
        intent.putExtra(LandingActivity.LOGGED_IN_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mSearchForItemEditText = binding.searchPageItemSearchTextView;

        mSearchButton = binding.searchPageSearchButton;
        mBuyButton = binding.searchPageBuyButton;
        mBackButton = binding.searchPageBackButton;

        mItemNameTextView = binding.searchPageItemNameTextView;
        mPriceTextView = binding.searchPagePriceTextView;
        mQuantityTextView = binding.searchPageStockTextView;
        mLocationTextView = binding.searchPageShippingTextView;
        mProductDetailsLabelTextView = binding.searchPageProductDetailsLabelTextView;
        mProductDetailsTextTextView = binding.searchPageProductDetailsTextTextView;

        mBuyButton.setVisibility(View.INVISIBLE);

        setupDatabase();

        username = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        if (username != null) {
            user = mUserDAO.getUserInfo(username);
        }


        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getProductNames());
        mSearchForItemEditText.setAdapter(mAdapter);

        /**
         * Back button.
         */
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LandingActivity.intentFactory(getApplicationContext(), username);
                startActivity(intent);
            }
        });

        /**
         * Search button.
         * Fills out information about the product.
         */
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSearchForItemEditText.getText().toString().length() > 0) {

                    String prodcutName = mSearchForItemEditText.getText().toString();
                    Product product = mProductDAO.getProduct(prodcutName.toLowerCase());

                    if (product != null) {

                        mBuyButton.setVisibility(View.VISIBLE);
                        mItemNameTextView.setText(product.getProductName());
                        mPriceTextView.setText("Price : $" + product.getPrice());
                        mQuantityTextView.setText("In Stock : " + product.getQuantity());
                        mLocationTextView.setText("Shipping From : " + product.getLocation());

                        mProductDetailsLabelTextView.setText("Item Details : ");
                        mProductDetailsTextTextView.setText(product.getDescription());

                        mSelectedProduct = product;

                    } else {
                        Toast.makeText(SearchActivity.this, "Item not found.",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        /**
         * Buy button.
         * Tries to buy current selected product.
         */
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canPurchase()) {
                    confirmPurchase();
                }
            }
        });
    }

    /**
     * Checks if a user is able to purchase the selected item and returns true.
     * Otherwise returns false.
     *
     * @return
     */
    private boolean canPurchase() {
        if (mSelectedProduct != null) {

            if (mSelectedProduct.getQuantity() > 0) {

                if (!user.getProductsOwned().contains(mSelectedProduct.getProductName())) {
                    return true;
                } else {
                    Toast.makeText(SearchActivity.this, "You already own this item.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }

            } else {
                Toast.makeText(SearchActivity.this, "This item is out of stock.",
                        Toast.LENGTH_LONG).show();
                return false;
            }

        } else {
            Toast.makeText(SearchActivity.this, "There is no item selected.",
                    Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Sets up the user and product table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
        mProductDAO = GetDatabases.productDatabase(this);
    }

    /**
     * Asks user if they want to confirm their purchase.
     * If confirmed the purchase will proceed.
     */
    private void confirmPurchase() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

        alertBuilder.setMessage(R.string.confirm_purchase);

        alertBuilder.setPositiveButton(getString(R.string.confirm),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSelectedProduct.setQuantity(mSelectedProduct.getQuantity() - 1);
                        user.getProductsOwned().add(mSelectedProduct.getProductName());

                        mUserDAO.update(user);
                        mProductDAO.update(mSelectedProduct);

                        mQuantityTextView.setText("In Stock : " + mSelectedProduct.getQuantity());

                        Toast.makeText(SearchActivity.this, mSelectedProduct.getProductName() + " purchased!", Toast.LENGTH_SHORT).show();

                    }
                });
        alertBuilder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        alertBuilder.create().show();
    }

    /**
     * Returns a list of all of the product names in the shop.
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