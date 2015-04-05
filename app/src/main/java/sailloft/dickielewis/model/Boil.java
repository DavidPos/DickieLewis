package sailloft.dickielewis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David's Laptop on 3/29/2015.
 */
public class Boil implements Serializable {
    private String mBoilTime;
    private ArrayList<HashMap<String, String>> mTimers;

    private String[] mKeys;

    public String[] getKeys() {
        return mKeys;
    }

    public void setKeys(String[] keys) {
        mKeys = keys;
    }

    public ArrayList<HashMap<String, String>> getTimers() {
        return mTimers;
    }

    public void setTimers(ArrayList<HashMap<String, String>> timers) {
        mTimers = timers;
    }


    public String getBoilTime() {
        return mBoilTime;
    }

    public void setBoilTime(String boilTime) {
        mBoilTime = boilTime;
    }
}





