package loadTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SearchModuleTest {
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        // Set up the WebDriver (Chrome in this case)
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.n11.com/");
    }

    @Test
    public void testSearchFunctionality() {
        // Locate the search box and enter a search term
        WebElement searchBox = driver.findElement(By.id("searchData"));
        searchBox.sendKeys("laptop");

        // Locate and click the search button
        WebElement searchButton = driver.findElement(By.className("searchBtn"));
        searchButton.click();

        // Wait for the results to load and verify the results page
        WebElement resultsHeader = driver.findElement(By.className("resultText"));
        Assert.assertTrue(resultsHeader.getText().contains("laptop"), "Search results do not contain the expected term.");
    }

    @AfterMethod
    public void tearDown() {
        // Close the browser
        if (driver != null) {
            driver.quit();
        }
    }
}