package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable, Persistable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage() {
    }

    public ChatMessage(Cursor cursor) {
        // TODO
        this.id = Long.parseLong(MessageContract.getId(cursor));
        this.sender = MessageContract.getSender(cursor);
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = MessageContract.getTimestamp(cursor);
        this.longitude = MessageContract.getLongitude(cursor);
        this.latitude = MessageContract.getLatitude(cursor);
    }

    public ChatMessage(Parcel in) {
        // TODO
        id = in.readLong();
        sender = in.readString();
        messageText = in.readString();
        timestamp = new Date(in.readLong());
        longitude = in.readDouble();
        latitude = in.readDouble();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        MessageContract.putSender(out, sender);
        MessageContract.putTimestamp(out, timestamp);
        MessageContract.putMessageText(out, messageText);
        MessageContract.putLongitude(out, longitude);
        MessageContract.putLatitude(out, latitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {

        @Override
        public ChatMessage createFromParcel(Parcel source) {
            // TODO
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            // TODO
            return new ChatMessage[size];
        }

    };

}
