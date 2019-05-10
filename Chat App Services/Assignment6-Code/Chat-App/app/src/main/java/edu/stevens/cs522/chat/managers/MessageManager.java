package edu.stevens.cs522.chat.managers;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.IQueryListener;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.Message;
import edu.stevens.cs522.chat.entities.Peer;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<Message> {

    public static final int LOADER_ID = 1;

    private static final IEntityCreator<Message> creator = new IEntityCreator<Message>() {
        @Override
        public Message create(Cursor cursor) {
            return new Message(cursor);
        }
    };

    private AsyncContentResolver contentResolver;

    private ContentResolver syncResolver;


    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
        syncResolver = getSyncResolver();
    }

    public void getAllMessagesAsync(IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        QueryBuilder.executeQuery(tag, (Activity) context, MessageContract.CONTENT_URI, loaderID, creator, listener);
    }

    public void getMessagesByPeerAsync(Peer peer, IQueryListener<Message> listener) {
        // TODO use QueryBuilder to complete this
        // Remember to reset the loader!
        QueryBuilder.executeQuery(tag, (Activity) context, MessageContract.CONTENT_URI(peer.name), loaderID, creator, listener);
    }

    public void persistAsync(Message message) {
        // TODO
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        contentResolver.insertAsync(MessageContract.CONTENT_URI, values, new IContinue<Uri>() {
            @Override
            public void kontinue(Uri value) {
                //message.setId(MessageContract.getId(value));
                getSyncResolver().notifyChange(value, null);
            }
        });
    }

    public void  persist(Message message) {
        // Synchronous version, executed on background thread
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        syncResolver.insert(MessageContract.CONTENT_URI, values);
    }


}
