package com.gabrielavara.choiceplayer.beatport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

abstract class BeatportParser<T extends BeatportSearchInput, U extends BeatportSearchOutput> {
    static final String BEATPORT_COM = "http://classic.beatport.com";
    static final String UTF_8 = "UTF-8";

    private Map<String, U> searchResults = new HashMap<>();

    private WebDriver driver;

    BeatportParser(WebDriver driver) {
        this.driver = driver;
    }

    U parse(T input) {
        String url = getUrl(input);
        if (searchResults.containsKey(url)) {
            return searchResults.get(url);
        }

        driver.get(url);
        U results = parseDocument(driver);
        searchResults.put(url, results);
        return results;
    }

    protected abstract String getUrl(T input);

    abstract U parseDocument(WebDriver driver);

    List<String> getTexts(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).distinct().filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    List<String> getHrefs(List<WebElement> elements) {
        return elements.stream().map(webElement -> webElement.getAttribute("href")).distinct().collect(Collectors.toList());
    }
}
