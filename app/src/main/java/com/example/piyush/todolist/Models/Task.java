package com.example.piyush.todolist.Models;

/**
 * Created by Piyush on 13-07-2016.
 */
public class Task {
    private int taskID;
    private String taskName;
    private String deadline;
    private boolean isDone;

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public Task(boolean isDone, String deadline, String task, int taskID) {
        this.isDone = isDone;
        this.deadline = deadline;
        this.taskName = task;
        this.taskID = taskID;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }
}
