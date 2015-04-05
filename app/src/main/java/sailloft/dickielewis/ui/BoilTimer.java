package sailloft.dickielewis.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.HashMap;

import sailloft.dickielewis.model.Boil;
import sailloft.dickielewis.adapters.BoilTimerListAdapter;
import sailloft.dickielewis.R;


public class BoilTimer extends ListActivity {
    Button mOkButton;
    Button mAddButton;
    private ArrayList<HashMap<String, String>>boilTimers;
    private HashMap boilTimer;
    public static final String TAG = BoilTimer.class.getSimpleName();
    private Boil timerBoil;
    private EditText boilLength;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boil_timer);
        boilTimers = new ArrayList<>();
        boilLength = (EditText)findViewById(R.id.boilLengthEditText);
        mAddButton = (Button) findViewById(R.id.addTimersButton);
        mOkButton = (Button) findViewById(R.id.okButton);



        final BoilTimerListAdapter adapter = new BoilTimerListAdapter(BoilTimer.this, boilTimers);
        setListAdapter(adapter);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] keys = {"KEY_TIME", "KEY_ADD_INFO"};
                timerBoil = new Boil();
                timerBoil.setTimers(boilTimers);
                timerBoil.setBoilTime(boilLength.getText().toString());
                timerBoil.setKeys(keys);

                Intent intent = new Intent(BoilTimer.this, MainActivity.class);

                intent.putExtra("boil",timerBoil);

                startActivity(intent);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder boilAlert = new AlertDialog.Builder(BoilTimer.this);
                LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.boil_dialog, null);
                boilAlert.setTitle("New Addition");
                boilAlert.setView(dialogView);
                final EditText mTime = (EditText) dialogView.findViewById(R.id.timeOfAddEditText);
                mTime.requestFocus();
                final EditText mAddInfo = (EditText) dialogView.findViewById(R.id.addInfoText);

                boilAlert.setNegativeButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String time = mTime.getText().toString();
                        String info = mAddInfo.getText().toString();

                        boilTimer = new HashMap<String, String>();

                        boilTimer.put("KEY_TIME", time);
                        boilTimer.put("KEY_ADD_INFO", info);


                        boilTimers.add(boilTimer);
                        Log.i(TAG, boilTimers.toString());
                        mTime.setText("");
                        mAddInfo.setText("");
                        adapter.notifyDataSetChanged();
                        Log.i(TAG, boilTimers.toString());

                    }
                });
                boilAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                boilAlert.show();


            }
        });




    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getListView().getAdapter() == null) {
            BoilTimerListAdapter adapter = new BoilTimerListAdapter(getListView()
                    .getContext(),boilTimers);
            setListAdapter(adapter);
        }
        else{
            ((BoilTimerListAdapter)getListView().getAdapter()).refill(boilTimers);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_boil_timer, menu);
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