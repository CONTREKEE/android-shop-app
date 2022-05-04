package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ProductDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityCancelOrderBinding;

import java.util.ArrayList;
import java.util.List;

public class CancelOrderActivity extends AppCompatActivity {

    ActivityCancelOrderBinding binding;

    private Spinner mOrdersSpinner;

    private Button mBackButton;
    private Button mCancelOrderButton;

    private List<String> mItemsOrdered;

    private UserDAO mUserDAO;
    private ProductDAO mProductDAO;

    private String mUsername;

    private User mUser;

    private ArrayAdapter<String> mAdapter;


    public static Intent intentFactory(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, CancelOrderActivity.class);
        intent.putExtra(LandingActivity.LOGGED_IN_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_order);

        binding = ActivityCancelOrderBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mOrdersSpinner = binding.cancelOrderPageSpinner;

        mBackButton = binding.cancelOrderPageBackButton;
        mCancelOrderButton = binding.cancelOrderPageCancelOrderButton;

        mUsername = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        setupDatabase();

        if (mUsername != null) {
            mUser = mUserDAO.getUserInfo(mUsername);
        }

        setupItemList();

        /**
         * Cancel order button.
         */
        mCancelOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelItemOrder();
            }
        });

        /**
         * Sends user back to the landing page.
         */
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LandingActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });


    }


    /**
     * Cancels the current selected item.
     * Removes the item from the user table.
     * Adds 1 more to the quantity of the item in the product table.
     */
    private void cancelItemOrder() {
        String productName = mOrdersSpinner.getSelectedItem().toString();
        Product product = mProductDAO.getProduct(productName.toLowerCase());
        List<String> items = mUser.getProductsOwned();
        if (product != null) {
            if (items.contains(productName)) {
                items.remove(productName);
                mUser.setProductsOwned(items);
                mUserDAO.update(mUser);

                System.out.println(mProductDAO.getProductsInfo());
                product.setQuantity(product.getQuantity() + 1);
                mProductDAO.update(product);

                Toast.makeText(CancelOrderActivity.this, "Order with item " + productName + " cancelled!", Toast.LENGTH_SHORT).show();
                mAdapter.remove(productName);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Updates the items the user ordered.
     */
    private void itemsOrderedList() {
        ArrayList<String> items = new ArrayList<>();
        checkForNullItems();

        for (String product : mUserDAO.getUserInfo(mUsername).getProductsOwned()) {
            items.add(product);
        }
        mItemsOrdered = items;
    }

    /**
     * Sets up the spinner for the ordered items.
     */
    private void setupItemList() {
        itemsOrderedList();

        mAdapter = new ArrayAdapter<>(this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, mItemsOrdered);
        mAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        mOrdersSpinner.setAdapter(mAdapter);
    }

    /**
     * Sets up the user and product table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
        mProductDAO = GetDatabases.productDatabase(this);
    }

    /**
     * Checks if the user that has items that do not exist in the product table.
     */
    private void checkForNullItems() {
        List<String> list = mUser.getProductsOwned();
        int size = list.size();
        for (int i = 0; i < list.size(); i++) {
            Product product = mProductDAO.getProduct(list.get(i).toLowerCase());

            if (product == null) {
                list.remove(i);
            }
        }

        if (list.size() != size) {
            mUser.setProductsOwned(list);
            mUserDAO.update(mUser);
        }
    }

}