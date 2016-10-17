package admob.learn.com.poc_adb_connection;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Scanner;

import android.os.Handler;

/**
 * Created by becker on 10.03.15.
 */
public class Connection extends Activity implements View.OnClickListener {

    public static final String TAG="Connection";
    public static final int TIMEOUT=10;

    private Intent i = null;
    private TextView tv = null;
    private String connectionStatus = null;
    private Handler mHandler = null;
    private ServerSocket server = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);

        //Set up click listeners for the buttons
        View connectButton = findViewById(R.id.connect_button);
        connectButton.setOnClickListener(this);

        this.i = new Intent(this, MainActivity.class);
        this.mHandler = new Handler();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                //initialize server socket in a new separate thread
                new Thread(initializeConnection).start();
                String msg = "Attempting to connect ...";
                Toast.makeText(Connection.this, msg, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private Runnable initializeConnection = new Thread() {

        public void run() {

            Socket client=null;
            // initialize server socket
            try{
                server = new ServerSocket(38300);
//                server.setSoTimeout(TIMEOUT * 2000);

                Log.d(TAG, "Waiting for connection.");
                //attempt to accept a connection
                client = server.accept();
                Log.d(TAG, "Accepted");

                Globals.socketIn = new Scanner(client.getInputStream());
                Globals.socketOut = new PrintWriter(client.getOutputStream(), true);
            } catch (SocketTimeoutException e) {
                // print out TIMEOUT
                connectionStatus="Connection has timed out! Please try again";
                mHandler.post(showConnectionStatus);
                Log.d(TAG, "Connection timeout");
            } catch (IOException e) {
                Log.e(TAG, "" + e);
            } finally {

                //close the server socket
                try {
                    if (server!=null)
                        Log.d(TAG, "Closing server");
                    server.close();
                } catch (IOException ec) {
                    Log.e(TAG, "Cannot close server socket" + ec);
                }
            }

            if (client!=null) {

                Globals.connected = true;
                // print out success
                connectionStatus="Connection was successful!";
                mHandler.post(showConnectionStatus);

                startActivity(i);
            }
        }
    };

    /**
     * Pops up a “toast” to indicate the connection status
     */
    private Runnable showConnectionStatus = new Runnable() {
        public void run() {
            Toast.makeText(Connection.this, connectionStatus, Toast.LENGTH_SHORT).show();
        }
    };
}