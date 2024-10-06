package com.carmengitit.prioritytodo.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskList {

    public static final List<Task> tasks = new ArrayList<Task>();



    public static Task getNextTask() {
        Task priority = tasks.get(0);

        for (Task task : tasks) {

        }

        return priority;
    }

    public static class Task{
        public String title;
        public String description;
        public int priority;
        public Date dateDue;

        public Task(String title, String description, int priority, Date dateDue) {
            this.title = title;
            this.description = description;
            this.priority = priority;
            this.dateDue = dateDue;
        }

        @Override
        public String toString() {
            return this.title + " " + this.description;
        }
    }
}
