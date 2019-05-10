package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;
import static edu.stevens.cs522.chatserver.activities.ViewPeerActivity.PEER_KEY;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener {

    public static final String PEERS_KEY = "peers";

    private ArrayList<Peer> peers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        peers = getIntent().getParcelableArrayListExtra(PEERS_KEY);

        List<String> list = new ArrayList<>();

        for(Peer p : peers) {
            list.add(p.name);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.message, list);
        // TODO display the list of peers, set this activity as onClick listener
        ListView listView = findViewById(R.id.peer_list);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        final Intent intent = new Intent(this, ViewPeerActivity.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intent.putExtra(PEER_KEY, peers.get(position));
                startActivity(intent);
            }

        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Peer peer = peers.get(position);
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
        startActivity(intent);
    }
}
