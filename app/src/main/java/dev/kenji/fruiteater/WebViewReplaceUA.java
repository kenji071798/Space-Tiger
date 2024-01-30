package dev.kenji.fruiteater;

import android.util.Log;
import android.webkit.WebView;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebViewReplaceUA {
    //可用的可替换的有效UA数组（根据情况自行追加数据）
    private static final String UA_DATA[] = {
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36"
    };

    //检测是否被系统识别为webview
    public static boolean isWebviewUA(String useragent) {
        String[] rules = {"WebView","Android.*(wv|\\.0\\.0\\.0)"};
        String regex = "(" + String.join("|", rules) + ")";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(useragent);
        return matcher.find();
    }

    //随机数
    public static int getRandom(int min,int max){

        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }

    //替换UA（先通过方法判断是否被识别成了webview）
    public static void replaceUA(WebView mWebview){
        String ua = mWebview.getSettings().getUserAgentString();
        boolean isWebviewUA = WebViewReplaceUA.isWebviewUA(ua);
        Log.d("WebViewReplaceUA","isWebviewUA："+isWebviewUA);

        if(isWebviewUA){
            int index = WebViewReplaceUA.getRandom(0,WebViewReplaceUA.UA_DATA.length -1 );
            ua = WebViewReplaceUA.UA_DATA[index];
        }

        ua = ua.replace("; wv", "");
        mWebview.getSettings().setUserAgentString(ua);
    }
}
