package com.carmengitit.prioritytodo.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.carmengitit.prioritytodo.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
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
    public static boolean initialRequestComplete = false;

    public static void checkUserRegistered() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(MainActivity.TAG, "Checking user registration");

        queryComplete = false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.i(MainActivity.TAG, "User already registered");
                        userRegistered = true;
                    } else {
                        Log.i(MainActivity.TAG, "User is not registered");
                        userRegistered = false;
                    }
                    queryComplete = true;
                }
            });
        }
    }

    public static void registerUser() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (!userRegistered) {
                checkUserRegistered();

                long timeOut = System.currentTimeMillis() + 60000;

                Log.d(MainActivity.TAG, "Attempting to register");
                // Set timeout to 1 minute
//                while(!queryComplete && timeOut > System.currentTimeMillis()) {
//                    // Wait until query is complete
//                }

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
                                    Log.i(MainActivity.TAG, "New user registered");
                                }
                                queryComplete = true;
                            }
                        });
            }
        }
    }

    public static void loadTasks() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && userRegistered) {
            queryComplete = false;

            db.collection("users")
                    .document(user.getUid())
                    .collection("tasks")
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                            Log.i(MainActivity.TAG, "Get all tasks request completed");
                            initialRequestComplete = true;
                            queryComplete = true;
                            if(task.isSuccessful()) {
                                tasks.clear();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Task newTask = new Task((String)document.get("name"),
                                            (String)document.get("description"),
                                            (long) document.get("priority"),
                                            new Date((long)document.get("date")),
                                            (boolean)document.get("complete"));
                                    tasks.add(newTask);
                                }
                                Log.i(MainActivity.TAG, "Tasks loaded");
                                sortTasks();
                            }
                        }
                    });
        }
    }

    private static void dbAddTask(Task new_task) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Map<String, Object> task_map = new HashMap<>();
            task_map.put("complete", new_task.complete);
            task_map.put("date", new_task.dateDue.getTime());
            task_map.put("description", new_task.description);
            task_map.put("name", new_task.name);
            task_map.put("priority", new_task.priority);

            if (tasks.isEmpty() && complete_tasks.isEmpty()) {
                Log.i(MainActivity.TAG, "Empty list creating collection");
                db.collection("users")
                        .document(user.getUid())
                        .collection("tasks")
                        .document("0")
                        .set(task_map)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i(MainActivity.TAG, "Successfully created collection");
                                } else {
                                    Log.e(MainActivity.TAG, "Failed to create collection: " + task.getException());
                                }
                            }
                        });
            }
            else {
                db.collection("users")
                        .document(user.getUid())
                        .collection("tasks")
                        .add(task_map)
                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                            @Override
                            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentReference> task) {
                                if (task.isSuccessful()) {
                                    Log.i(MainActivity.TAG, "New task added");
                                }
                            }
                        });
            }
        }
    }

    public static void removeTask(int index) {
        if (index >= tasks.size()) {
            return;
        }
        Task task = tasks.get(index);
        tasks.remove(index);

        current_index = 0;
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

    private static void sortTasks() {
        // Sort tasks each time a new one is added
        tasks.sort((a, b) -> {
            long aValue = a.getValue();
            long bValue = b.getValue();

            return Long.compare(aValue, bValue);
        });
    }

    public static void addTask(String name, String description, int priority, Date dateDue) {
        Task task = new Task(name, description, priority, dateDue);

        dbAddTask(task);

        tasks.add(task);

        sortTasks();
    }

    public static class Task{
        public String name;
        public String description;
        public long priority;
        public Date dateDue;
        public boolean complete;

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
