package edu.stevens.cs522.chat.async;

import java.util.List;

public interface ISimpleQueryListener<T> {

    public void handleResults(List<T> results);

}