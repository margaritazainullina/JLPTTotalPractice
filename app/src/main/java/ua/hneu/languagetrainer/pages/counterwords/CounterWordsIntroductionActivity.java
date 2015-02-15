package ua.hneu.languagetrainer.pages.counterwords;

import java.util.List;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.other.CounterWord;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class CounterWordsIntroductionActivity extends FragmentActivity {
	public static List<Integer> shownIndexes;
	public boolean isLast = true;
	public static CounterWord curWord;
	public static int idx = -1;
	TextToVoiceMediaPlayer twmp;

	TextView wordTextView;
	TextView hiraganaTextView;
	TextView romajiTextView;
	TextView translationTextView;
	Button prevButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_counterwords_introduction);
		// if it's not first time when user sees entries of current level
		// sort - entries shown less times will be first shown
		// else it makes no sense, all is sorted initially
		if (App.userInfo.isLevelLaunchedFirstTime == 0)
			App.counterWordsDictionary.sortByTimesShown();
		// change this value
		App.userInfo.isLevelLaunchedFirstTime = 0;
		App.userInfo.updateUserData(getContentResolver());

		// Initialize views
		wordTextView = (TextView) findViewById(R.id.wordTextView);
		hiraganaTextView = (TextView) findViewById(R.id.hiraganaTextView);
		romajiTextView = (TextView) findViewById(R.id.romajiTextView);
		translationTextView = (TextView) findViewById(R.id.translationTextView);

		// increment number of
		App.vp.incrementNumberOfPassingsInARow();
		prevButton = (Button) findViewById(R.id.buttonPrevious);
		// show first entry
		curWord = App.counterWordsDictionary.get(0);
		idx = 0;
		showEntry(curWord);
		wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		hiraganaTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
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
			curWord = App.counterWordsDictionary.get(idx);
			showEntry(curWord);
		}
	}

	public void buttonSkipOnClick(View v) {
		goToNextPassingActivity();
	}

	public void goToNextPassingActivity() {
		Intent CounterWordsTestActivity = new Intent(this,
				CounterWordsTestActivity.class);
		startActivity(CounterWordsTestActivity);
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
                Log.e("Text to speecch error", "Text to speecch error");
            }
        }
	}

	public void buttonPreviousOnClick(View v) {
		if (idx > 0) {
			idx--;
			curWord = App.counterWordsDictionary.get(idx);
			showEntry(curWord);
		} else {
			prevButton.setEnabled(false);
		}
	}
	public void onPlayClick3(View v) {
		// getting layout with text				
        speakOut(curWord);
	}
    private void speakOut(final CounterWord entry) {
        twmp.play(curWord.getHiragana());
    }
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_counterwords_introduction, container, false);
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
