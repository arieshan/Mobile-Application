package edu.stevens.cs522.chat.rest;

import android.appwidget.AppWidgetProvider;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.EnumUtils;
import edu.stevens.cs522.chat.settings.Settings;

/**
 * Created by dduggan.
 */

public abstract class Request implements Parcelable {

    private final static String TAG = Request.class.getCanonicalName();

    public static enum RequestType {
        REGISTER("Register"),
        POST_MESSAGE("Post Message"),
        SYNCHRONIZE("Synchronize");
        private String value;
        private RequestType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    public static enum StatusType {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        FAILED("Failed"),
        COMPLETED("Completed");
        private String value;
        private StatusType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    // Sender senderId (database key)
    public long senderId;

    // Status of a request
    public StatusType status;

    // Installation id
    public UUID appID;

    public String clientId;
    public long time;

    // App version
    public long version;

    // Time stamp
    public Date timestamp;

    // Device coordinates
    public double longitude;

    public double latitude;

    // Output in case of errors
    public String responseMessage;

    public Request() {
        this.timestamp = DateUtils.now();
    }


    protected Request(long id, UUID clientID) {
        Log.e("hello request", clientID.toString());
        this.senderId = id;
        this.status = StatusType.PENDING;
        this.timestamp = DateUtils.now();
        this.appID = clientID;
        clientId = clientID.toString();
        time = timestamp.getTime();
    }

    protected Request(Request request) {
        senderId = request.senderId;
        status = request.status;
        appID = request.appID;
        version = request.version;
        timestamp = request.timestamp;
        longitude = request.longitude;
        latitude = request.latitude;
        responseMessage = request.responseMessage;
    }

    protected Request(Parcel in) {
        // TODO assume tag has already been read, this will be called by subclass constructor
        this.senderId = in.readLong();
        //int tmpStatus = in.readInt();
        //this.status = tmpStatus == -1 ? null : StatusType.values()[tmpStatus];
        status = EnumUtils.readEnum(StatusType.class, in);

        final String appId = in.readString();
        if (appId != null) {
            appID = UUID.fromString(appId);
            clientId = appId;
        }
        this.version = in.readLong();
        this.timestamp = DateUtils.readDate(in);
        time = timestamp.getTime();
        this.longitude=in.readDouble();
        this.latitude=in.readDouble();
        this.responseMessage = in.readString();

        Log.i(TAG, "in:::::::" + "senderId:" + senderId +  "+status:" + status + "+clientID:" + appID + "+version:" + version +
                "+timestamp:" + timestamp.toString() + "+longitude:" + longitude);

    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO subclasses write tag, then call this, then write out their own fields
        out.writeLong(this.senderId);
        EnumUtils.writeEnum(out, status);
        out.writeString(this.appID.toString());
        out.writeLong(this.version);
        DateUtils.writeDate(out, this.timestamp);
        out.writeDouble(this.longitude);
        out.writeDouble(this.latitude);
        out.writeString(this.responseMessage);

        Log.i(TAG, "out:::::::" + "senderId:" + senderId + "+status:" + status + "+clientID:" + appID + "+version:" + version +
                "+timestamp:" + timestamp.toString() + "+longitude:" + longitude);
    }

    /*
     * HTTP request headers
     */
    public static String APP_ID_HEADER = "X-App-Id";

    public static String TIMESTAMP_HEADER = "X-Timestamp";

    public static String LONGITUDE_HEADER = "X-Longitude";

    public static String LATITUDE_HEADER = "X-Latitude";

    // App-specific HTTP request headers.
    public Map<String,String> getRequestHeaders() {

        Map<String,String> headers = new HashMap<>();
        headers.put(APP_ID_HEADER, appID.toString());
        //headers.put(APP_ID_HEADER, "5bbc2ec2-7349-47f7-b559-9f884d82f4e8");
        headers.put("X-Client-Id", appID.toString());
        headers.put("X-Client-ID", appID.toString());
        headers.put(TIMESTAMP_HEADER, Long.toString(time));
        headers.put(LONGITUDE_HEADER, Double.toString(longitude));
        headers.put(LATITUDE_HEADER, Double.toString(latitude));
        return headers;
    }

    // JSON body (if not null) for request data not passed in headers.
    public abstract String getRequestEntity() throws IOException;

    // Define your own Response class, including HTTP response code.
    public abstract Response getResponse(HttpURLConnection connection,
                                         JsonReader rd /* Null for streaming */)
            throws IOException;

    public final Response getResponse(HttpURLConnection connection) throws IOException {
        return getResponse(connection, null);
    }

    public abstract Response process(RequestProcessor processor);

    public int describeContents() {
        return 0;
    }

    public static Request createRequest(Parcel in) {
        RequestType requestType = EnumUtils.readEnum(RequestType.class, in);
        switch (requestType) {
            case REGISTER:
                return new RegisterRequest(in);
            case POST_MESSAGE:
                return new PostMessageRequest(in);
            case SYNCHRONIZE:
                return new SynchronizeRequest(in);
            default:
                break;
        }
        throw new IllegalArgumentException("Unknown request type: "+requestType.name());
    }

    public static final Parcelable.Creator<Request> CREATOR = new Parcelable.Creator<Request>() {
        public Request createFromParcel(Parcel in) {
            return createRequest(in);
        }

        public Request[] newArray(int size) {
            return new Request[size];
        }
    };

}