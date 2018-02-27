package com.gabrielavara.choiceplayer.beatport;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class WebDriverProvider {
    private WebDriverProvider() {
    }

    static WebDriver getWebDriver() {
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver-2.35.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");
        options.addArguments("window-size=1200x600");
        return new ChromeDriver(options);
    }
}
