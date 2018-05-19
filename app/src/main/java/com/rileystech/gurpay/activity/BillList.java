package com.rileystech.gurpay.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rileystech.gurpay.R;

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
    }
}
