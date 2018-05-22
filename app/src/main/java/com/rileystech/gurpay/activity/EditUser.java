package com.rileystech.gurpay.activity;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

public class EditUser extends AppCompatActivity {

    TextView userNameEdit;

    Button changeButton;

    String oldUserName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Edit User");

        userNameEdit = this.findViewById(R.id.editUserNameField);
        changeButton = this.findViewById(R.id.changeButton);

        userNameEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(oldUserName.equals(s.toString())){
                    changeButton.setEnabled(false);
                    changeButton.setBackgroundResource(R.color.disabledGray);
                }
                else {
                    changeButton.setEnabled(true);
                    changeButton.setBackgroundColor(getResources().getColor(R.color.darkOrange));
                }
            }
        });
        loadData();
    }

    void loadData() {
        final Context ctx = this.getApplicationContext();
       ServiceBase.user.getUser(ctx, Util.getUUID(ctx), new APICallResponse() {
           @Override
           public void success(Object obj) {
               User u = (User)obj;
               oldUserName = u.name;
               userNameEdit.setText(u.name);
           }

           @Override
           public void error(APIError err) {
               Toast.makeText(ctx,"Error getting name.",Toast.LENGTH_LONG).show();
           }
       });
    }

    public void changeButton(View view) {
        final Context ctx = this.getApplicationContext();
        ServiceBase.user.UpdateUser(ctx, userNameEdit.getText().toString(), new APICallResponse() {
            @Override
            public void success(Object obj) {
                User u = (User)obj;
                oldUserName = u.name;
                userNameEdit.setText(u.name);
                if(oldUserName.equals(userNameEdit.getText().toString())){
                    changeButton.setEnabled(false);
                    changeButton.setBackgroundResource(R.color.disabledGray);
                }
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error updating name.",Toast.LENGTH_LONG).show();
            }
        });
    }


    public void leaveButton(View view) {
        final Context ctx = this.getApplicationContext();
        ServiceBase.group.LeaveGroup(ctx, new APICallResponse() {
            @Override
            public void success(Object obj) {
                SharedPreferences prefs = ctx.getSharedPreferences("com.rileystech.gurpay",MODE_PRIVATE);
                prefs.edit().clear().apply();
                Intent intent = new Intent(ctx, SelectGroup.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
                startActivity(intent);
            }

            @Override
            public void error(APIError err) {
               Toast.makeText(ctx,"Error leaving group.",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
