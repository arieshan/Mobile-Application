package edu.stevens.cs522.chat.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import java.util.Date;

/**
 * Created by dduggan.
 */

public class MessageContract extends BaseContract {

    public static final Uri CONTENT_URI = CONTENT_URI(AUTHORITY, "Message");

    public static final Uri CONTENT_URI(long id) {
        return CONTENT_URI(Long.toString(id));
    }

    public static final Uri CONTENT_URI(String id) {
        return withExtendedPath(CONTENT_URI, id);
    }

    public static final String CONTENT_PATH = CONTENT_PATH(CONTENT_URI);

    public static final String CONTENT_PATH_ITEM = CONTENT_PATH(CONTENT_URI("#"));

    public static final String PORT = "port";


    public static final String ID = _ID;

    public static final String MESSAGE_TEXT = "message";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    // TODO remaining columns in Messages table



    public static final String ADDRESS = "address";


    public static final String SENDRT_ID = "senderId";


    public static final String CONTENT_TYPE = "vnd.android.cursor/vnd."
            + AUTHORITY + "."
            + "messages";
    public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/vnd."
            + AUTHORITY + "."
            + "message";


    private static int messageTextColumn = -1;

    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODO remaining getter and putter operations for other columns

    private static int idColumn = -1;

    public static String getId(Cursor cursor) {
        if (idColumn < 0) {
            idColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getString(idColumn);
    }

    public static void putId(ContentValues out, Long id) {
        out.put(ID, id);
    }

    private static int timeColumn = -1;

    public static Date getTimestamp (Cursor cursor) {
        if (timeColumn < 0) {
            timeColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return new Date(cursor.getLong(cursor.getColumnIndexOrThrow(TIMESTAMP)));
    }

    public static void putTimestamp (ContentValues out, String timeStamp) {
        out.put(TIMESTAMP, timeStamp);
    }

    private static int senderColumn = -1;

    public static String getSender (Cursor cursor) {
        if (senderColumn < 0) {
            senderColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(senderColumn);
    }

    public static void putSender(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    private static int senderIdColumn = -1;

    public static String getSenderId (Cursor cursor) {
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDRT_ID);
        }
        return cursor.getString(senderIdColumn);
    }

    public static void putSenderId(ContentValues out, Long sender_id) {
        out.put(SENDRT_ID, sender_id);
    }

    private static int portColumn = -1;

    public static String getPort (Cursor cursor) {
        if (portColumn < 0) {
            portColumn = cursor.getColumnIndexOrThrow(PORT);
        }
        return cursor.getString(portColumn);
    }

    public static void putPort(ContentValues out, int port) {
        out.put(PORT, port);
    }

}
