package edu.stevens.cs522.chatserver.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor>{

    private String tag;

    private IEntityCreator<T> creator;

    private ISimpleQueryListener<T> listener;

    private SimpleQueryBuilder(String tag,
                         IEntityCreator<T> creator,
                         ISimpleQueryListener<T> listener) {
        // TODO
        this.creator = creator;
        this.listener = listener;
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        String[] columns,
                                        String select,
                                        String[] selectArgs,
                                        IEntityCreator<T> creator,
                                        ISimpleQueryListener<T> listener) {

        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(tag, creator, listener);

        AsyncContentResolver contentResolver = new AsyncContentResolver(context.getContentResolver());

        contentResolver.queryAsync(uri, columns, select, selectArgs, null, qb);
    }

    public static <T> void executeQuery(String tag,
                                        Activity context,
                                        Uri uri,
                                        IEntityCreator<T> creator,
                                        ISimpleQueryListener<T> listener) {
        executeQuery(tag, context, uri, null, null, null, creator, listener);
    }

    @Override
    public void kontinue(Cursor value) {
        // TODO complete this
        List<T> list = new ArrayList<T>();
        if (value.moveToFirst()) {
            do {
                T instance = creator.create(value);
                list.add(instance);
            } while (value.moveToNext());
        }
        value.close();
        listener.handleResults(list);
    }

}
