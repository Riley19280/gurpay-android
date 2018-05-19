package com.rileystech.gurpay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class BillView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bill_view, menu);//TODO: if this for different menu (owner vs payer)
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.actionDeleteBill:
                Toast.makeText(this, "Delete Bill", Toast.LENGTH_SHORT)
                        .show();
                break;
            // action with ID action_settings was selected
            case R.id.actionDuplicateBill:
                Toast.makeText(this, "Duplicate Bill", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.actionNotifyUnpaid:
                Toast.makeText(this, "Notify Unpaid", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.actionEditBill:
                Toast.makeText(this, "Edit Bill", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }

        return true;
    }
}
