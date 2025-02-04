package webAutomation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class InsiderCareerTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String browser;

    // Tarayıcıyı parametrik olarak değiştirmek için constructor
    public InsiderCareerTest(String browser) {
        this.browser = browser;
    }

    // Tarayıcı seçenekleri: Chrome ve Firefox
    @Parameterized.Parameters
    public static Collection<Object[]> browsers() {
        return Arrays.asList(new Object[][]{
                {"chrome"},
                {"firefox"}
        });
    }

    @Before
    public void setUp() {
        // WebDriverManager ile tarayıcıyı ayarla
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("w3c", true);
        if (browser.equals("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equals("firefox")) {
            driver = new FirefoxDriver();
        }

        driver.manage().window().maximize(); // Tarayıcı penceresini tam ekran yapar
        wait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Bekleme süresi ayarla
    }


    @Test
    public void testInsiderCareerProcess() throws InterruptedException {
        try {
            // 1. Visit https://useinsider.com/ and check Insider home page is opened or not
            driver.get("https://useinsider.com/");
            WebElement exploreInsiderText = driver.findElement(By.xpath("/html/body/nav/div[2]/div/ul[1]/li[7]/a"));
            assertTrue("'Explore Insider' | 'Platform Tour'", exploreInsiderText.isDisplayed());

            // 2. Select the “Company” menu in the navigation bar, select “Careers” and check Career page
            WebElement companyMenu = driver.findElement(By.xpath("//a[contains(text(), 'Company')]"));
            companyMenu.click();

            WebElement careersLink = driver.findElement(By.xpath("//a[contains(text(), 'Careers')]"));
            careersLink.click();


            WebElement teams = driver.findElement(By.xpath("//a[@href='javascript:void(0)']"));
            scrollToElement(teams);
            Thread.sleep(3000);
            assertEquals("See all teams", teams.getText());

            WebElement locations = driver.findElement(By.xpath("//*[@class='category-title-media ml-0']"));
            scrollToElement(locations);
            Thread.sleep(3000);
            assertEquals("Our Locations", locations.getText());

            WebElement life = driver.findElement(By.xpath("(//h2[@class='elementor-heading-title elementor-size-default'])[2]"));
            scrollToElement(life);
            Thread.sleep(3000);
            assertEquals("Life at Insider", life.getText());

            // 3. Go to https://useinsider.com/careers/quality-assurance/, click “See all QA jobs”, filter jobs
            driver.get("https://useinsider.com/careers/quality-assurance/");
            WebElement seeAllQAJobsButton = driver.findElement(By.xpath("//*[@class='btn btn-outline-secondary rounded text-medium mt-2 py-3 px-lg-5 w-100 w-md-50']"));
            seeAllQAJobsButton.click();

            // Filter jobs by Location: “Istanbul, Turkey”, and Department: “Quality Assurance”
//            WebElement locationFilter = driver.findElement(By.xpath("(//label[@class='text-small font-weight-bold text-gray-200'])[1]]"));
//            scrollToElement(locationFilter);
//            locationFilter.click();

            WebElement locationHiddenFilter = driver.findElement(By.xpath("((//span[@class='select2-selection__clear'])[1]"));
            scrollToElement(locationHiddenFilter);
            locationHiddenFilter.click();

            WebElement city = driver.findElement(By.xpath("/li[@id='select2-filter-by-location-result-6iqb-Istanbul, Turkey']"));
            scrollToElement(city);
            city.click();

            WebElement departmentFilter = driver.findElement(By.xpath("//*[@id='select2-filter-by-department-container']"));
            scrollToElement(departmentFilter);
            departmentFilter.click();

            WebElement role = driver.findElement(By.xpath("//*[@id='select2-filter-by-department-result-s9pf-Quality Assurance']"));
            scrollToElement(role);
            role.click();


            // Check the presence of the job list

            WebElement jobs = driver.findElement(By.xpath("/html/body/section[3]/div/div/div[2]"));
            scrollToElement(jobs);
            assertFalse(jobs.getText().isEmpty());



            // 5. Click the “View Role” button and check that this action redirects us to the Lever Application form page
            WebElement firstJobViewRoleButton = driver.findElement(By.xpath("(//div[@id='jobs-list']//a[contains(text(), 'View Role')])[1]"));
            String firstJobUrl = firstJobViewRoleButton.getAttribute("href");
            firstJobViewRoleButton.click();

            // Yeni sekme veya pencereye geçiş yap
            String originalWindow = driver.getWindowHandle();
            for (String windowHandle : driver.getWindowHandles()) {
                if (!windowHandle.equals(originalWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }

            // Lever Application form sayfasında olduğunu kontrol et
            wait.until(ExpectedConditions.urlContains("jobs.lever.co"));
            assertTrue("Not redirected to Lever Application form page!", driver.getCurrentUrl().contains("jobs.lever.co"));
        } catch (AssertionError | Exception e) {
            // Hata durumunda ekran görüntüsü al
            takeScreenshot("test_failure_" + browser + "_" + System.currentTimeMillis() + ".png");
            throw e; // Hatayı yeniden fırlat
        }
    }

    public void scrollToElement(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }


    @After
    public void tearDown() {
        // Tarayıcıyı kapat
        if (driver != null) {
            driver.quit();
        }
    }

    // Ekran görüntüsü alma metodu
    private void takeScreenshot(String fileName) {
        try {
            File screenshotFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Path destinationPath = Paths.get("screenshots", fileName);
            Files.createDirectories(destinationPath.getParent()); // Klasör yoksa oluştur
            Files.copy(screenshotFile.toPath(), destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Belirli bir elemente kadar sayfayı kaydırma metodu
    private JavascriptExecutor scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
        wait.until(ExpectedConditions.visibilityOf(element));
        return null;
    }
}