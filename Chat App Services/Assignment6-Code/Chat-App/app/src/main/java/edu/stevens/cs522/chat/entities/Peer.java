package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.Persistable;

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

    public int port;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        // TODO
        this.name = PeerContract.getName(cursor);
        //this.timestamp = DateUtils.getDate(cursor, 4);
        this.timestamp = PeerContract.getTimestamp(cursor);
        String address = PeerContract.getAddress(cursor);
        try {
            this.address = InetAddress.getByName(address.substring(1));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        //this.port = Integer.parseInt(PeerContract.getPort(cursor));
    }

    public Peer(Parcel in) {
        // TODO
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        address = InetAddressUtils.readAddress(in);
        //port = in.readInt();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
        PeerContract.putName(out, name);
        PeerContract.putTimestamp(out, timestamp.toString());
        PeerContract.putAddress(out, address.toString());
        //PeerContract.putPort(out, port);
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
        //out.writeInt(port);

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
