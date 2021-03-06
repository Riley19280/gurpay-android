package com.rileystech.gurpay.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.rileystech.gurpay.Util;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.models.Bill;
import com.rileystech.gurpay.models.User;
import com.rileystech.gurpay.network.*;

import com.rileystech.gurpay.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BillView extends AppCompatActivity {

    TableLayout table;
    TextView ownerLabel;
    MenuItem editMenuItem;
    Boolean editing = false;

    EditText nameField;
    EditText totalField;
    EditText dateAssignedField;
    EditText datePaidField;
    EditText dateDueField;
    TextView splitCostLabel;

    Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);

        bill = (Bill)getIntent().getSerializableExtra("bill");

        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(bill.name);
        }

        nameField = ((EditText)this.findViewById(R.id.nameField));
        totalField = ((EditText)this.findViewById(R.id.totalField));
        dateAssignedField = ((EditText)this.findViewById(R.id.dateAssignedField));
        datePaidField = ((EditText)this.findViewById(R.id.datePaidField));
        dateDueField = ((EditText)this.findViewById(R.id.dateDueField));

        nameField.setText(bill.name);
        totalField.setText(String.format("%.2f",bill.total));
        dateAssignedField.setText(Util.displayDate(bill.date_assigned));
        datePaidField.setText(Util.displayDate(bill.date_paid));
        dateDueField.setText(Util.displayDate(bill.date_due));

        splitCostLabel = ((TextView)this.findViewById(R.id.splitCostLabel));
        splitCostLabel.setText(String.format("Total per person: $%.2f",bill.split_cost));
        ownerLabel = this.findViewById(R.id.ownerLabel);
        table = this.findViewById(R.id.payersTableView);

        ServiceBase.user.getUser(this.getApplicationContext(), Integer.toString(bill.owner_id), new APICallResponse() {
            @Override
            public void success(Object obj) {
                User u = (User)obj;
                ownerLabel.setText("Owner: "+u.name);
            }
            @Override
            public void error(APIError err) {
                ownerLabel.setText("Owner: Unknown");
            }
        });

        for(HashMap.Entry<User,Boolean> u : bill.payers.entrySet()) {
            final TableRow row = (TableRow) LayoutInflater.from(BillView.this).inflate(R.layout.element_payer_table_row, null);
            ((TextView)row.findViewById(R.id.nameLabel)).setText(u.getKey().name);
            ((TextView)row.findViewById(R.id.statusLabel)).setText(u.getValue() ? "Paid" : "Unpaid");
            row.setTag(u.getKey());
            table.addView(row);
        }


    }



    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        final Context ctx = this.getApplicationContext();

        if(bill.is_archive)
            return false;

        ServiceBase.user.getUser(ctx, Util.getUUID(ctx), new APICallResponse() {
            @Override
            public void success(Object obj) {
                User user = (User)obj;

                if(user.id == bill.owner_id) {
                    if(bill.date_paid != null){
                        int count = 0;
                        for(HashMap.Entry<User,Boolean> u : bill.payers.entrySet()) {
                            if(!u.getValue()){
                                count++;
                            }
                        }
                        if (count == 0) {
                            inflater.inflate(R.menu.menu_bill_view_archive, menu);
                            editMenuItem = menu.getItem(0);
                        }else {
                            inflater.inflate(R.menu.menu_bill_view_editor, menu);
                            editMenuItem = menu.getItem(0);
                        }
                    }
                    else {
                        inflater.inflate(R.menu.menu_bill_view_editor, menu);
                        editMenuItem = menu.getItem(0);
                    }
                }
                else {
                    for(HashMap.Entry<User,Boolean> u : bill.payers.entrySet()) {
                        if(u.getKey().id == user.id){
                            inflater.inflate(R.menu.menu_bill_view_payer, menu);
                            return;
                        }
                    }
                }



            }

            @Override
            public void error(APIError err) {
                super.error(err);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            // action with ID action_refresh was selected
            case R.id.actionDeleteBill:
              actionDelete();
                break;
            case R.id.actionArchiveBill:
                actionArchive();
                break;
            // action with ID action_settings was selected
            case R.id.actionDuplicateBill:
                actionDuplicate();
                break;
            case R.id.actionNotifyUnpaid:
                actionNotify();
                break;
            case R.id.actionEditBill:
                actionEdit();
                break;
            default:
                break;
        }

        return true;
    }

    public void buttonRemovePayer(View view) {
        final Context ctx = this.getApplicationContext();
        final TableRow row = (TableRow)view.getParent();
        final User u = (User)row.getTag();

        ServiceBase.bill.DeletePayers(ctx, bill, Arrays.asList(u), new APICallResponse() {
            @Override
            public void success(Object obj) {
                //bill.payers.remove(u);

                    bill.payers.remove(u);
                    table.removeView(row);

            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Unable to remove payer.\n"+err.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    void actionDelete(){
        final Context ctx = this.getApplicationContext();
        ServiceBase.bill.DeleteBill(ctx, bill, new APICallResponse() {
            @Override
            public void success(Object obj) {
                finish();
                Toast.makeText(ctx,"Bill deleted.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error deleting bill.",Toast.LENGTH_LONG).show();
            }
        });
    }

    void actionDuplicate(){
        final Context ctx = this.getApplicationContext();
        List<Bill> bills = new ArrayList<>();
        bills.add(bill);

        ServiceBase.bill.DuplicateBills(ctx, bills, new APICallResponse() {
            @Override
            public void success(Object obj) {
                finish();
                Toast.makeText(ctx,"Bill duplicated.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error duplicating bill.",Toast.LENGTH_LONG).show();
            }
        });
    }

    void actionNotify(){
        final Context ctx = this.getApplicationContext();

        ServiceBase.bill.NotifyUnpaidPayers(ctx, bill, new APICallResponse() {
            @Override
            public void success(Object obj) {

                Toast.makeText(ctx,"Payers notified!",Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error notifying payers.",Toast.LENGTH_LONG).show();
            }
        });
    }

    private TableRow addTableRowButton;
    void actionEdit(){
        final Context ctx = this.getApplicationContext();
        final BillView billView = this;
        if(editing == false) {
            editing = true;
            editMenuItem.setTitle("Finish editing");
            nameField.setEnabled(true);
            totalField.setEnabled(true);
            dateAssignedField.setEnabled(true);
            datePaidField.setEnabled(true);
            dateDueField.setEnabled(true);

            addTableRowButton = new TableRow(this);
            addTableRowButton.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));

            Button b = new Button(this);
            b.setText("Add Payers");
            b.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1f));
            b.setBackgroundColor(getResources().getColor(R.color.darkOrange));
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<User> filter = new ArrayList<>();
                    for(HashMap.Entry<User,Boolean> b : bill.payers.entrySet())
                        filter.add(b.getKey());

                    Util.payersSelect(billView, filter, new APICallResponse() {
                        @Override
                        public void success(Object obj) {
                            final User u = (User)obj;

                            ServiceBase.bill.AddPayers(ctx, bill, Arrays.asList(u), new APICallResponse() {
                                @Override
                                public void success(Object obj) {
                                    bill.payers.put(u,false);
                                    final TableRow row = (TableRow) LayoutInflater.from(BillView.this).inflate(R.layout.element_payer_table_row, null);
                                    ((TextView)row.findViewById(R.id.nameLabel)).setText(u.name);
                                    ((TextView)row.findViewById(R.id.statusLabel)).setText("Unpaid");
                                    row.setTag(u);
                                    row.findViewById(R.id.removeButton).setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                                    table.addView(row,table.getChildCount()-1);
                                }

                                @Override
                                public void error(APIError err) {
                                    Toast.makeText(ctx,"Error adding payer.\n"+err.toString(),Toast.LENGTH_LONG).show();
                                }
                            });

                        }

                        @Override
                        public void error(APIError err) {
                            Toast.makeText(ctx,"Unable to get payers.\n"+err.toString(),Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });
            addTableRowButton.addView(b);

            for(int i = 0; i < table.getChildCount(); i++) {
                TableRow row = (TableRow)table.getChildAt(i);
                row.findViewById(R.id.removeButton).setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            }


            table.addView(addTableRowButton, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        }
        else {
            HashMap<Boolean,Date> hA = Util.parseDate(dateAssignedField.getText().toString());
            HashMap<Boolean,Date> hP = Util.parseDate(datePaidField.getText().toString());
            HashMap<Boolean,Date> hD = Util.parseDate(dateDueField.getText().toString());

            if(hA.containsKey(false) || hP.containsKey(false) || hD.containsKey(false)) {
                Toast.makeText(this.getApplicationContext(),"One or more dates is in an invalid format.",Toast.LENGTH_LONG).show();
                return;
            }

            Date dA = hA.get(true);
            Date dP = hP.get(true);
            Date dD = hD.get(true);

            bill.date_assigned = dA;
            bill.date_paid = dP;
            bill.date_due = dD;

            bill.name = nameField.getText().toString();
            try {
                Double t = Double.parseDouble(totalField.getText().toString());
                bill.total = t;
            }
            catch (Exception e) {
                Toast.makeText(this.getApplicationContext(),"Total must be a number.",Toast.LENGTH_LONG).show();
                return;
            }

            ServiceBase.bill.UpdateBill(ctx, bill, new APICallResponse() {
                @Override
                public void success(Object obj) {
                    editing = false;
                    editMenuItem.setTitle("Edit bill");
                    Bill b = (Bill)obj;
                    b.split_cost = b.total/(bill.payers.size()+1);
                    splitCostLabel.setText(String.format("Total per person: %.2f",b.split_cost));

                    nameField.setEnabled(false);
                    totalField.setEnabled(false);
                    dateAssignedField.setEnabled(false);
                    datePaidField.setEnabled(false);
                    dateDueField.setEnabled(false);

                    table.removeView(addTableRowButton);
                    for(int i = 0; i < table.getChildCount(); i++) {
                        TableRow row = (TableRow)table.getChildAt(i);
                        ((Button)row.findViewById(R.id.removeButton)).setLayoutParams(new TableRow.LayoutParams(0, 0));
                    }
                }

                @Override
                public void error(APIError err) {
                   Toast.makeText(ctx,"Error updating bill.\n"+err.toString(),Toast.LENGTH_LONG).show();
                }
            });

        }


    }

    void actionArchive(){
        final Context ctx = this.getApplicationContext();
        ServiceBase.bill.ArchiveBills(ctx, Arrays.asList(bill), new APICallResponse() {
            @Override
            public void success(Object obj) {

                Toast.makeText(ctx,"Bill Archived.",Toast.LENGTH_LONG).show();
            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Error archiving bill.",Toast.LENGTH_LONG).show();
            }
        });
    }

}
