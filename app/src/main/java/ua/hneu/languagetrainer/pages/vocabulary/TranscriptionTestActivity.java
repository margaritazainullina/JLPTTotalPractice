package ua.hneu.languagetrainer.pages.vocabulary;

import java.util.Collections;
import java.util.Set;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import ua.hneu.languagetrainer.LearningStatistics;
import ua.hneu.languagetrainer.ListViewAdapter;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyEntry;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;
import ua.hneu.languagetrainer.pages.MainActivity;
import ua.hneu.languagetrainer.pages.SettingsActivity;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class TranscriptionTestActivity extends Activity {
    // dictionary with random words for possible answers
    Set<VocabularyEntry> randomDictionary;
    VocabularyDictionary randomDictionaryList;
    // activity elements
    ListView answersListView;
    TextView wordTextView;
    TextView transcriptionTextView;
    TextView romajiTextView;
    ImageView isCorrect;
    VocabularyEntry rightAnswer;
    int answersNumber = 5;
    int currentWordNumber = -1;

    // custom adapter for ListView
    ListViewAdapter adapter;
    boolean ifWasWrong = false;

    // sets direction
    // true - question is kanji and answers are transcriptions
    // false - vice versa
    // sets randomly for every question
    boolean isKanjiShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation_transcription_cw_test);
        // Initialize
        wordTextView = (TextView) findViewById(R.id.wordTextView);
        transcriptionTextView = (TextView) findViewById(R.id.transcriptionTextView);
        romajiTextView = (TextView) findViewById(R.id.romajiTextView);
        answersListView = (ListView) findViewById(R.id.answersListView);
        isCorrect = (ImageView) findViewById(R.id.isCorrect);
        // at first show word and possible answers
        nextWord();
        wordTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        transcriptionTextView.setTypeface(App.kanjiFont, Typeface.NORMAL);
        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID set in
        // values/strings.xml.
        AdView mAdView = (AdView) findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);
    }

    public void nextWord() {
        // move pointer to next word
        // if word does't have a kanji, skip it
        currentWordNumber++;
        if (currentWordNumber >= App.vocabularyDictionary.size() - 1)
            endTesting();

        VocabularyEntry currentEntry = App.vocabularyDictionary
                .get(currentWordNumber);
        while (currentEntry.getKanji().isEmpty()) {
            currentWordNumber++;
            if (currentWordNumber >= App.vocabularyDictionary.size() - 1)
                endTesting();
            currentEntry = App.vocabularyDictionary.get(currentWordNumber);
        }

        if (currentEntry.getKanji().isEmpty())
            nextWord();
        // set randomly direction
        if (Math.random() < 0.5)
            isKanjiShown = false;
        else
            isKanjiShown = true;

        // show word, reading and translations - set text to all TextViews
        if (isKanjiShown) {
            wordTextView.setText(currentEntry.getKanji());
            transcriptionTextView.setText("");
            romajiTextView.setText("");
        } else {
            transcriptionTextView.setText(currentEntry.getTranscription());
            if (App.isShowRomaji)
                romajiTextView.setText(currentEntry.getRomaji());
            wordTextView.setText("");
        }

        // get dictionary with random entries, add current one and shuffle
        randomDictionary = App.allVocabularyDictionary.getRandomEntries(
                answersNumber - 1, true);
        randomDictionary.add(App.vocabularyDictionary.get(currentWordNumber));
        rightAnswer = App.vocabularyDictionary.get(currentWordNumber);

        // create List randomDictionaryList for ArrayAdapter from set
        // randomDictionary
        randomDictionaryList = new VocabularyDictionary();
        randomDictionaryList.getEntries().addAll(randomDictionary);
        randomDictionaryList
                .addEntriesToDictionaryAndGet(answersNumber, true);
        // shuffle list
        Collections.shuffle(randomDictionaryList.getEntries());

        // creating adapter for ListView with possible answers
        if (isKanjiShown) {
            adapter = new ListViewAdapter(this,
                    randomDictionaryList.getAllReadings(), true);
        } else {
            adapter = new ListViewAdapter(this,
                    randomDictionaryList.getAllKanji(), true);
        }
        // bindings adapter to ListView
        answersListView.setAdapter(adapter);
        answersListView.setOnItemClickListener(answersListViewClickListener);

        // set colors
        if (App.isColored) {
            int color = currentEntry.getIntColor();
            wordTextView.setTextColor(color);
            transcriptionTextView.setTextColor(color);
            romajiTextView.setTextColor(color);
            isCorrect.setImageResource(android.R.color.transparent);
        }
        // set this word shown
        rightAnswer.setLastView();
        App.vs.update(rightAnswer, getContentResolver());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.match_the_words, menu);
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

    // listeners for click on the list row
    final private transient OnItemClickListener answersListViewClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, final View view,
                                final int position, final long itemID) {
            VocabularyEntry selected = randomDictionaryList.get(position);
            // comparing correct and selected answer
            if (selected == rightAnswer) {
                App.vp.incrementNumberOfCorrectAnswersInTranslation();
                // increment percentage
                if (!ifWasWrong)
                    rightAnswer.setLearnedPercentage(rightAnswer
                            .getLearnedPercentage()
                            + App.getPercentageIncrement());

                if (rightAnswer.getLearnedPercentage() >= 1) {
                    makeWordLearned(rightAnswer, true);
                }
                App.vs.update(rightAnswer, getContentResolver());
                // change color to green and fade out
                isCorrect.setImageResource(R.drawable.yes);
                adapter.changeColor(view, Color.parseColor("#669900"));
                // fading out textboxes
                fadeOut(wordTextView, 750);
                fadeOut(transcriptionTextView, 750);
                fadeOut(romajiTextView, 750);
                fadeOut(isCorrect, 750);

                // fading out listview
                ListView v = (ListView) view.getParent();
                fadeOut(v, 750);

                Animation fadeOutAnimation = AnimationUtils.loadAnimation(
                        v.getContext(), android.R.anim.fade_out);
                fadeOutAnimation.setDuration(750);
                v.startAnimation(fadeOutAnimation);

                fadeOutAnimation
                        .setAnimationListener(new Animation.AnimationListener() {

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                // when previous information faded out
                                // show next word and possible answers or go to
                                // next exercise
                                if (currentWordNumber < App.vocabularyDictionary
                                        .size() - 1) {
                                    nextWord();
                                } else {
                                    endTesting();
                                }
                            }

                            // doesn't needed, just implementation
                            @Override
                            public void onAnimationRepeat(Animation arg0) {
                            }

                            @Override
                            public void onAnimationStart(Animation arg0) {
                            }
                        });
            } else {
                // change color of row and set text
                adapter.changeColor(view, Color.parseColor("#CC0000"));
                isCorrect.setImageResource(R.drawable.no);
                ifWasWrong = true;
                // set information about wrong answer in VocabularyPassing
                App.vp.incrementNumberOfIncorrectAnswersInTranscription();
                App.vp.addProblemWord(App.vocabularyDictionary
                        .get(currentWordNumber));
            }
        }
    };

    public void fadeOut(View v, long duration) {
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(
                v.getContext(), android.R.anim.fade_out);
        fadeOutAnimation.setDuration(750);
        v.startAnimation(fadeOutAnimation);
    }

    public void endTesting() {
        // go to resultActivity to show result of all vocabulary training
        Intent resultActivity = new Intent(this, VocabularyResultActivity.class);
        startActivity(resultActivity);

    }

    public void buttonSkipSelectOnClick(View v) {
        Intent matchWordsIntent = new Intent(this,
                VocabularyResultActivity.class);
        startActivity(matchWordsIntent);
    }

    public void buttonIAlrKnow(View v) {
        makeWordLearned(rightAnswer, true);
        nextWord();
    }

    public void makeWordLearned(VocabularyEntry ve, boolean isKanjiNeeded) {

        App.vp.makeWordLearned(ve, getContentResolver(), isKanjiNeeded);

        if (App.isVocabularyLearned()) {

            if (App.isAllLearned()) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.all_vocabulary_learned_toast,
                        (ViewGroup) findViewById(R.id.toast_layout_root));

                TextView text = (TextView) layout.findViewById(R.id.text);
                ImageView img = (ImageView) layout.findViewById(R.id.img);
                switch (App.userInfo.getLevel()) {
                    case 1:
                        img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations1_));
                    case 2:
                        img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations2_));
                    case 3:
                        img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations3_));
                    case 4:
                        img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations4_));
                    case 5:
                        img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations5_));
                }
                text.setText(getResources().getString(R.string.congratulations) + " N" + App.userInfo.getLevel());

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();

                if (App.userInfo.getLevel() != 1) App.goToLevel(App.userInfo.getLevel() + 1);
                Intent mainActivity = new Intent(this,
                        MainActivity.class);
                startActivity(mainActivity);
            }
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.all_vocabulary_learned_toast,
                    (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(getResources().getString(R.string.voc_learned) + " N" + App.userInfo.getLevel());
            ImageView img = (ImageView) layout.findViewById(R.id.img);
            switch (App.userInfo.getLevel()) {
                case 1:
                    img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations1_));
                case 2:
                    img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations2_));
                case 3:
                    img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations3_));
                case 4:
                    img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations4_));
                case 5:
                    img.setImageDrawable(getResources().getDrawable(R.drawable.conglaturations5_));
            }

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();

            Intent mainActivity = new Intent(this,
                    MainActivity.class);
            startActivity(mainActivity);
        }
    }
}
