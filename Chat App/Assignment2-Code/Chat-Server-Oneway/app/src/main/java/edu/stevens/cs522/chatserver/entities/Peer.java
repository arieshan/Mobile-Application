package edu.stevens.cs522.chatserver.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static edu.stevens.cs522.chatserver.activities.ChatServer.TAG;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public int port;

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
        out.writeInt(port);
    }

    public Peer(Parcel in) {
        // TODO
        id = in.readLong();
        name = in.readString();
        String date = in.readString();

        try {
            timestamp = new SimpleDateFormat("EEE MMM d HH:mm:ss zzz yyyy").parse(date);
            String ip = in.readString();
            if(ip.startsWith("/")) {
                ip = ip.substring(1, ip.length());
            }
            Log.d(TAG, "+++   ---    " + ip);
            address = InetAddress.getByName(ip);
        } catch (ParseException | UnknownHostException e) {
            e.printStackTrace();
        }

        port = in.readInt();


    }

    public Peer(long id, String name, Date date, InetAddress address, int port) {
        this.id = id;
        this.name = name;
        this.timestamp = date;
        this.address = address;
        this.port = port;
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
