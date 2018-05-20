package com.rileystech.gurpay.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Group;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.NetworkBase;
import com.rileystech.gurpay.network.ServiceBase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BillList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        TableLayout table = (TableLayout)BillList.this.findViewById(R.id.table);
        for(int i = 0; i <5; i++)
        {
            // Inflate your row "template" and fill out the fields.
            TableRow row = (TableRow) LayoutInflater.from(BillList.this).inflate(R.layout.element_bill_table_row, null);
            ((TextView)row.findViewById(R.id.titleLabel)).setText("Row " + (i+1));
            //((TextView)row.findViewById(R.id.attrib_value)).setText(b.VALUE);
            table.addView(row);
        }


        ServiceBase.group.GetGroupMembers(this.getApplicationContext(), new APICallResponse() {
            @Override
            public void success(Object obj) {

              //  Group g = (Group)obj;
             //   Log.d("TEST",g.name);

                List<User> members = (ArrayList<User>)obj;
                for( User u : members) {
                    Log.d("TEST",u.name);
                }

            }

            @Override
            public void error(APIError err) {
                Log.d("TEST","FINAL: "+ err.toString());
            }
        });


    }
}
