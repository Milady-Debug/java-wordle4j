package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
этот класс содержит в себе список слов List<String>
    его методы похожи на методы списка, но учитывают особенности игры
    также этот класс может содержать рутинные функции по сравнению слов, букв и т.д.
 */
public class WordleDictionary {
    private final PrintWriter logger;
    private List<String> words;

    public WordleDictionary(List<String> rawWords, PrintWriter logger) {
        this.logger = logger;
        this.words = filterWords(rawWords, 5);

        if (words.isEmpty()) {
            throw new IllegalStateException("Словарь пуст. Нет подходящих слов для игры.");
        }
    }

    public List<String> filterWords(List<String> rawWords, int length) {
        List<String> filteredWords = new ArrayList<>();
        for (String rawWord : rawWords) {
            if (rawWord.length() == length) {
                filteredWords.add(rawWord);
            }
        }
        return filteredWords;
    }

    public List<String> getWordList() {
        // Возвращаем копию, чтобы внешние классы не могли испортить словарь
        return new ArrayList<>(words);
    }

    public boolean contains(String word) {
        return words.contains(word);
    }

    public String getRandomWord() {
        Random random = new Random();
        return words.get(random.nextInt(words.size()));
    }

}
