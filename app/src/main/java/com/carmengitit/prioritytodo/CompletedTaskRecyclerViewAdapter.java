package com.carmengitit.prioritytodo;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.carmengitit.prioritytodo.databinding.FragmentTaskCompletedListBinding;
import com.carmengitit.prioritytodo.model.TaskList.Task;

import java.text.DateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Task}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CompletedTaskRecyclerViewAdapter extends RecyclerView.Adapter<CompletedTaskRecyclerViewAdapter.ViewHolder> {

    private final List<Task> mValues;

    public CompletedTaskRecyclerViewAdapter(List<Task> tasks) {
        mValues = tasks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(FragmentTaskCompletedListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mNameView.setText(mValues.get(position).name);
        holder.mDescriptionView.setText(mValues.get(position).description);
        holder.mPriorityView.setText("Priority: " + mValues.get(position).priority + " | " + mValues.get(position).getValue());
        holder.mDateView.setText("Date due: " + DateFormat.getDateInstance()
                .format(mValues.get(position).dateDue));
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

        public ViewHolder(FragmentTaskCompletedListBinding binding) {
            super(binding.getRoot());
            mNameView = binding.txtCompletedTaskCardName;
            mDescriptionView = binding.txtCompletedTaskCardDescription;
            mPriorityView = binding.txtCompletedTaskCardPriority;
            mDateView = binding.txtCompletedTaskCardDate;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDescriptionView.getText() + "'";
        }
    }
}