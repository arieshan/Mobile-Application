package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor>{

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
        TextView name = (TextView) findViewById(R.id.view_user_name);
        TextView timestamp = (TextView) findViewById(R.id.view_timestamp);
        TextView address = (TextView) findViewById(R.id.view_address);
        ListView messages = (ListView) findViewById(R.id.view_messages);
        Log.i(TAG, "---------------   peer id " + peer.id);


        name.setText(peer.name);
        timestamp.setText(peer.timestamp.toString());
        address.setText(peer.address.toString());
        String selectQuery = "sender ='"+peer.name+"'";

        Cursor cursor = this.getContentResolver().query(MessageContract.CONTENT_URI, null, selectQuery, null, null);
        SimpleCursorAdapter messagesAdapter = new SimpleCursorAdapter(this, R.layout.message, cursor, new String[]{"message_text"}, new int[]{R.id.message}, 0);
        messages.setAdapter(messagesAdapter);
        messagesAdapter.notifyDataSetChanged();


    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // TODO use a CursorLoader to initiate a query on the database
        // Filter messages with the sender id
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        // TODO populate the UI with the result of querying the provider
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // TODO reset the UI when the cursor is empty
    }

}
