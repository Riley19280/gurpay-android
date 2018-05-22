package com.rileystech.gurpay.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Bill;
import com.rileystech.gurpay.models.Group;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.NetworkBase;
import com.rileystech.gurpay.network.ServiceBase;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BillList extends AppCompatActivity {

    TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_list);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Group Bills");

        table = (TableLayout)BillList.this.findViewById(R.id.table);

        loadData();
    }

    void loadData(){
        final Context ctx = this.getApplicationContext();
        final BillList list = this;
        ServiceBase.bill.GetBills(ctx, getIntent().getBooleanExtra("archived",true), new APICallResponse() {
            @Override
            public void success(Object obj) {
                List<Bill> bills = (List<Bill>)obj;

                 for(final Bill b : bills) {
                     // Inflate your row "template" and fill out the fields.
                     final TableRow row = (TableRow) LayoutInflater.from(BillList.this).inflate(R.layout.element_bill_table_row, null);
                     ((TextView)row.findViewById(R.id.titleLabel)).setText(b.name);

                     ((TextView)row.findViewById(R.id.dateAssignedDateLabel)).setText(Util.displayDate(b.date_assigned));
                     ((TextView)row.findViewById(R.id.dateDueDateLabel)).setText(Util.displayDate(b.date_due));
                     ((TextView)row.findViewById(R.id.paidToDateLabel)).setText(String.format("$%.2f/%.2f",b.subtotal,b.total));

                     if(b.date_paid != null){
                         int payerCount = 0;
                         for(HashMap.Entry<User,Boolean> e : b.payers.entrySet()) {
                             if(!e.getValue())
                                 payerCount++;
                         }
                         if(payerCount > 0){
                             ((ImageView)row.findViewById(R.id.imageView)).setColorFilter(Color.parseColor("#ffc600"), PorterDuff.Mode.SRC_ATOP);
                         }
                         else {
                             ((ImageView)row.findViewById(R.id.imageView)).setColorFilter(Color.parseColor("#25d500"), PorterDuff.Mode.SRC_ATOP);
                         }
                     }

                     row.setClickable(true);
                     row.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent intent = new Intent(ctx,BillView.class);
                             intent.putExtra("bill", b);
                             startActivity(intent);
                         }
                     });

                     ServiceBase.user.getUser(ctx, Integer.toString(b.owner_id), new APICallResponse() {
                         @Override
                         public void success(Object obj) {
                             User u = (User)obj;
                             ((TextView)row.findViewById(R.id.ownerLabel)).setText("Owner: " + u.name);
                         }

                         @Override
                         public void error(APIError err) {
                             super.error(err);
                         }
                     });


                     table.addView(row);
                 }

            }

            @Override
            public void error(APIError err) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(this.getIntent().getBooleanExtra("archived",true))
            return false;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bill_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.actionAddBill:
                Intent intent = new Intent(this, NewBill.class);
                startActivity(intent);
               break;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
