package chr.ho.adhdhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final Context context;
    private final TaskListFragment fragment;

    public TaskAdapter(List<Task> taskList, Context context, TaskListFragment fragment) { // Update constructor
        this.taskList = taskList;
        this.context = context;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);

        // Ensure the item view uses wrap_content for height
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        view.setLayoutParams(layoutParams);

        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.taskTitle.setText(task.getTitle());
        holder.taskCheckBox.setChecked(task.isCompleted());

        holder.taskCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                fragment.updateTaskCompletion(adapterPosition, isChecked);
            }
        });

        if (position == taskList.size() - 1 && task.getTitle().contains("clear these instructions")) {
            holder.itemView.setOnClickListener(v -> clearInstructions());
            holder.taskCheckBox.setVisibility(View.GONE); // Hide checkbox for this special task
        } else {
            // Handle task editing on long press for regular tasks
            holder.itemView.setOnLongClickListener(v -> {
                showEditDeleteTaskDialog(holder.getBindingAdapterPosition());
                return true;
            });
            holder.taskCheckBox.setVisibility(View.VISIBLE); // Ensure checkbox is visible for regular tasks
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    private void showEditDeleteTaskDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit or Delete Task");

        final EditText input = new EditText(context);
        input.setText(taskList.get(position).getTitle());
        builder.setView(input);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTaskTitle = input.getText().toString().trim();
            if (!newTaskTitle.isEmpty()) {
                taskList.get(position).setTitle(newTaskTitle);
                notifyItemChanged(position);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.setNeutralButton("Delete", (dialog, which) -> {
            taskList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, taskList.size());
        });

        builder.show();
    }

    // Add this method to clear instructions
    private void clearInstructions() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Clear Instructions");
        builder.setMessage("Are you sure you want to clear these instructions? This action cannot be undone.");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            taskList.clear();
            taskList.add(new Task("Add your first task!", false));
            notifyDataSetChanged();
            fragment.saveTasks();
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskTitle;
        CheckBox taskCheckBox;

        public TaskViewHolder(View itemView) {
            super(itemView);
            taskTitle = itemView.findViewById(R.id.task_title);
            taskCheckBox = itemView.findViewById(R.id.task_checkbox);
        }
    }
}
