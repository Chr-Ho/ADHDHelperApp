package chr.ho.adhdhelper;

import android.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private List<Task> taskList;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        taskList = new ArrayList<>();
        taskList.add(new Task("Add your first task!", false));


        adapter = new TaskAdapter(taskList, getContext());  // Pass context here
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fab_add_task);
        fab.setOnClickListener(v -> showAddTaskDialog());

        return view;
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add New Task");

        final EditText input = new EditText(getContext());
        input.setHint("Task Title");
        builder.setView(input);

        builder.setPositiveButton("Add", (dialog, which) -> {
            String taskTitle = input.getText().toString();
            if (!taskTitle.isEmpty()) {
                taskList.add(new Task(taskTitle, false)); // Here, 'false' indicates that the task is not completed when added
                adapter.notifyItemInserted(taskList.size() - 1);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
