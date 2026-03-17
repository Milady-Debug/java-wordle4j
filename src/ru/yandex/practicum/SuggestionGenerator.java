package ru.yandex.practicum;
import java.io.PrintWriter;
import java.util.*;
public class SuggestionGenerator {
    private final WordleDictionary fullDictionary;
    private final PrintWriter logger;
    private List<String> possibleWords; // Текущие подходящие слова
    private final Set<String> usedSuggestions;
    private final Set<Character> mustHaveLetters = new HashSet<>();
    private final Set<Character> notHaveLetters = new HashSet<>();

    private final Map<Integer, Set<Character>> misplacedLetters = new HashMap<>();

    private final Map<Integer, Character> exactLetters = new HashMap<>();

    public SuggestionGenerator(WordleDictionary dictionary, PrintWriter logger) {
        this.fullDictionary = dictionary;
        this.logger = logger;
        this.possibleWords = new ArrayList<>(dictionary.getWordList()); // Начинаем со всего словаря
        this.usedSuggestions = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            misplacedLetters.put(i, new HashSet<>());
        }
    }

    private boolean containsAllMustHaveLetters(String word) {
        for (char c : mustHaveLetters) {
            if (word.indexOf(c) == -1) return false;
        }
        return true;
    }

    private boolean containsNoNotHaveLetters(String word) {
        for (char c : notHaveLetters) {
            if (word.indexOf(c) != -1) return false;
        }
        return true;
    }

    private boolean matchesExactPositions(String word) {
        for (int position : exactLetters.keySet()) {
            if (word.charAt(position) != exactLetters.get(position)) {
                return false;
            }
        }
        return true;
    }

    private boolean avoidsMisplacedPositions(String word) {
        for (int i = 0; i < 5; i++) {
            char letterInWord = word.charAt(i);
            if (misplacedLetters.get(i).contains(letterInWord)) {
                return false;
            }
        }
        return true;
    }

    private void filterPossibleWords() {
        List<String> allWords = fullDictionary.getWordList();
        possibleWords = new ArrayList<>();

        for (String word : allWords) {
            // Проверяем по очереди, с самой быстрой проверки
            if (!matchesExactPositions(word)) continue;
            if (!containsAllMustHaveLetters(word)) continue;
            if (!avoidsMisplacedPositions(word)) continue;
            if (!containsNoNotHaveLetters(word)) continue;

            // Если все проверки пройдены
            possibleWords.add(word);
        }

        logger.println("После фильтрации осталось возможных слов: " + possibleWords.size());
    }

    public void updateWithNewGuess(String guess, String hint) {
        possibleWords = null;

        for (int i = 0; i < 5; i++) {
            char letter = guess.charAt(i);
            char hintChar = hint.charAt(i);

            if (hintChar == '+') {
                exactLetters.put(i, letter);
                mustHaveLetters.add(letter);
                // Если буква точно на месте, она не может быть в списке misplaced для этой позиции
                misplacedLetters.get(i).remove(letter);
            } else if (hintChar == '^') {
                mustHaveLetters.add(letter);
                // Этой буквы тут быть не должно
                misplacedLetters.get(i).add(letter);
            } else if (hintChar == '-') {
                // Проверка: если буква уже есть в mustHave, то игнорируем (может быть дубль)
                // Иначе - ее вообще быть не должно.
                if (!mustHaveLetters.contains(letter)) {
                    notHaveLetters.add(letter);
                }
            }
        }
        logger.println("Фильтры обновлены. Должны быть: " + mustHaveLetters + ". Не должно быть: " + notHaveLetters);
    }

    public String getSuggestion() {
        if (possibleWords == null) {
            filterPossibleWords();
        }

        if (possibleWords.isEmpty()) {
            logger.println("Внимание! Нет слов, соответствующих фильтрам. Возвращаю случайное слово.");
            return fullDictionary.getRandomWord();
        }

        List<String> freshSuggestions = new ArrayList<>();
        for (String word : possibleWords) {
            if (!usedSuggestions.contains(word)) {
                freshSuggestions.add(word);
            }
        }

        String suggestion;
        Random random = new Random();

        if (freshSuggestions.isEmpty()) {
            usedSuggestions.clear();
            suggestion = possibleWords.get(random.nextInt(possibleWords.size()));
        } else {
            suggestion = freshSuggestions.get(random.nextInt(freshSuggestions.size()));
        }

        usedSuggestions.add(suggestion);
        logger.println("Подсказка: " + suggestion);
        return suggestion;
    }




}
