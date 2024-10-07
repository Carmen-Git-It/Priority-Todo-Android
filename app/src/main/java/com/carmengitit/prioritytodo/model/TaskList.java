package com.carmengitit.prioritytodo.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskList {
    public static final List<Task> tasks = new ArrayList<Task>();

    public boolean removeTask(int index) {
        // TODO: remove the task at the given index, return true if doable

        return false;
    }

    public static Task getNextTask() {
        if (tasks.isEmpty()) {
            return null;
        }

        return tasks.get(0);
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

        public Task(String name, String description, int priority, Date dateDue) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.dateDue = dateDue;
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
                value = ((priority + 1)^2) * 10000;
            }

            return value;
        }
    }
}
