package com.amazonaws.app.socialnews;

import java.util.List;

import javax.inject.Inject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class NewsViewModel extends ViewModel {
    private LiveData<News> news;
    private NewsRepository newsRepo;

    @Inject
    public NewsViewModel() {
        this.newsRepo = new NewsRepository();
    }

    public void init(String newsId) {
        if (this.news != null) {
            return;
        }
        news = newsRepo.getNews(newsId);
    }

    public LiveData<News> getNews(final String newsId) {
        return newsRepo.getNews(newsId);
    }

    public LiveData<List<News>> getNewsList() {
        return newsRepo.getNewsList();
    }

    public void refreshList() {
        newsRepo.refreshNewsList();
    }
}
