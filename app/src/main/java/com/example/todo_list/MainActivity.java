package com.example.todo_list;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ToDoAdapter.AdapterCallback {
    private List<ToDo> toDoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToDo();
        Button addButton = findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        updateView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == RESULT_OK) {
                String returnedData = data.getStringExtra("data_return");
                if (returnedData != null) {
                    if (!returnedData.equals("")) {
                        addListItem(returnedData);
                        updateListData();
                    }
                    updateView();
                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.timeSort) {
            sort();
            updateView();
        }
        return true;
    }

    /**
     * 用已存储的数据初始化toDoList
     */
    private void initToDo() {
        SharedPreferences pref = getSharedPreferences("toDoName", MODE_PRIVATE);
        int size = pref.getInt("listSize", 0);
        for (int i = 0; i < size; i++) {
            String index1 = "" + i;
            String index2 = "" + (i + 1000);
            String index3 = "" + (i + 2000);
            toDoList.add(new ToDo(pref.getString(index1, ""), pref.getInt(index2, 0), pref.getLong(index3, 0)));
        }
    }

    /**
     * 更新活动页面
     */
    public void updateView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        ToDoAdapter adapter = new ToDoAdapter(toDoList);
        recyclerView.setAdapter(adapter);
        ItemTouchHelper.Callback callback = new RecyclerItemTouchHelper(adapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * 更新（储存）List的子项数据
     */
    public void updateListData() {
        SharedPreferences.Editor editor = getSharedPreferences("toDoName", MODE_PRIVATE).edit();
        for (int i = 0; i < toDoList.size(); i++) {
            String index1 = "" + i;
            String index2 = "" + (i + 1000);
            String index3 = "" + (i + 2000);
            editor.putString(index1, toDoList.get(i).getName());
            editor.putInt(index2, toDoList.get(i).getId());
            editor.putLong(index3, toDoList.get(i).getTime());
        }
        editor.putInt("listSize", toDoList.size());
        editor.apply();
    }

    /**
     * 添加一个子项，置于List的表头
     *
     * @param returnedData：Item的name
     */
    public void addListItem(String returnedData) {
        SharedPreferences pref = getSharedPreferences("id", MODE_PRIVATE);
        int flag = pref.getInt("flag", 0);
        ToDo newItem = new ToDo(returnedData, flag, new Date().getTime());
        flag++;
        SharedPreferences.Editor editorF = getSharedPreferences("id", MODE_PRIVATE).edit();
        editorF.putInt("flag", flag);
        editorF.apply();
        toDoList.add(newItem);
        for (int i = toDoList.size() - 1; i > 0; i--) {
            Collections.swap(toDoList, i, i - 1);
        }
        toDoList.set(0, newItem);
    }

    /**
     * 按时间排序toDoList并更新数据
     */
    public void sort() {
        Collections.sort(toDoList, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return o1.getTime().compareTo(o2.getTime());
            }
        });
        updateListData();
    }

    /**
     * 删除一个item，并上传存储数据
     *
     * @param position
     */
    @Override
    public void onDelete(int position) {
        SharedPreferences.Editor editor = getSharedPreferences("toDoName", MODE_PRIVATE).edit();
        editor.remove(Integer.toString(position));
        editor.remove(Integer.toString(position + 1000));
        editor.putInt("listSize", toDoList.size());
        editor.apply();
    }

    /**
     * 交换两个item的位置，并上传存储数据
     *
     * @param fromPosition
     * @param toPosition
     */
    @Override
    public void onMove(int fromPosition, int toPosition) {
        SharedPreferences.Editor editor = getSharedPreferences("toDoName", MODE_PRIVATE).edit();
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                editor.putString(Integer.toString(i), toDoList.get(i + 1).getName());
                editor.putInt(Integer.toString(i + 1000), toDoList.get(i + 1).getId());
                editor.putLong(Integer.toString(i + 2000), toDoList.get(i + 1).getTime());
            }
            editor.putString(Integer.toString(toPosition), toDoList.get(fromPosition).getName());
            editor.putInt(Integer.toString(toPosition + 1000), toDoList.get(fromPosition).getId());
            editor.putLong(Integer.toString(toPosition + 2000), toDoList.get(fromPosition).getTime());
            editor.apply();
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(toDoList, i, i + 1);
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                editor.putString(Integer.toString(i - 1), toDoList.get(i).getName());
                editor.putInt(Integer.toString(i - 1 + 1000), toDoList.get(i).getId());
                editor.putLong(Integer.toString(i - 1 + 2000), toDoList.get(i).getTime());
            }
            editor.putString(Integer.toString(fromPosition), toDoList.get(toPosition).getName());
            editor.putInt(Integer.toString(fromPosition + 1000), toDoList.get(toPosition).getId());
            editor.putLong(Integer.toString(fromPosition + 2000), toDoList.get(toPosition).getTime());
            editor.apply();
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(toDoList, i, i - 1);
        }
    }
}




