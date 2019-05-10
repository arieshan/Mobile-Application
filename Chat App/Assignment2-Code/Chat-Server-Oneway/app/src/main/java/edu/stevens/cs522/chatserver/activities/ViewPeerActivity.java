package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_KEY = "peer";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        TextView username = (TextView)findViewById(R.id.view_user_name);
        TextView address = (TextView)findViewById(R.id.view_address);
        TextView port = (TextView)findViewById(R.id.view_port);
        TextView lastSeen = (TextView)findViewById(R.id.view_timestamp);

        username.setText(peer.name);
        address.setText(peer.address.toString());
        port.setText(String.valueOf(peer.port));
        lastSeen.setText(peer.timestamp.toString());





    }

}
