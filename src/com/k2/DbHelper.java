package com.k2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DbHelper {

	private Connection mConnection = null;

	public void initConnection() throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		try {
			// create a database connection
			mConnection = DriverManager.getConnection("jdbc:sqlite:" + Constants.DB_NAME);

		} catch (SQLException e) {
			// if the error message is "out of memory",
			// it probably means no database file is found
			System.err.println(e.getMessage());
		}
	}

	public void closeConnection() {
		try {
			if (mConnection != null) {
				mConnection.close();
				mConnection = null;
			}
		} catch (SQLException e) {
			// connection close failed.
			System.err.println(e);
		}
	}

	// ---------------------------------------------------------------------------------------- //
	public void createFeedsTable() {
		Statement statement;
		try {
			initConnection();

			statement = mConnection.createStatement();

			statement.setQueryTimeout(30); // set timeout to 30 sec.
			final String createTableFeeds = "CREATE TABLE IF NOT EXISTS feeds ("
					+ "PRIMARYID  INTEGER  PRIMARY KEY AUTOINCREMENT,"
					+ "TITLE      TEXT,"
					+ "ID         TEXT,"
					+ "UPDATED    TEXT,"
					+ "PUBLISHED  TEXT,"
					+ "LOCATION   TEXT," 
					+ "CONTENT    TEXT,"
					+ "CONSTRAINT unq UNIQUE (TITLE, ID)"
					+ ")";

			statement.executeUpdate(createTableFeeds);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		} finally {
			closeConnection();
		}
	}

	// ---------------------------------------------------------------------------------------- //
	private PreparedStatement mPreparedStmt = null;
	private boolean mInitPSSuccessful = false;

	public void initPreparedStatement() {
		mInitPSSuccessful = false;
		try {
			initConnection();
			
			mPreparedStmt = mConnection
					.prepareStatement("insert or ignore into feeds values ($next_id, ?, ?, ?, ?, ?, ?);");
			mInitPSSuccessful = true;
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void execPreparedStatement() {
		try {
			if (mInitPSSuccessful && mPreparedStmt != null && mConnection != null) {
				mConnection.setAutoCommit(false);
				mPreparedStmt.executeBatch();
				mPreparedStmt.close();
				mConnection.setAutoCommit(true);
				mConnection.commit();
			}
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} finally {
			mPreparedStmt = null;
			mInitPSSuccessful = false;
			closeConnection();
		}
	}

	public void insertFeedIntoBatch(Feed feed) {
		try {
			mPreparedStmt.setString(2, feed.getTitle());
			mPreparedStmt.setString(3, feed.getId());
			mPreparedStmt.setString(4, feed.getUpdated());
			mPreparedStmt.setString(5, feed.getPublished());
			mPreparedStmt.setString(6, feed.getLocation());
			mPreparedStmt.setString(7, feed.getContent());
			mPreparedStmt.addBatch();
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
	}

	// ---------------------------------------------------------------------------------------- //
	/* Insert feed one by one */
	public void insertFeed(Feed feed) {
		try {
			initConnection();
			
			PreparedStatement prep = mConnection
					.prepareStatement("insert into feeds values ($next_id, ?, ?, ?, ?, ?, ?);");
			prep.setString(2, feed.getTitle());
			prep.setString(3, feed.getId());
			prep.setString(4, feed.getUpdated());
			prep.setString(5, feed.getPublished());
			prep.setString(6, feed.getLocation());
			prep.setString(7, feed.getContent());

			System.out.println("Adding entry " + feed.getTitle() + " to the DB");
			prep.execute();
			mConnection.setAutoCommit(true);
		} catch (SQLException e) {
			System.err.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println(e.getMessage());
		} finally {
			closeConnection();
		}
	}

}
