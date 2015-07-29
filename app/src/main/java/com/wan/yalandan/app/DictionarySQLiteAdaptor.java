package com.wan.yalandan.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by abdulkadir.karabas on 28.07.2015.
 */
public class DictionarySQLiteAdaptor {
    private static final String LOG_TAG = "DATABASE";
    private static DictionarySqLiteHelper dictionarySqLiteHelper;
    private SQLiteDatabase dictionaryDb;
    private static final String TOKENWORDS_TABLENAME = "TakenWords";
    private static final String TOKENWORDS_ID = "Id";
    private static final String TOKENWORDS_WORD = "Word";
    private static final String TOKENWORDS_URI = "Uri";

    public DictionarySQLiteAdaptor(Context ctx) {
        dictionarySqLiteHelper = new DictionarySqLiteHelper(ctx);
        dictionaryDb = dictionarySqLiteHelper.getWritableDatabase();
    }

    public boolean insertWord(String word, String uri) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOKENWORDS_WORD, word);
        contentValues.put(TOKENWORDS_URI, uri);
        long result = dictionaryDb.insert(TOKENWORDS_TABLENAME, null, contentValues);
        if (result > 0) {
            Log.i(LOG_TAG, "Row added.");
            return true;
        } else {
            Log.i(LOG_TAG, "Add row has failed.");
            return false;
        }
    }

    public String getUri(String word) {
        Cursor query = dictionaryDb.rawQuery("SELECT * FROM " + TOKENWORDS_TABLENAME + " WHERE " + TOKENWORDS_WORD + " == '" + word + "' Order by Id desc", null);
        if (query.moveToFirst()) {
            return query.getString(2);
        } else {
            return "";
        }
    }
}
