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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.ExampleAbstr;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;
import ua.hneu.languagetrainer.pages.counterwords.CounterWordsTestActivity;

public class CWEntryDictionaryDetail extends Activity {
    ExamplesListViewAdapter adapter;
    public static List<Integer> shownIndexes;
    public boolean isLast = true;
    public static CounterWord curWord;
    public static int idx = -1;
    TextToVoiceMediaPlayer twmp;

    TextView wordTextView;
    TextView hiraganaTextView;
    TextView romajiTextView;
    TextView translationTextView;
    ListView cwExamplesListView;
    Button prevButton;
    ArrayList<ExampleAbstr> examples;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.cw_entry_dictionary_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            curWord=(CounterWord)extras.get("entry");
        }


        // Initialize views
        wordTextView = (TextView) findViewById(R.id.wordTextView);
        hiraganaTextView = (TextView) findViewById(R.id.hiraganaTextView);
        romajiTextView = (TextView) findViewById(R.id.romajiTextView);
        translationTextView = (TextView) findViewById(R.id.translationTextView);
        cwExamplesListView= (ListView) findViewById(R.id.examplesListView);

        showEntry(curWord);

        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        hiraganaTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
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

    @SuppressLint("SimpleDateFormat")
    private void showEntry(CounterWord currentWord) {
        // set info to the texViews
        wordTextView.setText(currentWord.getWord());
        hiraganaTextView.setText(currentWord.getHiragana());
        romajiTextView.setText(currentWord.getRomaji());
        translationTextView.setText(currentWord.translationsToString());

        // set color of entry
        if(App.isColored){
            int color = curWord.getIntColor();
            wordTextView.setTextColor(color);
        }

        // and write information to db
        currentWord.setLastView();
        currentWord.incrementShowntimes();
        App.cws.update(currentWord, getContentResolver());
        if(App.isAutoplayed) {
            try {
                speakOut(currentWord);
            } catch (Exception e) {
                Log.e("Text to speech error", "Text to speech error");
            }
        }

        examples = new ArrayList<ExampleAbstr>();
        examples.addAll(App.gres.getAllExamplesWithWord(currentWord.getWord(), App.cr));
        examples.addAll(App.ges.getAllExamplesWithWord(currentWord.getWord(), App.cr));

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

        adapter = new ExamplesListViewAdapter(this,text,romaji,translation, 0);
        cwExamplesListView.setAdapter(adapter);
    }

    public void onPlayClick3(View v) {
        // getting layout with text
        speakOut(curWord);
    }
    private void speakOut(final CounterWord entry) {
        twmp.play(curWord.getHiragana());
    }


}
