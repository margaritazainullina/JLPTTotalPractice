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
import ua.hneu.languagetrainer.model.grammar.GrammarRule;

public class GrammarActivityFragment extends Fragment {
    TextView infoTextView;
    ProgressBar progressBar;

    TextView wordTextView;
    TextView translationTextView;
    Button showTranslationButton;
    GrammarRule random;
    public static final String ARG_ITEM_ID = "item_id";

    public GrammarActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.grammar_fragment,
                container, false);

        infoTextView = (TextView) rootView.findViewById(R.id.grammarInfoTextView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.grammarProgressBar);

        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        translationTextView = (TextView) rootView.findViewById(R.id.translationGrammarTextView);
        showTranslationButton = (Button) rootView.findViewById(R.id.showGrammarTranslationButton);
        while (random == null) {
            try {
                random = App.allGrammarDictionary.fetchRandom();
            } catch (Exception e) {
            }
        }
        wordTextView.setText(random.getRule());
        translationTextView.setText("");
        translationTextView.setText(random.getDescription());

        translationTextView.setAlpha(0);
        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

        Log.i("GrammarActivityFragment",
                "GrammarActivityFragment.onCreateView()");

        // Show the dummy content as text in a TextView.
        int learned = App.userInfo.getLearnedGrammar();
        int all = App.userInfo.getNumberOfGrammarInLevel();

        int learnedPersentage = (int) Math
                .round(((double) learned / (double) all) * 100);
        String info = this.getString(R.string.grammar_learned) + ": "
                + learned + " " + this.getString(R.string.out_of) + " "
                + all + " - " + learnedPersentage + "%";

        infoTextView.setText(info);
        progressBar.setProgress(learnedPersentage);

        //TODO: from start is user wants
        if (App.isGrammarLearned()) rootView.findViewById(R.id.practiceGrammar).setEnabled(false);

        return rootView;
    }
}
