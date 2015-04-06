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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.todddavies.components.progressbar.ProgressWheel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sailloft.dickielewis.model.Boil;
import sailloft.dickielewis.R;
import sailloft.dickielewis.model.Mash;


public class MainActivity extends ActionBarActivity {
    private LinearLayout mStepLayout;
    private ProgressWheel mProgressWheel;
    private TextView mSummary;
    private Button mBoilButton;
    private Button mMashButton;
    private Boil boilTimers;
    private Mash mashTimers;
    private int mBoilTime;
    private int state = 0;
    private int index = 0;
    private long length = 0;
    private long timerInMinutes;
    private boolean timersFinished = false;
    private brewCounter mCountDownTimer;
    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> boilInfo = new ArrayList<>();
    private long timeLeft;
    private int mLength = 0;
    private List<String> boilTimes;
    private List<String> boilSummary;
    private ArrayList<HashMap<String,String>> mashInfo = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boilTimes =  new ArrayList<String>();
        boilSummary = new ArrayList<>();
        mStepLayout = (LinearLayout) findViewById(R.id.linearStepLayout);
        mStepLayout.setVisibility(View.INVISIBLE);
        mProgressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
        mBoilButton = (Button) findViewById(R.id.boilButton);
        mMashButton =(Button)findViewById(R.id.mashButton);
        mSummary = (TextView)findViewById(R.id.summaryLabel);


        mBoilButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BoilTimer.class);
                startActivityForResult(intent, 1);
            }
        });
        mMashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MashTimer.class);
                startActivityForResult(intent, 2);
            }
        });

        mProgressWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.i(TAG, boilTimers+"");
                Log.i(TAG, mashTimers+"");

                try{sortTimerData(mashTimers, boilTimers);}
                catch (NullPointerException e){
                    Toast.makeText(MainActivity.this, "Timer has no data, ", Toast.LENGTH_LONG).show();
                    Log.i(TAG, e+"");
                }



                mBoilTime = Integer.parseInt(boilTimers.getBoilTime());

                state += 1;


                length = TimeUnit.MINUTES.toMillis(mBoilTime);


                Log.i(TAG, length + "");


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
            if(boilTimes.contains(timerInMinutes)){
                int itemIndex = boilTimes.indexOf(timerInMinutes);
                mSummary.setText(boilSummary.get(itemIndex+1));

            }


        }

        @Override
        public void onFinish() {
            mProgressWheel.stopSpinning();
            //mProgressWheel.setText("Done!");
            state = 0;
            length = 0;
            index += 1;

            //if (index >= timerLengths.length){
            //length = 0;
            //index = 0;
            mProgressWheel.setText("Finished!");
            timersFinished = true;
            //}


        }

    }

    private void progressOnTick(long millisUntilFinished) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));
        int progress = (int) (millisUntilFinished / 1000);
        timeLeft = millisUntilFinished;
        mProgressWheel.setProgress(progress);
        mProgressWheel.setText(hms);
    }

    private void sortTimerData(Mash mash, Boil boil) throws NullPointerException{

            mashInfo = mash.getSteps();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                boilTimers = (Boil) getIntent().getSerializableExtra("boil");
                boilInfo = boilTimers.getTimers();
                for(HashMap<String, String> boilInt : boilInfo){
                    boilTimes.add(boilInt.get("KEY_TIME"));
                    boilSummary.add(boilInt.get("KEY_ADD_INFO"));
                }

            }
        }
        if (requestCode == 2){
            mashTimers = (Mash)getIntent().getSerializableExtra("mash");


        }
        if (resultCode == RESULT_CANCELED) {
            //Write your code if there's no result
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
