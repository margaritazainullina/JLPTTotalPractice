package ua.hneu.languagetrainer.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ua.hneu.languagetrainer.model.grammar.GrammarRule;

/**
 * Created by Margarita on 2015/02/13.
 */
public abstract class DictionaryAbstr {
    protected ArrayList<EntryAbstr> entries;

    public int size() {
        return entries.size();
    }

    public void sortByLastViewedTime() {
        try {
            Collections.sort(this.entries,
                    EntryAbstr.DictionaryEntryComparator.LAST_VIEWED);
        } catch (Exception e) {
            Log.e("sortByLastViewedTime",
                    e.getMessage() + " Caused:" + e.getCause());
        }
    }

    public void sortByPercentage() {
        try {
            Collections
                    .sort(this.entries,
                            EntryAbstr.DictionaryEntryComparator.LEARNED_PERCENTAGE);
        } catch (Exception e) {
            Log.e("sortByPercentage",
                    e.getMessage() + " Caused:" + e.getCause());
        }
    }

    public void sortByTimesShown() {
        try {
            Collections.sort(this.entries,
                    EntryAbstr.DictionaryEntryComparator.TIMES_SHOWN);
        } catch (Exception e) {
            Log.e("sortByTimesShown",
                    e.getMessage() + " Caused:" + e.getCause());
        }
    }

    public void sortRandomly() {
        try {
            Collections.sort(this.entries,
                    EntryAbstr.DictionaryEntryComparator.RANDOM);
        } catch (Exception e) {
            Log.e("sortRandomly", e.getMessage() + " Caused:" + e.getCause());
        }
    }

    public abstract DictionaryAbstr search(String query);

    //TODO: replace with  real in extended classes
    public ArrayList<String> allToString(){
        ArrayList<String> strings = new ArrayList<String>();
        for(EntryAbstr e: entries){
            strings.add(e.toString());
        }
        return strings;
    }


}
