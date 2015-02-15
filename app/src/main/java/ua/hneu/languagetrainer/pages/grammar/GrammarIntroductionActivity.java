package ua.hneu.languagetrainer.pages.grammar;

import java.util.List;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.grammar.GrammarRule;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GrammarIntroductionActivity extends FragmentActivity {
	ExamplesListViewAdapter adapter;
	public boolean isLast = true;
	public static GrammarRule curRule;
	public static int idx = -1;

	TextView ruleTextView;
	TextView descriptionTextView;
	ListView grammarExamplesListView;
	Button prevButton;
	TextToVoiceMediaPlayer twmp;
	String phrase = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grammar_introduction);
		// if it's not first time when user sees entries of current level
		// sort - entries shown less times will be first shown
		// else it makes no sense, all is sorted initially
		if (App.userInfo.isLevelLaunchedFirstTime == 0)
			App.grammarDictionary.sortByTimesShown();
		// change this value
		App.userInfo.isLevelLaunchedFirstTime = 0;
		App.userInfo.updateUserData(getContentResolver());

		// Initialize views
		ruleTextView = (TextView) findViewById(R.id.ruleTextView);
		descriptionTextView = (TextView) findViewById(R.id.transcriptionTextView);
		grammarExamplesListView = (ListView) findViewById(R.id.grammarExamplesListView);

		// increment number of
		App.vp.incrementNumberOfPassingsInARow();
		prevButton = (Button) findViewById(R.id.buttonPrevious);

		// show first entry
		curRule = App.grammarDictionary.get(0);
		idx = 0;

		// media player for playing example
		twmp = new TextToVoiceMediaPlayer();
		showEntry(curRule);
		ruleTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		
		prevButton.setEnabled(false);
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

    public void buttonNextOnClick(View v) {
		idx++;
		// if Next button was disabled
		prevButton.setEnabled(true);
		if (idx >= App.numberOfEntriesInCurrentDict) {
			// if all words have been showed go to next activity
			goToNextPassingActivity();
		} else {
			curRule = App.grammarDictionary.get(idx);
			showEntry(curRule);
		}
	}

	public void buttonSkipOnClick(View v) {
		goToNextPassingActivity();
	}

	public void goToNextPassingActivity() {

		Intent grammarTestActivity = new Intent(this, GrammarTestActivity.class);
		startActivity(grammarTestActivity);
	}

	@SuppressLint("SimpleDateFormat")
	private void showEntry(GrammarRule currentRule) {
		ruleTextView = (TextView) findViewById(R.id.ruleTextView);
		descriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
		grammarExamplesListView = (ListView) findViewById(R.id.grammarExamplesListView);
		// set grammar info to the texViews
		ruleTextView.setText(currentRule.getRule());
		descriptionTextView.setText(currentRule.getDescription());

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

	public void buttonPreviousOnClick(View v) {
		if (idx > 0) {
			idx--;
			curRule = App.grammarDictionary.get(idx);
			showEntry(curRule);
		} else {
			prevButton.setEnabled(false);
		}
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
    /**
     * A placeholder fragment containing a simple view. This fragment
     * would include your content.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_grammar_introduction, container, false);
            return rootView;
        }
    }

    /**
     * This class makes the ad request and loads the ad.
     */
    public static class AdFragment extends Fragment {

        private AdView mAdView;

        public AdFragment() {
        }

        @Override
        public void onActivityCreated(Bundle bundle) {
            super.onActivityCreated(bundle);

            // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
            // values/strings.xml.
            mAdView = (AdView) getView().findViewById(R.id.adView);

            // Create an ad request. Check logcat output for the hashed device ID to
            // get test ads on a physical device. e.g.
            // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .build();

            // Start loading the ad in the background.
            mAdView.loadAd(adRequest);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_ad, container, false);
        }

        /** Called when leaving the activity */
        @Override
        public void onPause() {
            if (mAdView != null) {
                mAdView.pause();
            }
            super.onPause();
        }

        /** Called when returning to the activity */
        @Override
        public void onResume() {
            super.onResume();
            if (mAdView != null) {
                mAdView.resume();
            }
        }

        /** Called before the activity is destroyed */
        @Override
        public void onDestroy() {
            if (mAdView != null) {
                mAdView.destroy();
            }
            super.onDestroy();
        }

    }
}
