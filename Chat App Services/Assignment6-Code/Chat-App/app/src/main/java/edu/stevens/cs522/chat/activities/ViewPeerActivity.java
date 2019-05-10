package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements IQueryListener<Message> {

    public static final String PEER_KEY = "peer";

    private SimpleCursorAdapter messageAdapter;

    private MessageManager messageManager;

    public TextView name;
    public TextView timestamp;
    public TextView address;

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
        name = (TextView) findViewById(R.id.view_user_name);
        timestamp = (TextView) findViewById(R.id.view_timestamp);
        address = (TextView) findViewById(R.id.view_address);
        listView = findViewById(R.id.view_messages);

        messageAdapter = new SimpleCursorAdapter(this, R.layout.message, null, new String[]{"message"},
                new int[]{R.id.message}, 0);

        messageManager=new MessageManager(this);
        //messageManager.getAllMessagesAsync( this);
        messageManager.getMessagesByPeerAsync(peer, this);
        listView.setAdapter(messageAdapter);
        messageAdapter.notifyDataSetChanged();

        name.setText(peer.name);
        timestamp.setText(peer.timestamp.toString());
        address.setText(peer.address.toString());

    }

    @Override
    public void handleResults(TypedCursor<Message> results) {
        messageAdapter.swapCursor(results.getCursor());
    }

    @Override
    public void closeResults() {
        messageAdapter.swapCursor(null);
    }
}
