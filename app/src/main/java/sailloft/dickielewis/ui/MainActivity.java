package sailloft.dickielewis.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import sailloft.dickielewis.R;
import sailloft.dickielewis.model.Boil;
import sailloft.dickielewis.model.Mash;
import sailloft.dickielewis.progressWheel.ProgressWheel;


public class MainActivity extends ActionBarActivity {
    private ProgressWheel mBoilPW;
    private ProgressWheel mProgressWheel;
    private TextView mSummary;

    private TextView inLabel;
    private Boil boilTimers;
    private Mash mashTimers;
    private int size;
    private int state = 0;
    private int index = 0;
    private long length = 0;
    private long timerInMinutes;
    private FloatingActionButton fabBoil;
    private FloatingActionButton fabMash;

    private boolean timersMashFinished = false;
    private brewCounter mCountDownTimer;
    public static final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> boilInfo = new ArrayList<>();
    private ArrayList<HashMap<String, String>> mashSteps = new ArrayList<>();
    private long timeLeft;
    private boolean timersFinished = false;
    private long itemTime =0;
    private long time;
    private List<String> boilTimes = new ArrayList<>();
    private List<Long> boilMillis = new ArrayList<>();
    private List<String> boilSummary = new ArrayList<>();
    private List<String> mashTemp = new ArrayList<>();
    private List<String> mashLength = new ArrayList<>();
    private MediaPlayer mp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alt_activity_main);
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mp = MediaPlayer.create(getApplicationContext(), notification);

        mProgressWheel = (ProgressWheel) findViewById(R.id.pw_spinner);
        inLabel = (TextView)findViewById(R.id.inLabel);
        mBoilPW = (ProgressWheel)findViewById(R.id.pw_boil);
        inLabel.setVisibility(View.VISIBLE);
        inLabel.setText("Add Timers to start....");
        fabBoil = (FloatingActionButton) findViewById(R.id.boilButton);
        fabMash = (FloatingActionButton)findViewById(R.id.mashButton);

        mBoilPW.setVisibility(View.INVISIBLE);

        mSummary = (TextView)findViewById(R.id.summaryLabel);
        mSummary.setVisibility(View.INVISIBLE);



        fabBoil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                mBoilPW.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(MainActivity.this, BoilTimer.class);

                startActivityForResult(intent, 1);
            }
        });
        fabMash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                mBoilPW.setVisibility(View.INVISIBLE);

                Intent intent = new Intent(MainActivity.this, MashTimer.class);
                startActivityForResult(intent, 2);
            }
        });

        mProgressWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                //checking if the timers have finished or have not been set
                if (timersFinished || !timersSet() ) {
                    if (!timersSet()){
                        Toast.makeText(MainActivity.this,
                                "Timers has not been set",
                                Toast.LENGTH_LONG).show();
                    }else {
                        mProgressWheel.startSpinning();
                        Toast.makeText(MainActivity.this,
                                "Timers have finished",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    state += 1;
                    if (timersMashFinished) {
                        mSummary.setText("Next: " + boilSummary.get(index));
                        length = TimeUnit.MINUTES.toMillis(Long.parseLong(boilTimers.getBoilTime()));



                    } else {
                        length = TimeUnit.MINUTES.toMillis(Integer.parseInt(mashLength.get(index)));
                        inLabel.setText("We are mashing, I hope you like mashing too ");

                    }
                    mProgressWheel.startSpinning();
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

                            mCountDownTimer = new brewCounter(time, 1000);
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
                v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                mCountDownTimer.cancel();
                mProgressWheel.stopSpinning();
                mProgressWheel.setText("00:00:00");
                mSummary.setText("Timer has been cancelled");
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
            progressOnTick(millisUntilFinished, mProgressWheel);
            time = millisUntilFinished;
            progressOnTick(timeLeft, mBoilPW);
            Log.i(TAG, timeLeft +"");
            timerInMinutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
            timeLeft = (millisUntilFinished - boilMillis.get(index)) - 1000;

            long timerInSeconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished);


            //For the boil timer
            if (timersMashFinished) {


                if (timerInSeconds != 1) {



                    if (timeLeft <= 0) {

                        if (index < size) {

                            index += 1;
                            mSummary.setText("Add " + boilSummary.get(index));
                            mp.start();



                        }
                        else{

                            mBoilPW.setVisibility(View.INVISIBLE);
                            inLabel.setVisibility(View.INVISIBLE);
                            mSummary.setText("All Additions added...");

                        }


                    }

                }else{
                    inLabel.setVisibility(View.INVISIBLE);
                    mBoilPW.setVisibility(View.INVISIBLE);


                }
            }
        }

        @Override
        public void onFinish() {
            mProgressWheel.stopSpinning();
            Log.i(TAG, timeLeft +"");
            mProgressWheel.setText("Done");
            mp.start();

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
                    mp.start();
                    mSummary.setText("Mash has finished");
                    timeLeft = TimeUnit.MINUTES.toMillis(Long.parseLong(boilTimers.getBoilTime())) - boilMillis.get(index);
                    mBoilPW.setText("");
                    
                    mBoilPW.setVisibility(View.VISIBLE);
                    mSummary.setVisibility(View.VISIBLE);
                    inLabel.setText("In..");
                    itemTime = Integer.parseInt(boilTimes.get(0));

                }else {
                    //still another step in mash timers
                    mProgressWheel.setText("Done");
                    inLabel.setText("Heat to " + mashTemp.get(index) + "\u2109");
                    mp.start();
                }
            }
            else{
                //both mash and boil timers have finished
                mSummary.setText("Finished!!");
                mp.start();
                timersFinished = true;
                index = 0;
            }


        }

    }

    private void progressOnTick(long millisUntilFinished, ProgressWheel progressWheel) {
        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished), TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)), TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        int progress = (int) (millisUntilFinished / 1000);

        progressWheel.setProgress(progress);
        progressWheel.setText(hms);


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
                    boilMillis.add(TimeUnit.MINUTES.toMillis(Integer.parseInt(boilInt.get("KEY_TIME"))));
                    boilSummary.add(boilInt.get("KEY_ADD_INFO"));

                }

                size = boilMillis.size() - 1;
                Log.i(TAG, boilMillis +"Size: " + size);
                timersFinished = false;


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
                    mProgressWheel.setText(mashLength.get(0));
                    inLabel.setVisibility(View.VISIBLE);
                    mSummary.setVisibility(View.INVISIBLE);
                    inLabel.setText("Heat to " + mashTemp.get(index) + "\u2109");

                }

            }
        }

    }
    private Boolean timersSet(){
        return mashTimers != null && boilTimers != null;

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
