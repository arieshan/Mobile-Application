package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;

/**
 * Created by dduggan.
 */

public class SynchronizeRequest extends Request {

    // Added by request processor
    public long lastSequenceNumber = 0;

    public UUID clientId;

    @Override
    public String getRequestEntity() throws IOException {
        // We stream output for SYNC, so this always returns null
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        assert rd == null;
        return new SynchronizeResponse(connection);
    }

    @Override
    public Response process(RequestProcessor processor) {
        return processor.perform(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        super.writeToParcel(dest, flags);
        //dest.writeLong(senderId);
        //dest.writeString(super.chatName);

        //dest.writeString(super.appID.toString());
        //dest.writeSerializable(super.appID);
        //dest.writeSerializable(super.timestamp);
        //dest.writeDouble(super.longitude);
        //dest.writeDouble(super.latitude);
        dest.writeLong(this.lastSequenceNumber);

        //dest.writeString(appID.toString());
    }

    public SynchronizeRequest(long senderId, UUID clientID) {
        super(senderId, clientID);
        this.senderId = senderId;
        this.appID = clientID;
        this.clientId = clientID;
        Log.e("called-----",  clientId.toString());
    }
    public SynchronizeRequest(long senderId, UUID clientID, ChatMessage message) {
        super(senderId, clientID);
//        this.message = message;
    }

    public SynchronizeRequest(Parcel in) {

        super(in);
        // TODO
       // this.senderId = in.readLong();
        //this.chatName = in.readString();
        //this.appID = (UUID) in.readSerializable();
        //this.clientId = this.appID;
        //this.timestamp = (Date) in.readSerializable();
        //this.longitude = in.readDouble();
        //this.latitude = in.readDouble();
        this.lastSequenceNumber = in.readLong();
    }

    public static Creator<SynchronizeRequest> CREATOR = new Creator<SynchronizeRequest>() {
        @Override
        public SynchronizeRequest createFromParcel(Parcel source) {
            return new SynchronizeRequest(source);
        }

        @Override
        public SynchronizeRequest[] newArray(int size) {
            return new SynchronizeRequest[size];
        }
    };

}
