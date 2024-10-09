package com.carmengitit.prioritytodo.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskList {
    public static final List<Task> tasks = new ArrayList<Task>();
    public static final List<Task> complete_tasks = new ArrayList<Task>();

    private static int current_index = 0;

    public static boolean removeTask(int index) {
        if (index >= tasks.size()) {
            return false;
        }
        Task task = tasks.get(index);
        tasks.remove(index);

        current_index = 0;
        return true;
    }

    public static Task getNextTask() {
        if (tasks.isEmpty()) {
            return null;
        }

        return tasks.get(current_index);
    }

    public static void skipTask() {
        if (tasks.isEmpty()) {
            return;
        }
        current_index++;
        if (current_index >= tasks.size()) {
            current_index = 0;
        }
    }

    public static void completeTask() {
        if (tasks.isEmpty()) {
            return;
        }
        Task task = tasks.get(current_index);
        task.complete = true;
        complete_tasks.add(task);
        tasks.remove(current_index);
        if (current_index >= tasks.size() && !tasks.isEmpty()) {
            current_index = tasks.size() - 1;
        }
    }

    public static void completeTask(int index) {
        Task task = tasks.get(index);
        task.complete = true;
        complete_tasks.add(task);
        tasks.remove(index);
        if (current_index >= tasks.size()) {
            current_index = tasks.size() - 1;
        }
    }

    public static void resetIndex() {
        current_index = 0;
    }

    public static void addTask(String name, String description, int priority, Date dateDue) {
        Task task = new Task(name, description, priority, dateDue);

        tasks.add(task);

        // Sort tasks each time a new one is added
        tasks.sort((a, b) -> {
            int aValue = a.getValue();
            int bValue = b.getValue();

            return Integer.compare(aValue, bValue);
        });
    }

    public static class Task{
        public String name;
        public String description;
        public int priority;
        public Date dateDue;
        public boolean complete;

        public Task(String name, String description, int priority, Date dateDue) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.dateDue = dateDue;
            this.complete = false;
        }

        @Override
        public String toString() {
            return this.name + " " + this.description;
        }

        public int getDaysLeft() {
            return (int) Math.ceil(((double) (this.dateDue.getTime() -
                    new Date().getTime())/ (1000*60*60*24)));
        }

        public int getValue() {
            int daysLeft = getDaysLeft();
            int value;

            Log.i("TODO", String.valueOf(daysLeft));

            if (daysLeft > 0) {
                value = (((priority + 1)^3) * 1000) / daysLeft;
            }
            else {
                Log.i("TODO", "Past due / due today");
                value = ((priority + 1)^2) * 10000;
            }

            return value;
        }
    }
}
