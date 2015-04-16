package ua.hneu.languagetrainer.pages.dictionary;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.ExampleAbstr;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;

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
            entry = (VocabularyEntry) extras.get("entry");
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
        twmp.loadAndPlay(entry.getTranscription(), App.speechVolume, App.speechSpeed);
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

        levelTv.setText(getResources().getString(R.string.jlpt_level) + " " + entry.getLevel());

        // set word info to the texViews
        wordTextView.setText(entry.getKanji());
        transcriptionTextView.setText("[" + entry.getTranscription() + "]");
        if (App.isShowRomaji)
            romajiTextView.setText("- [" + entry.getRomaji() + "]");
        translationTextView.setText(entry.translationsToString());

        if (App.isAutoplayed && isNetworkAvailable()) {
            try {
                speakOut(entry);
            } catch (Exception e) {
                Log.e("Text to speech error", "Text to speech error");
            }
        }

        examples = new ArrayList<ExampleAbstr>();
        examples.addAll(App.gres.getAllExamplesWithWord(entry.getKanjiOrHiragana(), App.cr));
        examples.addAll(App.ges.getAllExamplesWithWord(entry.getKanjiOrHiragana(), App.cr));

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

        adapter1 = new ExamplesListViewAdapter(this, text, romaji, translation, 0);
        examplesListView.setAdapter(adapter1);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
