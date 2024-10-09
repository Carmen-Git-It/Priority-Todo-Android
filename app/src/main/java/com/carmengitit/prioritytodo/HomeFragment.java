package com.carmengitit.prioritytodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.carmengitit.prioritytodo.databinding.FragmentHomeBinding;
import com.carmengitit.prioritytodo.model.TaskList;

import java.text.DateFormat;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

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
        TaskList.Task task = TaskList.getNextTask();

        if (task == null) {
            binding.textHomeTaskName.setText("No Tasks In List!");
            binding.textHomeTaskPriority.setText("");
            binding.textHomeTaskDescription.setText("");
            binding.textHomeTaskDate.setText("");
        } else {
            binding.textHomeTaskName.setText(task.name);
            binding.textHomeTaskDate.setText("Due date: " + DateFormat.getDateInstance().format(task.dateDue));
            binding.textHomeTaskDescription.setText(task.description);
            binding.textHomeTaskPriority.setText("Priority: " + String.valueOf(task.priority));
        }

        binding.fabHome.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}