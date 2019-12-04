package com.sdk.ui.google;

public interface OnGoogleTokenListener {

    void onSuccess(String token);

    void onFailed(String failed);
}
