package ua.hneu.languagetrainer.model;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Random;

import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;

/**
 * Created by Margarita on 2015/02/13.
 */
public abstract class EntryAbstr implements Comparable{
    protected int id;
    protected String kanji;
    protected int level;
    protected String lastview;
    protected int shownTimes;
    protected double learnedPercentage;
    protected String color;

    public abstract String translationsToString();

    public enum DictionaryEntryComparator implements Comparator<EntryAbstr> {
        ALPH_JAP {
            public int compare(EntryAbstr de1, EntryAbstr de2) {
                String s1= de1.toString();
                String s2=de2.toString();
                return (s1.compareTo(s2));
            }
        },

        ALPH_TRANSLATED {
            public int compare(EntryAbstr de1, EntryAbstr de2) {
            String s1= de1.translationsToString();
            String s2=de2.translationsToString();
            return (s1.compareTo(s2));
            }
        },

        LAST_VIEWED {
            @SuppressLint("SimpleDateFormat")
            public int compare(EntryAbstr de1, EntryAbstr de2) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");
                boolean isAfter = false;
                try {
                    if (de1.lastview.isEmpty()
                            || de2.lastview.isEmpty()) {
                        // gets randomly - entries wont' be in a row
                        isAfter = (Math.random() < 0.5);
                    } else {
                        Log.i("DictionaryEntryComparator",
                                "compared: " + de1.lastview + " and "
                                        + de2.lastview);
                        Date date1 = dateFormat.parse(de1.lastview);
                        Date date2 = dateFormat.parse(de2.lastview);
                        isAfter = (date1.after(date2));
                    }
                } catch (ParseException e) {
                    Log.e("DictionaryEntryComparator", "error in comparison: "
                            + de1.lastview + " and " + de2.lastview);
                    return 1;
                }
                if (isAfter)
                    return -1;
                else
                    return 1;
            }
        },

        LEARNED_PERCENTAGE {
            public int compare(EntryAbstr de1, EntryAbstr de2) {
                if (de1.learnedPercentage > de2.learnedPercentage)
                    return 1;
                else
                    return -1;
            }
        },
        TIMES_SHOWN {
            public int compare(EntryAbstr de1, EntryAbstr de2) {
                if (de1.shownTimes > de2.shownTimes)
                    return 1;
                else
                    return -1;
            }
        },
        RANDOM {
            public int compare(EntryAbstr de1, EntryAbstr de2) {
                if (Math.random() < 0.5)
                    return -1;
                else
                    return 1;
            }
        };
    }

}
