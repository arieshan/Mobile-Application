package edu.stevens.cs522.chat.rest;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.base.DateUtils;

/**
 * Created by dduggan.
 */

public class PostMessageRequest extends Request {

    public String chatRoom;

    public String message;

    public ChatMessage chatMessage;

    public PostMessageRequest(long senderId, UUID clientID, String chatRoom, String message) {
        super(senderId, clientID);
        this.chatRoom = chatRoom;
        this.message = message;
    }

    public PostMessageRequest(ChatMessage message, UUID clientId, long senderId) {
        super(senderId, clientId);
        this.chatMessage = message;
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        Map<String,String> headers = new HashMap<>();
        // TODO
        headers.put("X-Client-ID", super.appID.toString());
        headers.put("X-Client-Id", super.appID.toString());
        headers.put("X-App-Id", super.appID.toString());
        headers.put("X-Timestamp", String.valueOf(super.timestamp.getTime()));
        headers.put("X-Latitude", String.valueOf(super.latitude));
        headers.put("X-Longitude", String.valueOf(super.longitude));
        return headers;
    }

    @Override
    public String getRequestEntity() throws IOException {
        StringWriter wr = new StringWriter();
        JsonWriter jw = new JsonWriter(wr);
        // TODO write a JSON message of the form:
        // { "room" : <chat-room-name>, "message" : <message-text> }
        jw.beginObject();
        jw.name("chatroom").value(this.chatMessage.chatRoom);
        jw.name("text").value(this.chatMessage.messageText);
        jw.endObject();
        jw.flush();
        return wr.toString();
    }

    @Override
    public Response getResponse(HttpURLConnection connection, JsonReader rd) throws IOException{
        return new PostMessageResponse(connection);
    }

    public Response getDummyResponse() {
        return new DummyResponse();
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
        // TODO
       // super.writeToParcel(dest,flags);
        //dest.writeString(this.chatMessage.chatRoom);
        //dest.writeString(this.chatMessage.messageText);
        dest.writeParcelable(this.chatMessage, flags);

    }

    public PostMessageRequest(Parcel in) {
       // super(in);
        // TODO
        //this.chatRoom = in.readString();
        //this.message = in.readString();

        this.chatMessage = (ChatMessage) in.readParcelable(ChatMessage.class.getClassLoader());

    }

    public static Creator<PostMessageRequest> CREATOR = new Creator<PostMessageRequest>() {
        @Override
        public PostMessageRequest createFromParcel(Parcel source) {
            return new PostMessageRequest(source);
        }

        @Override
        public PostMessageRequest[] newArray(int size) {
            return new PostMessageRequest[size];
        }
    };

}
