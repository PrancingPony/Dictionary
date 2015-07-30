package com.wan.yalandan.app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class DatabaseAdapter {
    private static final String LOG_TAG = "DATABASE";
    private static DatabaseHelper databaseHelper;
    public static final String TOKENWORDS_TABLENAME = "TakenWords";
    public static final String TOKENWORDS_ID = "Id";
    public static final String TOKENWORDS_WORD = "Word";
    public static final String TOKENWORDS_URI = "Uri";

    public DatabaseAdapter(Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);
    }

    public Uri insertWord(String word, String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOKENWORDS_WORD, word);
        contentValues.put(TOKENWORDS_URI, uri);
        long rowId = databaseHelper.
                getWritableDatabase().
                insert(TOKENWORDS_TABLENAME, null, contentValues);
        return ContentUris.withAppendedId(Uri.parse(uri), rowId);
    }

    public String getUri(String word) {
        Cursor query = databaseHelper.
                getReadableDatabase().
                query(TOKENWORDS_TABLENAME,
                        new String[]{TOKENWORDS_ID, TOKENWORDS_WORD, TOKENWORDS_URI},
                        TOKENWORDS_WORD + "=?",
                        new String[]{word},
                        null,
                        null,
                        TOKENWORDS_ID);
        return query.moveToLast() ? query.getString(2) : "";
    }
}
