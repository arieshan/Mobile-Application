package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    /*
     * TODO See ChatServer for example of what to do, query peers database instead of messages database.
     */

    private static final int LOADER_ID = 1;

    private SimpleCursorAdapter peerAdapter;

    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODO initialize peerAdapter with empty cursor (null)

        Cursor c = this.getContentResolver().query(PeerContract.CONTENT_URI, null, null, null, null);
        listView = (ListView) findViewById(R.id.peerList);
        peerAdapter = new SimpleCursorAdapter(this, R.layout.peer, null, new String[]{"name"},
                new int[]{R.id.peer}, 0);
        listView.setAdapter(peerAdapter);
        listView.setOnItemClickListener(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            Log.i(TAG, "============>>>>>>>>>  " + peer.name + " " + peer.address);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // TODO use a CursorLoader to initiate a query on the database
        CursorLoader cursorLoader;
        switch (id){
            case LOADER_ID:
                String[] projection = {"peers" + "." + PeerContract._ID, PeerContract.NAME,
                        PeerContract.TIMESTAMP, PeerContract.ADDRESS};
                cursorLoader = new CursorLoader(this, PeerContract.CONTENT_URI, projection,
                        null, null, null);
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        // TODO populate the UI with the result of querying the provider
        switch (loader.getId()){
            case LOADER_ID:
                this.peerAdapter.changeCursor(data);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // TODO reset the UI when the cursor is empty
        switch (loader.getId()){
            case LOADER_ID:
                this.peerAdapter.changeCursor(null);
                break;
            default:
                throw new IllegalArgumentException("Unexpected loader id: " + loader.getId());
        }
    }

}
