package com.carmengitit.prioritytodo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.carmengitit.prioritytodo.model.TaskList;
import com.carmengitit.prioritytodo.databinding.FragmentAddTaskBinding;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.slider.Slider;

import java.text.DateFormat;
import java.util.Date;

public class AddTaskFragment extends Fragment {

    private FragmentAddTaskBinding binding;

    private int priority = 0;
    private Date date = new Date();

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentAddTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Due Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                date = new Date(selection);
                binding.etAddTaskDate.setText(DateFormat.getDateInstance().format(date));
            }
        });

        binding.btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        binding.sliderAddTaskPriority.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                priority = (int) value;
            }
        });

        binding.etAddTaskDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    picker.show(getActivity().getSupportFragmentManager(), "datepicker");
                } catch (NullPointerException e) {
                    System.out.print(e.getMessage());
                    binding.etAddTaskDate.setError("Unknown error with datepicker");
                }
            }
        });

        binding.etAddTaskDate.setText(DateFormat.getDateInstance().format(date));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void addTask() {
        String name = "";
        String description = "";

        binding.textlayoutAddTaskName.setError(null);
        binding.textlayoutAddTaskDescr.setError(null);

        try {
            name = binding.etAddTaskName.getText().toString();
        } catch(NullPointerException e) {
            System.out.print(e.toString());
        }

        try {
            description = binding.etAddTaskDescr.getText().toString();
        } catch(NullPointerException e){
            System.out.print(e.getMessage());
            binding.etAddTaskDescr.setError("Invalid Description");
            return;
        }

        if (name.isEmpty()) {
            binding.textlayoutAddTaskName.setError("Name Cannot Be Empty");
            return;
        }

        if (name.length() > 25) {
            binding.textlayoutAddTaskName.setError("Name Too Long");
            return;
        }

        if (description.isEmpty()) {
            binding.textlayoutAddTaskDescr.setError("Description Cannot Be Empty");
            return;
        }


        TaskList.Task task = new TaskList.Task(name, description, priority, date);

        TaskList.tasks.add(task);

        NavHostFragment.findNavController(AddTaskFragment.this)
                .navigateUp();
    }

}