package ru.yandex.practicum;

public class LetterChecker {
    public static String analyzeGuess(String guess, String answer) {
        if (guess.length() != 5) {
            throw new IllegalArgumentException("Слова должны быть 5-буквенными");
        }
        char[] result = new char[5];
        boolean[] answerMatched = new boolean[5];
        boolean[] guessMatched = new boolean[5];

        // 1-й проход: ищем точные совпадения (+)
        for (int i = 0; i < 5; i++) {
            if (guess.charAt(i) == answer.charAt(i)) {
                result[i] = '+';
                answerMatched[i] = true;
                guessMatched[i] = true;
            }
        }

        // 2-й проход: ищем буквы в других местах (^) и отсутствующие
        for (int i = 0; i < 5; i++) {
            if (guessMatched[i]) {
                continue; // Уже обработали как '+'
            }

            char guessChar = guess.charAt(i);
            boolean found = false;

            // Ищем эту букву в секретном слове среди еще не сопоставленных букв
            for (int j = 0; j < 5; j++) {
                if (!answerMatched[j] && guessChar == answer.charAt(j)) {
                    found = true;
                    answerMatched[j] = true; // Помечаем, что эту букву ответа мы "использовали"
                    break;
                }
            }

            if (found) {
                result[i] = '^';
            } else {
                result[i] = '-';
            }
        }

        return new String(result);
    }
}
