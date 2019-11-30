package com.example.todo_list;

public class ToDo {
    private String name;
    private int id;
    private Long time;

    ToDo(String name, int id, Long time) {
        this.name = name;
        this.id = id;
        this.time = time;
    }

    String getName() {
        return name;
    }

    public int getId() {
        return id;
    }


    Long getTime() {
        return time;
    }
}

