package se233.chapter6_3.controller;

import org.apache.pdfbox.text.PDFTextStripper;
import se233.chapter6_3.model.FileFreq;
import se233.chapter6_3.model.PdfDocument;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class WordCountMapTask implements Callable<Map<String, FileFreq>> {
    private PdfDocument doc;
    private Map<String, FileFreq> wordCount;

    public WordCountMapTask(PdfDocument doc) throws IOException {
        this.doc = doc;
    }

    @Override
    public Map<String, FileFreq> call() throws Exception {
        PDFTextStripper reader = new PDFTextStripper();
        Pattern pattern = Pattern.compile(" ");
        String s = reader.getText(doc.getDocument());
        wordCount = pattern.splitAsStream(s)
                .map(word -> word.replaceAll("[^a-zA-Z]", "").toLowerCase().trim())
                .filter(word -> word.length() > 3)
                .map(word -> new AbstractMap.SimpleEntry<>(word, 1))
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue(), (v1, v2) -> v1 + v2))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() > 1)
                .collect(Collectors.toMap(e -> e.getKey(), e -> new FileFreq(doc.getName(), doc.getFilePath(), e.getValue())));
        return wordCount;
    }

    public Map<String, FileFreq> getWordCount() {
        return wordCount;
    }

    public void setWordCount(Map<String, FileFreq> wordCount) {
        this.wordCount = wordCount;
    }
}