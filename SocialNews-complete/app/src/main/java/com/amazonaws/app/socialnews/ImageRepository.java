package com.amazonaws.app.socialnews;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import java.io.File;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class ImageRepository {
    private static final String relativeCacheDir = "/transferUtilityCache/";
    private static final String TAG = ImageRepository.class.getSimpleName();

    final String absoluteCacheDir;
    final TransferUtility transferUtility;
    final Context context;

    public ImageRepository() {
        context = ClientFactory.getContext();
        transferUtility = ClientFactory.getTransferUtility();
        absoluteCacheDir = ClientFactory.getContext().getCacheDir().getAbsolutePath() + relativeCacheDir;
    }

    public LiveData<Drawable> getDrawable(final String bucket, final String key, final String region) {
        MutableLiveData<Drawable> drawableMutableLiveData = new MutableLiveData<>();
        Drawable loading = ClientFactory.getContext().getDrawable(R.drawable.default_sign_in_logo);
        drawableMutableLiveData.setValue(loading);

        File file = new File(absoluteCacheDir + bucket + "/" + key);
        if (file.exists()) {
            try {
                drawableMutableLiveData.setValue(new BitmapDrawable(context.getResources(), file.getAbsolutePath()));
            } catch (Exception e) {
                drawableMutableLiveData.setValue(context.getDrawable(R.drawable.ic_launcher_background));
            }
        }

        transferUtility.download(bucket, key, file, new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                switch (state) {
                    case COMPLETED:
                        drawableMutableLiveData.postValue(new BitmapDrawable(context.getResources(), file.getAbsolutePath()));
                        break;
                    default:
                        drawableMutableLiveData.postValue(context.getDrawable(R.drawable.ic_launcher_foreground));
                        break;
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                Log.i("TAG", "onProgressChanged " + bytesCurrent + "/" + bytesTotal);
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "onError: ", ex);
            }
        });

        return drawableMutableLiveData;
    }
}
