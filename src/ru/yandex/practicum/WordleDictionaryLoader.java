package ru.yandex.practicum;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/*
этот класс содержит в себе всю рутину по работе с файлами словарей и с кодировками
    ему нужны методы по загрузке списка слов из файла по имени файла
    на выходе должен быть класс WordleDictionary
 */
public class WordleDictionaryLoader {
    private final PrintWriter logger;

    // Получаем логгер через конструктор
    public WordleDictionaryLoader(PrintWriter logger) {
        this.logger = logger;
    }

    public WordleDictionary loadDictionery(String filePath) throws WordNotFoundException {
        List<String> rawWords = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))){
            while(br.ready()){
                String line = br.readLine();
                if(!line.isBlank()){
                    String readyLine = line.trim().toLowerCase().replace("ё","е");
                    rawWords.add(readyLine);
                }
            }

        } catch (FileNotFoundException e) {
            logger.println("Критическая ошибка: Файл словаря не найден!");
            throw new WordNotFoundException("Файл словаря не найден: " + filePath);
        } catch (IOException e) {
            logger.println("Критическая ошибка: Ошибка ввода-вывода при чтении словаря.");
            throw new WordNotFoundException("Ошибка чтения файла словаря: " + e.getMessage());
        }
        try {
            return new WordleDictionary(rawWords, logger);
        } catch (IllegalStateException e) {
            logger.println("Ошибка создания словаря: " + e.getMessage());
            throw new WordNotFoundException("Словарь не содержит подходящих слов для игры");
        }

    }



}
