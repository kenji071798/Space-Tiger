package dev.kenji.fruiteater;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private FlyingOxView gameView;
    private Handler handler = new Handler();
    private static final long Interval = 30;
    public static final String APP_TAG = "97CC";

    public static final String APP_PREFS = "97CCAppPrefs";
    public static final String APP_CLIENT_ID = "97CC";
    public static final String APP_TOKEN = "lscp1f25x3b4";
    public static final String APP_ENVIRONMENT = AdjustConfig.ENVIRONMENT_SANDBOX;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AdjustConfig config = new AdjustConfig(this, APP_TOKEN, APP_ENVIRONMENT);
        Adjust.onCreate(config);


        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);

        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });
    }

    private void startGame() {
        // Your existing game initialization code
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gameView.invalidate();
                    }
                });
            }
        }, 0, Interval);

        // Hide the start button after the game starts
        Button btnStart = findViewById(R.id.btnStart);
        btnStart.setVisibility(View.GONE);
    }
}
