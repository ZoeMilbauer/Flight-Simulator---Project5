package com.example.ex4;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

// LoginActivity : class for login activity
public class LoginActivity extends AppCompatActivity {

    // create activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // connect : called when the connect button is pressed.
    // opens the joystick view
    public void connect(View view){
        // get ip and port from user
        EditText ipText = (EditText) findViewById(R.id.editIP);
        EditText portText = (EditText) findViewById(R.id.editPort);
        String ip= ipText.getText().toString();
        String port = portText.getText().toString();
        // if ip and port are not empty, start joystick activity
        if (ip.compareTo("")!= 0 && (port.compareTo("")!=0)) {
            Intent intent = new Intent(this, JoystickActivity.class);
            intent.putExtra("ip",ip);
            intent.putExtra("port",port);
            startActivity(intent);
        }
    }
}
