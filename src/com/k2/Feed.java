package com.k2;

public class Feed {
	private String mTitle;
	private String mId;
	private String mUpdated;
	private String mPublished;
	private String mLocation;
	private String mContent;

	public void set(String title, String id, String updated, String published, String location, String content) {
		mTitle     = checkNullAndTrim(title);
		mId        = checkNullAndTrim(id);
		mUpdated   = checkNullAndTrim(updated);
		mPublished = checkNullAndTrim(published);
		mLocation  = checkNullAndTrim(location);
		mContent   = checkNullAndTrim(content);
	}

	public String checkNullAndTrim(String str) {
		if (str == null)
			return "";
		
		return str.trim();
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getId() {
		return mId;
	}

	public void setId(String mId) {
		this.mId = mId;
	}

	public String getUpdated() {
		return mUpdated;
	}

	public void setUpdated(String mUpdated) {
		this.mUpdated = mUpdated;
	}

	public String getPublished() {
		return mPublished;
	}

	public void setPublished(String mPublished) {
		this.mPublished = mPublished;
	}

	public String getLocation() {
		return mLocation;
	}

	public void setLocation(String mLocation) {
		this.mLocation = mLocation;
	}

	public String getContent() {
		return mContent;
	}

	public void setContent(String mContent) {
		this.mContent = mContent;
	}

	
}
