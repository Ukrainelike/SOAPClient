package com.example.ukrainelike.soapclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView error_msg;
    private EditText login;
    private EditText password;
    private Button loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        error_msg=(TextView) findViewById(R.id.error_message);
        login=(EditText) findViewById(R.id.LoginDate);
        password=(EditText) findViewById(R.id.PasswordData);
        loginButton=(Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LoginButton:
                break;
            default:
                break;
        }
    }
}
