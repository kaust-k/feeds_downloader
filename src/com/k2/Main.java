package com.k2;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class Main {

	public static void main(String[] args) {
		Backup backup = new Backup();
		backup.performBackup();

		DbHelper helper = new DbHelper();
		FeedParser fp = new FeedParser(helper);

		try {
			helper.createFeedsTable();
			//fp.downloadFeed();
			fp.parse();
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
		} catch (SAXException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}

}
