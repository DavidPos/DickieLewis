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
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import sailloft.dickielewis.R;
import sailloft.dickielewis.adapters.MashListAdapter;
import sailloft.dickielewis.model.Mash;
import sailloft.dickielewis.utility.SwipeDismissListViewTouchListener;


public class MashTimer extends ListActivity {
    Button mOkButton;
    Button mStepButton;
    private ArrayList<HashMap<String, String>> mashTimers;
    private HashMap mashTimer;
    public static final String TAG = MashTimer.class.getSimpleName();
    private Mash mMash;
    private EditText boilLength;
    private Integer firstValue;
    private Integer secondValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mash_timer);

            mashTimers = new ArrayList<>();
            boilLength = (EditText)findViewById(R.id.boilLengthEditText);
            mStepButton = (Button) findViewById(R.id.addStepButton);
            mOkButton = (Button) findViewById(R.id.okButton);

            final MashListAdapter adapter = new MashListAdapter(MashTimer.this, mashTimers);
            setListAdapter(adapter);
        ListView listView = getListView();
        // Create a ListView-specific touch listener. ListViews are given special treatment because
        // by default they handle touches for their list items... i.e. they're in charge of drawing
        // the pressed state (the list selector), handling list item clicks, etc.
        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listView,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position));
                                }
                                Collections.sort(mashTimers, new MapComparator("KEY_TEMP"));
                                adapter.notifyDataSetChanged();
                            }
                        });
        listView.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        listView.setOnScrollListener(touchListener.makeScrollListener());

            mOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    if (mashTimers.size() == 0) {
                        Toast.makeText(MashTimer.this, "No steps have been set", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MashTimer.this, MainActivity.class);
                        startActivity(intent);


                    }else {
                        String[] keys = {"KEY_TEMP", "KEY_MASH_LENGTH"};
                        mMash = new Mash();
                        mMash.setSteps(mashTimers);
                        mMash.setKeys(keys);
                        Intent intent = new Intent(MashTimer.this, MainActivity.class);

                        intent.putExtra("mash", mMash);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }
            });
            mStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    v.playSoundEffect(android.view.SoundEffectConstants.CLICK);
                    final AlertDialog.Builder mashAlert = new AlertDialog.Builder(MashTimer.this);
                    LayoutInflater inflater = getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.mash_dialog, null);
                    mashAlert.setTitle("Mash Step");
                    mashAlert.setView(dialogView);
                    final EditText mashStepTemp = (EditText) dialogView.findViewById(R.id.tempMashEditText);
                    mashStepTemp.requestFocus();
                    final EditText mashStepLength = (EditText) dialogView.findViewById(R.id.lengthMashText);

                    mashAlert.setNegativeButton("Add", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            String temp = mashStepTemp.getText().toString();
                            String length = mashStepLength.getText().toString();

                            mashTimer = new HashMap<String, String>();

                            mashTimer.put("KEY_TEMP", temp);
                            mashTimer.put("KEY_MASH_LENGTH", length);


                            mashTimers.add(mashTimer);
                            Collections.sort(mashTimers, new MapComparator("KEY_TEMP"));
                            Log.i(TAG,mashTimers +"");
                            mashStepTemp.setText("");
                            mashStepLength.setText("");
                            adapter.notifyDataSetChanged();


                        }
                    });
                    mashAlert.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    mashAlert.show();


                }
            });

        }
    class MapComparator implements Comparator<HashMap<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(HashMap<String, String> first,
                           HashMap<String, String> second)
        {
            // TODO: Null checking, both for maps and values
            firstValue = Integer.parseInt(first.get(key));
            secondValue = Integer.parseInt(second.get(key));
            return firstValue.compareTo(secondValue);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mash_timer, menu);
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
