package com.carmengitit.prioritytodo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carmengitit.prioritytodo.databinding.FragmentTaskListBinding;
import com.carmengitit.prioritytodo.model.TaskList;
import com.carmengitit.prioritytodo.model.TaskList.Task;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TaskRecyclerViewAdapter extends RecyclerView.Adapter<TaskRecyclerViewAdapter.ViewHolder> {

    private final List<Task> mValues;

    public TaskRecyclerViewAdapter(List<Task> tasks) {
        mValues = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentTaskListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mNameView.setText(mValues.get(position).name);
        holder.mDescriptionView.setText(mValues.get(position).description);
        holder.mPriorityView.setText("Priority: " + mValues.get(position).priority);
        holder.mDateView.setText("Date due: " + DateFormat.getDateInstance()
                .format(mValues.get(position).dateDue));
        holder.mDeleteButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currPos = holder.getBindingAdapterPosition();
                TaskList.removeTask(currPos);
                notifyItemRemoved(currPos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mNameView;
        public final TextView mDescriptionView;
        public final TextView mPriorityView;
        public final TextView mDateView;
        public final MaterialButton mDeleteButtonView;
        public Task mItem;

        public ViewHolder(FragmentTaskListBinding binding) {
            super(binding.getRoot());
            mNameView = binding.txtTaskCardName;
            mDescriptionView = binding.txtTaskCardDescription;
            mPriorityView = binding.txtTaskCardPriority;
            mDateView = binding.txtTaskCardDate;
            mDeleteButtonView = binding.btnTaskCardDelete;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }
}