package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;

public interface Persistable {

    public void writeToProvider(ContentValues out);

}