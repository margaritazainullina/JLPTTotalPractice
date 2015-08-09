package ua.hneu.languagetrainer.masterdetailflow;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;


public class VocabularyActivityFragment extends Fragment {
    TextView infoTextView;
    ProgressBar progressBar;
    TextView wordTextView;
    TextView transcriptionTextView;
    TextView translationTextView;
    Button showTranslationButton;
    VocabularyEntry random;

    public static final String ARG_ITEM_ID = "item_id";

    public VocabularyActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.vocabulary_fragment,
                container, false);

        infoTextView = (TextView) rootView.findViewById(R.id.infoTextView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.vocabularyProgressBar);
        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        transcriptionTextView = (TextView) rootView.findViewById(R.id.transcriptionTextView);
        translationTextView = (TextView) rootView.findViewById(R.id.translationTextView);
        showTranslationButton = (Button) rootView.findViewById(R.id.showTranslationButton);
        while (random == null) {
            try {
                random = App.allVocabularyDictionary.fetchRandom();
            } catch (Exception e) {
            }
        }
        wordTextView.setText(random.getKanji());
        transcriptionTextView.setText("[" + random.getTranscription() + "]");
        translationTextView.setText("");
        translationTextView.setText(random.translationsToString());

        translationTextView.setAlpha(0);
        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        transcriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

        Log.i("VocabularyActivityFragment",
                "VocabularyActivityFragment.onCreateView()");

        int learned = App.userInfo.getLearnedVocabulary();
        int all = App.userInfo.getNumberOfVocabularyInLevel();

        int learnedPersentage = (int) Math
                .round(((double) learned / (double) all) * 100);
        String info = this.getString(R.string.words_learned) + ": " + learned
                + " " + this.getString(R.string.out_of) + " " + all + " - "
                + learnedPersentage + "%";

        infoTextView.setText(info);
        progressBar.setProgress(learnedPersentage);

        //TODO: from start is user wants
        if (App.isVocabularyLearned())
            rootView.findViewById(R.id.practiceVocabulary).setEnabled(false);
        return rootView;
    }


}
