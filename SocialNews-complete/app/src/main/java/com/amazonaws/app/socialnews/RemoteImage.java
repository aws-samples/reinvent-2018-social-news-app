package com.amazonaws.app.socialnews;

public class RemoteImage {
    String bucket, key, region;

    public RemoteImage(String bucket, String key, String region) {
        this.bucket = bucket;
        this.key = key;
        this.region = region;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
