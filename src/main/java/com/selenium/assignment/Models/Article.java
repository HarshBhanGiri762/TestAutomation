package com.selenium.assignment.Models;

public class Article {

    private String title;
    private String content;
    private String imagePath;

    // Constructor
    public Article(String title, String content, String imagePath) {
        this.title = title;
        this.content = content;
        this.imagePath = imagePath;
    }

    // Getters
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImagePath() {
        return imagePath;
    }

    @Override
    public String toString() {
        return "Title: " + title + "\nContent: " + content + "\nImage Path: " + imagePath + "\n------------------------";
    }
}

