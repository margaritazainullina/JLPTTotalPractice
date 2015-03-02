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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.ExampleAbstr;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.model.other.Giongo;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;

public class EntryDictionaryDetail extends FragmentActivity {
    //vocabulary fragment
    static TextView vocWordTextView;
    static TextView vocTranscriptionTextView;
    static TextView vocRomajiTextView;
    static TextView vocLevelTv;
    static TextView vocTranslationTextView;
    static TextToVoiceMediaPlayer twmp;
    static ListView vocExamplesListView;
    static ArrayList<ExampleAbstr> vocExamples;
    static ExamplesListViewAdapter vocAdapter1;
    public static VocabularyEntry vocEntry;

    //grammar fragment
    static TextView ruleTextView;
    static TextView descriptionTextView;
    static TextView grammarRomajiTextView;
    static TextView levelTv;
    static ListView grammarExamplesListView;
    static ExamplesListViewAdapter grammarAdapter;
    public static GrammarRule rule;

    //giongo fragment
    static TextView giongoTextView;
    static TextView translationTextView;
    static ListView giongoExamplesListView;
    static ArrayList<ExampleAbstr> giongoExamples;
    static ExamplesListViewAdapter giongoAdapter1;
    public static Giongo giongo;

    //counter words fragment
    static TextView cwWordTextView;
    static TextView cwHiraganaTextView;
    static TextView cwRomajiTextView;
    static TextView cwTranslationTextView;
    static ListView cwExamplesListView;
    public static CounterWord cw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_dictionary_detail);

        //initialise fragment elements
        vocWordTextView = (TextView)findViewById(R.id.voc_wordTextView);
        vocTranscriptionTextView = (TextView) findViewById(R.id.voc_transcriptionTextView);
        vocRomajiTextView = (TextView) findViewById(R.id.voc_romajiTextView);
        vocTranslationTextView = (TextView) findViewById(R.id.voc_translationTextView);
        vocExamplesListView = (ListView) findViewById(R.id.voc_examplesListView);
        vocLevelTv = (TextView) findViewById(R.id.voc_levelTv);

        ruleTextView = (TextView)findViewById(R.id.grammar_ruleTextView);
        descriptionTextView = (TextView) findViewById(R.id.grammar_descriptionTextView);
        levelTv = (TextView) findViewById(R.id.grammar_levelTv);
        grammarExamplesListView = (ListView) findViewById(R.id.grammar_grammarExamplesListView);

        giongoTextView = (TextView)findViewById(R.id.giongo_giongoTextView);
        translationTextView = (TextView) findViewById(R.id.giongo_translationTextView);
        giongoExamplesListView = (ListView) findViewById(R.id.giongo_giongoExamplesListView);

        cwWordTextView = (TextView) findViewById(R.id.cw_wordTextView);
        cwHiraganaTextView = (TextView) findViewById(R.id.cw_hiraganaTextView);
        cwRomajiTextView = (TextView) findViewById(R.id.cw_romajiTextView);
        cwTranslationTextView = (TextView) findViewById(R.id.cw_translationTextView);
        cwExamplesListView= (ListView) findViewById(R.id.cw_examplesListView);


        showEntry(App.allVocabularyDictionary.get(23));
        showEntry(App.allGrammarDictionary.get(5));

        vocWordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        vocTranscriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
    }

    @SuppressLint("SimpleDateFormat")
    private void showEntry(VocabularyEntry vocEntry) {

        vocLevelTv.setText(getResources().getString(R.string.jlpt_level)+" "+vocEntry.getLevel());

        // set word info to the texViews
        vocWordTextView.setText(vocEntry.getKanji());
        vocTranscriptionTextView.setText("["+vocEntry.getTranscription()+"]");
        if (App.isShowRomaji)
            vocRomajiTextView.setText("- ["+vocEntry.getRomaji()+"]");
        vocTranslationTextView.setText(vocEntry.translationsToString());

       /* if(App.isAutoplayed&&isNetworkAvailable()) {
            try {
                speakOut(entry);
            } catch (Exception e) {
                Log.e("Text to speech error", "Text to speech error");
            }
        }*/

        vocExamples = new  ArrayList<ExampleAbstr>();
        vocExamples.addAll(App.gres.getAllExamplesWithWord(vocEntry.getKanjiOrHiragana(), App.cr));
        vocExamples.addAll(App.ges.getAllExamplesWithWord(vocEntry.getKanjiOrHiragana(), App.cr));

        ArrayList<String> text = new ArrayList<String>();
        ArrayList<String> romaji = new ArrayList<String>();
        ArrayList<String> translation = new ArrayList<String>();
        for (ExampleAbstr ge : vocExamples) {
            text.add(ge.getText());
            romaji.add(ge.getRomaji());
            if (App.lang == App.Languages.ENG)
                translation.add(ge.getTranslationEng());
            else
                translation.add(ge.getTranslationRus());
        }

        vocAdapter1 = new ExamplesListViewAdapter(this,text,romaji,translation, 0);
        vocExamplesListView.setAdapter(vocAdapter1);
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

        rule = App.allGrammarDictionary.get(9);

        levelTv.setText(getResources().getString(R.string.jlpt_level)+" "+rule.getLevel());

        // set color of entry
        if(App.isColored) {
            ruleTextView.setTextColor(rule.getIntColor());
        }
        else
            ruleTextView.setTextColor(Color.WHITE);

        // and write information to db
        currentRule.setLastView();
        currentRule.incrementShowntimes();
        App.grs.update(currentRule, getContentResolver());

        grammarAdapter = new ExamplesListViewAdapter(this,
                rule.getAllExamplesText(), rule.getAllExamplesRomaji(),
                rule.getAllTranslations(), rule.getIntColor());
        grammarExamplesListView.setAdapter(grammarAdapter);

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

    public static class PlaceholderFragment1 extends Fragment {

        public PlaceholderFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.vocabulary_entry_dictionary_detail_fragment, container, false);

            /*Bundle extras = getIntent().getExtras();
            if (extras != null) {
                entry=(VocabularyEntry)extras.get("entry");
            }*/



            //actionBar.setDisplayHomeAsUpEnabled(true);
            return rootView;
        }
        private void speakOut(final VocabularyEntry entry) {
            twmp.loadAndPlay(entry.getTranscription(),  App.speechVolume,App.speechSpeed);
        }

        public void onPlayClick(View v) {
           // speakOut(vocEentry);
        }



        private boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    public static class PlaceholderFragment2 extends Fragment {

        public PlaceholderFragment2() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.grammar_entry_dictionary_detail_fragment, container, false);
            return rootView;
        }
    }
    public static class PlaceholderFragment3 extends Fragment {

        public PlaceholderFragment3() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.giongo_entry_dictionary_detail_fragment, container, false);
            return rootView;
        }
    }
    public static class PlaceholderFragment4 extends Fragment {

        public PlaceholderFragment4() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.cw_entry_dictionary_detail_fragment, container, false);
            return rootView;
        }
    }
}
