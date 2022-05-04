package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kfarris.shop.DB.AppDatabase;
import com.kfarris.shop.DB.ProductDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityOrderHistoryBinding;

import java.util.List;

public class OrderHistoryActivity extends AppCompatActivity {

    ActivityOrderHistoryBinding binding;

    Button mBackButton;

    TextView mOrderHistoryTextView;

    String mUsername;
    User mUser;

    UserDAO mUserDAO;
    ProductDAO mProductDAO;

    public static Intent intentFactory(Context packageContext, String username) {
        Intent intent = new Intent(packageContext, OrderHistoryActivity.class);
        intent.putExtra(LandingActivity.LOGGED_IN_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        binding = ActivityOrderHistoryBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mBackButton = binding.orderHistoryPageBackButton;

        mOrderHistoryTextView = binding.orderHistoryPageItemsListTextView;

        mUsername = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        setupDatabase();

        if (mUsername != null) {
            mUser = mUserDAO.getUserInfo(mUsername);
        }

        updateOrdersDisplay();

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
     * Updates the display for the items the user ordered.
     */
    private void updateOrdersDisplay() {
        checkForNullItems();

        List<String> list = mUser.getProductsOwned();

        if (list.size() > 0) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < list.size(); i++) {

                Product product = mProductDAO.getProduct(list.get(i).toLowerCase());

                sb.append(product.getProductName() + "\n");
                sb.append("You paid : $" + product.getPrice() + "\n");
                sb.append("Shipping from : " + product.getLocation() + "\n");
                sb.append("Product info : \n");
                sb.append(product.getDescription() + "\n");
                sb.append("- - - - - - - - - - - - - - - -\n");


            }

            if (sb.toString().length() > 0) {
                mOrderHistoryTextView.setText(sb.toString());
            }
        }
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
}