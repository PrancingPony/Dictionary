package com.wan.yalandan.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "DictionaryDb";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + DataStore.TOKENWORDS_TABLENAME + "(" +
            DataStore.TOKENWORDS_ID + "   INTEGER     PRIMARY KEY AUTOINCREMENT," +
            DataStore.TOKENWORDS_WORD + " CHAR(50)    NOT NULL," +
            DataStore.TOKENWORDS_URI + "  CHAR(256)   NOT NULL," +
            DataStore.TOKENWORDS_DATE + " DATE        NOT NULL," +
            DataStore.TOKENWORDS_LISTNUMBER + "  INTEGER     NOT NULL)";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DatabaseHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DataStore.TOKENWORDS_TABLENAME);
        onCreate(sqLiteDatabase);
    }

}
