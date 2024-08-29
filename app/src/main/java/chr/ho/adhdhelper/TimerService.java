package chr.ho.adhdhelper;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class TimerService extends Service {
    private CountDownTimer timer;
    private SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = getSharedPreferences("TimerPrefs", MODE_PRIVATE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String command = intent.getStringExtra("command");
            if (command != null) {
                switch (command) {
                    case "start":
                        long time = intent.getLongExtra("time", 0);
                        startTimer(time);
                        break;
                    case "pause":
                        pauseTimer();
                        break;
                    case "reset":
                        resetTimer(intent.getLongExtra("time", 0));
                        break;
                }
            }
        }
        return START_STICKY;
    }

    private void startTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }
        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                prefs.edit().putLong("remainingTime", millisUntilFinished).apply();
            }

            @Override
            public void onFinish() {
                prefs.edit().putLong("remainingTime", 0).apply();
                prefs.edit().putBoolean("isRunning", false).apply();
            }
        }.start();
        prefs.edit().putBoolean("isRunning", true).apply();
    }

    private void pauseTimer() {
        if (timer != null) {
            timer.cancel();
        }
        prefs.edit().putBoolean("isRunning", false).apply();
    }

    private void resetTimer(long duration) {
        if (timer != null) {
            timer.cancel();
        }
        prefs.edit().putLong("remainingTime", duration).apply();
        prefs.edit().putBoolean("isRunning", false).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
