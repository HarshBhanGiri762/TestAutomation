import com.selenium.assignment.Pages.ElPaisPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;

public class SeleniumTest {
    //WebDriver driver;
    private WebDriver driver;
    private ElPaisPage elPaisPage;
    int noOfArticles = 5;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        elPaisPage = new ElPaisPage(driver);
        driver.get("https://elpais.com");
    }

    @Test
    public void verifyWebsiteIsInSpanish() {
        System.out.println("Verifying the website language...");
        // Check if the lang attribute is set to Spanish
        boolean isLangSpanish = elPaisPage.isSpanishLanguage();
        Assert.assertTrue(isLangSpanish, "Page language is NOT set to Spanish!");
        // Check if common Spanish keywords are present
        boolean isSpanishTextVisible = elPaisPage.isSpanishTextDisplayed();
        Assert.assertTrue(isSpanishTextVisible, "Spanish content not detected on the page!");
        System.out.println("Website is correctly displayed in Spanish.");
    }

    @Test
    public void scrapeArticles(){

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
