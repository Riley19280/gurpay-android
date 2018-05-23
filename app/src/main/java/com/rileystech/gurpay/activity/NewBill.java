package com.rileystech.gurpay.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Bill;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class NewBill extends AppCompatActivity {

    Bill bill;
    TableLayout table;

    EditText nameField;
    EditText totalField;
    EditText dateAssignedField;
    EditText datePaidField;
    EditText dateDueField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bill);

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("");

        table = this.findViewById(R.id.payersTableView);

        nameField = ((EditText)this.findViewById(R.id.nameField));
        totalField = ((EditText)this.findViewById(R.id.totalField));
        dateAssignedField = ((EditText)this.findViewById(R.id.dateAssignedField));
        datePaidField = ((EditText)this.findViewById(R.id.datePaidField));
        dateDueField = ((EditText)this.findViewById(R.id.dateDueField));

        bill = new Bill();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_new_bill, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }

        return true;
    }

    public void addPayerButton(View view) {
        final Context ctx = this.getApplicationContext();
        final NewBill newBillView = this;

        List<User> filter = new ArrayList<>();
        for(HashMap.Entry<User,Boolean> b : bill.payers.entrySet())
            filter.add(b.getKey());

        Util.payersSelect(newBillView, filter, new APICallResponse() {
            @Override
            public void success(Object obj) {
                final User u = (User)obj;

                bill.payers.put(u,false);
                final TableRow row = (TableRow) LayoutInflater.from(NewBill.this).inflate(R.layout.element_payer_table_row, null);
                ((TextView)row.findViewById(R.id.nameLabel)).setText(u.name);
                ((TextView)row.findViewById(R.id.statusLabel)).setText("Unpaid");
                row.setTag(u);
                row.findViewById(R.id.removeButton).setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                table.addView(row,table.getChildCount()-1);

            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Unable to get payers.\n"+err.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void buttonRemovePayer(View view) {
        final TableRow row = (TableRow)view.getParent();
        final User u = (User)row.getTag();

        bill.payers.remove(u);
        table.removeView(row);
    }

    public void createButton(View view){
        final Context ctx = this.getApplicationContext();
        bill.name = ((EditText)this.findViewById(R.id.nameField)).getText().toString();
        try {
            bill.total = Double.parseDouble(((EditText)this.findViewById(R.id.totalField)).getText().toString());

            HashMap<Boolean,Date> hA = Util.parseDate(dateAssignedField.getText().toString());
            HashMap<Boolean,Date> hD = Util.parseDate(dateDueField.getText().toString());

            if(hA.containsKey(false) || hD.containsKey(false)) {
                Toast.makeText(this.getApplicationContext(),"One or more dates is in an invalid format.",Toast.LENGTH_LONG).show();
                return;
            }

            Date dA = hA.get(true);
            Date dD = hD.get(true);

            bill.date_assigned = dA;
            bill.date_due = dD;

            ServiceBase.bill.CreateBill(ctx, bill, new APICallResponse() {
                @Override
                public void success(Object obj) {
                    finish();
                    Toast.makeText(ctx, "Bill created!", Toast.LENGTH_LONG).show();

                }
                @Override
                public void error(APIError err) {
                    Toast.makeText(ctx, "Error creating bill.\n"+err.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (Exception e) {
            Toast.makeText(ctx,"Input is not in the correct format.",Toast.LENGTH_LONG).show();
        }
    }

}
