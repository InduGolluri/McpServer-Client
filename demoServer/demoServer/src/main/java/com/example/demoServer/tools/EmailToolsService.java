package com.example.demoServer.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.example.demoServer.model.Email;
import com.example.demoServer.service.EmailService;

@Component
public class EmailToolsService {

	private final EmailService emailSvc;

	public EmailToolsService(EmailService emailSvc) {
		this.emailSvc = emailSvc;
	}

	// --------------------------
	// LIST FOLDERS
	// --------------------------
	@Tool(name = "outlook_list_folders")
	public String listFolders() {
		List<String> folders = emailSvc.listFolders();
		if (folders.isEmpty())
			return "No folders found.";

		StringBuilder sb = new StringBuilder(" FOLDERS\n\n");
		folders.forEach(f -> sb.append(". ").append(f).append("\n"));
		return sb.toString();
	}

	// --------------------------
	// SEARCH EMAILS
	// --------------------------
//    @Tool(name = "outlook_search_emails")
//    public String search(
//            @ToolParam String q,
//            @ToolParam String folder,
//            @ToolParam Boolean unreadOnly,
//            @ToolParam Integer limit) {
//
//        List<Email> list = emailSvc.search(q, folder, unreadOnly, limit);
//        if (list.isEmpty()) return "No matching emails.";
//
//        StringBuilder sb = new StringBuilder("SEARCH RESULTS\n\n");
//        for (Email e : list) {
//            sb.append("-----------------------------------\n");
//            sb.append("ID: ").append(e.getId()).append("\n");
//            sb.append("From: ").append(e.getFrom()).append("\n");
//            sb.append("Subject: ").append(e.getSubject()).append("\n");
//            sb.append("Folder: ").append(e.getFolder()).append("\n");
//            sb.append("Unread: ").append(e.isUnread() ? "Yes" : "No").append("\n\n");
//        }
//        return sb.toString();
//    }

	// ---------------------------
	// SEARCH EMAILS (query required, unread optional)
	// ---------------------------
	@Tool(name = "outlook_search_emails", description = "Search emails using a natural language query. Optionally filter unread messages.")
	public String search(@ToolParam String query, @ToolParam Boolean unread) {
		// normalize unread flag
		boolean unreadOnly = Boolean.TRUE.equals(unread);

		// default limit (you can change this)
		final int defaultLimit = 10;

		// call underlying service (folder = null, limit = defaultLimit)
		List<Email> emails = emailSvc.search(query, null, unreadOnly, defaultLimit);

		if (emails == null || emails.isEmpty()) {
			return "No matching emails found.";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("🔎 Search Results\n");
		sb.append("Query: ").append(query == null ? "(none)" : query).append("\n");
		sb.append("Unread Only: ").append(unreadOnly ? "Yes" : "No").append("\n");
		sb.append("Results: ").append(emails.size()).append("\n\n");

		for (Email e : emails) {
			if (e == null)
				continue; // defensive
			sb.append("--------------------------------------------------\n");
			sb.append("📨 Email\n");
			sb.append("ID: ").append(e.getId()).append("\n");
			sb.append("From: ").append(e.getFrom() == null ? "(unknown)" : e.getFrom()).append("\n");
			// Use only fields we know exist in your model
			sb.append("Folder: ").append(e.getFolder() == null ? "(none)" : e.getFolder()).append("\n");
			sb.append("Subject: ").append(e.getSubject() == null ? "(no subject)" : e.getSubject()).append("\n");
			sb.append("Unread: ").append(e.isUnread() ? "Yes" : "No").append("\n");

			// safe body preview (avoid printing huge bodies)
			String body = e.getBody();
			if (body != null && !body.isBlank()) {
				String preview = body.length() > 120 ? body.substring(0, 120) + "..." : body;
				sb.append("Preview: ").append(preview).append("\n");
			}

			sb.append("--------------------------------------------------\n\n");
		}

		return sb.toString();
	}

	// --------------------------
	// READ EMAIL BY ID
	// --------------------------
	@Tool(name = "outlook_get_email")
	public String getEmail(@ToolParam String id) {
		Email e = emailSvc.get(id);
		if (e == null)
			return "Email not found.";

		return """
				EMAIL DETAILS

				ID: %s
				From: %s
				Subject: %s
				Folder: %s
				Unread: %s

				BODY:
				%s
				""".formatted(e.getId(), e.getFrom(), e.getSubject(), e.getFolder(), e.isUnread() ? "Yes" : "No",
				e.getBody());
	}

	// --------------------------
	// MAILBOX SUMMARY
	// --------------------------
	@Tool(name = "outlook_mailbox_summary")
	public String summary() {
		long unread = emailSvc.unread();
		return """
				MAILBOX SUMMARY

				Unread Emails: %d
				""".formatted(unread);
	}

	// --------------------------
	// ADD EMAIL
	// --------------------------
	@Tool(name = "outlook_add_email")
	public String addEmail(@ToolParam String from, @ToolParam String subject, @ToolParam String body,
			@ToolParam String folder) {

		return emailSvc.addEmail(from, subject, body, folder);
	}

	// --------------------------
	// DELETE EMAIL
	// --------------------------
	@Tool(name = "outlook_delete_email")
	public String deleteEmail(@ToolParam String id) {
		return emailSvc.deleteEmail(id);
	}

	// --------------------------
	// MARK UNREAD
	// --------------------------
	@Tool(name = "outlook_mark_unread")
	public String markUnread(@ToolParam String id) {
		return emailSvc.markUnread(id);
	}

	// --------------------------
	// LIST EMAILS IN FOLDER
	// --------------------------
	@Tool(name = "outlook_list_folder_emails")
	public String listFolderEmails(@ToolParam String folder) {
		List<Email> list = emailSvc.search(null, folder, null, null);
		if (list.isEmpty())
			return "No emails found in folder: " + folder;

		StringBuilder sb = new StringBuilder("📁 EMAILS IN FOLDER: " + folder + "\n\n");
		for (Email e : list) {
			sb.append("- ID: ").append(e.getId()).append(" | Subject: ").append(e.getSubject()).append(" | Unread: ")
					.append(e.isUnread() ? "Yes" : "No").append("\n");
		}
		return sb.toString();
	}

	// --------------------------
	// COUNT EMAILS BY FOLDER
	// --------------------------
	@Tool(name = "outlook_folder_counts", description = "Returns a JSON map of folder counts")
	public Map<String, Long> folderCounts() {
		Map<String, Long> map = new HashMap<>();
		for (String folder : emailSvc.listFolders()) {
			map.put(folder, (long) emailSvc.search(null, folder, null, null).size());
		}
		return map;
	}

}
