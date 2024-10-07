package com.carmengitit.prioritytodo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.carmengitit.prioritytodo.databinding.FragmentHomeBinding;
import com.carmengitit.prioritytodo.model.TaskList;

import java.util.Date;

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

        Log.i("TODO", "load");

        if (task == null) {
            binding.textHomeTaskName.setText("No Tasks In List!");
        } else {
            binding.textHomeTaskName.setText(task.name);
            binding.textHomeTaskDate.setText(task.dateDue.toString());
            binding.textHomeTaskDescription.setText(task.description);
            binding.textHomeTaskPriority.setText(String.valueOf(task.priority));
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