package com.rileystech.gurpay.activity;

import android.app.ActionBar;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

import java.text.SimpleDateFormat;

public class Dashboard extends AppCompatActivity {
    TextView unpaidCount;
    TextView nextDueLabel;
    TextView recievedToDateLabel;
    TextView paidToDateLabel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);


        unpaidCount = ((TextView)this.findViewById(R.id.unpaidBillsCount));
        nextDueLabel = ((TextView)this.findViewById(R.id.nextDueLabel));
        recievedToDateLabel = ((TextView)this.findViewById(R.id.recievedToDateLabel));
        paidToDateLabel = ((TextView)this.findViewById(R.id.paidToDateLabel));

    }

    @Override
    protected void onResume(){
        super.onResume();
        initialize();
    }

    public void viewBills(View view) {
        Intent intent = new Intent(this, BillList.class);
        intent.putExtra("archived",false);
        startActivity(intent);
    }

    public void viewArchive(View view) {
        Intent intent = new Intent(this, BillList.class);
        intent.putExtra("archived",true);
        startActivity(intent);
    }

    void initialize(){
        SharedPreferences prefs = this.getSharedPreferences("com.rileystech.gurpay",MODE_PRIVATE);
        getSupportActionBar().setTitle(prefs.getString("group_name",""));

        final Context ctx = this.getApplicationContext();

        ServiceBase.user.GetDashboard(ctx, new APICallResponse() {
            @Override
            public void success(Object obj) {
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, MMM d");
                com.rileystech.gurpay.models.Dashboard d = (com.rileystech.gurpay.models.Dashboard)obj;
                unpaidCount.setText(Integer.toString(d.myUnpaidBillCount));
                if(d.nextDueDate != null)
                    nextDueLabel.setText(String.format("Next bill due on %s", formatter.format(d.nextDueDate)));
                else
                    nextDueLabel.setText("");
                recievedToDateLabel.setText(String.format("You have recieved $%.2f/%.2f from %d/%d people.", d.myBillsPayerPaidToDate,d.myBillsPayerPaidTotal,d.myBillsPayersPaid,d.myBillsPayerCount));
                paidToDateLabel.setText(String.format("You have paid $%.2f/%.2f to others for $%d/%d bills.", d.payTotalToDate,d.payTotal,d.payTotalCountToDate,d.payTotalCount));

            }

            @Override
            public void error(APIError err) {
                Toast.makeText(ctx,"Unable to get dashboard at this time.\n"+err.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //menu controls
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.actionEditUser:
                Intent intent = new Intent(this, EditUser.class);
                startActivity(intent);
                break;
            // action with ID action_settings was selected
            case R.id.actionEditGroup:
                Intent intent2 = new Intent(this, EditGroup.class);
                startActivity(intent2);
                break;
            default:
                break;
        }

        return true;
    }

}
