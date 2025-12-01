package com.example.demoServer.model;

public class Email {

	private String id;
	private String from;
	private String subject;
	private String body;
	private String folder;
	private boolean unread;

	public Email(String id, String from, String subject, String body, String folder, boolean unread) {
		this.id = id;
		this.from = from;
		this.subject = subject;
		this.body = body;
		this.folder = folder;
		this.unread = unread;
	}

	public String getId() {
		return id;
	}

	public String getFrom() {
		return from;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public String getFolder() {
		return folder;
	}

	public boolean isUnread() {
		return unread;
	}
}
