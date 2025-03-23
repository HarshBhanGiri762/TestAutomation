import com.selenium.assignment.Pages.ElPaisPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BrowserStackTest1 {
    private WebDriver driver;
    private ElPaisPage elPaisPage;

    public static String  USERNAMSE = "harshbhangiri_70JY1x";
    public static String  ACCESSKEY = "Np8vqK1BqxpFsYeu1vUJ";
    public static String URL = "https://"+USERNAMSE+":"+ACCESSKEY+"@hub.browserstack.com/wd/hub";
    int noOfArticles = 5;
//    @BeforeMethod
//    public void setup() {
//        WebDriverManager.chromedriver().setup();
//        driver = new ChromeDriver();
//        driver.manage().window().maximize();
//        elPaisPage = new ElPaisPage(driver);
//        driver.get("https://elpais.com");
//    }
    @Test
    public void oprnSTD() throws MalformedURLException {
        DesiredCapabilities capability = new DesiredCapabilities();
        capability.setPlatform(Platform.MAC);
        capability.setBrowserName("firefox");
        capability.setVersion("57");

        URL browserStackURL = new URL(URL);
        WebDriver driver = new RemoteWebDriver(browserStackURL,capability);


        driver.get("https://elpais.com/");
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
//        String text = driver.getCurrentUrl();
//        String test1 = "https://elpais.com/";
//        Assert.assertEquals(text,test1,"same");
//        driver.quit();
        System.out.println("Navigating to El País homepage...");
        // Accept cookies
        elPaisPage.acceptCookies();

        elPaisPage.navigateToOpinionSection();

        // Scraping articles
        List<String> titles = elPaisPage.getArticlesFromOpinionSection(noOfArticles);

        // Translate and print titles
        List<String> translatedTitles = elPaisPage.translateAndPrintTitles(titles);
        // Analyze repeated words in titles
        elPaisPage.analyzeRepeatedWords(translatedTitles);
    }
    @AfterTest
    public void closeBrowser() {
        // Close the browser at the end of the test
        driver.quit();  // Assuming driver is your WebDriver instance
        System.out.println("Browser closed.");
    }
}
