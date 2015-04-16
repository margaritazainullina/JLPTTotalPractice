package ua.hneu.languagetrainer.pages.dictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    public static VocabularyEntry vocEntry;
    public static GrammarRule rule;
    public static Giongo giongo;
    public static CounterWord cw;
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
    //grammar fragment
    static TextView ruleTextView;
    static TextView descriptionTextView;
    static TextView grammarRomajiTextView;
    static TextView levelTv;
    static ListView grammarExamplesListView;
    static ExamplesListViewAdapter grammarAdapter;
    //giongo fragment
    static TextView giongoTextView;
    static TextView translationTextView;
    static ListView giongoExamplesListView;
    static ArrayList<ExampleAbstr> giongoExamples;
    static ExamplesListViewAdapter giongoAdapter;
    //counter words fragment
    static TextView cwWordTextView;
    static TextView cwHiraganaTextView;
    static TextView cwRomajiTextView;
    static TextView cwTranslationTextView;
    static ListView cwExamplesListView;
    static ExamplesListViewAdapter cwAdapter;
    static ArrayList<ExampleAbstr> cwExamples;
    static ExamplesListViewAdapter cwAdapter1;
    static Button vocButton;
    static Button cwButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entry_dictionary_detail);

        //initialise fragment elements
        vocWordTextView = (TextView) findViewById(R.id.voc_wordTextView);
        vocTranscriptionTextView = (TextView) findViewById(R.id.voc_transcriptionTextView);
        vocRomajiTextView = (TextView) findViewById(R.id.voc_romajiTextView);
        vocTranslationTextView = (TextView) findViewById(R.id.voc_translationTextView);
        vocExamplesListView = (ListView) findViewById(R.id.voc_examplesListView);
        vocLevelTv = (TextView) findViewById(R.id.voc_levelTv);

        ruleTextView = (TextView) findViewById(R.id.grammar_ruleTextView);
        descriptionTextView = (TextView) findViewById(R.id.grammar_descriptionTextView);
        levelTv = (TextView) findViewById(R.id.grammar_levelTv);
        grammarExamplesListView = (ListView) findViewById(R.id.grammar_grammarExamplesListView);

        giongoTextView = (TextView) findViewById(R.id.giongo_giongoTextView);
        translationTextView = (TextView) findViewById(R.id.giongo_translationTextView);
        giongoExamplesListView = (ListView) findViewById(R.id.giongo_giongoExamplesListView);

        cwWordTextView = (TextView) findViewById(R.id.cw_wordTextView);
        cwHiraganaTextView = (TextView) findViewById(R.id.cw_hiraganaTextView);
        cwRomajiTextView = (TextView) findViewById(R.id.cw_romajiTextView);
        cwTranslationTextView = (TextView) findViewById(R.id.cw_translationTextView);
        cwExamplesListView = (ListView) findViewById(R.id.cw_examplesListView);

        //buttons
        vocButton = (Button) findViewById(R.id.voc_soundButton);
        cwButton = (Button) findViewById(R.id.cw_soundButton);

        Intent intent = getIntent();
        vocEntry = intent.getParcelableExtra("entry1");
        rule = intent.getParcelableExtra("entry2");
        giongo = intent.getParcelableExtra("entry3");
        cw = intent.getParcelableExtra("entry4");

        showEntry();

        vocWordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        vocTranscriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

        twmp = new TextToVoiceMediaPlayer();
    }

    @SuppressLint("SimpleDateFormat")
    private void showEntry() {
        vocButton.setVisibility(View.INVISIBLE);
        cwButton.setVisibility(View.INVISIBLE);
        if (vocEntry != null) {
            vocButton.setVisibility(View.VISIBLE);

            vocWordTextView.setText(vocEntry.getKanji());
            vocTranscriptionTextView.setText(vocEntry.getTranscription());
            vocRomajiTextView.setText(vocEntry.getRomaji());
            vocTranslationTextView.setText(vocEntry.translationsToString());
            vocLevelTv.setText(getResources().getString(R.string.jlpt_level) + " " + vocEntry.getLevel());

            vocExamples = new ArrayList<ExampleAbstr>();
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

            vocAdapter1 = new ExamplesListViewAdapter(this, text, romaji, translation, 0);
            vocExamplesListView.setAdapter(vocAdapter1);
        }

        if (rule != null) {
            ruleTextView.setText(rule.getRule());
            descriptionTextView.setText(rule.getDescription());
            levelTv.setText(getResources().getString(R.string.jlpt_level) + " " + rule.getLevel());

            grammarAdapter = new ExamplesListViewAdapter(this,
                    rule.getAllExamplesText(), rule.getAllExamplesRomaji(),
                    rule.getAllTranslations(), rule.getIntColor());
            grammarExamplesListView.setAdapter(grammarAdapter);
        }
        if (giongo != null) {
            giongoTextView.setText(giongo.getWord());
            translationTextView.setText(giongo.getTranslEng());

            // set color of entry
            if (App.isColored) {
                giongoTextView.setTextColor(giongo.getIntColor());
            } else
                giongoTextView.setTextColor(Color.WHITE);

            giongoAdapter = new ExamplesListViewAdapter(this,
                    giongo.getAllExamplesText(), giongo.getAllExamplesRomaji(),
                    giongo.getAllTranslations(), giongo.getIntColor());
            giongoExamplesListView.setAdapter(giongoAdapter);
        }
        if (cw != null) {

            cwButton.setVisibility(View.VISIBLE);

            cwWordTextView.setText(cw.getWord());
            cwHiraganaTextView.setText(cw.getHiragana());
            cwRomajiTextView.setText(cw.getRomaji());
            cwTranslationTextView.setText(cw.getTranslation());

            ArrayList<String> text = new ArrayList<String>();
            ArrayList<String> romaji = new ArrayList<String>();
            ArrayList<String> translation = new ArrayList<String>();

            cwExamples = new ArrayList<ExampleAbstr>();
            cwExamples.addAll(App.gres.getAllExamplesWithWord(cw.getWord(), App.cr));
            cwExamples.addAll(App.ges.getAllExamplesWithWord(cw.getWord(), App.cr));

            for (ExampleAbstr ge : cwExamples) {
                text.add(ge.getText());
                romaji.add(ge.getRomaji());
                if (App.lang == App.Languages.ENG)
                    translation.add(ge.getTranslationEng());
                else
                    translation.add(ge.getTranslationRus());
            }
            cwAdapter1 = new ExamplesListViewAdapter(this, text, romaji, translation, 0);
            cwExamplesListView.setAdapter(cwAdapter1);
        }
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

    public void vocOnPlayClick(View v) {
        twmp.loadAndPlay(vocEntry.getTranscription(), App.speechVolume, App.speechSpeed);
    }

    public void cwOnPlayClick(View v) {
        twmp.loadAndPlay(cw.getTranscription(), App.speechVolume, App.speechSpeed);
    }

    public void onPlayClick1(View v) {
        // getting layout with text
        View v1 = (View) v.getParent();
        TextView textPart1 = (TextView) v1.findViewById(R.id.textPart1);
        TextView textPart2 = (TextView) v1.findViewById(R.id.textPart2);
        TextView textPart3 = (TextView) v1.findViewById(R.id.textPart3);
        String phrase = (String) textPart1.getText() + textPart2.getText()
                + textPart3.getText();
        twmp.loadAndPlay(phrase, App.speechVolume, App.speechSpeed);
    }

    public static class PlaceholderFragment1 extends Fragment {

        public PlaceholderFragment1() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.vocabulary_entry_dictionary_detail_fragment, container, false);
            return rootView;
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
