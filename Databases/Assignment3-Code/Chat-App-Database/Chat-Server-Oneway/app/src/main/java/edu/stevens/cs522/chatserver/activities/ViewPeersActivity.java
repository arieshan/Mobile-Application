package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.ChatDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ChatDbAdapter chatDbAdapter;

    private SimpleCursorAdapter peerAdapter;

    private Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with result of DB query
        chatDbAdapter = new ChatDbAdapter(this);
        cursor = chatDbAdapter.queryPeers();
        //Log.i(TAG, "==================== " + cursor.getColumnCount() + " " + ((cursor.moveToLast() == true) ? cursor.getPosition() : 0) + " " + cursor.getColumnIndex("_name"));

        ListView listView = (ListView) findViewById(R.id.peer_list);
        peerAdapter = new SimpleCursorAdapter(this, R.layout.peer, cursor, new String[]{"_name"},
                new int[]{R.id.peer}, 0);
        listView.setAdapter(peerAdapter);
        listView.setOnItemClickListener(this);
        peerAdapter.notifyDataSetChanged();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        cursor.moveToFirst();
        while(position > 0) {
            cursor.moveToNext();
            position--;
        }

        String peerid = PeerContract.getId(cursor);

        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, peerid);
        Log.i(TAG, "----------------- " + peerid);
        startActivity(intent);
    }
}
