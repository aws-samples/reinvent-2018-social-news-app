package com.amazonaws.app.socialnews;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateCommentMutation;
import com.amazonaws.amplify.generated.graphql.OnCreateCommentSubscription;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.appsync.AppSyncSubscriptionCall;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import javax.annotation.Nonnull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import type.CreateCommentInput;

public class DetailedActivity extends AppCompatActivity {
    public static final String TAG = DetailedActivity.class.getSimpleName();

    public static final String NEWS_ID = "NEWS_ID";
    private NewsViewModel viewModel;
    String newsId;
    AppSyncSubscriptionCall<OnCreateCommentSubscription.Data> subscriptionWatcher;
    TextView commentsView;

    public static void startActivity(Context context, String newsId) {
        Intent intent = new Intent(context, DetailedActivity.class);
        intent.putExtra(NEWS_ID, newsId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);

        newsId = getIntent().getStringExtra(NEWS_ID);
        Log.d("TAG", "onCreate: newsId: " + newsId);

        ClientFactory.getAnalyticsClient().recordEvent(
                ClientFactory.getAnalyticsClient().createEvent("ui").withAttribute("clicked", "newsId-" + newsId)
        );

        TextView titleView = findViewById(R.id.title);
        TextView contentView = findViewById(R.id.content);
        commentsView = findViewById(R.id.comments);
        EditText addCommentView = findViewById(R.id.add_comment);

        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        viewModel.getNews(newsId).observe(this, new Observer<News>() {
            @Override
            public void onChanged(News news) {
                titleView.setText(news.getTitle());
                contentView.setText(news.getContent());
                if (news.getComments() != null && news.getComments().size() != 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Comment c : news.getComments()) {
                        sb.append(c.getCommenter() + ":\n" + c.getMessage() + "\n");
                    }
                    commentsView.setText(sb.toString());
                } else {
                    commentsView.setText("No comments yet :(");
                }
            }
        });

        Button submitComment = findViewById(R.id.submit_comment);
        submitComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    final String username = AWSMobileClient.getInstance().getUsername();
                    if (username == null) {
                        Toast.makeText(v.getContext(), "You need to be signed-in to comment", Toast.LENGTH_LONG).show();
                        return;
                    }
                    String comment = addCommentView.getText().toString();
                    addCommentView.setText("");
                    CreateCommentMutation commentMutation = CreateCommentMutation.builder()
                            .input(CreateCommentInput.builder()
                                    .commentNewsId(newsId)
                                    .commenter(username)
                                    .message(comment)
                                    .build())
                            .build();
                    Toast.makeText(getApplicationContext(), "Sending comment to AWS AppSync", Toast.LENGTH_SHORT).show();
                    ClientFactory.getAppSyncClient().mutate(commentMutation).enqueue(new GraphQLCall.Callback<CreateCommentMutation.Data>() {
                        @Override
                        public void onResponse(@Nonnull Response<CreateCommentMutation.Data> response) {
                            if (response.hasErrors()) {
                                Log.e(TAG, "onResponse: has errors: " + response.errors());
                            }
                            Log.d(TAG, "onResponse: comment on news");
                        }

                        @Override
                        public void onFailure(@Nonnull ApolloException e) {
                            Log.e(TAG, "onFailure: ", e);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        OnCreateCommentSubscription newCommentsSubscription = OnCreateCommentSubscription.builder().build();
        subscriptionWatcher = ClientFactory.getAppSyncClient().subscribe(newCommentsSubscription);
        subscriptionWatcher.execute(new AppSyncSubscriptionCall.Callback<OnCreateCommentSubscription.Data>() {
            @Override
            public void onResponse(@Nonnull Response<OnCreateCommentSubscription.Data> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Received new comment event", Toast.LENGTH_SHORT).show();
                        final OnCreateCommentSubscription.OnCreateComment onCreateComment = response.data().onCreateComment();
                        commentsView.setText(onCreateComment.commenter() + "\n" + onCreateComment.message() + "\n" + commentsView.getText());
                    }
                });
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }
        });
    }
}
