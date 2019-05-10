package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
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
        this.name = PeerContract.getName(cursor);
        this.timestamp = PeerContract.getTimestamp(cursor);

        String address = PeerContract.getAddress(cursor);
        try {
            this.address = InetAddress.getByName(address.substring(1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Peer(Parcel in) {
        // TODO
        this.id = in.readLong();
        this.name = in.readString();
        this.timestamp = DateUtils.readDate(in);
        this.address = InetAddressUtils.readAddress(in);

    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        PeerContract.putName(out, name);
        //PeerContract.putId(out, this.id);
        PeerContract.putTimestamp(out, timestamp.toString());
        PeerContract.putAddress(out, address.toString());
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
        DateUtils.writeDate(out, this.timestamp);
        InetAddressUtils.writeAddress(out, this.address);
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
