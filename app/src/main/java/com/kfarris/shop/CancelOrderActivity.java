package com.kfarris.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.kfarris.shop.DB.AppDatabase;
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

        setupDatabases();
        setupItemList();

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = LandingActivity.intentFactory(getApplicationContext(), mUsername);
                startActivity(intent);
            }
        });


    }

    private void itemsOrderedList() {
        ArrayList<String> items = new ArrayList<>();

        for (String product : mUserDAO.getUserInfo(mUsername).getProductsOwned()) {
            items.add(product);
        }
        mItemsOrdered = items;
    }

    private void setupItemList() {
        itemsOrderedList();

        mOrdersSpinner.setAdapter(new ArrayAdapter<>(this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                mItemsOrdered
        ));
    }

    private void setupDatabases() {
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