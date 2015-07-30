package com.wan.yalandan.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class DatabaseAdaptor {
    private static final String LOG_TAG = "DATABASE";
    private static DatabaseHelper databaseHelper;
    public static final String TOKENWORDS_TABLENAME = "TakenWords";
    public static final String TOKENWORDS_ID = "Id";
    public static final String TOKENWORDS_WORD = "Word";
    public static final String TOKENWORDS_URI = "Uri";

    public DatabaseAdaptor(Context ctx) {
        databaseHelper = new DatabaseHelper(ctx);
    }

    public long insertWord(String word, String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOKENWORDS_WORD, word);
        contentValues.put(TOKENWORDS_URI, uri);
        return databaseHelper.
                getWritableDatabase().
                insert(TOKENWORDS_TABLENAME, null, contentValues);
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
