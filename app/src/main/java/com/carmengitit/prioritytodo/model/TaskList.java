package com.carmengitit.prioritytodo.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carmengitit.prioritytodo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskList {
    public static final List<Task> tasks = new ArrayList<Task>();
    public static final List<Task> complete_tasks = new ArrayList<Task>();

    private static int current_index = 0;

    public static boolean userRegistered = false;
    public static boolean queryComplete = false;
    public static boolean initialRequestStarted = false;
    public static boolean initialRequestCompleted = false;

    public static void registerUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(MainActivity.TAG, "Attempting to register user");

            queryComplete = false;
            Map<String, Object> name = new HashMap<>();
            name.put("name", user.getDisplayName());
            db.collection("users")
                    .document(user.getUid())
                    .set(name)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                userRegistered = true;
                                Log.i(MainActivity.TAG, "User registered");
                            }
                            queryComplete = true;
                        }
                    });
        }
    }

    public static void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && userRegistered) {
            queryComplete = false;
            initialRequestStarted = true;

            db.collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            Log.i(MainActivity.TAG, "Get all tasks request completed");
                            queryComplete = true;
                            if(task.isSuccessful()) {
                                tasks.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    Task newTask = new Task((String)document.get("name"),
                                            (String)document.get("description"),
                                            (long) document.get("priority"),
                                            new Date((long)document.get("date")),
                                            (boolean)document.get("complete"));
                                    newTask.uid = document.getId();
                                    if (!(boolean) document.get("complete")) {
                                        tasks.add(newTask);
                                    } else {
                                        complete_tasks.add(newTask);
                                    }

                                }
                                Log.i(MainActivity.TAG, "Tasks loaded");
                                sortTasks();
                                initialRequestCompleted = true;
                            }
                        }
                    });
        } else {
            Log.i(MainActivity.TAG, "Delaying task request while user is registered");
        }
    }

    private static void dbAddTask(Task new_task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            queryComplete = false;
            Map<String, Object> task_map = new HashMap<>();
            task_map.put("complete", new_task.complete);
            task_map.put("date", new_task.dateDue.getTime());
            task_map.put("description", new_task.description);
            task_map.put("name", new_task.name);
            task_map.put("priority", new_task.priority);

            db.collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .add(task_map)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                new_task.uid = task.getResult().getId();
                                tasks.add(new_task);
                                sortTasks();
                                Log.i(MainActivity.TAG, "Successfully created collection");
                            } else {
                                Log.e(MainActivity.TAG, "Failed to create collection: " + task.getException());
                            }
                            queryComplete = true;
                        }
                    });
        }
    }

    private static void dbEditTask(Task task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.i(MainActivity.TAG, "Editing task");

        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .document(task.uid)
                    .update("complete", task.complete,
                            "date", task.dateDue.getTime(),
                            "description", task.description,
                            "name", task.name,
                            "priority", task.priority)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(MainActivity.TAG, "Task successfully edited.");
                            } else {
                                Log.d(MainActivity.TAG, "Failed to update task");
                            }
                        }
                    });
        }
    }

    private static void dbRemoveTask(Task removed_task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Log.i(MainActivity.TAG, "Removing task");

        if (user != null) {
            db.collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .document(removed_task.uid)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i(MainActivity.TAG, "Task successfully deleted");
                            } else {
                                Log.i(MainActivity.TAG, "Task delete failed");
                            }
                        }
                    });
        }
    }

    public static void editTask(int index, Task task) {
        if (index >= 0 && index < tasks.size() && task != null) {
            // Make the change locally
            if (task.complete) {
                tasks.remove(index);
                complete_tasks.add(task);
            } else {
                //tasks.set(index, task);
                sortTasks();
            }

            dbEditTask(task);
        }
    }


    // TODO: Set this up with database
    public static void removeTask(int index) {
        if (index >= tasks.size()) {
            return;
        }
        Task task = tasks.get(index);
        tasks.remove(index);

        if (current_index >= tasks.size()) {
            current_index = tasks.size() - 1;
        }

        dbRemoveTask(task);
    }

    public static Task getNextTask() {
        if (tasks.isEmpty()) {
            return null;
        }

        return tasks.get(current_index);
    }

    public static Task getTask(int index) {
        if (tasks.isEmpty() || index >= tasks.size()) {
            return null;
        }

        return tasks.get(index);
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
        // Local changes
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

        // Push change to db
        dbEditTask(task);
    }

    // TODO: set this up with db
    public static void completeTask(int index) {
        // Local changes
        Task task = tasks.get(index);
        task.complete = true;
        complete_tasks.add(task);
        tasks.remove(index);
        if (current_index >= tasks.size()) {
            current_index = tasks.size() - 1;
        }

        // Push change to db
        dbEditTask(task);
    }

    public static void resetIndex() {
        current_index = 0;
    }

    private static void sortTasks() {
        // Sort tasks each time a new one is added
        tasks.sort((a, b) -> {
            long aValue = a.getValue();
            long bValue = b.getValue();

            return bValue == aValue ?
                    Long.compare(a.dateDue.getTime(), b.dateDue.getTime()) :
                    Long.compare(bValue, aValue);
        });
    }

    public static void addTask(String name, String description, int priority, Date dateDue) {
        Task task = new Task(name, description, priority, dateDue);

        dbAddTask(task);
    }

    public static class Task{
        public String name;
        public String description;
        public long priority;
        public Date dateDue;
        public boolean complete;
        public String uid;

        public Task(String name, String description, long priority, Date dateDue) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.dateDue = dateDue;
            this.complete = false;
        }

        public Task(String name, String description, long priority, Date dateDue, boolean complete) {
            this.name = name;
            this.description = description;
            this.priority = priority;
            this.dateDue = dateDue;
            this.complete = complete;

        }

        @Override
        public String toString() {
            return this.name + " " + this.description;
        }

        public int getDaysLeft() {
            return (int) Math.ceil(((double) (this.dateDue.getTime() -
                    new Date().getTime())/ (1000*60*60*24)));
        }

        public long getValue() {
            int daysLeft = getDaysLeft();
            long value;

            if (daysLeft > 0) {
                value = (((priority + 1)^3) * 1000) / daysLeft;
            }
            else {
                value = ((priority + 1)^2) * 10000 + (long) (-20000) * daysLeft;
            }

            return value;
        }
    }
}
