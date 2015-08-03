package com.wan.yalandan.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DatabaseAdapter {


    public static final String TOKENWORDS_TABLENAME = "TakenWords";
    public static final String TOKENWORDS_ID = "_id";
    public static final String TOKENWORDS_WORD = "Word";
    public static final String TOKENWORDS_URI = "Uri";
    public static final String TOKENWORDS_DATE = "Date";
    public static final String TOKENWORDS_LISTNUMBER = "ListNumber";

    private static final String LOG_TAG = "DATABASE";
    private static DatabaseHelper databaseHelper;

    public DatabaseAdapter(Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);
    }

    public Uri insertWord(String word, String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOKENWORDS_WORD, word);
        contentValues.put(TOKENWORDS_URI, uri);
        contentValues.put(TOKENWORDS_DATE, System.currentTimeMillis()/1000);
        contentValues.put(TOKENWORDS_LISTNUMBER, ListName.GENERAL.getId());
        long rowId = databaseHelper.
                getWritableDatabase().
                insert(TOKENWORDS_TABLENAME, null, contentValues);
        return ContentUris.withAppendedId(Uri.parse(uri), rowId);
    }

    public Cursor getUri(String word) {
        return databaseHelper.
                getReadableDatabase().
                query(TOKENWORDS_TABLENAME,
                        new String[]{TOKENWORDS_ID, TOKENWORDS_WORD, TOKENWORDS_URI,TOKENWORDS_DATE,TOKENWORDS_LISTNUMBER},
                        TOKENWORDS_WORD + "=?",
                        new String[]{word},
                        null,
                        null,
                        TOKENWORDS_ID);
    }

    public enum ListName {
        GENERAL("Genaral List", 1),
        USER_DEFINED("My List", 2);

        public final String name;
        public final int id;

        ListName(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }
    }
}

