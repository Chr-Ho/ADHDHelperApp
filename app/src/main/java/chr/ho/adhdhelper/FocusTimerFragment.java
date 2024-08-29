package chr.ho.adhdhelper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FocusTimerFragment extends Fragment {

    private TextView timerTextView;
    private Button startButton;
    private Button resetButton;
    private Button setTimeButton;
    private boolean isRunning = false;
    private long timeLeftInMillis = 1500000; // 25 minutes in milliseconds
    private long startTimeInMillis = timeLeftInMillis; // Default start time
    private MediaPlayer mediaPlayer; // MediaPlayer for alarm sound
    private SharedPreferences prefs;
    private Handler handler = new Handler();
    private Runnable updateTimerRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_timer, container, false);

        timerTextView = view.findViewById(R.id.timer_text);
        startButton = view.findViewById(R.id.start_button);
        resetButton = view.findViewById(R.id.reset_button);
        setTimeButton = view.findViewById(R.id.set_time_button);

        prefs = requireContext().getSharedPreferences("TimerPrefs", Context.MODE_PRIVATE);
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm_sound);

        startButton.setOnClickListener(v -> toggleTimer());
        resetButton.setOnClickListener(v -> resetTimer());
        setTimeButton.setOnClickListener(v -> showSetTimeDialog());

        updateTimerRunnable = new Runnable() {
            @Override
            public void run() {
                updateTimerText();
                handler.postDelayed(this, 1000);
            }
        };

        loadTimerState();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadTimerState();
        handler.post(updateTimerRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimerRunnable);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void loadTimerState() {
        timeLeftInMillis = prefs.getLong("remainingTime", startTimeInMillis);
        isRunning = prefs.getBoolean("isRunning", false);
        startTimeInMillis = prefs.getLong("startTime", startTimeInMillis);
        updateTimerText();
        updateButtonText();
    }

    private void toggleTimer() {
        if (isRunning) {
            pauseTimer();
        } else {
            startTimer();
        }
    }

    private void startTimer() {
        isRunning = true;
        prefs.edit().putBoolean("isRunning", true).apply();
        updateButtonText();
        requireContext().startService(new Intent(requireContext(), TimerService.class)
                .putExtra("command", "start")
                .putExtra("time", timeLeftInMillis));
    }

    private void pauseTimer() {
        isRunning = false;
        prefs.edit().putBoolean("isRunning", false).apply();
        updateButtonText();
        requireContext().startService(new Intent(requireContext(), TimerService.class)
                .putExtra("command", "pause"));
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        isRunning = false;
        prefs.edit()
                .putLong("remainingTime", timeLeftInMillis)
                .putBoolean("isRunning", false)
                .apply();
        updateTimerText();
        updateButtonText();
        requireContext().startService(new Intent(requireContext(), TimerService.class)
                .putExtra("command", "reset")
                .putExtra("time", timeLeftInMillis));
    }

    private void showSetTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Set Timer Duration");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter time in minutes");
        builder.setView(input);

        builder.setPositiveButton("Set", (dialog, which) -> {
            String inputText = input.getText().toString().trim();
            if (!inputText.isEmpty()) {
                int minutes = Integer.parseInt(inputText);
                setTime(minutes);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void setTime(int minutes) {
        startTimeInMillis = minutes * 60000L;
        prefs.edit().putLong("startTime", startTimeInMillis).apply();
        resetTimer();
    }

    private void updateTimerText() {
        timeLeftInMillis = prefs.getLong("remainingTime", timeLeftInMillis);
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);

        if (timeLeftInMillis == 0 && isRunning) {
            isRunning = false;
            prefs.edit().putBoolean("isRunning", false).apply();
            updateButtonText();
            playAlarm();
        }
    }

    private void updateButtonText() {
        startButton.setText(isRunning ? "Pause" : "Start");
    }

    private void playAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
}
