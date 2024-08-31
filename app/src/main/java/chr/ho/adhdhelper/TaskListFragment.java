package chr.ho.adhdhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private static final String PREFS_NAME = "TaskListPrefs";
    private static final String TASKS_KEY = "tasks";
    private static final String FIRST_RUN_KEY = "isFirstRun";
    private static final String DEFAULT_TASKS_SHOWN_KEY = "defaultTasksShown";

    private List<Task> taskList;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Add item decoration for consistent spacing
        int verticalSpacingInDp = getResources().getDimensionPixelSize(R.dimen.item_vertical_spacing);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(verticalSpacingInDp));

        taskList = loadTasks();
        if (taskList.isEmpty()) {
            taskList = createDefaultTasks();
            saveTasks();
        }

        adapter = new TaskAdapter(taskList, getContext(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.add_new_task);

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_task, null);
        final EditText input = dialogView.findViewById(R.id.edit_text_task);
        builder.setView(dialogView);

        builder.setPositiveButton(R.string.add, (dialog, which) -> {
            String taskTitle = input.getText().toString();
            if (!taskTitle.isEmpty()) {
                taskList.add(new Task(taskTitle, false)); // Here, 'false' indicates that the task is not completed when added
                adapter.notifyItemInserted(taskList.size() - 1);
                saveTasks();
            }
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    public void saveTasks() {
        saveTasks(taskList);
    }

    private void saveTasks(List<Task> tasks) {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(TASKS_KEY, json);
        editor.apply();
    }

    private List<Task> loadTasks() {
        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean defaultTasksShown = prefs.getBoolean(DEFAULT_TASKS_SHOWN_KEY, false);

        if (!defaultTasksShown) {
            List<Task> defaultTasks = createDefaultTasks();
            prefs.edit().putBoolean(DEFAULT_TASKS_SHOWN_KEY, true).apply();
            saveTasks(defaultTasks);
            return defaultTasks;
        } else {
            Gson gson = new Gson();
            String json = prefs.getString(TASKS_KEY, null);
            Type type = new TypeToken<ArrayList<Task>>() {}.getType();
            List<Task> loadedTasks = gson.fromJson(json, type);
            return loadedTasks != null ? loadedTasks : new ArrayList<>();
        }
    }

    private List<Task> createDefaultTasks() {
        List<Task> defaultTasks = new ArrayList<>();
        defaultTasks.add(new Task("Welcome to the ADHD Helper App! Here's how to use it:", false));
        defaultTasks.add(new Task("Add tasks by tapping the + button", false));
        defaultTasks.add(new Task("Tap the box to mark it as complete", false));
        defaultTasks.add(new Task("Long-press a task to edit or delete it", false));
        defaultTasks.add(new Task("Use the Focus Timer to stay on track", false));
        defaultTasks.add(new Task("Check Resources for helpful info about ADHD", false));
        defaultTasks.add(new Task("Tap here to clear these instructions", false));
        return defaultTasks;
    }

    // Add this method to update and save a task's completion status
    public void updateTaskCompletion(int position, boolean isCompleted) {
        if (position >= 0 && position < taskList.size()) {
            Task task = taskList.get(position);
            task.setCompleted(isCompleted);
            saveTasks();
        }
    }
}
