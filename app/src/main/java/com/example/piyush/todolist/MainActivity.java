package com.example.piyush.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.piyush.todolist.Models.Task;
import com.example.piyush.todolist.db.TasksTable;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText taskET;
    static Button dateET;
    Button addBtn, delBtn;
    RecyclerView listRV;

    // TODO 1.Sort Tasks according to date 2.Improve Delete

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        taskET = (EditText) findViewById(R.id.task_et);
        dateET = (Button) findViewById(R.id.date_et);
        addBtn = (Button) findViewById(R.id.add_btn);
        delBtn = (Button) findViewById(R.id.delete_btn);
        listRV = (RecyclerView) findViewById(R.id.list_rv);

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        updateRV();
    }

    // gets tasks from the db sorted according to the deadline date and puts them in an array list
    public ArrayList<Task> getTasksListFromDB() {

        ArrayList<Task> tasksList = new ArrayList<>();

        SQLiteDatabase myDatabase = MyDbOpener.openReadableDatabase(this);
        String[] projections = {TasksTable.Columns._ID, TasksTable.Columns.taskName, TasksTable.Columns.deadLine, TasksTable.Columns.isDone};
        Cursor c = myDatabase.query(TasksTable.TABLE_NAME, projections, null, null, null, null, TasksTable.Columns.deadLine);

        while (c.moveToNext()) {
            boolean isDone;
            if (c.getInt(c.getColumnIndex(TasksTable.Columns.isDone)) == TasksTable.TASK_NOT_DONE) {
                isDone = false;
            } else {
                isDone = true;
            }
            String name = c.getString(c.getColumnIndex(TasksTable.Columns.taskName));
            String dl = c.getString(c.getColumnIndex(TasksTable.Columns.deadLine));
            int id = c.getInt(c.getColumnIndex(TasksTable.Columns._ID));
            Task currTask = new Task(isDone, dl, name, id);
            tasksList.add(currTask);
        }
        c.close();
        return tasksList;
    }

    public void deleteCompletedTasks(View view) {
        // There are two ways this can be done
        // 1) get each task in a ArrayList and check isDone of each task
        // 2) (More Efficient) while querying tasks from db query only those tasks whose isDone is true
        ArrayList<Integer> taskIDList = getCompletedTaskIDFromDB();

        SQLiteDatabase myDatabase = MyDbOpener.openWritableDatabase(this);
        for (int i = 0; i < taskIDList.size(); i++) {
            myDatabase.delete(TasksTable.TABLE_NAME, TasksTable.Columns._ID + " = " + taskIDList.get(i),null);
        }
        updateRV();
    }

    // return an array list of ids of tasks whose isDone is true in the database
    public ArrayList<Integer> getCompletedTaskIDFromDB() {
        ArrayList<Integer> completedTasksID = new ArrayList<>();

        SQLiteDatabase myDatabase = MyDbOpener.openReadableDatabase(this);
        // get taskID only
        String[] projections = {TasksTable.Columns._ID};
        Cursor c = myDatabase.query(TasksTable.TABLE_NAME,projections,
                TasksTable.Columns.isDone + " = " + TasksTable.TASK_DONE,
                null,null,null,null);

        while (c.moveToNext()){
            completedTasksID.add(c.getInt(c.getColumnIndex(TasksTable.Columns._ID)));
        }
        c.close();
        return completedTasksID;
    }

    // sets isDone = true in the database for the task whose id is sent into the function
    public void setDone(int id) {
        Log.d(TAG, "setDone: " + id);
        SQLiteDatabase db = MyDbOpener.openWritableDatabase(this);

        ContentValues value = new ContentValues();
        value.put(TasksTable.Columns.isDone, TasksTable.TASK_DONE);

        db.update(TasksTable.TABLE_NAME, value, TasksTable.Columns._ID + " = " + id, null);

        updateRV();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView taskName;
        TextView taskDate;
        int id;

        public TaskViewHolder(View itemView) {
            super(itemView);

            taskName = (TextView) itemView.findViewById(R.id.task_name_et);
            taskDate = (TextView) itemView.findViewById(R.id.deadline_et);
        }
    }

    public class TaskListAdapter extends RecyclerView.Adapter<TaskViewHolder> {

        ArrayList<Task> tasksList = getTasksListFromDB();

        @Override
        public TaskViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

            LayoutInflater layoutInflater = getLayoutInflater();
            final View itemView = layoutInflater.inflate(R.layout.list_item, null);
            final TaskViewHolder tvh = new TaskViewHolder(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int id = tvh.id;
                    setDone(id);
                }
            });
            return tvh;
        }

        @Override
        public void onBindViewHolder(TaskViewHolder holder, int position) {
            holder.taskName.setText(tasksList.get(position).getTaskName());
            holder.taskDate.setText(tasksList.get(position).getDeadline());
            holder.id = tasksList.get(position).getTaskID();
            if(tasksList.get(position).isDone()){
                holder.taskDate.setTextColor(getColor(R.color.colorAccent));
                holder.taskName.setTextColor(getColor(R.color.colorAccent));
            }
        }

        @Override
        public int getItemCount() {
            return tasksList.size();
        }
    }

    // adds a task to the database
    public void addTask(View view) {
        // DB
        SQLiteDatabase myDatabase = MyDbOpener.openWritableDatabase(this);

        ContentValues values = new ContentValues();

        if (taskET.getText() == null || taskET.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Please Enter Task to be done and then press the Add button", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dateET.getText() == null || dateET.getText().toString().equals("")) {
            Toast.makeText(MainActivity.this, "Please the deadline and then press the Add button", Toast.LENGTH_SHORT).show();
            return;
        }

        values.put(TasksTable.Columns.taskName, String.valueOf(taskET.getText()));

        values.put(TasksTable.Columns.deadLine, String.valueOf(dateET.getText()));

        values.put(TasksTable.Columns.isDone, TasksTable.TASK_NOT_DONE);

        myDatabase.insert(TasksTable.TABLE_NAME, null, values);

        // RecyclerView
        updateRV();

        dateET.setText("");
        taskET.setText("");
    }

    // called whenever any change is made
    // basically makes the recycler view again
    private void updateRV() {
        TaskListAdapter taskListAdapter = new TaskListAdapter();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listRV.setLayoutManager(layoutManager);
        listRV.setAdapter(taskListAdapter);
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            // Checks that the entered date is in the future
            if (year < c.get(Calendar.YEAR)) {
                showToast(getActivity().getApplicationContext(),"Please Enter A Valid Date");
                return;
            } else if (year == c.get(Calendar.YEAR)) {
                if (month < c.get(Calendar.MONTH)) {
                    showToast(getActivity().getApplicationContext(),"Please Enter A Valid Date");
                    return;
                } else if (month == c.get(Calendar.MONTH)) {
                    if (day < c.get(Calendar.DAY_OF_MONTH)) {
                        showToast(getActivity().getApplicationContext(),"Please Enter A Valid Date");
                        return;
                    }
                }
            }
            dateET.setText(day+"/"+month+"/"+year);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public static void showToast(Context context, String text) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }
}
