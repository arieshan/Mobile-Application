/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ViewPeerActivity.PEER_KEY;

public class ChatServer extends Activity implements OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSendReceive serverSocket;
//  private DatagramSocket serverSocket;

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

    /*
     * UI for displayed received messages
     */
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private Button next;
    private  Cursor cursor;

    static final private int LOADER_ID = 1;
    static final private int PEER_LOADER_ID = 2;

	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            /*
             * Get port information from the resources.
             */
            int port = getResources().getInteger(R.integer.app_port);

            // serverSocket = new DatagramSocket(port);

            serverSocket = new DatagramSendReceive(port);

        } catch (Exception e) {
            throw new IllegalStateException("Cannot open socket", e);
        }

        setContentView(R.layout.messages);

        // TODO use SimpleCursorAdapter (with flags=0) to display the messages received.
        cursor = this.getContentResolver().query(MessageContract.CONTENT_URI, null, null, null, null);
        messageList = (ListView)findViewById(R.id.message_list);
        messagesAdapter = new SimpleCursorAdapter(this, R.layout.message, cursor, new String[]{"message_text"}, new int[]{R.id.message}, 0);
        messageList.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();


        // TODO bind the button for "next" to this activity as listener
        next = (Button)findViewById(R.id.next);
        next.setOnClickListener(this);

        // TODO use loader manager to initiate a query of the database
        getLoaderManager().initLoader(LOADER_ID, null, this);
        getLoaderManager().initLoader(PEER_LOADER_ID, null, this);


        final Intent intent = new Intent(this, ViewPeerActivity.class);
        final Cursor c = this.getContentResolver().query(PeerContract.CONTENT_URI, null, null, null, null);
        messageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = getContentResolver().query(PeerContract.CONTENT_URI, null, null, null, null);

                if(position == cursor.getCount() - 1) {
                    position --;
                }
                c.moveToPosition(position);

                Peer peer = new Peer(c);
                intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
                startActivity(intent);
            }

        });

	}



    public void onClick(View v) {
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet");

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);
			
			String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            final Message message = new Message();
            message.sender = msgContents[0];
            message.timestamp = new Date(Long.parseLong(msgContents[1]));
            message.messageText = msgContents[2];

			Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);


            /*
             * TODO upsert the peer and message into the content provider.
             */
            // For this assignment, OK to do CP insertion on the main thread.

            Peer sender = new Peer();
            sender.name = message.sender;
            sender.timestamp = message.timestamp;
            sender.address = receivePacket.getAddress();

            ContentValues values = new ContentValues();
            message.writeToProvider(values);
            this.getContentResolver().insert(MessageContract.CONTENT_URI, values);

            ContentValues peerValues = new ContentValues();
            sender.writeToProvider(peerValues);

            this.getContentResolver().delete(PeerContract.CONTENT_URI, "name = ?", new String[]{sender.name});
            this.getContentResolver().insert(PeerContract.CONTENT_URI, peerValues);

            Log.i(TAG, "Received from " + sender.id + " " + sender.name + " " + message.messageText + " " + sender.timestamp);
            cursor = this.getContentResolver().query(MessageContract.CONTENT_URI, null, null, null, null);
            messagesAdapter.changeCursor(cursor);
            messagesAdapter.notifyDataSetChanged();

            /*
             * End TODO
             */


        } catch (Exception e) {
			
			Log.e(TAG, "Problems receiving packet: " + e.getMessage(), e);
			socketOK = false;
		} 

	}

	/*
	 * Close the socket before exiting application
	 */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
            serverSocket = null;
        }
    }
	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // TODO use a CursorLoader to initiate a query on the database
        CursorLoader cursorLoader;
        switch (id){
            case LOADER_ID:
                String[] projection = {MessageContract._ID, MessageContract.SENDER,
                        MessageContract.TIMESTAMP, MessageContract.MESSAGE_TEXT};
                cursorLoader = new CursorLoader(this, MessageContract.CONTENT_URI, projection,
                        null, null, null);
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
//       return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        // TODO populate the UI with the result of querying the provider
        switch (loader.getId()){
            case LOADER_ID:
                this.messagesAdapter.changeCursor((Cursor)data);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        switch (loader.getId()){
            case LOADER_ID:
                this.messagesAdapter.changeCursor(null);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }

    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS and SETTINGS options
        getMenuInflater().inflate(R.menu.chatserver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                // TODO PEERS provide the UI for viewing list of peers
                Intent in = new Intent(this, ViewPeersActivity.class);
                startActivity(in);
                break;

            default:
        }
        return false;
    }


}