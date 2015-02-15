package ua.hneu.languagetrainer.pages.dictionary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.hneu.edu.languagetrainer.R;
import com.google.android.gms.ads.*;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.Utils;
import ua.hneu.languagetrainer.model.ExampleAbstr;
import ua.hneu.languagetrainer.model.grammar.GrammarExample;
import ua.hneu.languagetrainer.model.other.GiongoExample;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.MotionEvent;
import 	android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VocabularyEntryDictionaryDetail extends Activity {
    public static VocabularyEntry entry;
    public static int idx = -1;

    TextView wordTextView;
    TextView transcriptionTextView;
    TextView romajiTextView;
    TextView levelTv;
    TextView translationTextView;
    TextToVoiceMediaPlayer twmp;
    ListView examplesListView;
    ArrayList<ExampleAbstr> examples;

    ExamplesListViewAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocabulary_entry_dictionary_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            entry=(VocabularyEntry)extras.get("entry");
        }

        showEntry();

        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        transcriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.menu_statistics:
                intent = new Intent(this, LearningStatistics.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void speakOut(final VocabularyEntry entry) {
        twmp.play(entry.getTranscription());
    }

    public void onPlayClick(View v) {
        speakOut(entry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }


    @SuppressLint("SimpleDateFormat")
    private void showEntry() {

        wordTextView = (TextView) findViewById(R.id.wordTextView);
        transcriptionTextView = (TextView) findViewById(R.id.transcriptionTextView);
        romajiTextView = (TextView) findViewById(R.id.romajiTextView);
        translationTextView = (TextView) findViewById(R.id.translationTextView);
        examplesListView = (ListView) findViewById(R.id.examplesListView);
        levelTv = (TextView) findViewById(R.id.levelTv);

        levelTv.setText(getResources().getString(R.string.jlpt_level)+" "+entry.getLevel());

        // set word info to the texViews
        wordTextView.setText(entry.getKanji());
        transcriptionTextView.setText("["+entry.getTranscription()+"]");
        if (App.isShowRomaji)
            romajiTextView.setText("- ["+entry.getRomaji()+"]");
        translationTextView.setText(entry.translationsToString());

        if(App.isAutoplayed&&isNetworkAvailable()) {
            try {
                speakOut(entry);
            } catch (Exception e) {
                Log.e("Text to speech error", "Text to speech error");
            }
        }

        examples = new  ArrayList<ExampleAbstr>();
        examples.addAll(App.gres.getAllExamplesWithWord(entry.translationsToString(), App.cr));
        examples.addAll(App.ges.getAllExamplesWithWord(entry.translationsToString(), App.cr));

            ArrayList<String> text = new ArrayList<String>();
        ArrayList<String> romaji = new ArrayList<String>();
        ArrayList<String> translation = new ArrayList<String>();
            for (ExampleAbstr ge : examples) {
                text.add(ge.getText());
                romaji.add(ge.getRomaji());
                if (App.lang == App.Languages.ENG)
                    translation.add(ge.getTranslationEng());
                else
                    translation.add(ge.getTranslationRus());
            }

        adapter1 = new ExamplesListViewAdapter(this,text,romaji,translation, 0);
        examplesListView.setAdapter(adapter1);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
