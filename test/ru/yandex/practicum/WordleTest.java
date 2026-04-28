package ru.yandex.practicum;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleTest {
    @Test
    public void testAllCorrect() {
        assertEquals("+++++", LetterChecker.analyzeGuess("герой", "герой"));
    }

    @Test
    public void testWordleExample() {
        assertEquals("+^-^-", LetterChecker.analyzeGuess("гонец", "герой"));
    }

    @Test
    public void testDoubleLetters() {
        assertEquals("^+^+-", LetterChecker.analyzeGuess("лотос", "молот"));
    }
}
