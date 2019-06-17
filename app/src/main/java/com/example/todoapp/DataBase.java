package com.example.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    private static final String db_name = "appDB";
    private static final int db_ver = 1;
    private static final String db_table = "tasks";
    private static final String db_column = "taskName";

//    appDB -> tasks
//
//    id tasksName;
//    1. Набыць малако;
//    2. Памыць посуд;

    public DataBase(Context context) {
        super(context, db_name, null, db_ver);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, %s TEXT NOT NULL);",
                db_table, db_column);
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s", db_table);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertData(String task){
        SQLiteDatabase dB = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db_column, task);
        dB.insertWithOnConflict(db_table, null,
                values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public void deleteData(String task){
        SQLiteDatabase dB = this.getWritableDatabase();
        dB.delete(db_table, db_column + " = ?", new String[]{task});
        dB.close();
    }

    public ArrayList<String> getAllTasks() {
        ArrayList<String> allTasks = new ArrayList<>();
        SQLiteDatabase dB = this.getReadableDatabase();
        Cursor cursor = dB.query(db_table, new String[]{db_column},
                null, null, null, null, null);
        while(cursor.moveToNext()){
            int index = cursor.getColumnIndex(db_column);
            allTasks.add(cursor.getString(index));
        }
        cursor.close();
        dB.close();
        return allTasks;
    }

}
