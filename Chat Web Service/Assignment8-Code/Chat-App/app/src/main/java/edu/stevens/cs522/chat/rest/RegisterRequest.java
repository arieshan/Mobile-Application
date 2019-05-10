package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by dduggan.
 */

public class RegisterRequest extends Request {

    public String chatname;

    public RegisterRequest(String chatname, UUID clientID) {
        super(0, clientID);
        this.chatname = chatname;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        // TODO add headers
        headers.put("X-Chat-Name", this.chatname);
        headers.put("X-Client-ID", super.appID.toString());
        headers.put("X-Client-Id", super.appID.toString());
        headers.put("X-App-Id", super.appID.toString());
        headers.put("X-Timestamp", String.valueOf(this.timestamp.getTime()));
        headers.put("X-Latitude", String.valueOf(this.latitude));
        headers.put("X-Longitude", String.valueOf(this.longitude));
        return headers;
    }

    @Override
    public String getRequestEntity() throws IOException {
        return null;
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new RegisterResponse(connection);
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
        dest.writeString(chatname);
//        dest.writeLong(senderId);
//        dest.writeString(this.chatName);
//
//        dest.writeString(super.appID.toString());
//        dest.writeSerializable(super.appID);
//        dest.writeSerializable(super.timestamp);
//        dest.writeDouble(super.longitude);
//        dest.writeDouble(super.latitude);

    }

    public RegisterRequest(Parcel in) {
        super(in);
        // TODO
//        this.senderId = in.readLong();
//        this.chatName = in.readString();
//        this.clientId = in.readString();
//        this.appID = (UUID) in.readSerializable();
//        this.timestamp = (Date) in.readSerializable();
//        this.longitude = in.readDouble();
//        this.latitude = in.readDouble();
        this.chatname = in.readString();
    }

    public static Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel source) {
            return new RegisterRequest(source);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

}
