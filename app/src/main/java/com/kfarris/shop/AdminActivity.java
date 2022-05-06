package com.kfarris.shop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kfarris.shop.DB.GetDatabases;
import com.kfarris.shop.DB.ItemDAO;
import com.kfarris.shop.DB.UserDAO;
import com.kfarris.shop.databinding.ActivityAdminBinding;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    ActivityAdminBinding binding;

    private TextView mUserTextView;

    private Button mAddAnItemButton;
    private Button mModifyItemButton;
    private Button mViewExistingItemsButton;
    private Button mRemoveAnItemButton;
    private Button mViewExistingUsersButton;
    private Button mBackButton;
    private Button mXButton;

    private UserDAO mUserDAO;
    private ItemDAO mItemDAO;

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

        mAddAnItemButton = binding.adminPageAddAnItemButton;
        mModifyItemButton = binding.adminPageModifyAnItemButton;
        mViewExistingItemsButton = binding.adminPageViewExistingItemsButton;
        mRemoveAnItemButton = binding.adminPageRemoveAnItemButton;
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
         * Show add item dialog
         */
        mAddAnItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog();
            }
        });

        /**
         * Show modify item dialog
         */
        mModifyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyItemDialog();
            }
        });

        /**
         * View existing items dialog.
         */

        mViewExistingItemsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAllItemsDialog();
            }
        });

        /**
         * Remove items dialog.
         */

        mRemoveAnItemButton.setOnClickListener(new View.OnClickListener() {
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
     * Sets up the user and item table.
     */
    private void setupDatabase() {
        mUserDAO = GetDatabases.userDatabase(this);
        mItemDAO = GetDatabases.itemDatabase(this);
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
            sb.append("Items Owned : " + "\n");
            sb.append(user.getItemOwned().toString().replace("[", "").replace("]", "") + "\n");
            sb.append("- - - - - - - - - - - - - -" + "\n");

        }

        mAllUsersTextView.setText(sb.toString());


    }

    /**
     * Item add dialog.
     * An item can be added to the item table.
     */
    public void addItemDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.add_item_admin_dialog, null);
        EditText mItemName = (EditText) dialogView.findViewById(R.id.add_item_page_itemName_editText);
        EditText mItemPrice = (EditText) dialogView.findViewById(R.id.add_item_page_itemPrice_editText);
        EditText mItemLocation = (EditText) dialogView.findViewById(R.id.add_item_page_itemLocation_editText);
        EditText mItemQuantity = (EditText) dialogView.findViewById(R.id.add_item_page_itemQuantity_editText);
        EditText mItemDescription = (EditText) dialogView.findViewById(R.id.add_item_page_itemDescription_editText);
        Button mAddItemButton = (Button) dialogView.findViewById(R.id.add_item_page_additem_button);
        Button mCancelItemButton = (Button) dialogView.findViewById(R.id.add_item_page_cancelitem_button);
        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mItemName.getText().toString().length() > 0 &&
                        mItemPrice.getText().toString().length() > 0 &&
                        mItemLocation.getText().toString().length() > 0 &&
                        mItemQuantity.getText().toString().length() > 0 &&
                        mItemDescription.getText().toString().length() > 0) {

                    String name = mItemName.getText().toString();
                    Double price = Double.valueOf(mItemPrice.getText().toString());
                    String location = mItemLocation.getText().toString();
                    int quantity = Integer.valueOf(mItemQuantity.getText().toString());
                    String description = mItemDescription.getText().toString();

                    if (mItemDAO.getItem(name.toLowerCase()) == null) {

                        Item item = new Item(name, name.toLowerCase(), price, location, quantity, description);
                        mItemDAO.insert(item);

                        Toast.makeText(AdminActivity.this, "Item [ " + name + " ] added.",
                                Toast.LENGTH_LONG).show();

                        dialog.dismiss();

                    } else {
                        Toast.makeText(AdminActivity.this, "Item [ " + name + " ] already added.",
                                Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(AdminActivity.this, "Please fill out all fields.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        mCancelItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    /**
     * Modify item dialog.
     * An existing item can be modified.
     */
    public void modifyItemDialog() {

        System.out.println("==============================");

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.modify_item_admin_dialog, null);

        AutoCompleteTextView mModifyItemDropDownMenu = (AutoCompleteTextView) dialogView.findViewById(R.id.modify_item_page_item_dropDownMenu);
        ;

        Button mModifyItemButton = (Button) dialogView.findViewById(R.id.modify_item_page_modify_button);
        Button mCancelButton = (Button) dialogView.findViewById(R.id.modify_item_page_cancel_button);

        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        ArrayList<String> names = new ArrayList<>();

        for (Item item : mItemDAO.getItemInfo()) {
            names.add(item.getItemName());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.getAllItemNames(mItemDAO));
        mModifyItemDropDownMenu.setAdapter(adapter);

        mModifyItemDropDownMenu.setAdapter(adapter);

        /**
         * Selects an item to modify.
         */

        mModifyItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentItem = mModifyItemDropDownMenu.getText().toString();

                Item item = mItemDAO.getItem(currentItem.toLowerCase());

                if (item != null) {
                    changeItemFields(item);
                }else {
                    Toast.makeText(AdminActivity.this, "Item does not exist error.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        /**
         * Closes dialog.
         */
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void changeItemFields(Item item) {

        dialog.dismiss();
        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.add_item_admin_dialog, null);
        EditText mItemName = (EditText) dialogView.findViewById(R.id.add_item_page_itemName_editText);
        EditText mItemPrice = (EditText) dialogView.findViewById(R.id.add_item_page_itemPrice_editText);
        EditText mItemLocation = (EditText) dialogView.findViewById(R.id.add_item_page_itemLocation_editText);
        EditText mItemQuantity = (EditText) dialogView.findViewById(R.id.add_item_page_itemQuantity_editText);
        EditText mItemDescription = (EditText) dialogView.findViewById(R.id.add_item_page_itemDescription_editText);
        Button mAddItemButton = (Button) dialogView.findViewById(R.id.add_item_page_additem_button);
        Button mCancelItemButton = (Button) dialogView.findViewById(R.id.add_item_page_cancelitem_button);

        mItemName.setText(item.getItemName());
        mItemName.setKeyListener(null);
        mItemPrice.setText(String.valueOf(item.getPrice()));
        mItemLocation.setText(item.getLocation());
        mItemQuantity.setText(String.valueOf(item.getQuantity()));
        mItemDescription.setText(item.getDescription());
        mAddItemButton.setText(R.string.modify);


        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        mAddItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mItemName.getText().toString().length() > 0 &&
                        mItemPrice.getText().toString().length() > 0 &&
                        mItemLocation.getText().toString().length() > 0 &&
                        mItemQuantity.getText().toString().length() > 0 &&
                        mItemDescription.getText().toString().length() > 0) {

                    String name = mItemName.getText().toString();
                    Double price = Double.valueOf(mItemPrice.getText().toString());
                    String location = mItemLocation.getText().toString();
                    int quantity = Integer.valueOf(mItemQuantity.getText().toString());
                    String description = mItemDescription.getText().toString();

                    item.setItemName(name);
                    item.setPrice(price);
                    item.setLocation(location);
                    item.setQuantity(quantity);
                    item.setDescription(description);

                    mItemDAO.update(item);

                    Toast.makeText(AdminActivity.this, "Item [" + name + "] modified!", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                } else {
                    Toast.makeText(AdminActivity.this, "Please fill out all fields.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        mCancelItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * Item dialog.
     * Shows all items in the item table.
     */
    public void viewAllItemsDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.view_items_admin_dialog, null);
        TextView mAllItemsTextView = (TextView) dialogView.findViewById(R.id.all_items_page_allItems_textView);
        Button mDismissButton = (Button) dialogView.findViewById(R.id.all_items_page_dismiss_button);
        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        StringBuilder sb = new StringBuilder();

        List<Item> items = mItemDAO.getItemInfo();

        if (amountOfItems() > 0) {
            for (Item item : items) {
                sb.append(item.toString());
                sb.append("\n- - - - - - - - - - - - - - - - - - - - - - - -\n");
            }
        }else {
            sb.append("There are currently no items.");
        }
        mAllItemsTextView.setText(sb.toString());

        mAllItemsTextView.setMovementMethod(new ScrollingMovementMethod());


        mDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    /**
     * Returns the amount of unique items in a shop.
     * @return
     */
    public int amountOfItems() {
        return (mItemDAO.getItemInfo().size());
    }

    /**
     * Remove item dialog.
     * Item can be selected to be deleted from the item table.
     */
    public void removeItemDialog() {

        mDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = getLayoutInflater().inflate(R.layout.remove_item_admin_dialog, null);

        AutoCompleteTextView mRemoveItemDropDownMenu = (AutoCompleteTextView) dialogView.findViewById(R.id.remove_item_page_item_dropDownMenu);

        Button mRemoveItemButton = (Button) dialogView.findViewById(R.id.remove_item_page_remove_button);
        Button mRemoveCancelButton = (Button) dialogView.findViewById(R.id.remove_item_page_cancel_button);

        mDialogBuilder.setView(dialogView);
        dialog = mDialogBuilder.create();
        dialog.show();

        ArrayList<String> names = new ArrayList<>();

        for (Item item : mItemDAO.getItemInfo()) {
            names.add(item.getItemName());
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.getAllItemNames(mItemDAO));
        mRemoveItemDropDownMenu.setAdapter(adapter);

        mRemoveItemDropDownMenu.setAdapter(adapter);

        mRemoveItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String currentItem = mRemoveItemDropDownMenu.getText().toString();

                Item item = mItemDAO.getItem(currentItem.toLowerCase());

                if (item != null) {

                    mItemDAO.delete(item);

                    names.remove(item.getItemName());

                    adapter.notifyDataSetChanged();

                    dialog.dismiss();

                    Toast.makeText(AdminActivity.this, "Item [ " + currentItem + " ] removed.",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(AdminActivity.this, "Item does not exist error.",
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
}