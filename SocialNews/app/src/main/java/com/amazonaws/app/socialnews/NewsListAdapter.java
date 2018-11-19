package com.amazonaws.app.socialnews;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import java.util.List;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.NewsViewHolder> {

    private List<News> mNews;

    public NewsListAdapter(@NonNull final List<News> news) {
        mNews = news;
    }

    public void setNews(@NonNull final List<News> news) {
        mNews = news;
        for (News n : news) {
            Log.d("NewsListAdapter", "setNews: " + n.getPublishDate());
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LinearLayoutCompat v = (LinearLayoutCompat) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_news, viewGroup, false);
        NewsViewHolder vh = new NewsViewHolder(v);
        return vh;

    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder newsViewHolder, final int i) {
        final News news = mNews.get(i);
        newsViewHolder.title.setText(news.getTitle());
//        Bitmap bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.default_sign_in_logo);
//        final Drawable image = new BitmapDrawable(ClientFactory.getContext().getResources(), bitmap);
//        newsViewHolder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, image);
        newsViewHolder.synopsis.setText(news.getSynopsis());
        newsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailedActivity.startActivity(v.getContext(), mNews.get(i).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNews.size();
    }

    protected static class NewsViewHolder extends RecyclerView.ViewHolder {
        public final TextView title;
        public final TextView synopsis;
        public final TextView upVotes;
        public final TextView share;
        public final TextView comments;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            synopsis = itemView.findViewById(R.id.synopsis);
            upVotes = itemView.findViewById(R.id.synopsis);
            share = itemView.findViewById(R.id.share);
            comments = itemView.findViewById(R.id.comments);
        }
    }
}
