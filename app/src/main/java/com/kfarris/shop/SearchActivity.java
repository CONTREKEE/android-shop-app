package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ItemDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivitySearchBinding;

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
    private TextView mItemDetailsLabelTextView;
    private TextView mItemDetailsTextTextView;

    private UserDAO mUserDAO;
    private ItemDAO mItemDAO;

    private Item mSelectedItem;

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
        mItemDetailsLabelTextView = binding.searchPageItemDetailsLabelTextView;
        mItemDetailsTextTextView = binding.searchPageItemDetailsTextTextView;

        mBuyButton.setVisibility(View.INVISIBLE);

        setupDatabase();

        username = getIntent().getStringExtra(LandingActivity.LOGGED_IN_USERNAME);

        if (username != null) {
            user = mUserDAO.getUserInfo(username);
        }


        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.getAllItemNames(mItemDAO));
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
         * Fills out information about the item.
         */
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mSearchForItemEditText.getText().toString().length() > 0) {

                    String prodcutName = mSearchForItemEditText.getText().toString();
                    Item item = mItemDAO.getItem(prodcutName.toLowerCase());

                    if (item != null) {

                        mBuyButton.setVisibility(View.VISIBLE);
                        mItemNameTextView.setText(item.getItemName());
                        mPriceTextView.setText("Price : $" + item.getPrice());
                        mQuantityTextView.setText("In Stock : " + item.getQuantity());
                        mLocationTextView.setText("Shipping From : " + item.getLocation());

                        mItemDetailsLabelTextView.setText("Item Details : ");
                        mItemDetailsTextTextView.setText(item.getDescription());

                        mSelectedItem = item;

                    } else {
                        Toast.makeText(SearchActivity.this, "Item not found.",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        /**
         * Buy button.
         * Tries to buy current selected item.
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
        if (mSelectedItem != null) {

            if (mSelectedItem.getQuantity() > 0) {

                if (!user.getItemOwned().contains(mSelectedItem.getItemName())) {
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
     * Sets up the user and item table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
        mItemDAO = GetDatabases.itemDatabase(this);
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
                        mSelectedItem.setQuantity(mSelectedItem.getQuantity() - 1);
                        user.getItemOwned().add(mSelectedItem.getItemName());

                        mUserDAO.update(user);
                        mItemDAO.update(mSelectedItem);

                        mQuantityTextView.setText("In Stock : " + mSelectedItem.getQuantity());

                        Toast.makeText(SearchActivity.this, mSelectedItem.getItemName() + " purchased!", Toast.LENGTH_SHORT).show();

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

}