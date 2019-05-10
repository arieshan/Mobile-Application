package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer-id";

    private ChatDbAdapter chatDbAdapter;

    private SimpleCursorAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        chatDbAdapter = new ChatDbAdapter(this);

        long peerId = Long.parseLong(getIntent().getStringExtra(PEER_ID_KEY));
        if (peerId < 0) {
            throw new IllegalArgumentException("Expected peer id as intent extra");
        }

        // TODO init the UI
        TextView name = (TextView) findViewById(R.id.view_user_name);
        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        TextView address = (TextView) findViewById(R.id.view_address);
        ListView messages = (ListView) findViewById(R.id.messages);
        Log.i(TAG, "---------------   peer id " + peerId);


        Peer peer = chatDbAdapter.fetchPeer(peerId);
        Cursor message = chatDbAdapter.fetchMessagesFromPeer(peer);
        //Peer peer = getIntent().getParcelableExtra(PEER_ID_KEY);
        Log.i("============= peer:", peer.toString());

        name.setText(peer.name);
        timestamp.setText(peer.timestamp.toString());
        address.setText(peer.address.toString());

        adapter = new SimpleCursorAdapter(this, R.layout.message, message, new String[]{"_message"},
                new int[]{R.id.message}, 0);
        messages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
