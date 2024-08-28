package chr.ho.adhdhelper;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private long timeLeftInMillis = 1500000; // 25 minutes in milliseconds
    private long startTimeInMillis = timeLeftInMillis; // Default start time
    private MediaPlayer mediaPlayer; // MediaPlayer for alarm sound

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_focus_timer, container, false);

        timerTextView = view.findViewById(R.id.timer_text);
        startButton = view.findViewById(R.id.start_button);
        resetButton = view.findViewById(R.id.reset_button);
        setTimeButton = view.findViewById(R.id.set_time_button);

        mediaPlayer = MediaPlayer.create(getContext(), R.raw.alarm_sound); // Initialize MediaPlayer

        startButton.setOnClickListener(v -> {
            if (isRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        });

        resetButton.setOnClickListener(v -> resetTimer());
        setTimeButton.setOnClickListener(v -> showSetTimeDialog());

        updateTimerText();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Release MediaPlayer resources
            mediaPlayer = null;
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isRunning = false;
                startButton.setText("Start");
                playAlarm(); // Play alarm sound when timer finishes
            }
        }.start();

        isRunning = true;
        startButton.setText("Pause");
    }

    private void pauseTimer() {
        countDownTimer.cancel();
        isRunning = false;
        startButton.setText("Start");
    }

    private void resetTimer() {
        timeLeftInMillis = startTimeInMillis;
        updateTimerText();
        startButton.setText("Start");
        if (isRunning) {
            countDownTimer.cancel();
            isRunning = false;
        }
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
        resetTimer(); // Reset timer to new time
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;

        String timeFormatted = String.format("%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void playAlarm() {
        if (mediaPlayer != null) {
            mediaPlayer.start(); // Play the alarm sound
        }
    }
}
