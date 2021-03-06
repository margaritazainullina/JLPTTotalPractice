package ua.hneu.languagetrainer.service;

import ua.hneu.languagetrainer.db.dao.UserDAO;
import ua.hneu.languagetrainer.model.User;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Margarita Zainullina <margarita.zainullina@gmail.com>
 * @version 1.0
 */
public class UserService {
	/**
	 * Inserts a level passing information to a table User. A row in table - a
	 * level of passing
	 * 
	 * @param u
	 *            - User instance to insert
	 * @param cr
	 *            - content resolver to database
	 */
	public void insert(User u, ContentResolver cr) {
		ContentValues values = new ContentValues();
		values.put(UserDAO.LEVEL, u.getLevel());
		values.put(UserDAO.LEARNEDVOC, u.getLearnedVocabulary());
		values.put(UserDAO.ALLVOC, u.getNumberOfVocabularyInLevel());
		values.put(UserDAO.LEARNEDGRAMMAR, u.getLearnedGrammar());
		values.put(UserDAO.ALLGRAMMAR, u.getNumberOfGrammarInLevel());
		values.put(UserDAO.LEARNEDGIONGO, u.getLearnedGiongo());
		values.put(UserDAO.ALLGIONGO, u.getNumberOfGiongoInLevel());
		values.put(UserDAO.LEARNEDCWORDS, u.getLearnedCounterWords());
		values.put(UserDAO.ALLCWORDS, u.getNumberOfCounterWordsInLevel());
        values.put(UserDAO.ISLEVELLAUNCHEDFIRSTTIME,
                u.getIsLevelLaunchedFirstTime());

        values.put(UserDAO.ISVOCLAUNCHEDFIRSTTIME, u.getIsVocLaunchedFirstTime());
        values.put(UserDAO.ISGRAMMRAUNCHEDFIRSTTIME, u.getIsGrammarLaunchedFirstTime());
        values.put(UserDAO.ISGIONGOLAUNCHEDFIRSTTIME, u.getIsGiongoLaunchedFirstTime());
        values.put(UserDAO.ISCWLAUNCHEDFIRSTTIME, u.getIsCWLaunchedFirstTime());

		values.put(UserDAO.ISCURRENTLEVEL, u.getIsCurrentLevel());
		cr.insert(UserDAO.CONTENT_URI, values);
	}

	/**
	 * Updates a level passing information to a table User.
	 * 
	 * @param u
	 *            - User instance to insert
	 * @param cr
	 *            - content resolver to database
	 */
	public void update(User u, ContentResolver cr) {
		ContentValues values = new ContentValues();
		values.put(UserDAO.LEVEL, u.getLevel());
		values.put(UserDAO.LEARNEDVOC, u.getLearnedVocabulary());
		values.put(UserDAO.ALLVOC, u.getNumberOfVocabularyInLevel());
		values.put(UserDAO.LEARNEDGRAMMAR, u.getLearnedGrammar());
		values.put(UserDAO.ALLGRAMMAR, u.getNumberOfGrammarInLevel());
		values.put(UserDAO.LEARNEDGIONGO, u.getLearnedGiongo());
		values.put(UserDAO.ALLGIONGO, u.getNumberOfGiongoInLevel());
		values.put(UserDAO.LEARNEDCWORDS, u.getLearnedCounterWords());
		values.put(UserDAO.ALLCWORDS, u.getNumberOfCounterWordsInLevel());
		values.put(UserDAO.ISLEVELLAUNCHEDFIRSTTIME,
				u.getIsLevelLaunchedFirstTime());
		values.put(UserDAO.ISCURRENTLEVEL, u.getIsCurrentLevel());
//for sorting randomly first time, and on last show all other times
        values.put(UserDAO.ISVOCLAUNCHEDFIRSTTIME, u.getIsVocLaunchedFirstTime());
        values.put(UserDAO.ISGRAMMRAUNCHEDFIRSTTIME, u.getIsGrammarLaunchedFirstTime());
        values.put(UserDAO.ISGIONGOLAUNCHEDFIRSTTIME, u.getIsGiongoLaunchedFirstTime());
        values.put(UserDAO.ISCWLAUNCHEDFIRSTTIME, u.getIsCWLaunchedFirstTime());
		cr.update(UserDAO.CONTENT_URI, values, "_ID=" + u.getId(), null);
	}

	/**
	 * Deletes all entries from User table
	 */
	public void emptyTable() {
		UserDAO.getDb().execSQL("delete from " + UserDAO.TABLE_NAME);
	}

	/**
	 * Creates User table
	 */
	public void createTable() {
		SQLiteDatabase db = UserDAO.getDb();
		db.execSQL("CREATE TABLE if not exists " + UserDAO.TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " + UserDAO.LEVEL
				+ " INTEGER, " + UserDAO.LEARNEDVOC + " INTEGER, "
				+ UserDAO.ALLVOC + " INTEGER, " + UserDAO.LEARNEDGRAMMAR
				+ " INTEGER, " + UserDAO.ALLGRAMMAR + " INTEGER, "
				+ UserDAO.LEARNEDGIONGO + " INTEGER," + UserDAO.ALLGIONGO
				+ " INTEGER, " + UserDAO.LEARNEDCWORDS + " INTEGER,"
				+ UserDAO.ALLCWORDS + " INTEGER,"
				+ UserDAO.ISLEVELLAUNCHEDFIRSTTIME + " INTEGER,"
                + UserDAO.ISVOCLAUNCHEDFIRSTTIME + " INTEGER,"
                + UserDAO.ISGRAMMRAUNCHEDFIRSTTIME + " INTEGER,"
                + UserDAO.ISGIONGOLAUNCHEDFIRSTTIME + " INTEGER,"
                + UserDAO.ISCWLAUNCHEDFIRSTTIME + " INTEGER,"
				+ UserDAO.ISCURRENTLEVEL + " INTEGER);");
	}

	/**
	 * Drops User table
	 */
	public void dropTable() {
		UserDAO.getDb().execSQL(
				"DROP TABLE if exists " + UserDAO.TABLE_NAME + ";");
	}

	/**
	 * Selects a user with target level. If doesn't exist - returns null.
	 * 
	 * @param level
	 *            - target user level
	 * @param cr
	 *            - content resolver to database
	 */
	@SuppressLint("NewApi")
	public User selectUser(int level, ContentResolver cr) {
		String[] selectionArgs = { UserDAO.ID, UserDAO.LEVEL,
				UserDAO.LEARNEDVOC, UserDAO.ALLVOC, UserDAO.LEARNEDGRAMMAR,
				UserDAO.ALLGRAMMAR, UserDAO.LEARNEDGIONGO, UserDAO.ALLGIONGO,
				UserDAO.LEARNEDCWORDS, UserDAO.ALLCWORDS,
				UserDAO.ISLEVELLAUNCHEDFIRSTTIME,
                UserDAO.ISVOCLAUNCHEDFIRSTTIME,
                UserDAO.ISGRAMMRAUNCHEDFIRSTTIME,
                UserDAO.ISGIONGOLAUNCHEDFIRSTTIME,
                UserDAO.ISCWLAUNCHEDFIRSTTIME,
                UserDAO.ISCURRENTLEVEL };

		Cursor c = cr.query(UserDAO.CONTENT_URI, selectionArgs, UserDAO.LEVEL
				+ "=" + level, null, null, null);
		int id = 0;
		int learnedVocabulary = 0;
		int numberOfVocabularyInLevel = 0;
		int learnedGrammar = 0;
		int numberOfGrammarInLevel = 0;
		int learnedGiongo = 0;
		int numberOfGiongoInLevel = 0;
		int learnedCounterWords = 0;
		int numberOfCounterWordsInLevel = 0;
		int isLevelLaunchedFirstTime = 1;
        int isVocLaunchedFirstTime = 1;
        int isGrammarLaunchedFirstTime = 1;
        int isGiongoLaunchedFirstTime = 1;
        int isCWLaunchedFirstTime = 1;
		int isCurrentLevel = 1;
		c.moveToFirst();
		boolean isNotNull = false;
		while (!c.isAfterLast()) {
			isNotNull = true;
			id = c.getInt(0);
			learnedVocabulary = c.getInt(2);
			numberOfVocabularyInLevel = c.getInt(3);
			learnedGrammar = c.getInt(4);
			numberOfGrammarInLevel = c.getInt(5);
			learnedGiongo = c.getInt(6);
			numberOfGiongoInLevel = c.getInt(7);
			learnedCounterWords = c.getInt(8);
			numberOfCounterWordsInLevel = c.getInt(9);
			isLevelLaunchedFirstTime = c.getInt(10);
            isVocLaunchedFirstTime = c.getInt(11);
            isGrammarLaunchedFirstTime = c.getInt(12);
            isGiongoLaunchedFirstTime = c.getInt(13);
            isCWLaunchedFirstTime = c.getInt(14);
			isCurrentLevel = c.getInt(15);
			c.moveToNext();
		}
		c.close();
		if (isNotNull) {
			User u = new User(id, level, learnedVocabulary,
					numberOfVocabularyInLevel, learnedGrammar,
					numberOfGrammarInLevel, learnedGiongo,
					numberOfGiongoInLevel, learnedCounterWords,
					numberOfCounterWordsInLevel, isLevelLaunchedFirstTime,isVocLaunchedFirstTime,
                    isGrammarLaunchedFirstTime,isGiongoLaunchedFirstTime,isCWLaunchedFirstTime,
					isCurrentLevel);
			return u;
		} else
			return null;
	}

	/**
	 * Returns number of entries in User table
	 * 
	 * @param cr
	 *            content resolver to database
	 * @return number of entries
	 */
	public int getNumberOfUsers(ContentResolver cr) {
		Cursor countCursor = cr.query(UserDAO.CONTENT_URI,
				new String[] { "count(*) AS count" }, null, null, null);

		countCursor.moveToFirst();
		int count = countCursor.getInt(0);
		countCursor.close();
		return count;
	}

	/**
	 * Selects current user with current passing level. If doesn't exist -
	 * returns null.
	 * 
	 * @param cr
	 *            - content resolver to database
	 */
	@SuppressLint("NewApi")
	public User getUserWithCurrentLevel(ContentResolver cr) {
		String[] selectionArgs = { UserDAO.ID, UserDAO.LEVEL,
				UserDAO.LEARNEDVOC, UserDAO.ALLVOC, UserDAO.LEARNEDGRAMMAR,
				UserDAO.ALLGRAMMAR, UserDAO.LEARNEDGIONGO, UserDAO.ALLGIONGO,
				UserDAO.LEARNEDCWORDS, UserDAO.ALLCWORDS,
				UserDAO.ISLEVELLAUNCHEDFIRSTTIME,
                UserDAO.ISVOCLAUNCHEDFIRSTTIME,
                UserDAO.ISGRAMMRAUNCHEDFIRSTTIME,
                UserDAO.ISGIONGOLAUNCHEDFIRSTTIME,
                UserDAO.ISCWLAUNCHEDFIRSTTIME,
                UserDAO.ISCURRENTLEVEL };

		Cursor c = cr.query(UserDAO.CONTENT_URI, selectionArgs,
				UserDAO.ISCURRENTLEVEL + "=" + 1, null, null, null);
		int id = 0;
		int level = 0;
		int learnedVocabulary = 0;
		int numberOfVocabularyInLevel = 0;
		int learnedGrammar = 0;
		int numberOfGrammarInLevel = 0;
		int learnedAudio = 0;
		int numberOfGiongoInLevel = 0;
		int learnedCounterWords = 0;
		int numberOfCounterWordsInLevel = 0;
		int isLevelLaunchedFirstTime = 1;
        int isVocLaunchedFirstTime = 1;
        int isGrammarLaunchedFirstTime = 1;
        int isGiongoLaunchedFirstTime = 1;
        int isCWLaunchedFirstTime = 1;
		int isCurrentLevel = 1;
		c.moveToFirst();
		boolean isUserExists = false;
		while (!c.isAfterLast()) {
			isUserExists = true;
			id = c.getInt(0);
			level = c.getInt(1);
			learnedVocabulary = c.getInt(2);
			numberOfVocabularyInLevel = c.getInt(3);
			learnedGrammar = c.getInt(4);
			numberOfGrammarInLevel = c.getInt(5);
			learnedAudio = c.getInt(6);
			numberOfGiongoInLevel = c.getInt(7);
			learnedCounterWords = c.getInt(8);
			numberOfCounterWordsInLevel = c.getInt(9);
			isLevelLaunchedFirstTime = c.getInt(10);
            isVocLaunchedFirstTime = c.getInt(11);
            isGrammarLaunchedFirstTime = c.getInt(12);
            isGiongoLaunchedFirstTime = c.getInt(13);
            isCWLaunchedFirstTime = c.getInt(14);
			isCurrentLevel = c.getInt(15);
			c.moveToNext();
		}
		c.close();
		if (isUserExists) {
			User u = new User(id, level, learnedVocabulary,
					numberOfVocabularyInLevel, learnedGrammar,
					numberOfGrammarInLevel, learnedAudio,
					numberOfGiongoInLevel, learnedCounterWords,
					numberOfCounterWordsInLevel, isLevelLaunchedFirstTime, isVocLaunchedFirstTime,
                    isGrammarLaunchedFirstTime, isGiongoLaunchedFirstTime, isCWLaunchedFirstTime,
					isCurrentLevel);
			return u;
		} else
			return null;
	}

	/**
	 * Sets other users with other levels inactive, and user with target level -
	 * active
	 */
	public void setAsInactiveOtherLevels(int level, ContentResolver cr) {
		for (int i = 1; i < 6; i++) {
			User u = selectUser(i, cr);
			if (u != null && i != level) {
				u.setIsCurrentLevel(0);
				update(u, cr);
			}
		}
		User u = selectUser(level, cr);
		u.setIsCurrentLevel(1);
		update(u, cr);
	}

}