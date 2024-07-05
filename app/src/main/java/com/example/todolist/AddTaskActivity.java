package com.example.todolist;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.graphics.Color;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.todolist.Model.ToDoModel;
import com.example.todolist.Utils.DatabaseHandler;

import java.text.SimpleDateFormat;

public class AddTaskActivity extends AppCompatActivity {

    private EditText newTaskText;
    private Button newTaskSaveButton;
    private Button cancelButton;
    private RadioGroup priorityGroup;
    private TextView dueDateText; // 添加到期日期文本
    private DatabaseHandler db;
    private int taskId = -1;
    private boolean isUpdate = false;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_task);

        newTaskText = findViewById(R.id.newTaskText);
        newTaskSaveButton = findViewById(R.id.newTaskButton);
        cancelButton = findViewById(R.id.cancelButton);
        priorityGroup = findViewById(R.id.priorityGroup);
        dueDateText = findViewById(R.id.dueDateText);

        db = new DatabaseHandler(this);
        db.openDatabase();

        calendar = Calendar.getInstance();

        dueDateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(AddTaskActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        dueDateText.setText(dateFormat.format(calendar.getTime()));
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            isUpdate = true;
            taskId = bundle.getInt("id");
            String task = bundle.getString("task");
            String priority = bundle.getString("priority");
            String dueDate = bundle.getString("dueDate");
            newTaskText.setText(task);
            if (dueDate != null && !dueDate.isEmpty()) {
                dueDateText.setText(dueDate); // 设置到期日期
            }
            if (task.length() > 0) {
                newTaskSaveButton.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            }
            // 设置优先级选择
            if ("high".equals(priority)) {
                priorityGroup.check(R.id.highPriority);
            } else if ("medium".equals(priority)) {
                priorityGroup.check(R.id.mediumPriority);
            } else {
                priorityGroup.check(R.id.lowPriority);
            }
        }

        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.toString().isEmpty()) {
                    newTaskSaveButton.setEnabled(false);
                    newTaskSaveButton.setTextColor(Color.GRAY);
                } else {
                    newTaskSaveButton.setEnabled(true);
                    newTaskSaveButton.setTextColor(ContextCompat.getColor(AddTaskActivity.this, R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        newTaskSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                String priority = getSelectedPriority();
                String dueDate = dueDateText.getText().toString();
                if (isUpdate) {
                    db.updateTask(taskId, text, priority, dueDate);
                } else {
                    ToDoModel task = new ToDoModel();
                    task.setTask(text);
                    task.setStatus(0);
                    task.setPriority(priority);
                    task.setDueDate(dueDate);
                    db.insertTask(task);
                }
                setResult(Activity.RESULT_OK);
                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getSelectedPriority() {
        int selectedId = priorityGroup.getCheckedRadioButtonId();
        if (selectedId == R.id.highPriority) {
            return "high";
        } else if (selectedId == R.id.mediumPriority) {
            return "medium";
        } else {
            return "low";
        }
    }
}

