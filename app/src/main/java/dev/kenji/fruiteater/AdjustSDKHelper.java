package dev.kenji.fruiteater;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;

public class AdjustSDKHelper {

    @SuppressLint("StaticFieldLeak")
    static Context mContext;

    public static void init(Context childContext) {
        mContext = childContext;
    }

    @JavascriptInterface
    public void onEventJs(String eventName) {
        Log.e("注册成功: ", eventName);

        AdjustEvent adjustEvent;

        switch (eventName)
        {
            case "userconsent_accept":
                Intent intent = new Intent(mContext, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(intent);
                break;
            case "userconsent_dismiss":
                System.exit(0);
                break;
            case "register_success":
                adjustEvent = new AdjustEvent("kq7I6v");
                Adjust.trackEvent(adjustEvent);
                break;
            default:
                adjustEvent = new AdjustEvent(eventName);
                Adjust.trackEvent(adjustEvent);
                break;
        }
    }

    @JavascriptInterface
    public void onEventJsRecharge(String eventName) {
        Log.e("注册成功: ", eventName);

        AdjustEvent adjustEvent;
        adjustEvent = new AdjustEvent("pm0829");
        Adjust.trackEvent(adjustEvent);
    }

    @JavascriptInterface
    public void onEventJsFirstRecharge(String eventName) {
        Log.e("注册成功: ", eventName);

        AdjustEvent adjustEvent;
        adjustEvent = new AdjustEvent("6311lr");
        Adjust.trackEvent(adjustEvent);
    }



}
