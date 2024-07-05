package com.example.todolist.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

import com.example.todolist.AddTaskActivity;
import com.example.todolist.MainActivity;
import com.example.todolist.Model.ToDoModel;
import com.example.todolist.R;
import com.example.todolist.Utils.DatabaseHandler;

import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {

    private List<ToDoModel> todoList;
    private MainActivity activity;
    private DatabaseHandler db;
    private ActivityResultLauncher<Intent> editTaskLauncher;

    public ToDoAdapter(DatabaseHandler db, MainActivity activity, ActivityResultLauncher<Intent> editTaskLauncher) {
        this.db = db;
        this.activity = activity;
        this.editTaskLauncher = editTaskLauncher;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        db.openDatabase();
        ToDoModel item = todoList.get(position);
        holder.text.setText(item.getTask());
        holder.priority.setText(item.getPriority());
        holder.dueDate.setText(item.getDueDate());
        holder.task.setChecked(toBoolean(item.getStatus()));
        setTaskStyle(holder.text, item.getStatus());

        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    db.updateStatus(item.getId(), 1);
                    setTaskStyle(holder.text, 1);
                } else {
                    db.updateStatus(item.getId(), 0);
                    setTaskStyle(holder.text, 0);
                }
            }
        });
    }

    private void setTaskStyle(TextView textView, int status) {
        if (status == 1) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(textView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }

    @Override
    public int getItemCount() {
        return todoList.size();
    }

    private boolean toBoolean(int n) {
        return n != 0;
    }

    public void setTasks(List<ToDoModel> todoList) {
        this.todoList = todoList;
        notifyDataSetChanged();
    }

    public MainActivity getActivity() {
        return activity;
    }

    public void deleteItem(int position) {
        ToDoModel item = todoList.get(position);
        db.deleteTask(item.getId());
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel item = todoList.get(position);
        Intent intent = new Intent(activity, AddTaskActivity.class);
        intent.putExtra("id", item.getId());
        intent.putExtra("task", item.getTask());
        intent.putExtra("priority", item.getPriority());
        intent.putExtra("dueDate", item.getDueDate());
        editTaskLauncher.launch(intent);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView text;
        TextView priority;
        TextView dueDate;

        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            text = view.findViewById(R.id.todoText);
            priority = view.findViewById(R.id.priorityText);
            dueDate = view.findViewById(R.id.dueDateText);
        }
    }
}