package com.k2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class FeedParser {

	private DbHelper mDbHelper;
	private Feed mFeed;
	
	public FeedParser(DbHelper helper) {
		mDbHelper = helper;
		mFeed = new Feed();
	}

	public void setDbHelper(DbHelper helper) {
		mDbHelper = helper;
	}

	public void downloadFeed() throws IOException {
		FileOutputStream fos = null;
		try {
			URL feed = new URL(Constants.FEED_URL);
			ReadableByteChannel rbc = Channels.newChannel(feed.openStream());
			fos = new FileOutputStream(Constants.FEEDS_FILE);
			while (fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE) != 0);
			fos.flush();
			System.out.println("Feeds are saved to " + Constants.FEEDS_FILE);
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if (fos != null) {
				fos.close();
				fos = null;
			}
		}
	}

	public void parse() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		File file = new File(Constants.FEEDS_FILE);

		Document doc = builder.parse(file);
		NodeList entries = doc.getElementsByTagName("entry");
		final int entriesCnt = entries.getLength(); 

		mDbHelper.initPreparedStatement();

		for (int i = 0; i < entriesCnt; i++) {
			Element entry = (Element) entries.item(i);

			NodeList titleNode = entry.getElementsByTagName("title");
			final String title = titleNode.item(0).getTextContent();
			
			NodeList idNode = entry.getElementsByTagName("id");
			final String id = idNode.item(0).getTextContent();
			
			NodeList updatedNode = entry.getElementsByTagName("updated");
			final String updated = updatedNode.item(0).getTextContent();
			
			NodeList publishedNode = entry.getElementsByTagName("published");
			final String published = publishedNode.item(0).getTextContent();
			
			NodeList locationNode = entry.getElementsByTagName("location");
			final String location = locationNode.item(0).getTextContent();
			
			NodeList contentNode = entry.getElementsByTagName("content");
			final String content = contentNode.item(0).getTextContent();
			
			mFeed.set(title, id, updated, published, location, content);
			//mDbHelper.insertFeed(mFeed);
			mDbHelper.insertFeedIntoBatch(mFeed);
		}
		
		mDbHelper.execPreparedStatement();

		System.out.println("Added " + entriesCnt + " entries");
	}
}
