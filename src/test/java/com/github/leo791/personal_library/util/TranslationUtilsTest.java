package com.github.leo791.personal_library.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslationUtilsTest {

    @Test
    void isTranslationRequired() {
        assertTrue(TranslationUtils.isTranslationRequired("es", "en"));
        assertFalse(TranslationUtils.isTranslationRequired("en", "en"));
        assertFalse(TranslationUtils.isTranslationRequired("unknown", "en"));
    }
}