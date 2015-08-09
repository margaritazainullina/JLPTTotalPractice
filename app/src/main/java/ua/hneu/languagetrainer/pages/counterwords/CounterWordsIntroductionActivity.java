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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
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
    ProgressBar progressBar;
    private static final int ACTION_TYPE_DEFAULT = 0;
    private static final int ACTION_TYPE_UP = 1;
    private static final int ACTION_TYPE_RIGHT = 2;
    private static final int ACTION_TYPE_DOWN = 3;
    private static final int ACTION_TYPE_LEFT = 4;
    private static final int SLIDE_RANGE = 100;
    private float mTouchStartPointX;
    private float mTouchStartPointY;
    private int mActionType = ACTION_TYPE_DEFAULT;

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
        progressBar = (ProgressBar) findViewById(R.id.intro_progress);

		// increment number of
		App.vp.incrementNumberOfPassingsInARow();
		// show first entry
		curWord = App.counterWordsDictionary.get(0);
		idx = 0;
		showEntry(curWord);
		wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
		hiraganaTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
	}
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getRawX();
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mTouchStartPointX = event.getRawX();
                mTouchStartPointY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mTouchStartPointX - x > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_LEFT;
                } else if (x - mTouchStartPointX > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_RIGHT;
                } else if (mTouchStartPointY - y > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_UP;
                } else if (y - mTouchStartPointY > SLIDE_RANGE) {
                    mActionType = ACTION_TYPE_DOWN;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mActionType == ACTION_TYPE_UP) {}
                else if (mActionType == ACTION_TYPE_RIGHT) {
                    slideToLeft();
                } else if (mActionType == ACTION_TYPE_DOWN) {}
                else if (mActionType == ACTION_TYPE_LEFT) {
                    slideToRight();
                }
                break;
            default:
                break;
        }
        return true;
    }

    protected void slideToRight() {
        idx++;
        if (idx >= App.vocabularyDictionary.size()) {
            // if all words have been showed go to next activity
            goToNextPassingActivity();
        } else {
            int progress = (int) Math
                    .round(((double) idx / (double) App.vocabularyDictionary.size()) * 100);
            progressBar.setProgress(progress);
            showEntry(App.allCounterWordsDictionary.get(idx));
        }
    }

    protected void slideToLeft() {
        if (idx > 0) {
            idx--;
            int progress = (int) Math
                    .round(((double) idx / (double) App.vocabularyDictionary.size()) * 100);
            progressBar.setProgress(progress);
            showEntry(App.counterWordsDictionary.get(idx));
        }
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
                Log.e("Text to speech error", "Text to speech error");
            }
        }
	}

	public void onPlayClick3(View v) {
		// getting layout with text				
        speakOut(curWord);
	}
    private void speakOut(final CounterWord entry) {
        twmp.loadAndPlay(curWord.getHiragana(), 0.5f, 1f);
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
