package edu.stevens.cs522.chat.async;

import edu.stevens.cs522.chat.managers.TypedCursor;

public interface IQueryListener<T> {

    public void handleResults(TypedCursor<T> results);

    public void closeResults();

}
