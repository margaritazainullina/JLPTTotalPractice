package ua.hneu.languagetrainer.db.dao;

import java.util.HashMap;

import ua.hneu.languagetrainer.db.DictionaryDbHelper;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

public class UserDAO extends ContentProvider {

	public static String TABLE_NAME = "user";
	public static final String ID = "_id";
	public static final String LEVEL = "level";
	public static final String LEARNEDVOC = "learnedVocabulary";
	public static final String ALLVOC = "numberOfVocabularyInLevel";
	public static final String LEARNEDGRAMMAR = "learnedGrammar";
	public static final String ALLGRAMMAR = "numberOfGrammarInLevel";
	public static final String LEARNEDGIONGO = "learnedGiongo";
	public static final String ALLGIONGO = "numberOfGiongoInLevel";
	public static final String LEARNEDCWORDS = "learnedCounterWords";
	public static final String ALLCWORDS = "numberOfCounterWordsInLevel";
	public static final String ISLEVELLAUNCHEDFIRSTTIME = "isLevelLaunchedFirstTime";
    public static final String ISVOCLAUNCHEDFIRSTTIME = "isvocLaunchedFirstTime";
    public static final String ISGRAMMRAUNCHEDFIRSTTIME = "isgrammarLaunchedFirstTime";
    public static final String ISGIONGOLAUNCHEDFIRSTTIME = "isgiongoLaunchedFirstTime";
    public static final String ISCWLAUNCHEDFIRSTTIME = "iscwLaunchedFirstTime";
    public static final String ISCURRENTLEVEL = "isCurrentLevel";

	public static final Uri CONTENT_URI = Uri
			.parse("content://ua.edu.hneu.languagetrainer.db.userprovider/dictionary");
	
	public static final int URI_CODE = 1;
	public static final int URI_CODE_ID = 2;
	
	private static final UriMatcher mUriMatcher;

	private static HashMap<String, String> mContactMap;

	private static SQLiteDatabase db;

	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI("ua.hneu.languagetrainer.db", TABLE_NAME, URI_CODE);
		mUriMatcher.addURI("ua.hneu.languagetrainer.db", TABLE_NAME + "/#",
				URI_CODE_ID);

		mContactMap = new HashMap<String, String>();
		mContactMap.put(DictionaryDbHelper._ID, DictionaryDbHelper._ID);
		mContactMap.put(LEVEL, LEVEL);
		mContactMap.put(LEARNEDVOC, LEARNEDVOC);
		mContactMap.put(ALLVOC, ALLVOC);
		mContactMap.put(LEARNEDGRAMMAR, LEARNEDGRAMMAR);
		mContactMap.put(ALLGRAMMAR, ALLGRAMMAR);
		mContactMap.put(LEARNEDGIONGO, LEARNEDGIONGO);
		mContactMap.put(ALLGIONGO, ALLGIONGO);
		mContactMap.put(LEARNEDCWORDS, LEARNEDCWORDS);
		mContactMap.put(ALLCWORDS, ALLCWORDS);
		mContactMap.put(ISLEVELLAUNCHEDFIRSTTIME, ISLEVELLAUNCHEDFIRSTTIME);
        mContactMap.put(ISVOCLAUNCHEDFIRSTTIME, ISVOCLAUNCHEDFIRSTTIME);
        mContactMap.put(ISGRAMMRAUNCHEDFIRSTTIME, ISGRAMMRAUNCHEDFIRSTTIME);
        mContactMap.put(ISGIONGOLAUNCHEDFIRSTTIME, ISGIONGOLAUNCHEDFIRSTTIME);
        mContactMap.put(ISCWLAUNCHEDFIRSTTIME, ISCWLAUNCHEDFIRSTTIME);
		mContactMap.put(ISCURRENTLEVEL, ISCURRENTLEVEL);
	}

	public String getDbName() {
		return (DictionaryDbHelper.DB_NAME);
	}

	public static SQLiteDatabase getDb() {
		return db;
	}

	@Override
	public boolean onCreate() {
		db = (new DictionaryDbHelper(getContext())).getWritableDatabase();
		return (db == null) ? false : true;
	}

	@Override
	public Cursor query(Uri url, String[] projection, String selection,
			String[] selectionArgs, String sort) {

		String orderBy;
		if (TextUtils.isEmpty(sort)) {
			orderBy = LEVEL;
		} else {
			orderBy = sort;
		}

		Cursor c = db.query(TABLE_NAME, projection, selection, selectionArgs,
				null, null, orderBy);
		c.setNotificationUri(getContext().getContentResolver(), url);
		return c;
	}

	@Override
	public Uri insert(Uri url, ContentValues inValues) {

		ContentValues values = new ContentValues(inValues);

		long rowId = db.insert(TABLE_NAME, LEVEL, values);
		if (rowId > 0) {
			Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(uri, null);
			return uri;
		} else {
			throw new SQLException("Failed to insert row into " + url);
		}
	}

	@Override
	public int delete(Uri url, String where, String[] whereArgs) {
		int retVal = db.delete(TABLE_NAME, where, whereArgs);
		getContext().getContentResolver().notifyChange(url, null);
		return retVal;
	}

	@Override
	public int update(Uri url, ContentValues values, String where,
			String[] whereArgs) {
		int retVal = db.update(TABLE_NAME, values, where, whereArgs);
		getContext().getContentResolver().notifyChange(url, null);
		return retVal;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

}
