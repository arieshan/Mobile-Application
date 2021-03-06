package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable, Persistable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message() {
    }

    public Message(Cursor cursor) {
        // TODO
        this.messageText = MessageContract.getMessageText(cursor);
        String timestamp = MessageContract.getTimestamp(cursor);
        try {
            this.timestamp = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(timestamp);
        } catch (ParseException e) {
            Log.i(TAG, "parse timestamp failed " + e);
        }
        this.sender = MessageContract.getSender(cursor);
        this.senderId = Long.parseLong(MessageContract.getSenderId(cursor));

    }

    public Message(Parcel in) {
        // TODO
        id = in.readLong();
        messageText = in.readString();
        sender = in.readString();
        senderId = in.readLong();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putSender(out, this.sender);
        MessageContract.putSenderId(out, this.senderId);
        MessageContract.putTimestamp(out, this.timestamp.toString());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id);
        dest.writeString(messageText);
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public static final Creator<Message> CREATOR = new Creator<Message>() {

        @Override
        public Message createFromParcel(Parcel source) {
            // TODO
            return new Message(source);
        }

        @Override
        public Message[] newArray(int size) {
            // TODO
            return new Message[size];
        }

    };

}

