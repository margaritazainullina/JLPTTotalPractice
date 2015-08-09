package ua.hneu.languagetrainer.masterdetailflow;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;

import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import ua.hneu.languagetrainer.model.other.Giongo;

public class GiongoActivityFragment extends Fragment {
	TextView infoTextView;
	ProgressBar progressBar;
    TextView wordTextView;
    TextView translationTextView;
    Button showTranslationButton;
    Giongo random;

	public static final String ARG_ITEM_ID = "item_id";

	public GiongoActivityFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		 * if (getArguments().containsKey(ARG_ITEM_ID)) { mItem =
		 * MainMenuValues.ITEM_MAP.get(getArguments().getString( ARG_ITEM_ID));
		 * }
		 */
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.giongo_fragment, container,
				false);

		infoTextView = (TextView) rootView
				.findViewById(R.id.giongoInfoTextView);
		progressBar = (ProgressBar) rootView
				.findViewById(R.id.giongoProgressBar);
        wordTextView = (TextView) rootView.findViewById(R.id.wordTextView);
        translationTextView = (TextView) rootView.findViewById(R.id.translationGiongoTextView);
        showTranslationButton = (Button) rootView.findViewById(R.id.showTranslationGiongoButton);

        while(random==null) {
            try {
                random = App.allGiongoDictionary.fetchRandom();
            } catch (Exception e) {
            }
        }
        wordTextView.setText(random.getRule());
        translationTextView.setText(random.getTranslation());
        translationTextView.setAlpha(0);
        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

		int learned = App.userInfo.getLearnedGiongo();
		int all = App.userInfo.getNumberOfGiongoInLevel();

		int learnedPersentage = (int) Math
				.round(((double) learned / (double) all) * 100);
		String info = this.getString(R.string.words_learned) + ": " + learned
				+ " " + this.getString(R.string.out_of) + " " + all + " - "
				+ learnedPersentage + "%";

		infoTextView.setText(info);
		progressBar.setProgress(learnedPersentage);

		return rootView;
	}
}
