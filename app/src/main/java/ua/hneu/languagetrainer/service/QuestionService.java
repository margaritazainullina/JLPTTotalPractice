package ua.hneu.languagetrainer.service;

import java.util.ArrayList;

import ua.hneu.languagetrainer.db.dao.QuestionDAO;
import ua.hneu.languagetrainer.db.dao.TestDAO;
import ua.hneu.languagetrainer.model.tests.Answer;
import ua.hneu.languagetrainer.model.tests.Question;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author Margarita Zainullina <margarita.zainullina@gmail.com>
 * @version 1.0
 */
public class QuestionService {
	AnswerService as = new AnswerService();
	static int numberOfEnteries = 0;

	/**
	 * Inserts an Question to database
	 * 
	 * @param q
	 *            Question instance with answers to insert
	 * @param testId
	 *            id of test to which question belongs
	 * @param cr
	 *            content resolver to database
	 */
	public void insert(Question q, int testId, ContentResolver cr) {
		for (Answer a : q.getAnswers()) {
			as.insert(a, numberOfEnteries, cr);
		}
		numberOfEnteries++;
		ContentValues values = new ContentValues();
		values.put(QuestionDAO.SECTION, q.getSection());
		values.put(QuestionDAO.TASK, q.getTask());
		values.put(QuestionDAO.TITLE, q.getTitle());
		values.put(QuestionDAO.TEXT, q.getText());
		values.put(QuestionDAO.WEIGHT, q.getWeight());
		values.put(QuestionDAO.IMG, q.getImgRef());
		values.put(QuestionDAO.AUDIO, q.getAudioRef());
		values.put(QuestionDAO.T_ID, testId);
		cr.insert(QuestionDAO.CONTENT_URI, values);
	}

	/**
	 * Updates a Question in database
	 * 
	 * @param q
	 *            - Question instance to upate
	 * @param cr
	 *            - content resolver to database
	 */
	public void update(Question q, ContentResolver cr) {
		ContentValues values = new ContentValues();
		values.put(QuestionDAO.TITLE, q.getTitle());
		values.put(QuestionDAO.TEXT, q.getText());
		cr.update(QuestionDAO.CONTENT_URI, values, "_ID=" + q.getId(), null);
	}

	/**
	 * Deletes all entries from Question table
	 */
	public void emptyTable() {
		QuestionDAO.getDb().execSQL("delete from " + QuestionDAO.TABLE_NAME);
	}

	/**
	 * Creates Question table
	 */
	public void createTable() {
		SQLiteDatabase db = QuestionDAO.getDb();
		db.execSQL("CREATE TABLE if not exists " + QuestionDAO.TABLE_NAME
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ QuestionDAO.SECTION + " TEXT, " + QuestionDAO.TASK
				+ " TEXT, " + QuestionDAO.TITLE + " TEXT, " + QuestionDAO.TEXT
				+ " TEXT, " + QuestionDAO.WEIGHT + " DOUBLE," + QuestionDAO.IMG
				+ " DOUBLE," + QuestionDAO.AUDIO + " DOUBLE,"
				+ QuestionDAO.T_ID + " INTEGER, " + "FOREIGN KEY("
				+ QuestionDAO.T_ID + ") REFERENCES " + TestDAO.TABLE_NAME + "("
				+ TestDAO.ID + "));");
	}

	/**
	 * Drops Question table
	 */
	public void dropTable() {
		QuestionDAO.getDb().execSQL(
				"DROP TABLE if exists " + QuestionDAO.TABLE_NAME + ";");
	}

	/**
	 * Returns number of entries in Question table
	 * 
	 * @param cr
	 *            content resolver to database
	 * @return num number of entries
	 */
	public static int getNumberOfQuestions(ContentResolver contentResolver) {
		Cursor countCursor = contentResolver.query(QuestionDAO.CONTENT_URI,
				new String[] { "count(*) AS count" }, null + "", null, null);
		countCursor.moveToFirst();
		int num = countCursor.getInt(0);
		countCursor.close();
		return num;
	}

	/**
	 * A stub to find out last id in Question table
	 * 
	 * @param cr
	 *            content resolver to database
	 */
	public static void startCounting(ContentResolver contentResolver) {
		numberOfEnteries = getNumberOfQuestions(contentResolver) + 1;
	}

	/**
	 * Gets list of all Questions in db of target test
	 * 
	 * @param cr
	 *            - content resolver to database
	 * @return questionsList list of all questions in the test
	 */
	@SuppressLint("NewApi")
	public ArrayList<Question> getQuestionsByTestId(int testId,
			ContentResolver cr) {
		String[] col = { QuestionDAO.ID, QuestionDAO.T_ID, QuestionDAO.SECTION,
				QuestionDAO.TASK, QuestionDAO.TITLE, QuestionDAO.TEXT,
				QuestionDAO.WEIGHT, QuestionDAO.IMG, QuestionDAO.AUDIO };
		Cursor c = cr.query(QuestionDAO.CONTENT_URI, col, QuestionDAO.T_ID
				+ "=" + testId, null, null, null);
		c.moveToFirst();
		int id = 0;
		String section = "";
		String task = "";
		String title = "";
		String text = "";
		double weight = 0;
		String img = "";
		String audio = "";
		ArrayList<Question> questionsList = new ArrayList<Question>();
		while (!c.isAfterLast()) {
			id = c.getInt(0);
			section = c.getString(2).trim();
			task = c.getString(3).trim();
			title = c.getString(4).trim();
			text = c.getString(5).trim();
			weight = c.getDouble(6);
			img = c.getString(7).trim();
			audio = c.getString(8).trim();
			ArrayList<Answer> a = as.getAswersByQuestionId(id, cr);
			questionsList.add(new Question(id, section, task, title, text,
					weight, a, img, audio));
			c.moveToNext();
		}
		c.close();
		return questionsList;
	}
}
