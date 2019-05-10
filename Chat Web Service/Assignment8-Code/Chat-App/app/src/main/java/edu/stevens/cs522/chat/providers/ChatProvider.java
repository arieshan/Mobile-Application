package edu.stevens.cs522.chat.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }

    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGES_TABLE = "messages";

    private static final String PEERS_TABLE = "peers";

    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int MESSAGES_SYNC = 3;
    private static final int PEERS_ALL_ROWS = 4;
    private static final int PEERS_SINGLE_ROW = 5;

    private static final String PEER_FK = "peer_fk";

    private static final String PEER_ID = PeerContract.ID;

    private static final String MESSAGE_ID = MessageContract.ID;

    private static final String NAME = PeerContract.NAME;

    private static final String LATITUDE = PeerContract.LATITUDE;

    private static final String LONGITUDE = PeerContract.LONGITUDE;

    private static final String MESSAGE_TEXT = MessageContract.MESSAGE_TEXT;

    private static final String SENDER = MessageContract.SENDER;

    private static final String SENDER_ID = MessageContract.SENDER_ID;

    private static final String TIMESTAMP=MessageContract.TIMESTAMP;

    private static final String SEQUENCE_NUMBER = MessageContract.SEQUENCE_NUMBER;

    private static final String CHAT_ROOM = MessageContract.CHAT_ROOM;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE = "CREATE TABLE " + PEERS_TABLE + " (" + PEER_ID + " integer primary key autoincrement," +
                NAME + " text not null," +  TIMESTAMP + " text," + LATITUDE + " text," + LONGITUDE + " text );";

        private static final String DATABASE_CREATE2 = "CREATE TABLE " + MESSAGES_TABLE + " (" + MESSAGE_ID + " integer primary key autoincrement," +
                MESSAGE_TEXT + " text," + CHAT_ROOM + " text,"+ TIMESTAMP + " text," + SENDER + " text," + SENDER_ID + " integer," + LATITUDE + " text," + LONGITUDE + " text," +
                SEQUENCE_NUMBER + " integer," + PEER_FK + " integer, foreign key (" + PEER_FK + ") references " + PEERS_TABLE + "(" + PEER_ID + ") on delete cascade);";

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO initialize database tables
            db.execSQL(DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO upgrade database if necessary
            db.execSQL("drop table if exists " + MESSAGES_TABLE);
            db.execSQL("drop table if exists " + PEERS_TABLE);
            onCreate(db);
        }
    }

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case PEERS_ALL_ROWS:
                return PeerContract.CONTENT_TYPE;
            case PEERS_SINGLE_ROW:
                return PeerContract.CONTENT_TYPE_ITEM;
            case MESSAGES_SYNC:
                return MessageContract.CONTENT_URI.toString();
            case MESSAGES_ALL_ROWS:
                return MessageContract.CONTENT_TYPE;
            case MESSAGES_SINGLE_ROW:
                return MessageContract.CONTENT_TYPE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId;
        Uri newUri;
        Log.e("insert uri", uri.toString() + " " +  MessageContract.CONTENT_PATH + " " + uriMatcher.match(uri));
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new message.
                // Make sure to notify any observers
                //throw new UnsupportedOperationException("Not yet implemented");
                rowId = db.insert(MESSAGES_TABLE, null, values);
                newUri = ContentUris.withAppendedId(MessageContract.CONTENT_URI, rowId);
                break;
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle requests to insert a new peer.
                // Make sure to notify any observers
                //throw new UnsupportedOperationException("Not yet implemented");
                String name = values.get("name").toString();
                String sql = "select name from " + PEERS_TABLE;
                Cursor c = db.rawQuery(sql, null);
                c.moveToFirst();

                while(c.moveToNext()) {
                    if (c.getString(0).equalsIgnoreCase(name)) {
                        db.delete(PEERS_TABLE, "name=?", new String[]{name});
                        break;
                    }
                }

                rowId = db.insert(PEERS_TABLE, null, values);
                newUri = ContentUris.withAppendedId(PeerContract.CONTENT_URI, rowId);
                break;
            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        HashMap<String, String> map;
        String groupby = null;
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODO: Implement this to handle query of all messages.
                //return db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                builder.setTables(MESSAGES_TABLE
                        + " LEFT JOIN " + PEERS_TABLE
                        + " ON (" + PEER_FK
                        + " = " + PeerContract.ID + ")");
                map = new HashMap<>();
                map.put(MessageContract.SENDER, PeerContract.NAME + " as " + MessageContract.SENDER);
                for (String field :
                        projection) {
                    if (!map.containsKey(field)) map.put(field, field);
                }
                builder.setProjectionMap(map);
                cursor = db.query(MESSAGES_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                return cursor;
            case PEERS_ALL_ROWS:
                // TODO: Implement this to handle query of all peers.
                //return db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                builder.setTables(PEERS_TABLE);
                cursor = db.query(PEERS_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
                break;
            case MESSAGES_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific message.
                //throw new UnsupportedOperationException("Not yet implemented");
                builder.setTables(MESSAGES_TABLE);
                builder.appendWhere(MessageContract.ID + " = " + MessageContract.getId(uri));
                break;
            case PEERS_SINGLE_ROW:
                // TODO: Implement this to handle query of a specific peer.
                //throw new UnsupportedOperationException("Not yet implemented");
                builder.setTables(PEERS_TABLE);
                builder.appendWhere(PeerContract.ID + " = " + PeerContract.getId(uri));
                break;
            default:
                String name = uri.getLastPathSegment();
                String sql = "select _id, timestamp, sender, message from " + MESSAGES_TABLE + " where sender=?";
                return db.rawQuery(sql, new String[]{name});
        }
        Cursor c = builder.query(db, projection, selection, selectionArgs, groupby, null, null);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO Implement this to handle requests to update one or more rows.
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case PEERS_ALL_ROWS:
                builder.setTables(PEERS_TABLE);
                break;
            case PEERS_SINGLE_ROW:
                builder.setTables(PEERS_TABLE);
                builder.appendWhere(PeerContract.ID + " = " + PeerContract.getId(uri));
                break;
            case MESSAGES_ALL_ROWS:
                builder.setTables(MESSAGES_TABLE);
                break;
            case MESSAGES_SINGLE_ROW:
                builder.setTables(MESSAGES_TABLE);
                builder.appendWhere(MessageContract.ID + " = " + MessageContract.getId(uri));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        Cursor cursor = builder.query(db, null, selection, selectionArgs, null, null, null);
        return cursor.getCount();    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        int rowsDeleted = 0;
        Uri newUri = null;
        switch (uriMatcher.match(uri)) {
            case PEERS_ALL_ROWS:
                builder.setTables(PEERS_TABLE);
                rowsDeleted = db.delete(PEERS_TABLE, selection, selectionArgs);
                newUri = PeerContract.CONTENT_URI;
                break;
            case PEERS_SINGLE_ROW:
                builder.setTables(PEERS_TABLE);
                selection = PeerContract.ID + " = ?";
                selectionArgs = new String[]{
                        uri.getLastPathSegment()
                };
                rowsDeleted = db.delete(PEERS_TABLE, selection, selectionArgs);
                newUri = PeerContract.CONTENT_URI;
                break;
            case MESSAGES_ALL_ROWS:
                builder.setTables(MESSAGES_TABLE);
                rowsDeleted = db.delete(MESSAGES_TABLE, selection, selectionArgs);
                newUri = MessageContract.CONTENT_URI;
                break;
            case MESSAGES_SINGLE_ROW:
                builder.setTables(MESSAGES_TABLE);
                selection = MessageContract.ID + " = ?";
                selectionArgs = new String[]{
                        uri.getLastPathSegment()
                };
                rowsDeleted = db.delete(MESSAGES_TABLE, selection, selectionArgs);
                newUri = MessageContract.CONTENT_URI;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (rowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(newUri, null);
        }
        return rowsDeleted;
    }
}

