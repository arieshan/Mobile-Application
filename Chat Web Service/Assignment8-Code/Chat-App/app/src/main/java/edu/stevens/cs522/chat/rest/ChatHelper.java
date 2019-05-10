package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.settings.Settings;


/**
 * Created by dduggan.
 */

public class ChatHelper {

    public static final String DEFAULT_CHAT_ROOM = "_default";

    private Context context;

    // Provided by server when we register
    private long senderId;

    // Installation senderId created when the app is installed (and provided with every request for sanity check)
    private UUID clientID;

    public String chatName;

    public ChatHelper(Context context) {
        this.context = context;
        this.clientID = Settings.getAppId(context);
    }

    public void register (String chatName, ResultReceiver resultReceiver) {
        if (Settings.isRegistered(context)) {
            Toast.makeText(context, R.string.already_registered, Toast.LENGTH_LONG).show();
            return;
        }
        if (chatName != null && !chatName.isEmpty()) {
            // TODO save the chat name and add a registration request to the request queue
            Settings.saveChatName(context, chatName);
            RegisterRequest request = new RegisterRequest(chatName, clientID);
            addRequest(request, resultReceiver);

        }
    }

    public void postMessage(String chatRoom, String text, ResultReceiver receiver) {
        this.senderId = Settings.getSenderId(context);
        if (text != null && !text.isEmpty()) {
            if (chatRoom == null || chatRoom.isEmpty()) {
                chatRoom = DEFAULT_CHAT_ROOM;
            }
            // TODO add a post message request to the request queue (see addRequest)

//            Log.e("---------", clientID.toString());
//            PostMessageRequest request = new PostMessageRequest(senderId, clientID, chatRoom, text);
//            addRequest(request, receiver);
            ChatMessage message = new ChatMessage();
            message.seqNum = 0;
            //message.senderId = Settings.getSenderId(context);
            message.chatRoom = chatRoom;
            message.messageText = text;
            message.sender = Settings.getChatName(context);
            message.senderId = Settings.getSenderId(context);
            message.timestamp = new Date();
            message.longitude = 0.0;
            message.latitude = 0.0;
            Log.i("request", message.print());
            PostMessageRequest request = new PostMessageRequest(message, clientID, Settings.getSenderId(context));
            //PostMessageRequest request = new PostMessageRequest(context, Settings.getClientId(context), chatRoom, text);
            //PostMessageRequest request = new PostMessageRequest(message);
            addRequest(request, receiver);
        }
    }

    private void addRequest(Request request, ResultReceiver receiver) {
        context.startService(createIntent(context, request, receiver));
    }

    private void addRequest(Request request) {
        addRequest(request, null);
    }

    /**
     * Use an intent to send the request to a background service. The request is included as a Parcelable extra in
     * the intent. The key for the intent extra is in the RequestService class.
     */
    public static Intent createIntent(Context context, Request request, ResultReceiver receiver) {
        Intent requestIntent = new Intent(context, RequestService.class);
        requestIntent.putExtra(RequestService.SERVICE_REQUEST_KEY, request);
        Log.e("!!!---!!!", "-----");
        if (receiver != null) {
            requestIntent.putExtra(RequestService.RESULT_RECEIVER_KEY, receiver);
        }
        return requestIntent;
    }

    public static Intent createIntent(Context context, Request request) {
        return createIntent(context, request, null);
    }

}
