package com.carmengitit.prioritytodo;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.carmengitit.prioritytodo.databinding.FragmentHomeBinding;
import com.carmengitit.prioritytodo.model.TaskList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Handler handler;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnHomeTaskCardSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskList.skipTask();
                setTask();
            }
        });

        binding.btnHomeTaskCardComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TaskList.completeTask();
                setTask();
            }
        });

        binding.fabHome.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            binding.txtHomeWelcome.setText("Welcome " + user.getDisplayName());
        }

        setTask();

        if (TaskList.tasks.isEmpty() && !TaskList.initialRequestCompleted) {
            handler = new Handler();
            updateUI.run();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (TaskList.tasks.isEmpty()) {
            updateUI.run();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateUI);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateUI);
    }

    Runnable updateUI = new Runnable() {
        @Override
        public void run() {
            try {
                setName();
                setTask();
                if (!TaskList.initialRequestStarted) {
                    TaskList.loadTasks();
                }
            } finally {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (!TaskList.initialRequestCompleted || user == null){
                    handler.postDelayed(updateUI, 300);
                }
            }
        }
    };

    private void setName() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null && user.getDisplayName() != null && !user.getDisplayName().isEmpty()) {
            binding.txtHomeWelcome.setText("Welcome " + user.getDisplayName());
        }
    }

    private void setTask() {
        TaskList.Task task = TaskList.getNextTask();

        if (task == null) {
            binding.textHomeTaskName.setText("No Tasks In List!");
            binding.textHomeTaskPriority.setText("");
            binding.textHomeTaskDescription.setText("");
            binding.textHomeTaskDate.setText("");
            binding.btnHomeTaskCardComplete.setVisibility(View.INVISIBLE);
            binding.btnHomeTaskCardSkip.setVisibility(View.INVISIBLE);
        } else {
            binding.textHomeTaskName.setText(task.name);
            binding.textHomeTaskDate.setText("Due date: " + DateFormat.getDateInstance().format(task.dateDue));
            binding.textHomeTaskDescription.setText(task.description);
            binding.textHomeTaskPriority.setText("Priority: " + String.valueOf(task.priority));
            binding.btnHomeTaskCardComplete.setVisibility(View.VISIBLE);
            binding.btnHomeTaskCardSkip.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}