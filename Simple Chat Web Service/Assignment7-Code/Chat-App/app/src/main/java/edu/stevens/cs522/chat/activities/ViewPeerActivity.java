package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity  implements IQueryListener<ChatMessage>  {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter peerAdapter;

    private SimpleCursorAdapter messageAdapter;
    private MessageManager messageManager;


    public TextView name;
    public TextView timestamp;
    public TextView longtitude;
    public TextView latitute;

    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        Peer peer = getIntent().getParcelableExtra(PEER_KEY);
        if (peer == null) {
            throw new IllegalArgumentException("Expected peer as intent extra");
        }

        // TODO init the UI
        name = findViewById(R.id.view_user_name);
        timestamp = findViewById(R.id.view_timestamp);
        longtitude = findViewById(R.id.view_longitude);
        latitute = findViewById(R.id.view_latitude);
        listView = findViewById(R.id.view_messages);

        messageAdapter = new SimpleCursorAdapter(this, R.layout.message, null, new String[]{"message"},
                new int[]{R.id.message}, 0);

        messageManager=new MessageManager(this);
        //messageManager.getAllMessagesAsync( this);
        messageManager.getAllMessagesAsync( this);
        listView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        name.setText(peer.name);
        timestamp.setText(peer.timestamp.toString());
        longtitude.setText(String.valueOf(peer.longitude));
        latitute.setText(String.valueOf(peer.latitude));
    }

    @Override
    public void handleResults(TypedCursor<ChatMessage> results) {
        messageAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        messageAdapter.swapCursor(null);
    }
}
