package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class ChatDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    private static final int DATABASE_VERSION = 1;

    private static final String KEY_NAME = "_name";
    private static final String KEY_TIMESTAMP = "_timestamp";
    private static final String KEY_ADDRESS = "_address";
    private static final String KEY_ID = "_id";

    private static final String KEY_MESSAGETEXT = "_message";
    private static final String KEY_SENDER = "_sender";
    private static final String KEY_SENDER_ID = "_senderId";

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "create table "+ MESSAGE_TABLE +
                "(_id integer primary key autoincrement,_message text not null,_timestamp text not null," +
                "_sender text not null," + "_senderId text not null);";
        private static final String DATABASE_CREATE2 = "create table "+ PEER_TABLE +
                "(_id integer primary key autoincrement,_name text not null,_timestamp text not null," + "_address text not null);";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO
            db.execSQL("drop table if exists " + MESSAGE_TABLE);
            db.execSQL("drop table if exists " + PEER_TABLE);
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO
            db.execSQL("drop table if exists " + MESSAGE_TABLE);
            db.execSQL("drop table if exists " + PEER_TABLE);
            onCreate(db);
        }
    }


    public ChatDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void open() throws SQLException {
        // TODO
    }

    public Cursor query(){
        return dbHelper.getReadableDatabase().query(MESSAGE_TABLE,null,null,null,null,null,null);
    }

    public Cursor queryPeers(){
        return dbHelper.getReadableDatabase().query(PEER_TABLE,null,null,null,null,null,null);
    }

    public Cursor fetchAllMessages() {
        // TODO
        db = dbHelper.getReadableDatabase();
        String selectQuery = "select * from " + MESSAGE_TABLE;
        return db.rawQuery(selectQuery, null);
    }

    public Cursor fetchAllPeers() {
        // TODO
        db = dbHelper.getReadableDatabase();
        String selectQuery = "select * from " + PEER_TABLE;
        return db.rawQuery(selectQuery, null);
    }

    public Peer fetchPeer(long peerId) {
        // TODO
        db = dbHelper.getReadableDatabase();
        String selectQuery = "select _id, _name, _timestamp, _address  from " + PEER_TABLE + " where _id = " + peerId;
        Cursor c = db.rawQuery(selectQuery, null);
        if(c != null) {
            c.moveToFirst();
            Peer peer = new Peer(c);
            return peer;
        }
        Log.d("DB fetch peer ", " failed");
        return null;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TODO
        db = dbHelper.getReadableDatabase();
        String selectQuery = "select _id, _message, _sender, _timestamp, _senderId from " + MESSAGE_TABLE + " where _sender ='"+peer.name+"'";
        Cursor c = db.rawQuery(selectQuery, null);

        return c;
    }

    public long persist(Message message) throws SQLException {
        // TODO
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_MESSAGETEXT, message.messageText);
        values.put(KEY_TIMESTAMP, message.timestamp.toString());
        values.put(KEY_SENDER, message.sender);
        Log.i("insert message ", " sender name =-=-=-=-=-=-=-= " + message.sender);
        values.put(KEY_SENDER_ID, message.senderId);

        long code = db.insert(MESSAGE_TABLE, null, values);
        Log.i("insert message table ", "  =-=-=-=-=-=-=-= " + code);

        return code;
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     */
    public long persist(Peer peer) throws SQLException {
        // TODO
        if (find(peer)) {
            return peer.id;
        }
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, peer.name);
        values.put(KEY_TIMESTAMP, peer.timestamp.toString());
        values.put(KEY_ADDRESS, peer.address.toString());
        //values.put(KEY_ID, peer.id);

        peer.id = db.insert(PEER_TABLE, null, values);
        Log.i(TAG, "==============---------- " + peer.id);
        return peer.id;
    }

    public boolean find(Peer peer) {
        // TODO
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, peer.name);
        values.put(KEY_TIMESTAMP, peer.timestamp.toString());
        values.put(KEY_ADDRESS, peer.address.toString());
        return db.update(PEER_TABLE, values, KEY_NAME + " = ?", new String[]{String.valueOf(peer.name)}) > 0;
        //return db.delete(PEER_TABLE, KEY_NAME + " = ?",new String[]{String.valueOf(peer.name)}) > 0;
    }

    public void close() {
        // TODO
        db = dbHelper.getWritableDatabase();
        db.close();
    }
}