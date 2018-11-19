package com.amazonaws.app.socialnews;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.TypeConverter;

public class DataConverter {
    @TypeConverter
    public String fromCommentsList(List<Comment> commentList) {
        if (commentList == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Comment>>() {}.getType();
        return gson.toJson(commentList, type);
    }

    @TypeConverter
    public List<Comment> toCommentsList(String commentListString) {
        if (commentListString == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Comment>>() {}.getType();
        return gson.fromJson(commentListString, type);
    }
}
