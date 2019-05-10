/*********************************************************************

    Client for sending chat messages to the server..

    Copyright (c) 2012 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chatclient;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import edu.stevens.cs522.base.DatagramSendReceive;

/*
 * @author dduggan
 * 
 */
public class ChatClient extends Activity implements OnClickListener {

	final static private String TAG = ChatClient.class.getCanonicalName();

	/*
	 * Socket used for sending
	 */

//  private DatagramSocket clientSocket;
    private DatagramSendReceive clientSocket;

	/*
	 * Widgets for dest address, message text, send button.
	 */
	private EditText destinationHost;

	private EditText chatName;

	private EditText messageText;
	
	private Button sendButton;


	/*
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_client);

		
		/**
		 * Let's be clear, this is a HACK to allow you to do network communication on the chat_client thread.
		 * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
		 * this right in a future assignment (using a Service managing background threads).
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);

		// TODO initialize the UI.
		destinationHost = (EditText) findViewById(R.id.destination_host);
		chatName = (EditText) findViewById(R.id.chat_name);
		messageText = (EditText) findViewById(R.id.message_text);
		sendButton = (Button) findViewById(R.id.send_button);
		sendButton.setOnClickListener(this);
		// End todo

		try {

			int port = getResources().getInteger(R.integer.app_port);
            clientSocket = new DatagramSendReceive(port);
            // clientSocket = new DatagramSocket(port);

		} catch (IOException e) {
		    IllegalStateException ex = new IllegalStateException("Cannot open socket");
		    ex.initCause(e);
		    throw ex;
		}

	}

	/*
	 * Callback for the SEND button.
	 */
	public void onClick(View v) {
		try {
			/*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */
			InetAddress destAddr;
			
			int destPort = getResources().getInteger(R.integer.app_port);

			byte[] sendData;  // Combine sender and message text; default encoding is UTF-8
			
			// TODO get data from UI (no-op if chat name is blank)

			String message = messageText.getText().toString().trim();
			message = chatName.getText().toString().trim()+":"+message;
			sendData = message.getBytes();
			destAddr = InetAddress.getByName(destinationHost.getText().toString());
			// End todo

			Log.d(TAG, String.format("Sending data from address %s:%d", clientSocket.getInetAddress(), clientSocket.getPort()));

			DatagramPacket sendPacket = new DatagramPacket(sendData,
					sendData.length, destAddr, destPort);

			clientSocket.send(sendPacket);

			Log.d(TAG, "Sent packet: " + message);

			
		} catch (UnknownHostException e) {
			throw new IllegalStateException("Unknown host exception: " + e.getMessage());
		} catch (IOException e) {
            throw new IllegalStateException("IO exception: " + e.getMessage());
		}

		messageText.setText("");
	}

    @Override
    public void onDestroy() {
	    super.onDestroy();
	    if (clientSocket != null) {
            clientSocket.close();
        }
    }

}