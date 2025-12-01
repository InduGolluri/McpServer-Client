package com.example.demoServer.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demoServer.model.Email;

import jakarta.annotation.PostConstruct;

@Service // Marks this class as a Spring service layer
public class EmailService {

    private final List<Email> emails = new ArrayList<>(); // In-memory email storage

    @PostConstruct // Runs after the service bean is created
    public void init() {

        // Adding sample emails to mock a real mailbox
        emails.add(new Email("1", "hr@company.com", "Welcome to Company",
                "Hello Hindhu, welcome aboard!", "Inbox", false));

        emails.add(new Email("2", "manager@company.com", "Sprint Planning",
                "Meeting tomorrow at 10 AM.", "Inbox", true));

        emails.add(new Email("3", "billing@cloud.com", "Monthly Invoice",
                "Your cloud invoice is ready.", "Finance", false));

        emails.add(new Email("4", "github@notifications.com", "Security Alert",
                "High severity dependency issue found.", "Alerts", true));

        emails.add(new Email("5", "hindhu@gmail.com", "OTP Code",
                "Your OTP is 444222", "Spam", true));

        emails.add(new Email("6", "hindhu@company.com", "Project Sent",
                "Please check the attached document.", "Sent", false));

        emails.add(new Email("7", "hindhu@company.com", "Draft Email",
                "Work in progress...", "Drafts", false));
    }

    // ---------------- LIST ALL EMAILS ----------------
    public List<Email> allEmails() {
        return emails; // Returns all stored emails
    }

    // ---------------- LIST DISTINCT FOLDERS ----------------
    public List<String> listFolders() {
        return emails.stream()
                .map(Email::getFolder)         // Get folder name
                .distinct()                    // Remove duplicates
                .sorted()                      // Sort alphabetically
                .collect(Collectors.toList());
    }

    // ---------------- ADD A NEW EMAIL ----------------
    public String addEmail(String from, String subject, String body, String folder) {
        String id = UUID.randomUUID().toString().substring(0, 8); // Generate short ID
        emails.add(new Email(id, from, subject, body, folder, true)); // Add unread email
        return "Email added with ID: " + id;
    }

  
    // ---------------- GET EMAIL BY ID ----------------
    public Email get(String id) {
        return emails.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // ---------------- DELETE EMAIL (MOVE TO TRASH) ----------------
    public String deleteEmail(String id) {
        Email e = get(id);
        if (e == null) return "Email not found.";

        emails.remove(e);
        emails.add(new Email(e.getId(), e.getFrom(), e.getSubject(), e.getBody(),
                "Trash", false)); // Mark read when moved to trash

        return "Email moved to Trash.";
    }

    // ---------------- MARK EMAIL AS READ ----------------
    public String markRead(String id) {
        Email e = get(id);
        if (e == null) return "Not found.";

        emails.remove(e);
        emails.add(new Email(e.getId(), e.getFrom(), e.getSubject(), e.getBody(),
                e.getFolder(), false)); // unread = false

        return "Email marked as read.";
    }

    // ---------------- MARK EMAIL AS UNREAD ----------------
    public String markUnread(String id) {
        Email e = get(id);
        if (e == null) return "Not found.";

        emails.remove(e);
        emails.add(new Email(e.getId(), e.getFrom(), e.getSubject(), e.getBody(),
                e.getFolder(), true)); // unread = true

        return "Email marked as unread.";
    }

    // ---------------- COUNT UNREAD EMAILS ----------------
    public long unread() {
        return emails.stream()
                .filter(Email::isUnread)
                .count();
    }

    // ---------------- SEARCH EMAILS (MULTI-FILTER) ----------------
    public List<Email> search(String q, String folder, Boolean unreadOnly, Integer limit) {

        return emails.stream()
                // Search by keyword in subject/body/from
                .filter(e -> q == null ||
                        e.getSubject().toLowerCase().contains(q.toLowerCase()) ||
                        e.getBody().toLowerCase().contains(q.toLowerCase()) ||
                        e.getFrom().toLowerCase().contains(q.toLowerCase()))

                // Filter by folder
                .filter(e -> folder == null || e.getFolder().equalsIgnoreCase(folder))

                // Filter by unread only flag
                .filter(e -> unreadOnly == null || (unreadOnly && e.isUnread()))

                // Limit results (default 50)
                .limit(limit != null ? limit : 50)

                .collect(Collectors.toList());
    }
}
