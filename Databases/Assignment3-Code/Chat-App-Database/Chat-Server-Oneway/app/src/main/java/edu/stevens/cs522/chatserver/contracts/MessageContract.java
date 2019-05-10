package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by dduggan.
 */

public class MessageContract implements BaseColumns {

    public static final String MESSAGE_TEXT = "_message";

    public static final String TIMESTAMP = "_timestamp";

    public static final String SENDER = "_sender";

    public static final String SENDER_ID = "_senderId";

    // TODO remaining columns in Messages table

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

    private static int timeColumn = -1;

    public static String getTimestamp (Cursor cursor) {
        if (timeColumn < 0) {
            timeColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return cursor.getString(timeColumn);
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
        Log.d("test test ", "=============>>>>>>>>>>> " + cursor.getColumnCount() + " " + cursor.getColumnName(0) + " " + cursor.getColumnName(1) + " " + cursor.getColumnName(2) + " " + cursor.getColumnName(3) + " " + cursor.getColumnName(4));
        if (senderIdColumn < 0) {
            senderIdColumn = cursor.getColumnIndexOrThrow(SENDER_ID);
        }
        return cursor.getString(senderIdColumn);
    }

    public static void putSenderId(ContentValues out, Long sender_id) {
        out.put(SENDER_ID, sender_id);
    }
}
