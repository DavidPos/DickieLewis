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

import sailloft.dickielewis.R;
import sailloft.dickielewis.adapters.MashListAdapter;
import sailloft.dickielewis.model.Mash;


public class MashTimer extends ListActivity {
    Button mOkButton;
    Button mStepButton;
    private ArrayList<HashMap<String, String>> mashTimers;
    private HashMap mashTimer;
    public static final String TAG = MashTimer.class.getSimpleName();
    private Mash mMash;
    private EditText boilLength;

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

            mOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String [] keys = {"KEY_TEMP", "KEY_MASH_LENGTH"};
                    mMash = new Mash();
                    mMash.setSteps(mashTimers);
                    mMash.setKeys(keys);
                    Intent intent = new Intent(MashTimer.this, MainActivity.class);

                    intent.putExtra("mash", mMash);
                    startActivity(intent);
                }
            });
            mStepButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                            Log.i(TAG, mashTimers.toString());
                            mashStepTemp.setText("");
                            mashStepLength.setText("");
                            adapter.notifyDataSetChanged();
                            Log.i(TAG, mashTimers.toString());

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
