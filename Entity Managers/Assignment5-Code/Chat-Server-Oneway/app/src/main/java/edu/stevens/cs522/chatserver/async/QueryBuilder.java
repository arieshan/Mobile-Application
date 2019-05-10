package edu.stevens.cs522.chatserver.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.managers.MessageManager;
import edu.stevens.cs522.chatserver.managers.PeerManager;
import edu.stevens.cs522.chatserver.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODO complete the implementation of this

    private String tag;

    private String[] columns;

    private String select;

    private String[] selectArgs;

    private String order;

    private IEntityCreator<T> creator;

    private IQueryListener<T> listener;

    private Context context;
    private Uri uri;
    private int loaderId;

    private QueryBuilder(String tag,
                         Context context,
                         Uri uri,
                         String[] columns,
                         String select,
                         String[] selectArgs,
                         String order,
                         int loaderID,
                         IEntityCreator<T> creator,
                         IQueryListener<T> listener) {
        // TODO

        this.tag = tag;
        this.context = context;
        this.order = order;
        this.select = select;
        this.selectArgs = selectArgs;
        this.columns = columns;
        this.uri = uri;
        this.loaderId = loaderID;
        this.creator = creator;
        this.listener = listener;
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        String[] columns,
                                        String select,
                                        String[] selectArgs,
                                        String order,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context,
                uri, columns, select, selectArgs, order,
                loaderID, creator, listener);

        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener) {

        executeQuery(tag, context, uri, null, null, null, null, loaderID, creator, listener);
    }

    public static <T> void reexecuteQuery(String tag,
                                          Activity context,
                                          Uri uri,
                                          String[] columns,
                                          String select,
                                          String[] selectArgs,
                                          String order,
                                          int loaderID,
                                          IEntityCreator<T> creator,
                                          IQueryListener<T> listener) {

        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context,
                uri, columns, select, selectArgs, order,
                loaderID, creator, listener);

        LoaderManager lm = context.getLoaderManager();

        lm.restartLoader(loaderID, null, qb);
    }

    public static <T> void reexecuteQuery(String tag,
                                          Activity context,
                                          Uri uri,
                                          int loaderID,
                                          IEntityCreator<T> creator,
                                          IQueryListener<T> listener) {

        reexecuteQuery(tag, context, uri, null, null, null, null, loaderID, creator, listener);
    }


    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        // TODO
        if (id == loaderId) {
            String[] projection = null;
            switch (id) {
                case PeerManager.LOADER_ID:
                    projection = new String[]{
                            PeerContract.ID,
                            PeerContract.NAME,
                            PeerContract.ADDRESS,
                            PeerContract.TIMESTAMP
                    };
                    break;
                case MessageManager.LOADER_ID:
                    projection = new String[]{
                            MessageContract.ID,
                            MessageContract.MESSAGE_TEXT,
                            MessageContract.SENDER,
                            MessageContract.TIMESTAMP
                    };
                    break;
                default:
                    throw new IllegalArgumentException("Unexpected loader id: " + id);
            }
            return new CursorLoader(context,
                    uri,
                    projection,
                    null,
                    null,
                    null);
        }
        throw new IllegalArgumentException("Unexpected loader id: " + id + " loaderId = " + loaderId);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {
        if (loader.getId() == loaderId) {
            listener.handleResults(new TypedCursor<T>(data, creator));
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        // TODO
        if (loader.getId() == loaderId) {
            listener.closeResults();
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }
}
