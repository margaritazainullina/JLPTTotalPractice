package ua.hneu.languagetrainer.pages.dictionary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.pages.SettingsActivity;

public class GiongoEntryDictionaryDetail extends Activity {
    public static Giongo curWord;
    public static int idx = -1;
    ExamplesListViewAdapter adapter;
    TextView giongoTextView;
    TextView translationTextView;
    ListView giongoExamplesListView;
    TextToVoiceMediaPlayer twmp;
    String phrase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.giongo_entry_dictionary_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            curWord = (Giongo) extras.get("entry");
        }


        // Initialize views
        giongoTextView = (TextView) findViewById(R.id.giongoTextView);
        translationTextView = (TextView) findViewById(R.id.translationTextView);
        giongoExamplesListView = (ListView) findViewById(R.id.giongoExamplesListView);

        // show first entry
        curWord = App.giongoWordsDictionary.get(0);
        idx = 0;

        // media player for playing example
        twmp = new TextToVoiceMediaPlayer();
        showEntry(curWord);

        giongoTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
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
    private void showEntry(Giongo currentWord) {
        // set giongo info to the texViews
        giongoTextView.setText(currentWord.getRule());
        translationTextView.setText(currentWord.translationsToString());

        // set color of entry
        if (App.isColored) {
            giongoTextView.setTextColor(currentWord.getIntColor());
        } else
            giongoTextView.setTextColor(Color.WHITE);

        // and write information to db
        currentWord.setLastView();
        currentWord.incrementShowntimes();
        App.gs.update(currentWord, getContentResolver());

        adapter = new ExamplesListViewAdapter(this,
                curWord.getAllExamplesText(), curWord.getAllExamplesRomaji(),
                curWord.getAllTranslations(), curWord.getIntColor());
        giongoExamplesListView.setAdapter(adapter);
    }


    public void onPlayClick1(View v) {
        // getting layout with text
        View v1 = (View) v.getParent();
        TextView textPart1 = (TextView) v1.findViewById(R.id.textPart1);
        TextView textPart2 = (TextView) v1.findViewById(R.id.textPart2);
        TextView textPart3 = (TextView) v1.findViewById(R.id.textPart3);
        phrase = (String) textPart1.getText() + textPart2.getText()
                + textPart3.getText();
        twmp.loadAndPlay(phrase, App.speechVolume, App.speechSpeed);
    }
}