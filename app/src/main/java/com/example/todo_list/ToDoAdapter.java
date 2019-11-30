package com.example.todo_list;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> implements RecyclerItemTouchHelper.ItemTouchHelperCallback {


    private List<ToDo> mToDoList;
    private AdapterCallback adapterCallback;

    ToDoAdapter(List<ToDo> toDoList) {
        mToDoList = toDoList;
    }

    void setAdapterCallback(AdapterCallback adapterCallback) {
        this.adapterCallback = adapterCallback;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        ToDo toDo = mToDoList.get(position);
        holder.ToDoName.setText(toDo.getName());
        holder.ToDoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = mToDoList.get(holder.getAdapterPosition()).getId();
                Intent intent = new Intent(v.getContext(), EditActivity.class);
                intent.putExtra("extra_position", id);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mToDoList.size();
    }

    @Override
    public void onItemDelete(int position) {
        mToDoList.remove(position);
        if (adapterCallback != null) {
            adapterCallback.onDelete(position);
        }
        notifyItemRemoved(position);
    }

    @Override
    public void onMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(mToDoList, i, i + 1);
        } else {
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(mToDoList, i, i - 1);
        }
        if (adapterCallback != null) {
            adapterCallback.onMove(fromPosition, toPosition);
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    interface AdapterCallback {
        void onDelete(int position);

        void onMove(int fromPosition, int toPosition);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button ToDoName;

        ViewHolder(View view) {
            super(view);
            ToDoName = view.findViewById(R.id.Button_Name);
        }
    }
}
