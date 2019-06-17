package com.example.todoapp;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DataBase dbHelper;
    private ListView all_tasks;
    private ArrayAdapter<String> adapter;
    private EditText fieldText;
    private SharedPreferences prefs;
    private String nameList;

    private TextView infoApp;
    private String text_for_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoApp = (TextView)findViewById(R.id.infoString);
        infoApp.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_text));

        dbHelper = new DataBase(this);
        all_tasks = (ListView)findViewById(R.id.task_list);
        fieldText = (EditText)findViewById(R.id.listName);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        nameList = prefs.getString("listName", "");
        fieldText.setText(nameList);

        loadAllTasks();
        changeTextAction();
        }

    private void changeTextAction() {
        fieldText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                SharedPreferences.Editor editPrefs = prefs.edit();
                editPrefs.putString("listName", String.valueOf(fieldText.getText()));
                editPrefs.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void loadAllTasks() {
        ArrayList<String> task_list = dbHelper.getAllTasks();
        if(adapter == null){
            adapter = new ArrayAdapter<String>(this, R.layout.raw,
                    R.id.txt_task, task_list);
            all_tasks.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(task_list);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);

        Drawable icon = menu.getItem(0).getIcon();
        icon.mutate();
        icon.setColorFilter(getResources().getColor(android.R.color.white),
                PorterDuff.Mode.SRC_IN);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.add_new_task){
            final EditText userTaskGet = new EditText(this);
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Даданне новага задання")
                    .setMessage("Што жадаеце дадаць?")
                    .setView(userTaskGet)
                    .setPositiveButton("Дадаць", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String task = String.valueOf(userTaskGet.getText());
                            dbHelper.insertData(task);
                            loadAllTasks();
                        }
                    })
                    .setNegativeButton("Нічога", null)
                    .create();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void  deleteTask(View view){
        View parent = (View)view.getParent();
        TextView txtTask = (TextView)parent.findViewById(R.id.txt_task);
        text_for_delete = String.valueOf(txtTask.getText());


        parent.animate().alpha(0).setDuration(2000).withEndAction(new Runnable() {
            @Override
            public void run() {
                dbHelper.deleteData(text_for_delete);
                loadAllTasks();
            }
        });
    }
}
