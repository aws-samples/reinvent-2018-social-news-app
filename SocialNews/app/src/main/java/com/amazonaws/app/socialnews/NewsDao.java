package com.amazonaws.app.socialnews;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface NewsDao {
    @Insert(onConflict = REPLACE)
    void save(News news);
    @Insert(onConflict = REPLACE)
    void save(List<News> news);
    @Query("SELECT * FROM news WHERE id = :newsId")
    LiveData<News> load(final String newsId);
    @Query("SELECT * FROM news")
    LiveData<List<News>> list();
    @Query("SELECT * FROM news ORDER BY publishDate DESC")
    LiveData<List<News>> listByNew();
}
