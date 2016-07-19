package com.example.piyush.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.piyush.todolist.db.TasksTable;

/**
 * Created by Piyush on 13-07-2016.
 */
public class MyDbOpener extends SQLiteOpenHelper {

    public static final String DB_NAME = "myDatabase";
    public static final int DB_VER = 1;

    public MyDbOpener(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    private static MyDbOpener myDbOpener = null;


    public static SQLiteDatabase openReadableDatabase (Context context){
        if ( myDbOpener == null){
            myDbOpener = new MyDbOpener(context);
        }
        return myDbOpener.getReadableDatabase();
    }

    public static SQLiteDatabase openWritableDatabase (Context context){
        if ( myDbOpener == null){
            myDbOpener = new MyDbOpener(context);
        }
        return myDbOpener.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TasksTable.TABLE_CREATE_CMD);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
