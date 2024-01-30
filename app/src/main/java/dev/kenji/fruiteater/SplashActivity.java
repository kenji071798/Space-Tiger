package dev.kenji.fruiteater;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000; // 2 seconds
    public static String gameURL = "";
    public static String appStatus = "";
    public static String apiResponse = "";
    SharedPreferences MyPrefs;
    private MCryptHelper crypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(1);
        getWindow().setFlags(1024, 1024);
        setContentView(R.layout.activity_splash_activity);

        // Instantiate MCryptHelper
        crypt = MCryptHelper.getInstance();

        MyPrefs = getSharedPreferences("Fruit Eater", MODE_PRIVATE);
        boolean isFirstTime = MyPrefs.getBoolean("isFirstTime", true);

        if (isFirstTime) {
            // If it's the first time, redirect to Policy.class
            MyPrefs.edit().putBoolean("isFirstTime", false).apply();
            Intent intent = new Intent(SplashActivity.this, AppPolicy.class);
            startActivity(intent);
            return;
        }

        VideoView videoView = findViewById(R.id.videoView);

        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", "97CC");
            requestBody.put("package", getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String endPoint = "https://backend.madgamingdev.com/api/gameid" + "?appid=97CC&package=" + getPackageName();

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    apiResponse = response.toString();

                    try {
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = crypt.decrypt(jsonData.getString("data"), "21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        appStatus = jsonData.getString("gameKey");
                        gameURL = gameData.getString("gameURL");

                        MyPrefs.edit().putString("gameURL", gameURL).apply();

                        String videoPath = "android.resource://" + getPackageName() + File.separator + R.raw.fruiteater;
                        Uri uri = Uri.parse(videoPath);
                        videoView.setVideoURI(uri);
                        videoView.setOnCompletionListener(mp -> {
                            Log.d("apIRes", "stats:"+ appStatus + "  url:" + gameURL);
                            new Handler().postDelayed(() -> {

                                if (Boolean.parseBoolean(appStatus)) {
                                    Intent intent = new Intent(SplashActivity.this, LaunchWebActivity.class);
                                    intent.putExtra("url", gameURL);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Intent intent = new Intent(SplashActivity.this, AppPolicy.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }, SPLASH_TIME_OUT);
                        });

                        videoView.start();

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, error -> {
//            Log.d("API:RESPONSE", error.toString());
        });
        connectAPI.add(jsonRequest);
    }
}
