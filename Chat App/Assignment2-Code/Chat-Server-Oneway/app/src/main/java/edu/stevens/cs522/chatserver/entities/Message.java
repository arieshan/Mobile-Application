package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Message implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
        dest.writeLong(id);
        dest.writeString(messageText);
        dest.writeString(timestamp.toString());
        dest.writeString(sender);
        dest.writeLong(senderId);
    }

    public Message(Parcel in) {
        id = in.readLong();
        messageText = in.readString();
        try {
            timestamp = new SimpleDateFormat("dd/MM/yyyy").parse(in.readString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sender = in.readString();
        senderId = in.readLong();
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

