/*********************************************************************

 Chat server: accept chat messagesAdapter from clients.

 Sender name and GPS coordinates are encoded
 in the messagesAdapter, and stripped off upon receipt.

 Copyright (c) 2017 Stevens Institute of Technology

 **********************************************************************/
package edu.stevens.cs522.chatserver.activities;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import edu.stevens.cs522.base.DatagramSendReceive;
import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ViewPeerActivity.PEER_KEY;
import static edu.stevens.cs522.chatserver.activities.ViewPeersActivity.PEERS_KEY;

public class ChatServer extends Activity implements OnClickListener {

    final static public String TAG = ChatServer.class.getCanonicalName();

    /*
     * Socket used both for sending and receiving
     */
    // private DatagramSocket serverSocket;
    private DatagramSendReceive serverSocket;

    /*
     * True as long as we don't get socket errors
     */
    private boolean socketOK = true;

    private ArrayList<Peer> peers;

    /*
     * TODO: Declare a listview for messagesAdapter, and an adapter for displaying messagesAdapter.
     */

    private static List<String> messages;
    private static ArrayAdapter<String> messageAdapter;
    private static ListView clientMessageListView;


    /*
     * End Todo
     */

    Button next;

    /*
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_messages);

        /**
         * Let's be clear, this is a HACK to allow you to do network communication on the view_messages thread.
         * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
         * this right in a future assignment (using a Service managing background threads).
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        next = findViewById(R.id.next);
        next.setOnClickListener(this);

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

        // List of peers
        peers = new ArrayList<Peer>();

        /*
         * TODO: Initialize the UI.
         */
        clientMessageListView = (ListView)findViewById(R.id.message_list);
        next = findViewById(R.id.next);
        messages = new ArrayList<>();
        messageAdapter = new ArrayAdapter<String>(this, R.layout.message, messages);

        clientMessageListView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();
        next.setOnClickListener(this);
        final Intent intent = new Intent(this, ViewPeerActivity.class);
        clientMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra(PEER_KEY, peers.get(position));
                startActivity(intent);
            }

        });
        /*
         * End Todo
         */

    }

    public void onClick(View v) {

        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

        try {
            serverSocket.receive(receivePacket);
            Log.d(TAG, "Received a packet");


            InetAddress sourceIPAddress = receivePacket.getAddress();
            Log.d(TAG, "Source IP Address: " + sourceIPAddress);

            String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split(":");

            Log.d(TAG, "content " + msgContents[0]);
            String name = msgContents[0];
            String message = msgContents[1];

            Log.d(TAG, "Received from " + name + ": " + message);

            /*
             * TODO: Add message with sender to the display.
             */
            String info = new String(receivePacket.getData());
            Log.i("GET?", info);
            messages.add(info);
            messageAdapter.notifyDataSetChanged();

            long id = 192389L; //id

            Peer peer = new Peer(id, name, Calendar.getInstance().getTime(), sourceIPAddress, receivePacket.getPort());
            addPeer(peer);

            /*
             * End Todo
             */

        } catch (Exception e) {
            Log.e(TAG, "Problems receiving packet: " + e.getMessage());
            socketOK = false;
        }

    }

    /*
     * Close the socket before exiting application
     */
    public void closeSocket() {
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    /*
     * If the socket is OK, then it's running
     */
    boolean socketIsOK() {
        return socketOK;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        closeSocket();
    }

    private void addPeer(Peer peer) {
        for (Peer p : peers) {
            if (p.name.equals(peer.name)) {
                return;
            }
        }
        peers.add(peer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODO inflate a menu with PEERS option
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            case R.id.peers:
                // TODO PEERS provide the UI for viewing list of peers
                // Send the list of peers to the subactivity as a parcelable list
                Log.d(TAG, "---------------- " + peers.size());
                Intent intent = new Intent(this, ViewPeersActivity.class);

                intent.putParcelableArrayListExtra(PEERS_KEY, peers);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }


}