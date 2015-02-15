package ua.hneu.languagetrainer;

import java.util.Locale;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.masterdetailflow.MenuElements;
import ua.hneu.languagetrainer.model.User;
import ua.hneu.languagetrainer.model.grammar.GrammarDictionary;
import ua.hneu.languagetrainer.model.other.CounterWordsDictionary;
import ua.hneu.languagetrainer.model.other.GiongoDictionary;
import ua.hneu.languagetrainer.model.vocabulary.VocabularyDictionary;
import ua.hneu.languagetrainer.pages.GreetingActivity;
import ua.hneu.languagetrainer.passing.CounterWordsPassing;
import ua.hneu.languagetrainer.passing.GiongoPassing;
import ua.hneu.languagetrainer.passing.GrammarPassing;
import ua.hneu.languagetrainer.passing.TestPassing;
import ua.hneu.languagetrainer.passing.VocabularyPassing;
import ua.hneu.languagetrainer.service.AnswerService;
import ua.hneu.languagetrainer.service.CounterWordsService;
import ua.hneu.languagetrainer.service.GiongoExampleService;
import ua.hneu.languagetrainer.service.GiongoService;
import ua.hneu.languagetrainer.service.GrammarExampleService;
import ua.hneu.languagetrainer.service.GrammarService;
import ua.hneu.languagetrainer.service.QuestionService;
import ua.hneu.languagetrainer.service.TestService;
import ua.hneu.languagetrainer.service.UserService;
import ua.hneu.languagetrainer.service.VocabularyService;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.util.Log;

public class App extends Application {

	// dictionary for session
	public static VocabularyDictionary vocabularyDictionary;
	// grammar for session
	public static GrammarDictionary grammarDictionary;
	// giongo for session
	public static GiongoDictionary giongoWordsDictionary;
	// counter words for session
	public static CounterWordsDictionary counterWordsDictionary;

	// all dictionary
	public static VocabularyDictionary allVocabularyDictionary;
	public static GrammarDictionary allGrammarDictionary;
	public static GiongoDictionary allGiongoDictionary;
	public static CounterWordsDictionary allCounterWordsDictionary;

	public static String testName;

	// user info
	public static User userInfo;
	// service for access to db
	public static UserService us = new UserService();
	// Object for saving information about current vocabulary passing
	public static VocabularyPassing vp = new VocabularyPassing();
	// Object for saving information about current grammar passing;
	public static GrammarPassing grp = new GrammarPassing();
	// Object for saving information about current giongo passing;
	public static GiongoPassing gp = new GiongoPassing();
	// Object for saving information about current counter words passing;
	public static CounterWordsPassing cwp = new CounterWordsPassing();
	// Object for saving information about test passing;
	public static TestPassing tp = new TestPassing();
	// contentResolver for database
	public static ContentResolver cr;
	public static VocabularyService vs = new VocabularyService();
	public static TestService ts = new TestService();
	public static QuestionService qs = new QuestionService();
	public static AnswerService as = new AnswerService();
	public static GiongoService gs = new GiongoService();
	public static GiongoExampleService ges = new GiongoExampleService();
	public static GrammarService grs = new GrammarService();
	public static GrammarExampleService gres = new GrammarExampleService();
	public static CounterWordsService cws = new CounterWordsService();
	public static Context context;
	public static Languages lang;
	public static boolean isShowRomaji;
    public static boolean isColored;
    public static boolean isAutoplayed;
	public static int numberOfEntriesInCurrentDict = 0;
	public static int numberOfRepeatationsForLearning = 0;
    public static SharedPreferences settings;
	public static Editor editor;
	public static String sectionName = "";

	public enum Languages {
		ENG, RUS
	};

	public static Typeface kanjiFont;
	public static Typeface titleFont;
	public static Typeface titleFontItalic;

	@Override
	public void onCreate() {
        // get current location
        Log.e("Locale", Locale.getDefault().getDisplayLanguage());
        if (Locale.getDefault().getDisplayLanguage().equals("русский"))
            lang = Languages.RUS;
        else
            lang = Languages.ENG;
        // preferences from storage
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = settings.edit();

        // set localized menu elements
        MenuElements.addItem(new MenuElements.MenuItem("vocabulary", this
                .getString(R.string.vocabulary)));
        MenuElements.addItem(new MenuElements.MenuItem("grammar", this
                .getString(R.string.grammar)));
        MenuElements.addItem(new MenuElements.MenuItem("mock_tests", this
                .getString(R.string.mock_tests)));
        MenuElements.addItem(new MenuElements.MenuItem("giongo", this
                .getString(R.string.giongo)));
        MenuElements.addItem(new MenuElements.MenuItem("counter_words", this
                .getString(R.string.counter_words)));
        MenuElements.addItem(new MenuElements.MenuItem("settings", this
                .getString(R.string.settings)));

        cr = getContentResolver();

        // creating and inserting into whole database
        // vocabulary
       /*vs.dropTable();
        vs.createTable();
        vs.bulkInsertFromCSV("vocabulary/N5.txt", getAssets(), 5,
                getContentResolver());
        Log.d("App", "loaded vocabulary/N5.txt");
        vs.bulkInsertFromCSV("vocabulary/N4.txt",
                getAssets(), 4, getContentResolver());
        Log.d("App", "loaded vocabulary/N4.txt");
        vs.bulkInsertFromCSV("vocabulary/N3.txt", getAssets(), 3,
                getContentResolver());
        Log.d("App", "loaded vocabulary/N3.txt");
        vs.bulkInsertFromCSV("vocabulary/N2.txt",
                getAssets(), 2, getContentResolver());
        Log.d("App", "loaded vocabulary/N2.txt");
        vs.bulkInsertFromCSV("vocabulary/N1.txt", getAssets(), 1,
                getContentResolver());
        Log.d("App", "loaded vocabulary/N1.txt");

         // test
        ts.createTable();
        TestService.startCounting(getContentResolver());
        qs.createTable();
        QuestionService.startCounting(getContentResolver());
        as.createTable();
        ts.insertFromXml("tests/level_def_test.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/level_def_test.xml");
        ts.insertFromXml("tests/mock_test_n1_#1.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n1_#1.xml");
        ts.insertFromXml("tests/mock_test_n1_#2.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n1_#2.xml");
        ts.insertFromXml("tests/mock_test_n1_#3.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n1_#3.xml");
        ts.insertFromXml("tests/mock_test_n1_#4.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n1_#4.xml");
        ts.insertFromXml("tests/mock_test_n1_#5.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n1_#5.xml");

        ts.insertFromXml("tests/mock_test_n2_#1.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n2_#1.xml");
        ts.insertFromXml("tests/mock_test_n2_#2.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n2_#2.xml");
        ts.insertFromXml("tests/mock_test_n2_#3.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n2_#3.xml");
        ts.insertFromXml("tests/mock_test_n2_#4.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n2_#4.xml");
        ts.insertFromXml("tests/mock_test_n2_#5.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n2_#5.xml");

        ts.insertFromXml("tests/mock_test_n3_#1.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n3_#1.xml");
        ts.insertFromXml("tests/mock_test_n3_#2.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n3_#2.xml");

        ts.insertFromXml("tests/mock_test_n4_#1.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n4_#1.xml");
        ts.insertFromXml("tests/mock_test_n4_#2.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n4_#2.xml");
        ts.insertFromXml("tests/mock_test_n4_#3.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n4_#3.xml");
        ts.insertFromXml("tests/mock_test_n4_#4.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n4_#4.xml");
        ts.insertFromXml("tests/mock_test_n4_#5.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n4_#5.xml");
        ts.insertFromXml("tests/mock_test_n5_#1.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n5_#1.xml");
        ts.insertFromXml("tests/mock_test_n5_#2.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n5_#2.xml");
        ts.insertFromXml("tests/mock_test_n5_#3.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n5_#3.xml");
        ts.insertFromXml("tests/mock_test_n5_#4.xml", getAssets(),
                getContentResolver());
        Log.d("App", "loaded tests/mock_test_n5_#4.xml");
        ts.insertFromXml("tests/mock_test_n5_#5.xml",
                getAssets(), getContentResolver());
        Log.d("App", "loaded tests/mock_test_n5_#5.xml");

        GiongoService gs = new GiongoService();
        gs.dropTable();
        gs.createTable();
        ges.dropTable();
        GiongoService.startCounting(getContentResolver());
        ges.createTable();
        gs.bulkInsertFromCSV("giongo/giongo.txt", getAssets(),
                getContentResolver());
        Log.d("App", "loaded giongo.txt");

       cws.dropTable();
        cws.createTable();
        cws.bulkInsertFromCSV("counters/numbers.txt", getAssets(),
                getContentResolver());
        Log.d("App", "loaded numbers.txt");
        cws.bulkInsertFromCSV("counters/people_and_things.txt", getAssets(),
                getContentResolver());
        Log.d("App", "loaded people_and_things.txt");
        cws.bulkInsertFromCSV("counters/time_calendar.txt", getAssets(),
                getContentResolver());
        Log.d("App", "loaded time_calendar.txt");
        cws.bulkInsertFromCSV("counters/extent_freq.txt", getAssets(),
                getContentResolver());
        Log.d("App", "loaded extent_freq.txt");

        grs.dropTable();
        grs.createTable();
        GrammarService.startCounting(getContentResolver());
        gres.dropTable();
        gres.createTable();
        grs.bulkInsertFromCSV("grammar/grammar_n5.txt",
                5, getAssets(), getContentResolver());
        Log.d("App", "loaded grammar_n5.txt");
        grs.bulkInsertFromCSV("grammar/grammar_n4.txt", 4, getAssets(),
                getContentResolver());
        Log.d("App", "loaded grammar_n4.txt");
        grs.bulkInsertFromCSV("grammar/grammar_n3.txt", 3, getAssets(),
                getContentResolver());
        Log.d("App", "loaded grammar_n3.txt");
        grs.bulkInsertFromCSV("grammar/grammar_n2.txt", 2, getAssets(),
                getContentResolver());
        Log.d("App", "loaded grammar_n2.txt");
        grs.bulkInsertFromCSV("grammar/grammar_n1.txt", 1, getAssets(),
                getContentResolver());
        Log.d("App", "loaded grammar_n1.txt");

        us.dropTable();
        Log.d("App", "all users deleted");
        us.createTable();*/

        // if it isn't first time when launching app - user exists in db
        userInfo = us.getUserWithCurrentLevel(App.cr);
        if (userInfo != null) {
            // fetch user data from db
            editor.putInt("level", userInfo.getLevel());
            editor.putString("language", Locale.getDefault().getLanguage());

            if (userInfo.getLevel() == 4 || userInfo.getLevel() == 5)
                App.isShowRomaji = true;
            else
                App.isShowRomaji = false;

            App.isColored = App.settings.getBoolean("color", true);
            App.isAutoplayed = App.settings.getBoolean("autoplay", true);

        }
        else{

        }
        App.context = getApplicationContext();
        kanjiFont = Typeface.createFromAsset(context.getAssets(),
                "fonts/EPKYOUKA.TTF");
        titleFont = Typeface.createFromAsset(context.getAssets(),
                "fonts/MAIAN.TTF");
        titleFontItalic = Typeface.createFromAsset(context.getAssets(),
                "fonts/mvboli.ttf");
    }

	public static void updateUserData() {
		us.update(userInfo, cr);
	}

	public static void goToLevel(int level) {
		// if user with this level doesn't exist - create new user with new
		// level
		userInfo = us.selectUser(level, cr);
		int numOfVoc = vs.getNumberOfWordsInLevel(level, cr);
		int numOfGrammar = grs.getNumberOfGrammarInLevel(level, cr);
		int numOfGiongo = gs.getNumberOfGiongo(cr);
		int numOfCounterWords = cws.getNumberOfCounterWords(cr);
            int id = us.getNumberOfUsers(cr) + 1;
            userInfo = new User(id, level, 0, numOfVoc, 0, numOfGrammar, 0,
                    numOfGiongo, 0, numOfCounterWords, 1, 1, 1, 1, 1, 1);
            // set all other users as not current
            us.insert(userInfo, cr);
        us.setAsInactiveOtherLevels(level, cr);
            editor.putString("level", level + "");
            editor.apply();
	}

	public static long[] getTimeTestLimits() {
		long timeLimit1 = 0;
		long timeLimit2 = 0;
		long timeLimit3 = 0;
		switch (userInfo.getIsCurrentLevel()) {
		// in milliseconds
		case 1:
			timeLimit1 = 110 * 60 * 1000;
			timeLimit2 = 0;
			timeLimit3 = 60 * 60 * 1000;
		case 2:
			timeLimit1 = 105 * 60 * 1000;
			timeLimit2 = 0;
			timeLimit3 = 50 * 60 * 1000;
		case 3:
			timeLimit1 = 30 * 60 * 1000;
			timeLimit2 = 70 * 60 * 1000;
			timeLimit3 = 40 * 60 * 1000;
		case 4:
			timeLimit1 = 30 * 60 * 1000;
			timeLimit2 = 60 * 60 * 1000;
			timeLimit3 = 35 * 60 * 1000;
		case 5:
			// timeLimit1 = 20 * 1000;
			timeLimit1 = 25 * 60 * 1000;
			timeLimit2 = 50 * 60 * 1000;
			timeLimit3 = 30 * 60 * 1000;
		}
		return new long[] { timeLimit1, timeLimit2, timeLimit3 };
	}

	// increment for percentage of learned element when responding correctly
	public static double getPercentageIncrement() {
		return 1.0 / numberOfRepeatationsForLearning;
	}

	public static boolean isVocabularyLearned() {
		return (userInfo.getNumberOfVocabularyInLevel() <= userInfo
				.getLearnedVocabulary());
	}

	public static boolean isGrammarLearned() {
		return (userInfo.getNumberOfGrammarInLevel() <= userInfo
				.getLearnedGrammar());
	}

	public static boolean isGiongoLearned() {
		return (userInfo.getNumberOfGiongoInLevel() <= userInfo
				.getLearnedGiongo());
	}

	public static boolean isCounterWordsLearned() {
		return (userInfo.getNumberOfCounterWordsInLevel() <= userInfo
				.getLearnedCounterWords());
	}

	public static boolean isAllTestsPassed() {
		return ts.isAllTestsPassed(cr, userInfo.getLevel());
	}

	public static boolean isAllLearned() {
		if (isVocabularyLearned() && isGrammarLearned()) {
			if (userInfo.getLevel() != 1)
				goToLevel(userInfo.getLevel() - 1);
			return true;
		}
		return false;
	}
}