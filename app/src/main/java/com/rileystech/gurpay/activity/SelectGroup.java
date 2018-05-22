package com.rileystech.gurpay.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rileystech.gurpay.R;
import com.rileystech.gurpay.models.APIError;
import com.rileystech.gurpay.network.APICallResponse;
import com.rileystech.gurpay.network.ServiceBase;

public class SelectGroup extends AppCompatActivity {

    enum State {
        JOIN,
        CREATE
    }
    State state = State.JOIN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_select_group);

    }


    public void actionButton(View view) {
        String nameField = ((EditText)this.findViewById(R.id.nameTextField)).getText().toString();
        String groupField = ((EditText)this.findViewById(R.id.groupTextField)).getText().toString();

        final Context ctx = this.getApplicationContext();
        final SelectGroup this1 = this;

        if( state == State.JOIN) {
            ServiceBase.group.JoinGroup(ctx, groupField, null, nameField, new APICallResponse() {
                @Override
                public void success(Object obj) {
                    Intent intent = new Intent(this1, Dashboard.class);
                    startActivity(intent);
                }

                @Override
                public void error(APIError err) {
                    Toast.makeText(ctx,err.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (state == State.CREATE) {
            ServiceBase.group.JoinGroup(this.getApplicationContext(), null, groupField, nameField, new APICallResponse() {
                @Override
                public void success(Object obj) {
                    Intent intent = new Intent(this1, Dashboard.class);
                    startActivity(intent);
                }

                @Override
                public void error(APIError err) {
                   Toast.makeText(ctx,err.toString(),Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public void swapButton(View view) {
        if( state == State.JOIN){
            state = State.CREATE;
            ((TextView)this.findViewById(R.id.containerTitle)).setText("Create a Group!");
            ((TextView)this.findViewById(R.id.groupTextLabel)).setText("Group Name");
            ((EditText)this.findViewById(R.id.groupTextField)).setHint("Joe's Group");
            ((EditText)this.findViewById(R.id.groupTextField)).setInputType(EditorInfo.TYPE_CLASS_TEXT);
            ((Button)this.findViewById(R.id.actionButton)).setText("Create!");
            ((Button)this.findViewById(R.id.swapButton)).setText("Join a Group!");

        } else if (state == State.CREATE){
            state = State.JOIN;
            ((TextView)this.findViewById(R.id.containerTitle)).setText("Join a Group!");
            ((TextView)this.findViewById(R.id.groupTextLabel)).setText("Group Code");
            ((EditText)this.findViewById(R.id.groupTextField)).setHint("159632");
            ((EditText)this.findViewById(R.id.groupTextField)).setInputType(EditorInfo.TYPE_CLASS_NUMBER);
            ((Button)this.findViewById(R.id.actionButton)).setText("Join!");
            ((Button)this.findViewById(R.id.swapButton)).setText("Create a Group!");
        }
    }
}
