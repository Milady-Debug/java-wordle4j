package ru.yandex.practicum;

import java.io.PrintWriter;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
/*
в главном классе нам нужно:
    создать лог-файл (он должен передаваться во все классы)
    создать загрузчик словарей WordleDictionaryLoader
    загрузить словарь WordleDictionary с помощью класса WordleDictionaryLoader
    затем создать игру WordleGame и передать ей словарь
    вызвать игровой метод в котором в цикле опрашивать пользователя и передавать информацию в игру
    вывести состояние игры и конечный результат
 */
public class Wordle {
    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "game.log";

    public static void main(String[] args) {
        try (PrintWriter logger = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(LOG_FILE), StandardCharsets.UTF_8), true)) {

            logger.println("Запуск Wordle");
            WordleGame game = initializeGame(logger);

            if (game == null) {
                System.out.println("Критическая ошибка инициализации. Игра не может быть запущена.");
                return;
            }

            playGame(game, logger);

        }catch (IOException e) {
            System.err.println("Не удалось создать лог-файл: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static WordleGame initializeGame(PrintWriter logger) {
        try {
            WordleDictionaryLoader loader = new WordleDictionaryLoader(logger);
            WordleDictionary dictionary = loader.loadDictionery(DICTIONARY_FILE);
            return new WordleGame(dictionary, logger);
        } catch (WordNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void playGame(WordleGame game, PrintWriter logger) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в Wordle! У вас " + game.getSteps() + " попыток.");
        System.out.println("Введите слово из 5 букв. Для подсказки нажмите Enter.");

        while (!game.isGameOver()) {
            System.out.print("> ");
            String input = scanner.nextLine();

            // Обработка пустого ввода (подсказка)
            if (input.trim().isEmpty()) {
                String suggestion = game.getSuggestion();
                if (suggestion != null) {
                    System.out.println("Подсказка: " + suggestion);
                } else {
                    System.out.println("Не могу найти подходящую подсказку.");
                }
                continue; // Не засчитываем как ход
            }

            try {
                String hint = game.processGuess(input);
                if (hint != null) {
                    System.out.println(hint);
                } else {
                    // Это может быть случай, когда игра уже окончена
                    if (game.isGameOver()) {
                        break;
                    }
                }
            } catch (WordNotFoundException e) {
                // Игровая ситуация: пользователь ввел неверное слово
                System.out.println("Ошибка: " + e.getMessage() + " Попробуйте снова.");
                logger.println("Пользовательский ввод отклонен: " + input + " - " + e.getMessage());
            } catch (Exception e) {
                logger.println("Необработанная ошибка в игровом цикле:");
                e.printStackTrace(logger);
                System.out.println("Произошла внутренняя ошибка. Попробуйте еще раз.");
            }
        }

        // Финал игры
        if (game.isWin()) {
            System.out.println("Поздравляем! Вы отгадали слово!");
        } else {
            System.out.println("К сожалению, вы проиграли. Загаданное слово: " + game.getAnswer());
        }
        logger.println("Игра завершена. Победитель: " + (game.isWin() ? "Игрок" : "Компьютер"));
    }

}
