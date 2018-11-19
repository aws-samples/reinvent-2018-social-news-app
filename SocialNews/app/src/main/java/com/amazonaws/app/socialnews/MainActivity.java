package com.amazonaws.app.socialnews;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.amazonaws.amplify.generated.graphql.CreateNewsMutation;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import java.util.ArrayList;

import javax.annotation.Nonnull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import type.CreateNewsInput;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private NewsListAdapter mAdapter;
    private NewsViewModel viewModel;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.signIn);
        item.setTitle(AWSMobileClient.getInstance().isSignedIn() ? "Sign-out" : "Sign-in");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signIn:
                // TODO Add sign-in sign-out code
                break;
            case R.id.uploadSampleData:
                uploadSampleData();
                break;
            case R.id.refresh:
                viewModel.refreshList();
                break;
            default:
                Toast.makeText(getApplicationContext(), "Unknown menu option selected", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(NewsViewModel.class);

        viewModel.getNewsList().observe(this, newsList -> mAdapter.setNews(newsList));

        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final ArrayList<News> newsList = new ArrayList<>(1);

        mAdapter = new NewsListAdapter(newsList);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientFactory.getAnalyticsClient().submitEvents();
    }

    private void uploadSampleData() {
        _uploadSampleData(CreateNewsInput.builder()
                .id("123")
                .title("Today I Learned a Coconut is a Seed")
                .content("Who knew? I thought for the longest time that it was a fruit.")
                .synopsis("Who knew?")
                .publishDate("2018-11-19")
                .build()
        );
        _uploadSampleData(CreateNewsInput.builder()
                .id("456")
                .title("What animal sleeps 18 to 20 hours a day?")
                .content("A koala")
                .synopsis("The sleepiest animal in world...")
                .publishDate("2018-11-18")
                .build());
        _uploadSampleData(CreateNewsInput.builder()
                .id("789")
                .title("What body part never grows?")
                .content("The eyeball is the only organism which does not grow from birth. " +
                        "It is fully grown when you are born. When you look at a baby's face, " +
                        "you see mostly iris and little white. As the baby grows, " +
                        "you get to see more and more of the eyeball.")
                .synopsis("I'll bet you never saw this coming...")
                .publishDate("2018-11-17")
                .build());
    }

    private void _uploadSampleData(CreateNewsInput input) {
        CreateNewsMutation createNewsMutation = CreateNewsMutation.builder()
                .input(input)
                .build();
        ClientFactory.getAppSyncClient().mutate(createNewsMutation).enqueue(new GraphQLCall.Callback<CreateNewsMutation.Data>() {
            @Override
            public void onResponse(@Nonnull Response<CreateNewsMutation.Data> response) {
                if (response.hasErrors()) {
                    Log.e(TAG, "onResponse: errors from service" + response.errors());
                    return;
                }
                Log.d(TAG, "onResponse: Uploaded sample data");
            }

            @Override
            public void onFailure(@Nonnull ApolloException e) {
                Log.e(TAG, "onFailure: Failed to load sample data", e);
            }
        });
    }
}
