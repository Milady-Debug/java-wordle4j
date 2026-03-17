package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*
в этом классе хранится словарь и состояние игры
    текущий шаг
    всё что пользователь вводил
    правильный ответ

в этом классе нужны методы, которые
    проанализируют совпадение слова с ответом
    предложат слово-подсказку с учётом всего, что вводил пользователь ранее

не забудьте про специальные типы исключений для игровых и неигровых ошибок
 */
public class WordleGame {
    private String answer;
    private int steps;
    private WordleDictionary dictionary;

    private final PrintWriter logger;
    private final SuggestionGenerator suggestionEngine;

    private boolean isGameOver;
    private boolean isWin;
    private final List<String> guessedWords;
    private final List<String> hintResults;

    public WordleGame(WordleDictionary dictionary, PrintWriter logger) {
        this.dictionary = dictionary;
        this.logger = logger;
        this.answer = dictionary.getRandomWord();
        this.steps = 6;
        this.isGameOver = false;
        this.isWin = false;
        this.guessedWords = new ArrayList<>();
        this.hintResults = new ArrayList<>();
        this.suggestionEngine = new SuggestionGenerator(dictionary, logger);

        logger.println("Новая игра начата. Загаданное слово (для лога): " + answer);
    }

    public String processGuess(String userInput) throws WordNotFoundException {
        if (isGameOver) {
            return null;
        }

        String normalizedGuess = normalizeWord(userInput);

        if (normalizedGuess.isEmpty()) {
            return null;
        }
        if (normalizedGuess.length() != 5) {
            throw new WordNotFoundException("Слово должно быть из 5 букв.");
        }
        if (!dictionary.contains(normalizedGuess)) {
            throw new WordNotFoundException("Слово \"" + normalizedGuess + "\" отсутствует в словаре.");
        }

        steps--;
        guessedWords.add(normalizedGuess);

        String hint = LetterChecker.analyzeGuess(normalizedGuess, answer);
        hintResults.add(hint);

        if (normalizedGuess.equals(answer)) {
            isWin = true;
            isGameOver = true;
        } else if (steps <= 0) {
            isGameOver = true;
        }

        suggestionEngine.updateWithNewGuess(normalizedGuess, hint);

        logger.println("Ход: " + normalizedGuess + " -> " + hint + " (осталось попыток: " + steps + ")");
        return hint;
    }

    public String getSuggestion() {
        return suggestionEngine.getSuggestion();
    }

    private String normalizeWord(String word) {
        if (word == null) return "";
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public boolean canMakeMove() {
        return !isGameOver && steps > 0;
    }

    // Геттеры (возвращаем списки напрямую)
    public boolean isGameOver() {
        return isGameOver;
    }

    public boolean isWin() {
        return isWin;
    }

    public int getSteps() {
        return steps;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getGuessedWords() {
        return guessedWords;  // Просто возвращаем список, без Collections
    }

    public List<String> getHintResults() {
        return hintResults;    // Просто возвращаем список, без Collections
    }

    public WordleDictionary getDictionary() {
        return dictionary;
    }

    public void resetGame() {
        this.answer = dictionary.getRandomWord();
        this.steps = 6;
        this.isGameOver = false;
        this.isWin = false;
        this.guessedWords.clear();
        this.hintResults.clear();
        logger.println("Новая игра начата. Загаданное слово (для лога): " + answer);
    }

    public String getGameStats() {
        return String.format("Осталось попыток: %d, Использовано слов: %d",
                steps, guessedWords.size());
    }

}
