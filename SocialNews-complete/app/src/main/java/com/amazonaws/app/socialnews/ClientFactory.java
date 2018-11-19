package com.amazonaws.app.socialnews;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.config.AWSConfiguration;
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient;
import com.amazonaws.mobileconnectors.pinpoint.PinpointConfiguration;
import com.amazonaws.mobileconnectors.pinpoint.PinpointManager;
import com.amazonaws.mobileconnectors.pinpoint.analytics.AnalyticsClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.util.concurrent.CountDownLatch;

public class ClientFactory {
    public static final String TAG = ClientFactory.class.getSimpleName();

    private static volatile Context mContext;
    private static volatile AWSConfiguration mAWSConfiguration;
    private static volatile PinpointManager mPinpointClient;
    private static volatile AWSAppSyncClient mAppSyncClient;
    private static volatile TransferUtility mTransferUtility;

    public static synchronized void init(final Context context) {
        if (mContext == null) {
            Log.d(TAG, "Initializing clients");
            mContext = context.getApplicationContext();
            CountDownLatch latch = new CountDownLatch(1);
            final AWSMobileClient mobileClient = AWSMobileClient.getInstance();
            mobileClient.initialize(mContext, new Callback<UserStateDetails>() {
                @Override
                public void onResult(UserStateDetails result) {
                    try {
                        mAWSConfiguration = new AWSConfiguration(mContext);
                    } finally {
                        latch.countDown();
                    }
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                    latch.countDown();
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static synchronized AnalyticsClient getAnalyticsClient() {
        if (mPinpointClient == null) {
            final PinpointConfiguration pinpointConfiguration = new PinpointConfiguration(
                    mContext,
                    AWSMobileClient.getInstance(),
                    mAWSConfiguration
            );
            mPinpointClient = new PinpointManager(pinpointConfiguration);
        }
        return mPinpointClient.getAnalyticsClient();
    }

    public static synchronized AWSAppSyncClient getAppSyncClient() {
        if (mAppSyncClient == null) {
            mAppSyncClient = AWSAppSyncClient.builder()
                    .context(mContext)
                    .awsConfiguration(mAWSConfiguration)
                    .build();
        }
        return mAppSyncClient;
    }

    public static synchronized TransferUtility getTransferUtility() {
        if (mTransferUtility == null) {
            mTransferUtility = TransferUtility.builder()
                    .context(mContext)
                    .s3Client(new AmazonS3Client(AWSMobileClient.getInstance()))
                    .awsConfiguration(mAWSConfiguration)
                    .build();
        }
        return mTransferUtility;
    }

    public static synchronized Context getContext() {
        return mContext;
    }
}
