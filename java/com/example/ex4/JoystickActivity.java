package com.example.ex4;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

// Activity of joystick
public class JoystickActivity extends AppCompatActivity {
    static private String ip = null;
    static private int port = 0;
    static private TcpClient mTcpClient = null;
    private String setAileron = "set /controls/flight/aileron ";
    private String setElevator = "set /controls/flight/elevator ";

    // onCreate : create the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent= getIntent();
        // if ip and port were not updated, update them
        if (ip == null && port == 0) {
            ip=intent.getStringExtra("ip");
            port = Integer.parseInt(intent.getStringExtra("port"));
        }
        // create joystick view
        JoystickView joystickView = new JoystickView(this);
        setContentView(joystickView);
        // connect server
        if (mTcpClient == null) {
            new ConnectTask(ip, port).execute("");
        }
    }

    // setAileronElevator : send aileron and elevator to server
    public void setAileronElevator(float aileron, float elevator) {
        //sends the message to the server
        if (mTcpClient != null) {
            mTcpClient.sendMessage(setAileron + aileron + "\r\n");
            mTcpClient.sendMessage(setElevator + elevator+ "\r\n");
        }
    }

    // OnDestroy : disconnect server when app is destroy
    public void OnDestroy(){
        super.onDestroy();
        mTcpClient.stopClient();
    }

    // ConnectTask : class for async connection task
    public class ConnectTask extends AsyncTask<String, String, TcpClient> {

        private String ip;
        private int port;

        // constructor
        public ConnectTask(String server_ip, int server_port) {
            ip = server_ip;
            port = server_port;
        }

        // doInBackground : connect server
        @Override
        protected TcpClient doInBackground(String... message) {

            // create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            }, ip, port);
            mTcpClient.run(); // connect
            return null;
        }
    }
}

