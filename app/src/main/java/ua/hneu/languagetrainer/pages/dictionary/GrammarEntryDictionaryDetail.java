package ua.hneu.languagetrainer.pages.dictionary;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;

public class GrammarEntryDictionaryDetail extends Activity {
    ExamplesListViewAdapter adapter;
    public static GrammarRule curRule;
    public static int idx = -1;

    TextView ruleTextView;
    TextView descriptionTextView;
    TextView levelTv;
    ListView grammarExamplesListView;
    TextToVoiceMediaPlayer twmp;
    String phrase = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grammar_entry_dictionary_detail);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            curRule=(GrammarRule)extras.get("entry");
        }


        // Initialize views
        ruleTextView = (TextView) findViewById(R.id.ruleTextView);
        descriptionTextView = (TextView) findViewById(R.id.transcriptionTextView);
        grammarExamplesListView = (ListView) findViewById(R.id.grammarExamplesListView);

        // media player for playing example
        twmp = new TextToVoiceMediaPlayer();
        showEntry(curRule);
        ruleTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
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
    private void showEntry(GrammarRule currentRule) {
        ruleTextView = (TextView) findViewById(R.id.ruleTextView);
        descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        grammarExamplesListView = (ListView) findViewById(R.id.grammarExamplesListView);
        // set grammar info to the texViews
        ruleTextView.setText(currentRule.getRule());
        descriptionTextView.setText(currentRule.getDescription());
        levelTv = (TextView) findViewById(R.id.levelTv);

        levelTv.setText(getResources().getString(R.string.jlpt_level)+" "+curRule.getLevel());

        // set color of entry
        if(App.isColored) {
            ruleTextView.setTextColor(curRule.getIntColor());
        }
        else
            ruleTextView.setTextColor(Color.WHITE);

        // and write information to db
        currentRule.setLastView();
        currentRule.incrementShowntimes();
        App.grs.update(currentRule, getContentResolver());

        adapter = new ExamplesListViewAdapter(this,
                curRule.getAllExamplesText(), curRule.getAllExamplesRomaji(),
                curRule.getAllTranslations(), curRule.getIntColor());
        grammarExamplesListView.setAdapter(adapter);

    }


    public void onPlayClick1(View v) {
        // getting layout with text
        View v1 = (View) v.getParent();
        TextView textPart1 = (TextView) v1.findViewById(R.id.textPart1);
        TextView textPart2 = (TextView) v1.findViewById(R.id.textPart2);
        TextView textPart3 = (TextView) v1.findViewById(R.id.textPart3);
        phrase = (String) textPart1.getText() + textPart2.getText()
                + textPart3.getText();
        twmp.play(phrase);
    }
}