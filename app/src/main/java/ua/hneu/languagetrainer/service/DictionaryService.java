package ua.hneu.languagetrainer.service;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.android.gms.analytics.l;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.db.dao.DictionaryDAO;

/**
 * @author Margarita Zainullina <margarita.zainullina@gmail.com>
 * @version 1.0
 */
public class DictionaryService {
    boolean isFirstTimeCreated;
    DictionaryDAO d = new DictionaryDAO();

    /**
     * Inserts an VocabularyEntry instance to database
     *
     * @param ve
     *            VocabularyEntry instance to insert
     * @param cr
     *            content resolver to database
     */
    public void insert(String word, String lang, ContentResolver cr) {
        ContentValues values = new ContentValues();
        values.put(DictionaryDAO.WORD, word);
        values.put(DictionaryDAO.LANGUAGE, lang);
        cr.insert(DictionaryDAO.CONTENT_URI, values);
    }

    /**
     * Updates an VocabularyEntry instance in database
     *
     * @param ve
     *            VocabularyEntry instance to insert
     * @param cr
     *            content resolver to database
     */
    public void update(String word, int lang, int id, ContentResolver cr) {
        ContentValues values = new ContentValues();
        values.put(DictionaryDAO.WORD, word);
        values.put(DictionaryDAO.LANGUAGE, lang);
        cr.update(DictionaryDAO.CONTENT_URI, values, "_ID=" + id, null);
    }

    /**
     * Deletes all entries from Vocabulary table
     */
    public void emptyTable() {
        DictionaryDAO.getDb()
                .execSQL("delete from " + DictionaryDAO.TABLE_NAME);
    }

    /**
     * Creates Vocabulary table
     */
    public void createTable() {
        SQLiteDatabase db = DictionaryDAO.getDb();
        db.execSQL("CREATE TABLE  if not exists " + DictionaryDAO.TABLE_NAME
                + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DictionaryDAO.WORD + " TEXT, " + DictionaryDAO.LANGUAGE
                + " TEXT);");
    }

    /**
     * Drops Vocabulary table
     */
    public void dropTable() {
        DictionaryDAO.getDb().execSQL(
                "DROP TABLE if exists " + DictionaryDAO.TABLE_NAME + ";");
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
    public TreeSet<String> getListByLang(String lang, ContentResolver cr) {
        String[] col = { "WORD", "LANGUAGE" };
        Cursor c = cr.query(DictionaryDAO.CONTENT_URI, col, "LANGUAGE=\"" + lang+"\"", null,
                "WORD", null);
        c.moveToFirst();
        TreeSet<String> words=new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        while (!c.isAfterLast()) {
            words.add(c.getString(0).trim());
           // Log.d("getListByLang",c.getString(0));
            c.moveToNext();
        }
        return words;
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
                                  String lang, ContentResolver cr) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    assetManager.open(filepath)));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                this.insert(mLine, lang, cr);
            }
        } catch (IOException e) {
            Log.e("DictionaryService", e.getMessage() + " " + e.getCause());
        }
    }
}
