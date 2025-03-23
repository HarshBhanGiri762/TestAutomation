package com.selenium.assignment.Pages;

import com.selenium.assignment.Models.Article;
//import com.selenium.assignment.utils.FileUtils;
import com.selenium.assignment.utils.FileUtils1;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

public class ElPaisPage {
    private WebDriver driver;
    private static final String RAPID_API_KEY = "7a57c73375msh39b7e2e70efc2b6p1c6e5djsn287588520789";  // Translate API key 1
    //private static final String RAPID_API_KEY = "b957a10142mshff8897d0e3ecef6p1f2b38jsne7a045718917";  // Translate API key 2
    private static final String RAPID_API_HOST = "rapid-translate-multi-traduction.p.rapidapi.com";

    public ElPaisPage(WebDriver driver){
        this.driver = driver;
    }
    // Locators
    private By htmlLang = By.tagName("html");
    private By spanishKeywords = By.xpath("//*[contains(text(), 'Noticias') or contains(text(), 'Deportes') or contains(text(), 'Opinión') or contains(text(), 'Economía')]");

    // Method to check the lang attribute
    public boolean isSpanishLanguage() {
        String lang = driver.findElement(htmlLang).getAttribute("lang");
        System.out.println("Page language: " + lang);
        return lang != null && (lang.equalsIgnoreCase("es") || lang.equalsIgnoreCase("es-ES"));
    }

    // Method to check if Spanish keywords are displayed
    public boolean isSpanishTextDisplayed() {
        return !driver.findElements(spanishKeywords).isEmpty();
    }

    public void acceptCookies(){
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // Wait until the element is visible and clickable
            WebElement acceptButton = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='didomi-notice-agree-button']")));
            acceptButton.click();
            System.out.println("Cookies accepted.");
        } catch (NoSuchElementException e) {
            System.out.println("No cookie popup found.");
        }
    }

    public void navigateToOpinionSection(){
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='sm _df']/.//a[@href='https://elpais.com/opinion/']"))).click();
        System.out.println("Navigating to the Opinion section...");
    }

    public List<String> getArticlesFromOpinionSection(int noOfArticles){
        // Wait for articles to load
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        List<WebElement> articles = driver.findElements(By.xpath("//article"));
        List<Article> articleList = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        int count = 0;
        for (WebElement article : articles) {
            if (count >= 5) break;
            try {
                String title = article.findElement(By.xpath(".//h2")).getText();
                titles.add(title);
                String content = article.findElement(By.xpath(".//p")).getText();
                String spanishContent = "SPANISH: " + content;
                String imagePath = "No Image";
                try {
                    WebElement img = article.findElement(By.tagName("img"));
                    String imageUrl = img.getAttribute("src");
                    // Save image to local machine
                    imagePath = "images/article_" + (count + 1) + ".png";
                    FileUtils1.downloadImage(imageUrl, imagePath);
                } catch (Exception e) {
                    System.out.println("No image found for article " + (count + 1));
                }
                // Create and add article to the list
                articleList.add(new Article(title, spanishContent, imagePath));
                count++;
            } catch (Exception e) {
                System.out.println("Error scraping article: " + e.getMessage());
            }
        }
        // Print the articles
        for (Article article : articleList) {
            System.out.println(article);
        }
        return titles;
    }

    //Translate and Print Titles
    public List<String> translateAndPrintTitles(List<String> titles) {
        System.out.println("\nTranslated Article Titles:");
        List<String> translatedTitles = new ArrayList<>();

        for (String title : titles) {
            String translatedTitle = translateTextWithAsyncClient(title, "es-ES", "en");
            System.out.println("Spanish: " + title);
            System.out.println("English: " + translatedTitle + "\n");
            translatedTitles.add(translatedTitle);
        }
        // Return the list of translated titles
        return translatedTitles;
    }

    public String translateTextWithAsyncClient(String text, String sourceLang, String targetLang) {
        try (AsyncHttpClient client = Dsl.asyncHttpClient()) {

            //Proper JSON Payload
            JSONObject requestBody = new JSONObject();
            requestBody.put("from", sourceLang);
            requestBody.put("to", targetLang);
            requestBody.put("q", text);

            //Send API Request
            ListenableFuture<Response> future = client.prepare("POST",
                            "https://rapid-translate-multi-traduction.p.rapidapi.com/t")
                    .setHeader("x-rapidapi-key", RAPID_API_KEY)
                    .setHeader("x-rapidapi-host", RAPID_API_HOST)
                    .setHeader("Content-Type", "application/json")
                    .setBody(requestBody.toString())
                    .execute();

            Response response = future.get();

            //Print Raw Response for Debugging
            String responseBody = response.getResponseBody();
            //System.out.println("Raw Response: " + responseBody);

            if (response.getStatusCode() != 200) {
                System.out.println("Translation failed: " + response.getStatusCode() + " - " + responseBody);
                return text;  // Return original text if translation fails
            }

            //Handle JSON Array Response
            if (responseBody.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(responseBody);
                if (jsonArray.length() > 0) {
                    return jsonArray.getString(0);  // Return the first translation
                }
            } else if (responseBody.startsWith("{")) {
                JSONObject jsonResponse = new JSONObject(responseBody);
                return jsonResponse.getJSONArray("translated_text").getString(0);
            }
            // Fallback in case of unexpected format
            return text;

        } catch (Exception e) {
            System.out.println("Translation failed: " + e.getMessage());
            return text;  // Return original text if translation fails
        }
    }

    //Method to analyze repeated words
    public void analyzeRepeatedWords(List<String> translatedTitles) {
        System.out.println("\nAnalyzing Repeated Words:");

        // Map to count occurrences of each word
        Map<String, Integer> wordCount = new HashMap<>();
        //Split each title into words and count occurrences
        for (String title : translatedTitles) {
            String[] words = title.toLowerCase().split("\\W+");  // Split by non-word characters
            for (String word : words) {
                if (!word.isEmpty()) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }
        //Print repeated words with their occurrences
        boolean found = false;
        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 2) {  // Words repeated more than twice
                found = true;
                System.out.println(entry.getKey() + " -> " + entry.getValue());
            }
        }
        if (!found) {
            System.out.println("No words repeated more than twice.");
        }
    }

}

