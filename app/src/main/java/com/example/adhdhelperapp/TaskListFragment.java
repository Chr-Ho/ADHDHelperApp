package com.example.adhdhelperapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private List<Task> taskList;
    private RecyclerView recyclerView;
    private TaskAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);

        // Set LayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize task list
        taskList = new ArrayList<>();
        taskList.add(new Task("Task 1"));
        taskList.add(new Task("Task 2"));
        // Add more tasks as needed

        // Set Adapter
        adapter = new TaskAdapter(taskList);
        recyclerView.setAdapter(adapter);

        return view;
    }
}