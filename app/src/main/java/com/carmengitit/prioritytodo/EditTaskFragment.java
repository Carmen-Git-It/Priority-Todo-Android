package com.carmengitit.prioritytodo;

import android.icu.util.Calendar;
import android.icu.util.TimeZone;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carmengitit.prioritytodo.databinding.FragmentEditTaskBinding;
import com.carmengitit.prioritytodo.databinding.FragmentHomeBinding;
import com.carmengitit.prioritytodo.model.TaskList;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.text.DateFormat;
import java.util.Date;

public class EditTaskFragment extends Fragment {

    private FragmentEditTaskBinding binding;
    private int index;
    private Date date;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentEditTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        index = getArguments().getInt("index");

        TaskList.Task task = TaskList.getTask(index);

        if (task != null) {
            date = task.dateDue;

            // Set elements to task values
            binding.etEditTaskName.setText(task.name);
            binding.etEditTaskDescr.setText(task.description);
            binding.etEditTaskDate.setText(DateFormat.getDateInstance().format(task.dateDue));
            binding.sliderEditTaskPriority.setValue(task.priority);
            binding.switchEditTaskCompleted.setChecked(task.complete);

            MaterialDatePicker<Long> picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select Due Date")
                    .setSelection(date.getTime()).build();
            picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                @Override
                public void onPositiveButtonClick(Long selection) {
                    Calendar utc = Calendar.getInstance(TimeZone.getDefault());
                    utc.setTimeInMillis(selection);
                    utc.add(Calendar.DATE, 1);
                    date = utc.getTime();
                    binding.etEditTaskDate.setText(DateFormat.getDateInstance().format(date));
                }
            });

            binding.etEditTaskDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        picker.show(getActivity().getSupportFragmentManager(), "datepicker");
                    } catch (NullPointerException e) {
                        System.out.print(e.getMessage());
                        binding.etEditTaskDate.setError("Unknown error with datepicker");
                    }
                }
            });

            binding.btnEditTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.textlayoutEditTaskName.setError(null);
                    binding.textlayoutEditTaskDate.setError(null);

                    if (binding.etEditTaskName.getText() == null
                    || binding.etEditTaskName.getText().toString().isEmpty()) {
                        binding.textlayoutEditTaskName
                                .setError("Error, task names must not be blank.");
                        return;
                    }

                    if (binding.etEditTaskDescr.getText() == null) {
                        return;
                    }

                    task.name = binding.etEditTaskName.getText().toString();
                    task.description = binding.etEditTaskDescr.getText().toString();
                    task.dateDue = date;
                    task.priority = (int) binding.sliderEditTaskPriority.getValue();
                    task.complete = binding.switchEditTaskCompleted.isChecked();

                    // TODO: consider making this safer by reworking it to use task UID instead of index
                    TaskList.editTask(index, task);

                    // Go back to previous task
                    NavHostFragment.findNavController(EditTaskFragment.this)
                            .navigateUp();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}