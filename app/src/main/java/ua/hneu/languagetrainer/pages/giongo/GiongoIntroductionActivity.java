package ua.hneu.languagetrainer.pages.giongo;

import java.util.List;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.ExamplesListViewAdapter;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.model.other.Giongo;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GiongoIntroductionActivity extends FragmentActivity {
	ExamplesListViewAdapter adapter;
	public static List<Integer> shownIndexes;
	public boolean isLast = true;
	public static Giongo curWord;
	public static int idx = -1;

	TextView giongoTextView;
	TextView translationTextView;
	ListView giongoExamplesListView;
	TextToVoiceMediaPlayer twmp;
	String phrase = "";

    private static final int ACTION_TYPE_DEFAULT = 0;
    private static final int ACTION_TYPE_UP = 1;
    private static final int ACTION_TYPE_RIGHT = 2;
    private static final int ACTION_TYPE_DOWN = 3;
    private static final int ACTION_TYPE_LEFT = 4;
    private static final int SLIDE_RANGE = 100;
    private float mTouchStartPointX;
    private float mTouchStartPointY;
    private int mActionType = ACTION_TYPE_DEFAULT;
    ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_giongo_introduction);
		// if it's not first time when user sees entries of current level
		// sort - entries shown less times will be first shown
		// else it makes no sense, all is sorted initially
		if (App.userInfo.isLevelLaunchedFirstTime == 0)
			App.giongoWordsDictionary.sortByTimesShown();
		// change this value
		App.userInfo.isLevelLaunchedFirstTime = 0;
		App.userInfo.updateUserData(getContentResolver());

		// increment number of
		App.vp.incrementNumberOfPassingsInARow();

        // Initialize views
        giongoTextView = (TextView) findViewById(R.id.giongoTextView);
        translationTextView = (TextView) findViewById(R.id.translationTextView);
        giongoExamplesListView = (ListView) findViewById(R.id.giongoExamplesListView);
        progressBar = (ProgressBar) findViewById(R.id.intro_progress);

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
            showEntry(App.allGiongoDictionary.get(idx));
        }
    }

    protected void slideToLeft() {
        if (idx > 0) {
            idx--;
            int progress = (int) Math
                    .round(((double) idx / (double) App.vocabularyDictionary.size()) * 100);
            progressBar.setProgress(progress);
            showEntry(App.allGiongoDictionary.get(idx));
        }
    }

	public void buttonSkipOnClick(View v) {
		goToNextPassingActivity();
	}

	public void goToNextPassingActivity() {
		Intent matchWordsIntent = new Intent(this, GiongoTestActivity.class);
		startActivity(matchWordsIntent);
	}

	@SuppressLint("SimpleDateFormat")
	private void showEntry(Giongo currentWord) {
		// set giongo info to the texViews
		giongoTextView.setText(currentWord.getRule());
		translationTextView.setText(currentWord.translationsToString());

        // set color of entry
        if(App.isColored) {
            giongoTextView.setTextColor(currentWord.getIntColor());
        }
        else
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
				twmp.loadAndPlay(phrase, App.speechVolume,App.speechSpeed);
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
            View rootView = inflater.inflate(R.layout.fragment_giongo_introduction, container, false);
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
