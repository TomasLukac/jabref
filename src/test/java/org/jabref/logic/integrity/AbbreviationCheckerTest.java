package org.jabref.logic.integrity;

import java.util.Optional;

import org.jabref.logic.journals.Abbreviation;
import org.jabref.logic.journals.JournalAbbreviationRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbbreviationCheckerTest {

    private JournalAbbreviationRepository abbreviationRepository;
    private AbbreviationChecker checker;

    @BeforeEach
    void setUp() {
        abbreviationRepository = new JournalAbbreviationRepository(new Abbreviation("Test Journal", "T. J."));
        checker = new AbbreviationChecker(abbreviationRepository);
    }

    @Test
    void checkValueComplainsAboutAbbreviatedJournalName() {
        assertNotEquals(Optional.empty(), checker.checkValue("T. J."));
    }

    @Test
    void checkValueDoesNotComplainAboutJournalNameThatHasSameAbbreviation() {
        abbreviationRepository.addEntry(new Abbreviation("Journal", "Journal"));
        assertEquals(Optional.empty(), checker.checkValue("Journal"));
    }

    @Test
    void checkValue() {

        // mock
        JournalAbbreviationRepository journalAbbreviationRepository = mock(JournalAbbreviationRepository.class);
        AbbreviationChecker abbreviationChecker = new AbbreviationChecker(journalAbbreviationRepository);

        when(journalAbbreviationRepository.isAbbreviatedName("J C")).thenReturn(true);

        // test
        Optional<String> result = abbreviationChecker.checkValue("J C");

        assertEquals(result, Optional.of("abbreviation detected"));
    }
}
