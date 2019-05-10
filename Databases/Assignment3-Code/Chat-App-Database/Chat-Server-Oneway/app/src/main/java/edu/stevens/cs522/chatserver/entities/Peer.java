package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.PeerContract;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable, Persistable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        // TODO
        this.id = Long.parseLong(PeerContract.getId(cursor));
        String timestamp = PeerContract.getTimestamp(cursor);
        try {
            this.timestamp = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(timestamp);
        } catch (ParseException e) {
            Log.e(TAG, "parse timestamp failed " + e);
        }
        this.name = PeerContract.getName(cursor);
        try {
            String address = PeerContract.getAddress(cursor);
            this.address = InetAddress.getByName(address.substring(1));
        } catch (UnknownHostException e) {
            Log.e(TAG, "parse inet address failed " + e);
        }
    }

    public Peer(Parcel in) {
        // TODO
        id = in.readLong();
        name = in.readString();
        String date = in.readString();
        try {
            this.timestamp = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);
        } catch (ParseException e) {
            Log.e(TAG, "parse timestamp failed " + e);
        }

        try {
            String address = in.readString();
            address = address.substring(1);
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            Log.e(TAG, "parse inet address failed " + e);
        }
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        PeerContract.putAddress(out, this.address.toString());
        PeerContract.putId(out, this.id);
        PeerContract.putName(out, this.name);
        PeerContract.putTimestamp(out, this.timestamp.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO
        out.writeLong(id);
        out.writeString(name);
        out.writeString(timestamp.toString());
        out.writeString(address.toString());
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            // TODO
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            // TODO
            return new Peer[size];
        }

    };
}
