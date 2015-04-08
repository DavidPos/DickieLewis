package sailloft.dickielewis.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sailloft.dickielewis.R;
import sailloft.dickielewis.model.Boil;
import sailloft.dickielewis.model.Mash;


public class MainActivity extends ActionBarActivity {
    private ProgressWheel mBoilPW;
    private ProgressWheel mProgressWheel;
    private TextView mSummary;
    private Button mBoilButton;
    private Button mMashButton;
    private TextView inLabel;
    private Boil boilTimers;
    private Mash mashTimers;
    private int mBoilTime;
    private int state = 0;
    private int index = 0;
    private long length = 0;
    private long timerInMinutes;
    private List<String> boilAddTime = new ArrayList<>();
    private boolean timersMashFinished = false;
    private brewCounter mCountDownTimer;
    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> boilInfo = new ArrayList<>();
    private ArrayList<HashMap<String, String>> mashSteps = new ArrayList<>();
    private int timeLeft;
    private boolean timersFinished = false;
    private int itemTime =0;

    private List<String> boilTimes = new ArrayList<>();
    private List<String> boilSummary = new ArrayList<>();
    private List<String> mashTemp = new ArrayList<>();
    private List<String> mashLength = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
        inLabel = (TextView)findViewById(R.id.inLabel);
        mBoilPW = (ProgressWheel)findViewById(R.id.pw_boil);
        inLabel.setVisibility(View.VISIBLE);
        inLabel.setText("Add Timers to start....");

        mBoilPW.setVisibility(View.INVISIBLE);
        mBoilButton = (Button) findViewById(R.id.boilButton);
        mMashButton =(Button)findViewById(R.id.mashButton);
        mSummary = (TextView)findViewById(R.id.summaryLabel);
        mSummary.setVisibility(View.INVISIBLE);


        mBoilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoilPW.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(MainActivity.this, BoilTimer.class);
                startActivityForResult(intent, 1);
            }
        });
        mMashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBoilPW.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(MainActivity.this, MashTimer.class);
                startActivityForResult(intent, 2);
            }
        });

        mProgressWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (timersFinished) {
                    mProgressWheel.spin();
                    Toast.makeText(MainActivity.this,
                            "Timers have finished or has not been set",
                            Toast.LENGTH_LONG).show();
                } else {
                    state += 1;
                    if (timersMashFinished) {
                        mBoilTime = Integer.parseInt(boilTimers.getBoilTime());
                        length = TimeUnit.MINUTES.toMillis(mBoilTime);

                        mSummary.setText("Next addition: " + boilSummary.get(index));
                        Log.i(TAG, length + "");
                    } else {
                        length = TimeUnit.MINUTES.toMillis(Integer.parseInt(mashLength.get(index)));
                        inLabel.setText("We are mashing, I hope you like mashing too ");

                    }
                    mProgressWheel.spin();
                    switch (state) {
                        case 1:
                            //Start Timer
                            mCountDownTimer = new brewCounter(length, 1000);
                            mCountDownTimer.start();
                            break;
                        //pause
                        case 2:
                            mCountDownTimer.cancel();
                            mProgressWheel.setText("Paused");
                            mCountDownTimer = new brewCounter(timeLeft, 1000);
                            break;
                        //resume
                        case 3:
                            mCountDownTimer.start();
                            state = 1;
                            break;

                    }


                }
            }
        });

        mProgressWheel.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mCountDownTimer.cancel();
                mProgressWheel.stopSpinning();
                mProgressWheel.setText("00:00:00");
                Toast.makeText(MainActivity.this, "Timer has been cancelled!!!!", Toast.LENGTH_LONG).show();
                return true;
            }
        });


    }


    public class brewCounter extends CountDownTimer {

        public brewCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);


        }

        @Override
        public void onTick(long millisUntilFinished) {
            progressOnTick(millisUntilFinished);

            timerInMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);

            long timerInSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


            //For the boil timer
            if (timersMashFinished) {


                if (timerInSeconds != 1) {
                     timeLeft = (int)timerInMinutes - itemTime;
                     mBoilPW.setText(timeLeft+"");

                    if (itemTime == timerInMinutes) {
                        index +=1;
                        int timeIn = Integer.parseInt(boilTimes.get(index)) - Integer.parseInt(boilTimes.get(index-1));
                        mBoilPW.setText(timeIn+"");
                        mSummary.setText("Add "+ boilSummary.get(index));
                        itemTime = Integer.parseInt(boilTimes.get(index));


                    }

                }else{
                    inLabel.setVisibility(View.INVISIBLE);
                    mBoilPW.setVisibility(View.INVISIBLE);
                    mSummary.setText("Finished!!");


                }
            }
        }

        @Override
        public void onFinish() {
            mProgressWheel.stopSpinning();

            mProgressWheel.setText("Done");

            state = 0;
            length = 0;
            index += 1;

            if (!timersMashFinished) {

                //Setting up boil timer and labels
                if (index >= mashLength.size()) {
                    length = 0;
                    index = 0;
                    mProgressWheel.setText("Done");
                    timersMashFinished = true;
                    mSummary.setText("Mash has finished");
                    int time =  mBoilTime - Integer.parseInt(boilTimes.get(index));
                    mBoilPW.setText(time +"");
                    mBoilPW.spin();
                    mBoilPW.setVisibility(View.VISIBLE);
                    mSummary.setVisibility(View.VISIBLE);
                    inLabel.setText("In..");
                    itemTime = Integer.parseInt(boilTimes.get(index));

                }else {
                    mProgressWheel.setText("Done");
                    inLabel.setText("Heat to " + mashTemp.get(index) + "\u2109");
                }
            }
            else{
                timersFinished = true;
            }


        }

    }

    private void progressOnTick(long millisUntilFinished) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        int progress = (int) (millisUntilFinished / 1000);

        mProgressWheel.setProgress(progress);
        mProgressWheel.setText(hms);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                boilTimers = (Boil)data.getSerializableExtra("boil");

                boilInfo = boilTimers.getTimers();
                for(HashMap<String, String> boilInt : boilInfo){
                    boilTimes.add(boilInt.get("KEY_TIME"));
                    boilSummary.add(boilInt.get("KEY_ADD_INFO"));

                }


            }
        }
        if (requestCode == 2){
            if(resultCode == RESULT_OK) {
                mashTimers = (Mash) data.getSerializableExtra("mash");
                Log.i(TAG, mashTimers+"");
                if (mashTimers != null) {
                    timersMashFinished = false;

                    mashSteps = mashTimers.getSteps();

                    for (HashMap<String, String> mashItem : mashSteps) {
                        mashTemp.add(mashItem.get("KEY_TEMP"));
                        mashLength.add(mashItem.get("KEY_MASH_LENGTH"));

                    }
                    progressOnTick(Long.parseLong(mashLength.get(index)));
                    inLabel.setText("Heat to " + mashTemp.get(index) + "\u2109");

                }

            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
