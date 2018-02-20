package com.gabrielavara.choiceplayer.beatport;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

abstract class BeatportParser<T extends BeatportSearchInput, U extends BeatportSearchOutput> {
    static final String BEATPORT_COM = "http://classic.beatport.com";
    static final String UTF_8 = "UTF-8";
    private static Logger log = LoggerFactory.getLogger("com.gabrielavara.choiceplayer.beatport.BeatportParser");

    private Map<String, U> searchResults = new HashMap<>();
    private WebClient webClient;

    BeatportParser() {
        webClient = new WebClient();
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(false);
    }

    public U search(T input) {
        String url = getUrl(input);
        if (searchResults.containsKey(url)) {
            return searchResults.get(url);
        }

        U results = getResults(url);
        searchResults.put(url, results);
        return results;
    }

    private U getResults(String url) {
        try {
            HtmlPage page = webClient.getPage(url);
            return parseDocument(page);
        } catch (IOException e) {
            log.error("Could not parse website", e);
        }
        return null;
    }

    protected abstract String getUrl(T input);

    abstract U parseDocument(HtmlPage page);

    @SafeVarargs
    static boolean areDifferentInSize(List<HtmlElement>... elements) {
        for (int i = 0; i < elements.length; i++) {
            if (elements[i].size() != elements[i + 1 == elements.length ? 0 : i + 1].size()) {
                return true;
            }
        }
        return false;
    }

    String getText(HtmlElement element) {
        return sanitize(element.getTextContent());
    }

    List<String> getTexts(List<HtmlElement> elements) {
        return elements.stream().flatMap(this::getChildrenIfHas).map(this::sanitize).collect(Collectors.toList());
    }

    List<String> getHrefs(List<HtmlElement> elements) {
        return elements.stream().map(htmlElement -> htmlElement.getAttribute("href")).collect(Collectors.toList());
    }

    private Stream<? extends DomNode> getChildrenIfHas(HtmlElement element) {
        if (element.hasChildNodes()) {
            return StreamSupport.stream(element.getChildren().spliterator(), false);
        } else {
            return Stream.of(element);
        }
    }

    private String sanitize(DomNode domNode) {
        return sanitize(domNode.getTextContent());
    }

    String sanitize(String s) {
        while (s.contains("/n")) {
            s = s.replaceAll("/n", "");
        }
        while (s.contains("  ")) {
            s = s.replaceAll(" {2}", " ");
        }
        return s.trim();
    }

}
