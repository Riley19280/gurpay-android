package com.rileystech.gurpay.activity;

import android.content.Context;
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
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Bill;
import com.rileystech.gurpay.models.Group;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

import java.util.List;

public class EditGroup extends AppCompatActivity {

    TextView nameField;
    Button changeButton;
    TextView groupCodeLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Manage Group");
        }


        nameField = this.findViewById(R.id.nameField);
        changeButton = this.findViewById(R.id.changeButton);
        groupCodeLabel = this.findViewById(R.id.groupCodeLabel);

        final SharedPreferences prefs = this.getSharedPreferences("com.rileystech.gurpay",MODE_PRIVATE);
        nameField.setText(prefs.getString("group_name",""));

        groupCodeLabel.setText("Group Code: " + prefs.getString("group_code",""));

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(prefs.getString("group_name","").equals(s.toString())){
                    changeButton.setEnabled(false);
                    changeButton.setBackgroundResource(R.color.disabledGray);
                }
                else {
                    changeButton.setEnabled(true);
                    changeButton.setBackgroundColor(getResources().getColor(R.color.darkOrange));
                }
            }


        });

    }

    public void changeButton(View view) {
        final SharedPreferences prefs = this.getSharedPreferences("com.rileystech.gurpay",MODE_PRIVATE);
        final Context ctx = this.getApplicationContext();
        Group g = new Group(nameField.getText().toString(),prefs.getString("group_code",null));
        ServiceBase.group.EditGoup(ctx, g, new APICallResponse() {
            @Override
            public void success(Object obj) {
                prefs.edit().putString("group_name", nameField.getText().toString()).apply();
                //reupdate the button
                if(prefs.getString("group_name","").equals(nameField.getText().toString().toString())){
                    changeButton.setEnabled(false);
                    changeButton.setBackgroundResource(R.color.disabledGray);
                }
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error changing group name.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void currentToArchive(View view) {
        final Context ctx = this.getApplicationContext();

        ServiceBase.bill.GetBills(ctx, false, new APICallResponse() {
            @Override
            public void success(Object obj) {
                List<Bill> bills = (List<Bill>)obj;

                ServiceBase.bill.ArchiveBills(ctx, bills, new APICallResponse() {
                    @Override
                    public void success(Object obj) {
                        Toast.makeText(ctx,"Eligible bills archived!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void error(APIError err) {
                        Toast.makeText(ctx,"Error archiving bills.",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error archiving bills.",Toast.LENGTH_LONG).show();
            }
        });


    }

    public void duplicateCurrent(View view) {
        final Context ctx = this.getApplicationContext();

        ServiceBase.bill.GetBills(ctx, false, new APICallResponse() {
            @Override
            public void success(Object obj) {
                List<Bill> bills = (List<Bill>)obj;

                ServiceBase.bill.DuplicateBills(ctx, bills, new APICallResponse() {
                    @Override
                    public void success(Object obj) {
                        Toast.makeText(ctx,"Current bills duplicated!",Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void error(APIError err) {
                        Toast.makeText(ctx,"Error duplicating bills.",Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error duplicating bills.",Toast.LENGTH_LONG).show();
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
