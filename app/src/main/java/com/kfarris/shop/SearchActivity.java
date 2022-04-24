package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.ProductDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivitySearchBinding;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    ActivitySearchBinding binding;

    AutoCompleteTextView mSearchForItemEditText;

    Button mSearchButton;
    Button mBuyButton;
    Button mBackButton;

    TextView mItemNameTextView;
    TextView mPriceTextView;
    TextView mQuantityTextView;
    TextView mLocationTextView;
    TextView mProductDetailsLabelTextView;
    TextView mProductDetailsTextTextView;

    UserDAO mUserDAO;
    ProductDAO mProductDAO;

    Product mSelectedProduct;

    String username;

    User user;

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

        username = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        if (username != null) {
            user = mUserDAO.getUserInfo(username);
        }


        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getProductNames());
        mSearchForItemEditText.setAdapter(adapter);

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

                    }else {
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

                if (mSelectedProduct != null &&
                mSelectedProduct.getQuantity() > 0) {

                    if (!user.getProductsOwned().contains(mSelectedProduct.getProductName())) {

                        mSelectedProduct.setQuantity(mSelectedProduct.getQuantity()-1);
                        user.getProductsOwned().add(mSelectedProduct.getProductName());

                        mUserDAO.update(user);
                        mProductDAO.update(mSelectedProduct);

                        mQuantityTextView.setText("In Stock : " + mSelectedProduct.getQuantity());

                    }else {
                        Toast.makeText(SearchActivity.this, "You already own this item!",
                                Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(SearchActivity.this, "There is no item selected!",
                            Toast.LENGTH_LONG).show();
                }

            }
        });




    }

    public ArrayList<String> getProductNames() {

        ArrayList<String> names = new ArrayList<>();

        for (Product product : mProductDAO.getProductsInfo()) {
            names.add(product.getProductName());
        }
        return names;

    }

}