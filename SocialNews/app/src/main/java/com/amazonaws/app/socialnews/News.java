package com.amazonaws.app.socialnews;

import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.TypeConverters;

@Entity(primaryKeys = "id")
@TypeConverters({DataConverter.class})
public class News {

    @NonNull
    private String id;
    private String title;
    private String synopsis;
    private String content;
    private long publishDate;
    private List<Comment> comments;

    // Constructurs, getters, and setters

    @Ignore
    public News(@NonNull String id) {
        this.id = id;
    }

    public News(@NonNull String id, String title, String synopsis, String content, long publishDate) {
        this.id = id;
        this.title = title;
        this.synopsis = synopsis;
        this.content = content;
        this.publishDate = publishDate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(long publishDate) {
        this.publishDate = publishDate;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        News news = (News) o;
        return Objects.equals(id, news.id);
    }
}
