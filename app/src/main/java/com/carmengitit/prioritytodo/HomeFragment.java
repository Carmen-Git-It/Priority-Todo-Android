package com.carmengitit.prioritytodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.carmengitit.prioritytodo.databinding.FragmentHomeBinding;
import com.carmengitit.prioritytodo.databinding.FragmentAddTaskBinding;
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

        // TEMPORARY SEEDING
        TaskList.Task t = new TaskList.Task("Laundry", "Do a couple loads of " +
                "laundry, make sure to use fabric softener this time", 4, new Date());
        TaskList.tasks.add(t);

        TaskList.Task task = TaskList.getNextTask();

        binding.textHomeTaskName.setText(task.title);
        binding.textHomeTaskDate.setText(task.dateDue.toString());
        binding.textHomeTaskDescription.setText(task.description);
        binding.textHomeTaskPriority.setText(String.valueOf(task.priority));

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