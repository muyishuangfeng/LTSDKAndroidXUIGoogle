package com.sdk.ui.google;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.sdk.ui.core.impl.OnLoginSuccessListener;
import com.sdk.ui.core.manager.LoginResultManager;

import java.util.Map;
import java.util.WeakHashMap;

import androidx.annotation.NonNull;

public class GoogleLoginManager {


    public static void initGoogle(Activity context, String clientID, int selfRequestCode) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        context.startActivityForResult(signInIntent, selfRequestCode);
    }


    public static void onActivityResult(boolean isServerTest, int requestCode, Intent data, int selfRequestCode,
                                        Context context, String LTAppID, String LTAppKey, String adID,
                                        String packageID,
                                        OnLoginSuccessListener mListener) {
        if (requestCode == selfRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (!TextUtils.isEmpty(adID)) {
                handleSignInResult(context, isServerTest, LTAppID, LTAppKey, adID, packageID, task, mListener);
            }
        }
    }


    private static void handleSignInResult(Context context, boolean isServerTest, String LTAppID,
                                           String LTAppKey, String adID, String packageID,
                                           @NonNull Task<GoogleSignInAccount> completedTask,
                                           OnLoginSuccessListener mListener) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            Map<String, Object> map = new WeakHashMap<>();
            if (!TextUtils.isEmpty(idToken)) {
                if (!TextUtils.isEmpty(adID)) {
                    map.put("access_token", idToken);
                    map.put("platform", 2);
                    map.put("adid", "");
                    map.put("gps_adid", adID);
                    map.put("platform_id", packageID);
                }
            }
            LoginResultManager.googleLogin(context, isServerTest, LTAppID,
                    LTAppKey, map, mListener);
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    public static void onActivityResult(int requestCode, Intent data, int selfRequestCode, OnGoogleTokenListener listener) {
        if (requestCode == selfRequestCode) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task, listener);
        }
    }


    private static void handleSignInResult(@NonNull Task<GoogleSignInAccount> completedTask, OnGoogleTokenListener listener) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String idToken = account.getIdToken();
            if (!TextUtils.isEmpty(idToken)) {
                listener.onSuccess(idToken);
            } else {
                listener.onFailed("get google token failed");
            }
        } catch (ApiException e) {
            e.printStackTrace();
            listener.onFailed(e.getMessage());
        }
    }

    /**
     * 退出登录
     */
    public static void GoogleSingOut(Context context, String clientID, final OnGoogleSignOutListener mListener) {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(clientID)
                .requestEmail()
                .build();
        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) context, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mListener.onSignOutSuccess();
            }
        });
    }


}
