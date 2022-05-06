package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ItemDAO;
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
    ItemDAO mItemDAO;

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

        List<String> list = mUser.getItemOwned();

        if (list.size() > 0) {

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < list.size(); i++) {

                Item item = mItemDAO.getItem(list.get(i).toLowerCase());

                sb.append(item.getItemName() + "\n");
                sb.append("You paid : $" + item.getPrice() + "\n");
                sb.append("Shipping from : " + item.getLocation() + "\n");
                sb.append("Item info : \n");
                sb.append(item.getDescription() + "\n");
                sb.append("- - - - - - - - - - - - - - - -\n");


            }

            if (sb.toString().length() > 0) {
                mOrderHistoryTextView.setText(sb.toString());
            }
        }
    }

    /**
     * Checks if the user that has items that do not exist in the item table.
     */
    private void checkForNullItems() {
        List<String> list = mUser.getItemOwned();
        int size = list.size();
        for (int i = 0; i < list.size(); i++) {
            Item item = mItemDAO.getItem(list.get(i).toLowerCase());

            if (item == null) {
                list.remove(i);
            }
        }

        if (list.size() != size) {
            mUser.setItemOwned(list);
            mUserDAO.update(mUser);
        }
    }

    /**
     * Sets up the user and item table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
        mItemDAO = GetDatabases.itemDatabase(this);
    }
}