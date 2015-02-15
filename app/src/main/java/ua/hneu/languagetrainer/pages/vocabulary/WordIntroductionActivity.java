package ua.hneu.languagetrainer.pages.vocabulary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ua.hneu.edu.languagetrainer.R;
import com.google.android.gms.ads.*;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.TextToVoiceMediaPlayer;
import ua.hneu.languagetrainer.Utils;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.MotionEvent;
import 	android.support.v4.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WordIntroductionActivity extends FragmentActivity {
    JSONObject json;

    private static final int ACTION_TYPE_DEFAULT = 0;
    private static final int ACTION_TYPE_UP = 1;
    private static final int ACTION_TYPE_RIGHT = 2;
    private static final int ACTION_TYPE_DOWN = 3;
    private static final int ACTION_TYPE_LEFT = 4;
    private static final int SLIDE_RANGE = 100;
    private float mTouchStartPointX;
    private float mTouchStartPointY;
    private int mActionType = ACTION_TYPE_DEFAULT;
    public static VocabularyEntry curEntry;
    public static int idx = -1;

    TextView wordTextView;
    TextView transcriptionTextView;
    TextView romajiTextView;
    TextView translationTextView;
    ProgressBar progressBar;
    Button skip;
    TextToVoiceMediaPlayer twmp;
    ImageView wordImage;
    ImageView logo;

    String search_item = "";
    String imageUrl="";
    Bitmap bmp;

    private class LoadImageTask extends AsyncTask<Void, Integer, Void>{

    @Override
        protected Void doInBackground(Void... params) {

        if(!curEntry.getKanji().isEmpty()){
            Log.d("curEntry.getKanji()",curEntry.getKanji());
            String kanji = curEntry.getKanji();
            //if contains /
            String[] s = kanji.split("/");
            try{
                search_item = URLEncoder.encode(s[0], "UTF-8");
            }
            catch (Exception e){
                //if can't search japanese, search english without : ; , and spaces
                search_item = curEntry.getTranslationsEng().get(0);
                search_item=search_item.replaceAll("\\W","%20");
                Log.e("query encoding","unable to encode japanese characters");
            }
        }
        else {
            search_item = curEntry.getTranslationsEng().get(0);
            search_item=search_item.replaceAll("\\W","%20");
        }
        Log.d("search_item",search_item);

        try {
            //searching 1 medium clipart image
            String search_query = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + search_item+"&rsz=1&imgtype=clipart&imgsz=medium&as_filetype=jpg|png&userip="+Utils.getIPAddress(true);

            Log.d("search_query",search_query);
            ParseResult(sendQuery(search_query));
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("doInBackground","JSONException "+e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("doInBackground","IOException "+e.toString());
        }
        return null;
    }

        private String sendQuery(String query) throws IOException{
            StringBuffer result = new StringBuffer();
            Log.d("query",query);

            URL searchURL = new URL(query);

            HttpURLConnection httpURLConnection = (HttpURLConnection) searchURL.openConnection();
            httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
            httpURLConnection.setRequestProperty("Referer",query);

            if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(
                        inputStreamReader);

                String line = null;
                while((line = bufferedReader.readLine()) != null){
                    result.append(line);
                }

                bufferedReader.close();
            }

            return result.toString();
        }

        private void ParseResult(String json) throws JSONException{
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonObject_responseData = jsonObject.getJSONObject("responseData");
            JSONArray jsonArray_results = jsonObject_responseData.getJSONArray("results");

            Log.d("json",json);

//if empty for japanese - search english
            if(jsonArray_results.length()==0){
               try {
                search_item = curEntry.getTranslationsEng().get(0);
                search_item=search_item.replaceAll("\\W","%20");
                   String search_query = "http://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + search_item+"&rsz=1&imgtype=clipart&imgsz=medium&as_filetype=jpg|png&userip="+Utils.getIPAddress(true);
                sendQuery(search_item);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for(int i = 0; i < jsonArray_results.length(); i++){
                JSONObject jsonObject_i = jsonArray_results.getJSONObject(i);

                imageUrl = jsonObject_i.getString("unescapedUrl");
                Log.d("imageUrl",imageUrl);


                URL url = null;
                try {
                    url = new URL(imageUrl);
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                wordImage.setImageBitmap(bmp);
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_introduction);
        wordImage = (ImageView) findViewById(R.id.wordImage);
        logo = (ImageView) findViewById(R.id.logo);


        // if it's not first time when user sees entries of current level
        // sort - entries shown less times will be first shown
        // else it makes no sense, all is sorted initially
        if (App.userInfo.isLevelLaunchedFirstTime == 0)
        {
            App.vocabularyDictionary.addEntriesToDictionaryAndGet(App.numberOfEntriesInCurrentDict, false);
            App.vocabularyDictionary.sortByTimesShown();
        }
        // change this value
        App.userInfo.isLevelLaunchedFirstTime = 0;
        App.userInfo.updateUserData(getContentResolver());

        twmp = new TextToVoiceMediaPlayer();

        // increment number of
        App.vp.incrementNumberOfPassingsInARow();

        skip = (Button) findViewById(R.id.buttonSkip);

        // show first entry
        curEntry = App.vocabularyDictionary.get(0);
        idx = 0;
        showEntry();

        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        transcriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


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
            curEntry = App.vocabularyDictionary.get(idx);
            showEntry();
        }
    }

    protected void slideToLeft() {
        if (idx > 0) {
            idx--;
            curEntry = App.vocabularyDictionary.get(idx);
            showEntry();
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

    private void speakOut(final VocabularyEntry entry) {
        twmp.play(entry.getTranscription());
    }

    public void onPlayClick(View v) {
        speakOut(curEntry);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    public void buttonSkipOnClick(View v) {
        goToNextPassingActivity();
    }

    public void goToNextPassingActivity() {
        Intent matchWordsIntent = new Intent(this, MatchWordsActivity.class);
        startActivity(matchWordsIntent);
    }

    @SuppressLint("SimpleDateFormat")
    private void showEntry() {

        //load images from search if connection is on
        if(isNetworkAvailable()) {
            logo.setVisibility(View.VISIBLE);
            wordImage.setVisibility(View.VISIBLE);
            new LoadImageTask().execute();
        }
        else{
            logo.setVisibility(View.INVISIBLE);
            wordImage.setVisibility(View.INVISIBLE);
        }

        wordTextView = (TextView) findViewById(R.id.wordTextView);
        transcriptionTextView = (TextView) findViewById(R.id.transcriptionTextView);
        romajiTextView = (TextView) findViewById(R.id.romajiTextView);
        translationTextView = (TextView) findViewById(R.id.translationTextView);
        progressBar = (ProgressBar) findViewById(R.id.intro_progress);


        int progress = (int) Math
                .round(((double) idx / (double) App.vocabularyDictionary.size()) * 100);
        progressBar.setProgress(progress);

        // set word info to the texViews
        wordTextView.setText(curEntry.getKanji());
        transcriptionTextView.setText("["+curEntry.getTranscription()+"]");
        if (App.isShowRomaji)
            romajiTextView.setText("- ["+curEntry.getRomaji()+"]");
        translationTextView.setText(curEntry.translationsToString());

        // set color of entry
        if(App.isColored){
            int color = App.vocabularyDictionary.get(idx).getIntColor();
            wordTextView.setTextColor(color);
            transcriptionTextView.setTextColor(color);
            romajiTextView.setTextColor(color);
        }

        // and write information to db
        curEntry.setLastView();
        curEntry.incrementShowntimes();
        App.vs.update(curEntry, getContentResolver());
        if(App.isAutoplayed&&isNetworkAvailable()) {
            try {
                speakOut(curEntry);
            } catch (Exception e) {
                Log.e("Text to speech error", "Text to speech error");
            }
        }
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
            View rootView = inflater.inflate(R.layout.fragment_word_introduction, container, false);
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

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
