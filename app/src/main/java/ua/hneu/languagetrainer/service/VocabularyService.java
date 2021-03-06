package ua.hneu.languagetrainer.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.db.dao.VocabularyDAO;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * @author Margarita Zainullina <margarita.zainullina@gmail.com>
 * @version 1.0
 */
public class VocabularyService {
    boolean isFirstTimeCreated;

    /**
     * Inserts an VocabularyEntry instance to database
     *
     * @param ve
     *            VocabularyEntry instance to insert
     * @param cr
     *            content resolver to database
     */
    public void insert(VocabularyEntry ve, ContentResolver cr) {
        ContentValues values = new ContentValues();
        values.put(VocabularyDAO.KANJI, ve.getKanji());
        values.put(VocabularyDAO.LEVEL, ve.getLevel());
        values.put(VocabularyDAO.TRANSCRIPTION, ve.getTranscription());
        values.put(VocabularyDAO.ROMAJI, ve.getRomaji());
        values.put(VocabularyDAO.TRANSLATIONS, ve.translationsToString());
        values.put(VocabularyDAO.TRANSLATIONS_RUS, ve.translationsRusToString());
        values.put(VocabularyDAO.PERCENTAGE, ve.getLearnedPercentage());
        values.put(VocabularyDAO.LASTVIEW, ve.getLastview());
        values.put(VocabularyDAO.SHOWNTIMES, ve.getShownTimes());
        values.put(VocabularyDAO.COLOR, ve.getColor());
        cr.insert(VocabularyDAO.CONTENT_URI, values);
    }

    /**
     * Updates an VocabularyEntry instance in database
     *
     * @param ve
     *            VocabularyEntry instance to insert
     * @param cr
     *            content resolver to database
     */
    public void update(VocabularyEntry ve, ContentResolver cr) {
        ContentValues values = new ContentValues();
        values.put(VocabularyDAO.ID, ve.getId());
        values.put(VocabularyDAO.KANJI, ve.getKanji());
        values.put(VocabularyDAO.LEVEL, ve.getLevel());
        values.put(VocabularyDAO.TRANSCRIPTION, ve.getTranscription());
        values.put(VocabularyDAO.ROMAJI, ve.getRomaji());
        values.put(VocabularyDAO.TRANSLATIONS, ve.translationsToString());
        values.put(VocabularyDAO.TRANSLATIONS_RUS, ve.translationsRusToString());
        values.put(VocabularyDAO.PERCENTAGE, ve.getLearnedPercentage());
        values.put(VocabularyDAO.LASTVIEW, ve.getLastview());
        values.put(VocabularyDAO.SHOWNTIMES, ve.getShownTimes());
        values.put(VocabularyDAO.COLOR, ve.getColor());
        cr.update(VocabularyDAO.CONTENT_URI, values, "_ID=" + ve.getId(), null);
    }

    /**
     * Deletes all entries from Vocabulary table
     */
    public void emptyTable() {
        VocabularyDAO.getDb()
                .execSQL("delete from " + VocabularyDAO.TABLE_NAME);
    }

    /**
     * Creates Vocabulary table
     */
    public void createTable() {
        SQLiteDatabase db = VocabularyDAO.getDb();
        db.execSQL("CREATE TABLE  if not exists " + VocabularyDAO.TABLE_NAME
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + VocabularyDAO.KANJI + " TEXT, " + VocabularyDAO.LEVEL
                + " TEXT, " + VocabularyDAO.TRANSCRIPTION + " TEXT, "
                + VocabularyDAO.ROMAJI + " TEXT, " + VocabularyDAO.TRANSLATIONS
                + " TEXT, " + VocabularyDAO.TRANSLATIONS_RUS + " TEXT, "
                + VocabularyDAO.PERCENTAGE + " REAL, " + VocabularyDAO.LASTVIEW
                + " DATETIME," + VocabularyDAO.SHOWNTIMES + " INTEGER, "
                + VocabularyDAO.COLOR + " TEXT);");
    }

    /**
     * Drops Vocabulary table
     */
    public void dropTable() {
        VocabularyDAO.getDb().execSQL(
                "DROP TABLE if exists " + VocabularyDAO.TABLE_NAME + ";");
    }

    /**
     * Return VocabularyEntry by it's id
     *
     * @param id
     *            VocabularyEntry id
     * @param cr
     *            content resolver to database
     */
    @SuppressLint("NewApi")
    public static VocabularyEntry getEntryById(int id, ContentResolver cr) {
        String[] col = { "KANJI", "LEVEL", "TRANSCRIPTION", "ROMAJI",
                "TRANSLATIONS", "TRANSLATIONS_RUS", "PERCENTAGE", "LASTVIEW",
                "SHOWNTIMES", "COLOR" };
        Cursor c = cr.query(VocabularyDAO.CONTENT_URI, col, "_ID=" + id, null,
                null, null);
        c.moveToFirst();

        VocabularyEntry ve;

        String kanji = "";
        int level = 0;
        String transcription = "";
        String romaji = "";
        String translationsStr = "";
        String translationsStrRus = "";
        int percentage = 0;
        String lastview = "";
        int showntimes = 0;
        String color = "";

        while (!c.isAfterLast()) {
            id = c.getInt(0);
            kanji = c.getString(1).trim();
            level = c.getInt(2);
            transcription = c.getString(3).trim();
            romaji = c.getString(4).trim();
            translationsStr = c.getString(5).trim();
            translationsStrRus = c.getString(6).trim();
            percentage = c.getInt(7);
            lastview = c.getString(8).trim();
            showntimes = c.getInt(9);
            color = c.getString(10).trim();
            c.moveToNext();
        }

        List<String> translations1 = new ArrayList<String>();
        List<String> translations2 = new ArrayList<String>();

        for(String s:translationsStr.split(";")){
            s = s.trim();
            s = s.replaceAll("\\(\\d\\)", "");
            s = s.replaceAll("^(a||the||to)\\s", "");
            s = s.replaceAll("\\((.*?)\\)", "");
            s = s.replaceAll("^(be||become||being)\\s", "");
            s = s.replaceAll("\\d(\\.\\s*)\\w", "");
            s = s.trim();
            translations1.add(s);
        }
        for(String s:translationsStrRus.split(";")){
            s = s.trim();
            s = s.replaceAll("\\(\\d\\)", "");
            s = s.replaceAll("\\d(\\.\\s*)\\w", "");
            s = s.trim();
            translations2.add(s);
        }

        ve = new VocabularyEntry(id, kanji, level, transcription, romaji,
                translations1, translations2, translationsStr, translationsStrRus,
                percentage, lastview, showntimes,color);
        c.close();
        return ve;
    }

    /**
     * Inserts all entries divided by tabs from assets file
     *
     * @param filepath
     *            path to assets file
     * @param assetManager
     *            assetManager from activity context
     * @param level
     *            target level
     * @param cr
     *            content resolver to database
     */
    public void bulkInsertFromCSV(String filepath, AssetManager assetManager,
                                  int level, ContentResolver cr) {

        ArrayList<String[]> entries1 = new ArrayList<String[]>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    assetManager.open(filepath)));
            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                // 3 columns separated by tabulation characters
                if (mLine != null) {
                    String[] parts = mLine.split("\t");
                    entries1.add(parts);
                }
            }
        } catch (IOException e) {
            Log.e("VocabularyService", e.getMessage() + " " + e.getCause());
        }

        for (final String[] entry : entries1) {
            String kanji = entry[0];
            String romaji = entry[2];
            String transcription = entry[1];
            List<String> translations = new ArrayList<>();
            List<String> translationsRus= new ArrayList<>();
            for(String s : entry[3].split(";")){
                s = s.trim();
                s = s.replaceAll("\\((.*?)\\)", "");
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("^(a|an|the|to)\\s", "");
                s = s.replaceAll("^(be|become|being)\\s", "");
                s = s.trim();
                translations.add(s);
            }
            for(String s : entry[4].split(";")){
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("^(a|the)\\s", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translationsRus.add(s);
            }


            // setting random colors
            Random r = new Random();
            int r1 = r.nextInt(5);
            String color = "";
            switch (r1) {
                case 0: {
                    color = "138,213,240";
                    break;
                }
                case 1: {
                    color = "214,173,235";
                    break;
                }
                case 2: {
                    color = "197,226,109";
                    break;
                }

                case 3: {
                    color = "255,217,128";
                    break;
                }
                case 4: {
                    color = "255,148,148";
                    break;
                }
            }

            VocabularyEntry de = new VocabularyEntry(0, kanji, level,
                    transcription, romaji, translations, translationsRus, entry[3], entry[4], 0,
                    "", 0, color);
            this.insert(de, cr);
        }
    }

    /**
     * Returns VocabularyDictionary to store it in the App class
     *
     * @param level
     *            target level
     * @param numberWordsInCurrentDict
     *            number of entries to select for learning
     * @param contentResolver
     *            content resolver to database
     * @return currentDict CounterWordsDictionary with CounterWords entries
     */
    public static VocabularyDictionary createCurrentDictionary(int level,
                                                               int numberWordsInCurrentDict, ContentResolver contentResolver) {
        if (App.allVocabularyDictionary == null) {
            App.allVocabularyDictionary = new VocabularyDictionary();
            App.allVocabularyDictionary = selectAllEntriesOflevel(level,
                    contentResolver);
        }
        VocabularyDictionary currentDict = new VocabularyDictionary();
        // if words have never been showed - set entries randomly
        if (App.userInfo.getIsVocLaunchedFirstTime() == 1) {
            currentDict.getEntries().addAll(
                    App.allVocabularyDictionary.getRandomEntries(
                            App.numberOfEntriesInCurrentDict, false));
            App.userInfo.setIsVocLaunchedFirstTime(0);
        } else {
            currentDict = VocabularyService.getNLastViewedEntries(
                    App.numberOfEntriesInCurrentDict, App.userInfo.getLevel(), contentResolver);
        }
        return currentDict;
    }

    public static VocabularyDictionary allEntriesDictionary(ContentResolver contentResolver) {
        return selectAllEntriesOflevel(-1,contentResolver);
    }

    /**
     * Selects all words of current level
     *
     * @param level
     *            target level, if -1 - returns entries of all levels
     * @param contentResolver
     *            content resolver to database
     * @return currentDict CounterWordsDictionary with CounterWords entries
     */
    public static VocabularyDictionary selectAllEntriesOflevel(int level,
                                                               ContentResolver contentResolver) {
        VocabularyDictionary wd = new VocabularyDictionary();

        String[] selectionArgs = { VocabularyDAO.ID, VocabularyDAO.KANJI,
                VocabularyDAO.LEVEL, VocabularyDAO.TRANSCRIPTION,
                VocabularyDAO.ROMAJI, VocabularyDAO.TRANSLATIONS,
                VocabularyDAO.TRANSLATIONS_RUS, VocabularyDAO.PERCENTAGE,
                VocabularyDAO.LASTVIEW, VocabularyDAO.SHOWNTIMES,
                VocabularyDAO.COLOR };
        Cursor c;
        if(level!=-1)
            c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                    selectionArgs, "level=" + level, null, null);
        else c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                selectionArgs, null, null, null);

        c.moveToFirst();
        int id = 0;
        String kanji = "";
        int level1 = 0;
        String transcription = "";
        String romaji = "";
        String translationsStr = "";
        String translationsStrRus = "";
        double percentage = 0;
        String lastview = "";
        int showntimes = 0;
        String color = "";

        while (!c.isAfterLast()) {
            id = c.getInt(0);
            kanji = c.getString(1);
            level1= c.getInt(2);
            transcription = c.getString(3);
            romaji = c.getString(4);
            translationsStr = c.getString(5);
            translationsStrRus = c.getString(6);
            percentage = c.getDouble(7);
            lastview = c.getString(8);
            showntimes = c.getInt(9);
            color = c.getString(10);
            c.moveToNext();

            HashSet<String> translations1 = new HashSet<String>();
            HashSet<String> translations2 = new HashSet<String>();



            /*for(String s:translationsStr.split(";")){
               // s = s.trim().replaceAll("\\(\\d\\)", "").replaceAll("^(a||the||to||be||become||being)\\s", "").replaceAll("\\((.*?)\\)", "").replaceAll("\\d(\\.\\s*)\\w", "");
                translations1.add(s.replaceAll("^\\s*(a||the||to||be||become||being)\\s+", "").replaceAll("\\((.*?)\\)", "").trim());
            }
            for(String s:translationsStrRus.split(";")){
                s = s.replaceAll("\\(\\d\\)", "").replaceAll("\\d(\\.\\s*)\\w", "");
                translations2.add(s.trim());
            }*/

            wd.add( new VocabularyEntry(id, kanji, level1,
                    transcription, romaji, new ArrayList<String>(translations1), new ArrayList<String>(translations2), translationsStr, translationsStrRus,
                    percentage, lastview, showntimes, color));
        }
        c.close();
        return wd;
    }


    public static VocabularyDictionary selectTestEntries(ContentResolver contentResolver) {
        VocabularyDictionary wd = new VocabularyDictionary();

        String[] selectionArgs = { VocabularyDAO.ID, VocabularyDAO.KANJI,
                VocabularyDAO.LEVEL, VocabularyDAO.TRANSCRIPTION,
                VocabularyDAO.ROMAJI, VocabularyDAO.TRANSLATIONS,
                VocabularyDAO.TRANSLATIONS_RUS, VocabularyDAO.PERCENTAGE,
                VocabularyDAO.LASTVIEW, VocabularyDAO.SHOWNTIMES,
                VocabularyDAO.COLOR };
        Cursor c;
        c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                selectionArgs, null, null, null);
        int y=0;
        c.moveToFirst();
        int id = 0;
        String kanji = "";
        String transcription = "";
        String romaji = "";
        String translationsStr = "";
        String translationsStrRus = "";
        double percentage = 0;
        String lastview = "";
        int showntimes = 0;
        String color = "";

        while (!c.isAfterLast()) {
            y++;
            id = c.getInt(0);
            kanji = c.getString(1);
            transcription = c.getString(3);
            romaji = c.getString(4);
            translationsStr = c.getString(5);
            translationsStrRus = c.getString(6);
            percentage = c.getDouble(7);
            lastview = c.getString(8);
            showntimes = c.getInt(9);
            color = c.getString(10);
            c.moveToNext();

            HashSet<String> translations1 = new HashSet<String>();
            HashSet<String> translations2 = new HashSet<String>();

            for(String s:translationsStr.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("\\((.*?)\\)", "");
                s = s.replaceAll("^(a||the||to)\\s", "");
                s = s.replaceAll("^(be||become||being)\\s", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations1.add(s);
            }
            for(String s:translationsStrRus.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations2.add(s);
            }

            VocabularyEntry de = new VocabularyEntry(id, kanji, 0,
                    transcription, romaji, new ArrayList<String>(translations1), new ArrayList<String>(translations1), translationsStr, translationsStrRus,
                    percentage, lastview, showntimes, color);

            wd.add(de);
            if(y>20) break;
        }
        c.close();
        return wd;
    }

    public static VocabularyDictionary getNLastViewedEntries(int size, int level,
                                                             ContentResolver contentResolver){
        VocabularyDictionary lastViewedEntries = new VocabularyDictionary();

        String[] selectionArgs = { VocabularyDAO.ID, VocabularyDAO.KANJI,
                VocabularyDAO.LEVEL, VocabularyDAO.TRANSCRIPTION,
                VocabularyDAO.ROMAJI, VocabularyDAO.TRANSLATIONS,
                VocabularyDAO.TRANSLATIONS_RUS, VocabularyDAO.PERCENTAGE,
                VocabularyDAO.LASTVIEW, VocabularyDAO.SHOWNTIMES,
                VocabularyDAO.COLOR };

        final Cursor c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                selectionArgs, VocabularyDAO.PERCENTAGE + "<1 AND " + VocabularyDAO.LEVEL + "=" + level, null,
                VocabularyDAO.LASTVIEW + " ASC limit " + size);

        c.moveToFirst();
        int id = 0;
        String kanji = "";
        String transcription = "";
        String romaji = "";
        String translationsStr = "";
        String translationsStrRus = "";
        double percentage = 0;
        String lastview = "";
        int showntimes = 0;
        String color = "";

        while (!c.isAfterLast()) {
            id = c.getInt(0);
            kanji = c.getString(1);
            transcription = c.getString(3);
            romaji = c.getString(4);
            translationsStr = c.getString(5);
            translationsStrRus = c.getString(6);
            percentage = c.getDouble(7);
            lastview = c.getString(8);
            showntimes = c.getInt(9);
            color = c.getString(10);
            c.moveToNext();

            List<String> translations1 = new ArrayList<String>();
            List<String> translations2 = new ArrayList<String>();

            for(String s:translationsStr.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("^(a||the||to)\\s", "");
                s = s.replaceAll("\\((.*?)\\)", "");
                s = s.replaceAll("^(be||become||being)\\s", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations1.add(s);
            }
            for(String s:translationsStrRus.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations2.add(s);
            }

            VocabularyEntry de = new VocabularyEntry(id, kanji, level,
                    transcription, romaji, translations1, translations2, translationsStr, translationsStrRus,
                    percentage, lastview, showntimes, color);
            lastViewedEntries.add(de);
        }
        c.close();
        return lastViewedEntries;
    }

    /**
     * Returns number of all words in level
     *
     * @param contentResolver
     *            content resolver to database
     * @return num number of all counter words in table
     */
    public int getNumberOfWordsInLevel(int level,
                                       ContentResolver contentResolver) {
        Cursor countCursor = contentResolver.query(VocabularyDAO.CONTENT_URI,
                new String[] { "count(*) AS count" }, VocabularyDAO.LEVEL + "="
                        + level + "", null, null);

        countCursor.moveToFirst();
        int num = countCursor.getInt(0);
        countCursor.close();
        return num;
    }

    // just for test! TODO:remove after testing!
    public void makeProgress(ContentResolver cr) {
        Random r = new Random();
        int i=0;
        for (VocabularyEntry e : App.allVocabularyDictionary.getEntries()) {
            int rr = r.nextInt(100);
            if (rr < 99){
                i++;
                e.setLearnedPercentage(1);
                update(e, cr);
            }
            else
            {
                e.setLearnedPercentage(0.9);
                update(e, cr);
            }
        }

        App.userInfo.setLearnedVocabulary(i);
        App.us.update(App.userInfo, cr);

    }

    public static VocabularyDictionary getNLeastLearned(int size, int level, boolean onlyWithKanji, ContentResolver contentResolver){

        VocabularyDictionary lastViewedEntries = new VocabularyDictionary();

        String[] selectionArgs = { VocabularyDAO.ID, VocabularyDAO.KANJI,
                VocabularyDAO.LEVEL, VocabularyDAO.TRANSCRIPTION,
                VocabularyDAO.ROMAJI, VocabularyDAO.TRANSLATIONS,
                VocabularyDAO.TRANSLATIONS_RUS, VocabularyDAO.PERCENTAGE,
                VocabularyDAO.LASTVIEW, VocabularyDAO.SHOWNTIMES,
                VocabularyDAO.COLOR };

        final Cursor c;

        if(onlyWithKanji)
            c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                    selectionArgs, VocabularyDAO.PERCENTAGE + "<1 AND "+ VocabularyDAO.LEVEL+"="+level+" AND "+VocabularyDAO.KANJI+" != ''" , null,
                    VocabularyDAO.PERCENTAGE + " ASC limit " + size);
        else
            c = contentResolver.query(VocabularyDAO.CONTENT_URI,
                    selectionArgs, VocabularyDAO.PERCENTAGE + "<1 AND "+ VocabularyDAO.LEVEL+"="+level, null,
                    VocabularyDAO.PERCENTAGE + " ASC limit " + size);

        c.moveToFirst();
        int id = 0;
        String kanji = "";
        String transcription = "";
        String romaji = "";
        String translationsStr = "";
        String translationsStrRus = "";
        double percentage = 0;
        String lastview = "";
        int showntimes = 0;
        String color = "";

        while (!c.isAfterLast()) {
            id = c.getInt(0);
            kanji = c.getString(1);
            transcription = c.getString(3);
            romaji = c.getString(4);
            translationsStr = c.getString(5);
            translationsStrRus = c.getString(6);
            percentage = c.getDouble(7);
            lastview = c.getString(8);
            showntimes = c.getInt(9);
            color = c.getString(10);
            c.moveToNext();

            HashSet<String> translations1 = new HashSet<String>();
            HashSet<String> translations2 = new HashSet<String>();

            for(String s:translationsStr.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("\\((.*?)\\)", "");
                s = s.replaceAll("^(a||the||to)\\s", "");
                s = s.replaceAll("^(be||become||being)\\s", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations1.add(s);
            }
            for(String s:translationsStrRus.split(";")){
                s = s.trim();
                s = s.replaceAll("\\(\\d\\)", "");
                s = s.replaceAll("\\d(\\.\\s*)\\w", "");
                s = s.trim();
                translations2.add(s);
            }

            VocabularyEntry de = new VocabularyEntry(id, kanji, level,
                    transcription, romaji, new ArrayList<String>(translations1), new ArrayList<String>(translations1), translationsStr, translationsStrRus,
                    percentage, lastview, showntimes, color);
            lastViewedEntries.add(de);
        }
        c.close();
        return lastViewedEntries;
    }
}
