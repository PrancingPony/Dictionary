package com.wan.yalandan.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by abdulkadir.karabas on 28.07.2015.
 */
public class DictionarySqLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "DictionaryDb";
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS TakenWords(" +
            "Id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "Word            CHAR(50)    NOT NULL," +
            "Uri             CHAR(256)   NOT NULL)";

    public DictionarySqLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(DictionarySqLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS TakenWords");
        onCreate(sqLiteDatabase);
    }

}
