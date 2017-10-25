package com.android.ressin;

/**
 * Created by prashanth kurella on 10/26/2017.
 */

public class ToDoObject {
    String key;
    String text;

    public ToDoObject(String key, String text) {
        this.key = key;
        this.text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
