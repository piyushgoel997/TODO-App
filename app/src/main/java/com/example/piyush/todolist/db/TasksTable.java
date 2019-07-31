package com.example.piyush.todolist.db;

import android.provider.BaseColumns;

/**
 * Created by Piyush on 13-07-2016.
 */
public class TasksTable extends SQLStrings {

    public static final String TABLE_NAME = "tasks";
    public static final int TASK_DONE = 0;
    public static final int TASK_NOT_DONE = -1;

    public interface Columns{
        String taskName = "TASK_NAME";
        String deadLine = "DEADLINE";
        String isDone = "IS_DONE";
        String _ID = "ID";
    }

    public static final String TABLE_CREATE_CMD =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + LBR
            + Columns._ID + TYPE_INT_PK + " AUTOINCREMENT " + COMMA
            + Columns.taskName + TYPE_TEXT + COMMA
            + Columns.deadLine + TYPE_TEXT +COMMA
            + Columns.isDone + TYPE_INT
            + RBR
            + SEMI_COLON ;

}
