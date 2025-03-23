package com.selenium.assignment;

import com.selenium.assignment.Pages.ElPaisPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BrowserStackTest{
    private WebDriver driver;
    private ElPaisPage elPaisPage;

    public static String  USERNAMSE = "harshbhangiri_70JY1x";
    public static String  ACCESSKEY = "Np8vqK1BqxpFsYeu1vUJ";
    public static String URL = "https://"+USERNAMSE+":"+ACCESSKEY+"@hub.browserstack.com/wd/hub";
    int noOfArticles = 5;

    @DataProvider(name = "browserProvider", parallel = true)
    public Object[][] browserProvider() {
        return new Object[][]{
                {"Chrome", "latest", "Windows", "desktop"},
                {"Firefox", "latest", "Windows", "desktop"},
                {"Safari", "latest", "macOS", "desktop"},
                {"Chrome", "122", "Android", "mobile"},
                {"Safari", "latest", "iOS", "mobile"}
        };
    }


    @Test(dataProvider = "browserProvider")
    public void oprnSTD(String browserName, String browserVersion, String platform, String deviceType) throws MalformedURLException {
        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setCapability("browserName", browserName);
        capability.setCapability("browserVersion", browserVersion);

        if (platform.equalsIgnoreCase("Windows")) {
            capability.setPlatform(Platform.WIN10);
        } else if (platform.equalsIgnoreCase("macOS")) {
            capability.setPlatform(Platform.MAC);
        } else if (platform.equalsIgnoreCase("Android")) {
            capability.setCapability("platformName", "Android");
            capability.setCapability("platformVersion", "11.0");
            capability.setCapability("deviceName", "Samsung Galaxy S20");
            capability.setCapability("stack:options",  new org.json.JSONObject().put("deviceName", "Samsung Galaxy S20").toString());
        } else if (platform.equalsIgnoreCase("iOS")) {
            capability.setCapability("platformName", "iOS");
            capability.setCapability("platformVersion", "14.0");
            capability.setCapability("deviceName", "iPhone 12");
            capability.setCapability("stack:options",  new org.json.JSONObject().put("deviceName", "iPhone 12").toString());
        }
        URL browserStackURL = new URL(URL);
        driver = new RemoteWebDriver(browserStackURL, capability); // initialize driver here

        driver.manage().window().maximize();
        if (!deviceType.equalsIgnoreCase("mobile")) {
            driver.manage().window().maximize();
        }

        driver.get("https://elpais.com/");
        elPaisPage = new ElPaisPage(driver);

        System.out.println("Running test on " + browserName + " " + browserVersion + " on " + platform + " (" + deviceType + ")");
        boolean isLangSpanish = elPaisPage.isSpanishLanguage();
        Assert.assertTrue(isLangSpanish, "Page language is NOT set to Spanish!");
        // Check if common Spanish keywords are present
        boolean isSpanishTextVisible = elPaisPage.isSpanishTextDisplayed();
        Assert.assertTrue(isSpanishTextVisible, "Spanish content not detected on the page!");
        System.out.println("Website is correctly displayed in Spanish.");
        // Accept cookies
        try {
            elPaisPage.acceptCookies();
        } catch (org.openqa.selenium.TimeoutException e) {
            System.err.println("Timeout while accepting cookies: " + e.getMessage());
            // Handle the timeout
        } catch (org.openqa.selenium.NoSuchElementException e) {
            System.err.println("Cookie element not found: " + e.getMessage());
        }
        elPaisPage.navigateToOpinionSection();

        // Scraping articles
        List<String> titles = elPaisPage.getArticlesFromOpinionSection(noOfArticles);

        // Translate and print titles
        List<String> translatedTitles = elPaisPage.translateAndPrintTitles(titles);
        // Analyze repeated words in titles
        elPaisPage.analyzeRepeatedWords(translatedTitles);
    }


    @AfterTest(alwaysRun = true)
    public void closeBrowser() {
        if (driver != null) {
            driver.quit();
            System.out.println("Browser closed.");
        }
    }
}