package com.android.example.todoer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.example.todoer.R;
import com.android.example.todoer.model.TaskRealm;

import io.realm.Realm;

import static com.android.example.todoer.activity.TaskEditorActivity.EXTRA_TASK_ID;

public class PomodoroActivity extends AppCompatActivity {

    private static final int PERIOD = 25 * 60;
    private static final int BREAK = 5 * 60;
    private static final int BIG_BREAK = 20 * 60;

    private int seconds = PERIOD;
    private boolean running = false;
    private boolean wasRunning = false;
    private boolean isPeriod = false;
    private int completedPeriods = 0;

    private final String SECONDS = "seconds";
    private final String RUNNING = "running";
    private final String WAS_RUNNING = "wasRunning";
    private final String IS_PERIOD = "isPeriod";

    private TextView pomodoroTimeTextView;
    private TextView pomodoroTaskTextView;

    private FloatingActionButton playButton;
    private FloatingActionButton breakButton;
    private FloatingActionButton nextButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;
    private FloatingActionButton plusOneButton;
    private FloatingActionButton completeTaskButton;

    private ImageView periodFirst;
    private ImageView periodSecond;
    private ImageView periodThird;
    private ImageView periodFourth;

    private Realm realm;
    private TaskRealm task;
    private long taskId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.pomodoro);

        taskId = getIntent().getLongExtra(EXTRA_TASK_ID, 0);
        realm = Realm.getDefaultInstance();
        task = realm.where(TaskRealm.class).equalTo(TaskRealm.ID, taskId).findFirst();

        pomodoroTimeTextView = (TextView) findViewById(R.id.pomodoro_time);
        pomodoroTaskTextView = (TextView) findViewById(R.id.pomodoro_task_title);
        pomodoroTaskTextView.setText(task.getTitle());

        playButton = (FloatingActionButton) findViewById(R.id.pomodoro_play);
        breakButton = (FloatingActionButton) findViewById(R.id.pomodoro_break);
        nextButton = (FloatingActionButton) findViewById(R.id.pomodoro_next);
        pauseButton = (FloatingActionButton) findViewById(R.id.pomodoro_pause);
        stopButton = (FloatingActionButton) findViewById(R.id.pomodoro_stop);
        plusOneButton = (FloatingActionButton) findViewById(R.id.pomodoro_plus_one);
        completeTaskButton = (FloatingActionButton) findViewById(R.id.pomodoro_complete_task);

        playButton.setOnClickListener(playClick);
        breakButton.setOnClickListener(breakClick);
        nextButton.setOnClickListener(nextClick);
        pauseButton.setOnClickListener(pauseClick);
        stopButton.setOnClickListener(stopClick);
        plusOneButton.setOnClickListener(plusOneClick);
        completeTaskButton.setOnClickListener(completeTaskClick);

        periodFirst = (ImageView) findViewById(R.id.pomodoro_period_first);
        periodSecond = (ImageView) findViewById(R.id.pomodoro_period_second);
        periodThird = (ImageView) findViewById(R.id.pomodoro_period_third);
        periodFourth = (ImageView) findViewById(R.id.pomodoro_period_fourth);

        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt(SECONDS);
            running = savedInstanceState.getBoolean(RUNNING);
            wasRunning = savedInstanceState.getBoolean(WAS_RUNNING);
            isPeriod = savedInstanceState.getBoolean(IS_PERIOD);
        }

        refreshPeriods();
        runTimer();
    }

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTimeText();
                if (running && seconds > 0) {
                    seconds--;
                }
                refreshPeriods();
                handler.postDelayed(this, 1000);
            }
        });
    }

    private void setTimeText() {
        int mins = (seconds%3600)/60;
        int secs = seconds%60;
        String time = String.format("%02d:%02d", mins, secs);
        pomodoroTimeTextView.setText(time);
    }

    private void refreshPeriods() {
        if (completedPeriods == 0) {
            periodFirst.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodSecond.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodThird.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodFourth.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
        } else if (completedPeriods == 1) {
            periodFirst.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodSecond.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodThird.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodFourth.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
        } else if (completedPeriods == 2) {
            periodFirst.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodSecond.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodThird.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
            periodFourth.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
        } else if (completedPeriods == 3) {
            periodFirst.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodSecond.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodThird.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodFourth.setColorFilter(ContextCompat.getColor(this, R.color.colorEditor));
        } else if (completedPeriods == 4) {
            periodFirst.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodSecond.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodThird.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
            periodFourth.setColorFilter(ContextCompat.getColor(this, R.color.colorAccent));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SECONDS, seconds);
        outState.putBoolean(RUNNING, running);
        outState.putBoolean(WAS_RUNNING, wasRunning);
        outState.putBoolean(IS_PERIOD, isPeriod);
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasRunning = running;
        running = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (wasRunning)
            running = true;
    }

    View.OnClickListener playClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playButton.setVisibility(View.GONE);
            breakButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            plusOneButton.setVisibility(View.VISIBLE);

            running = true;
            seconds = PERIOD;
            isPeriod = true;
        }
    };

    View.OnClickListener breakClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playButton.setVisibility(View.GONE);
            breakButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);
            plusOneButton.setVisibility(View.VISIBLE);

            running = true;
            seconds = BREAK;
            isPeriod = false;
        }
    };

    View.OnClickListener nextClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playButton.setVisibility(View.GONE);
            breakButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);

            if (isPeriod) {
                completedPeriods++;
                isPeriod = false;
                if (completedPeriods == 4) {
                    seconds = BIG_BREAK;
                    completedPeriods = 0;
                } else {
                    seconds = BREAK;
                }
            } else {
                isPeriod = true;
                seconds = PERIOD;
            }
        }
    };

    View.OnClickListener pauseClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playButton.setVisibility(View.GONE);
            breakButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.VISIBLE);

            if (running) {
                running = false;
                pauseButton.setImageResource(R.drawable.ic_play);
            } else {
                running = true;
                pauseButton.setImageResource(R.drawable.ic_pause);
            }
        }
    };

    View.OnClickListener stopClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playButton.setVisibility(View.VISIBLE);
            breakButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            pauseButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.GONE);
            plusOneButton.setVisibility(View.GONE);

            running = false;
            seconds = PERIOD;
            completedPeriods = 0;
            refreshPeriods();
        }
    };

    View.OnClickListener plusOneClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            seconds += 60;
        }
    };

    View.OnClickListener completeTaskClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            realm.beginTransaction();
            task.setActive(false);
            realm.commitTransaction();
            Intent intent = new Intent(PomodoroActivity.this, TaskEditorActivity.class);
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.putExtra(EXTRA_TASK_ID, taskId);
            startActivity(intent);
        }
    };
}
