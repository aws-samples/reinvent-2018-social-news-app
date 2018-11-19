package com.amazonaws.app.socialnews;

import android.util.Log;

import com.amazonaws.amplify.generated.graphql.GetNewsQuery;
import com.amazonaws.amplify.generated.graphql.ListNewssQuery;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

@Singleton
public class NewsRepository {
    private static final String TAG = NewsRepository.class.getSimpleName();
    private final NewsDao newsDao;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private AWSAppSyncClient client;

    public NewsRepository() {
        newsDao = Room.databaseBuilder(ClientFactory.getContext(),
                NewsDatabase.class, "news-database").build().newsDao();
        client = ClientFactory.getAppSyncClient();
    }

    public LiveData<News> getNews(final String newsId) {
        refreshNews(newsId);
        return newsDao.load(newsId);
    }

    private void refreshNews(final String newsId) {
        Log.d(TAG, "Fetching from service, use TTL or other metric to determine staleness");
        GetNewsQuery getNewsQuery = GetNewsQuery.builder().id(newsId).build();
        client.query(getNewsQuery)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<GetNewsQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<GetNewsQuery.Data> response) {
                        if (response.hasErrors()) {
                            Log.e(TAG, "onResponse: errors:" + response.errors());
                            return;
                        }
                        Log.d(TAG, "onResponse: accessing data");
                        GetNewsQuery.GetNews responseNews = response.data().getNews();
                        News news = null;
                        try {
                            long publishDate = simpleDateFormat.parse(responseNews.publishDate()).getTime();
                            Log.d(TAG, "onResponse: publishDate as long: " + publishDate);
                            news = new News(responseNews.id(), responseNews.title(), responseNews.synopsis(), responseNews.content(), publishDate);

                            List<GetNewsQuery.Item> items = responseNews.comments().items();
                            List<Comment> comments = new ArrayList<Comment>(items.size());
                            for (GetNewsQuery.Item item : items) {
                                comments.add(new Comment(item.id(), item.message(), item.commenter()));
                            }
                            news.setComments(comments);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        newsDao.save(news);
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.e(TAG, "Failed to refresh news item", e);
                    }
                });
    }

    public LiveData<List<News>> getNewsList() {
        refreshNewsList();
        return newsDao.listByNew();
    }

    public void refreshNewsList() {
        Log.d(TAG, "Fetching from service, use TTL or other metric to determine staleness");
        ListNewssQuery listNewssQuery = ListNewssQuery.builder().build();
        client.query(listNewssQuery)
                .responseFetcher(AppSyncResponseFetchers.NETWORK_ONLY)
                .enqueue(new GraphQLCall.Callback<ListNewssQuery.Data>() {
                    @Override
                    public void onResponse(@Nonnull Response<ListNewssQuery.Data> response) {
                        if (response.hasErrors()) {
                            Log.e(TAG, "onResponse: errors:" + response.errors());
                            return;
                        }
                        List<News> newsList = marshallListNews(response);
                        newsDao.save(newsList);
                    }

                    @Override
                    public void onFailure(@Nonnull ApolloException e) {
                        Log.e(TAG, "Failed to refresh news item", e);
                    }
                });
    }

    private List<News> marshallListNews(@Nonnull Response<ListNewssQuery.Data> response) {
        Log.d(TAG, "onResponse: accessing data");
        List<ListNewssQuery.Item> items = response.data().listNewss().items();
        Log.d(TAG, "onResponse: size" + items.size());
        List<News> newsList = new ArrayList<>(items.size());
        for (ListNewssQuery.Item item : items) {
            Log.d(TAG, "onResponse: " + item.id() + " " + item.publishDate());
            try {
                long publishDate = simpleDateFormat.parse(item.publishDate()).getTime();
                Log.d(TAG, "onResponse: publishDate as long: " + publishDate);
                newsList.add(new News(item.id(), item.title(), item.synopsis(), "Content loading...", publishDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return newsList;
    }

}
