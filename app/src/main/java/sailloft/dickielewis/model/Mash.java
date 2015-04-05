package sailloft.dickielewis.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by David's Laptop on 4/4/2015.
 */
public class Mash implements Serializable {

    private ArrayList<HashMap<String, String>> mSteps;

    private String[] mKeys;

    public ArrayList<HashMap<String, String>> getSteps() {
        return mSteps;
    }

    public void setSteps(ArrayList<HashMap<String, String>> steps) {
        mSteps = steps;
    }

    public String[] getKeys() {
        return mKeys;
    }

    public void setKeys(String[] keys) {
        mKeys = keys;
    }
}
