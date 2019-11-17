package org.jabref.model.entry;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.entry.field.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.*;

public class BibEntryTest {

    private BibEntry entry;

    @BeforeEach
    public void setUp() {
        entry = new BibEntry();
    }

    @AfterEach
    public void tearDown() {
        entry = null;
    }

    @Test
    public void getFieldIsCaseInsensitive() throws Exception {
        entry.setField(new UnknownField("TeSt"), "value");

        assertEquals(Optional.of("value"), entry.getField(new UnknownField("tEsT")));
    }

    @Test
    public void getFieldWorksWithBibFieldAsWell() throws Exception {
        entry.setField(StandardField.AUTHOR, "value");

        assertEquals(Optional.of("value"), entry.getField(new BibField(StandardField.AUTHOR, FieldPriority.IMPORTANT).getField()));
    }

    @Test
    public void setFieldWorksWithBibFieldAsWell() throws Exception {
        entry.setField(new BibField(StandardField.AUTHOR, FieldPriority.IMPORTANT).getField(), "value");

        assertEquals(Optional.of("value"), entry.getField(StandardField.AUTHOR));
    }

    @Test
    public void clonedBibentryHasUniqueID() throws Exception {
        BibEntry entry = new BibEntry();
        BibEntry entryClone = (BibEntry) entry.clone();

        assertNotEquals(entry.getId(), entryClone.getId());
    }

    @Test
    public void testGetAndAddToLinkedFileList() {
        List<LinkedFile> files = entry.getFiles();
        files.add(new LinkedFile("", "", ""));
        entry.setFiles(files);
        assertEquals(Arrays.asList(new LinkedFile("", "", "")), entry.getFiles());
    }

    @Test
    public void testGetEmptyKeywords() {
        KeywordList actual = entry.getKeywords(',');

        assertEquals(new KeywordList(), actual);
    }

    @Test
    public void testGetSingleKeywords() {
        entry.addKeyword("kw", ',');
        KeywordList actual = entry.getKeywords(',');

        assertEquals(new KeywordList(new Keyword("kw")), actual);
    }

    @Test
    public void testGetKeywords() {
        entry.addKeyword("kw", ',');
        entry.addKeyword("kw2", ',');
        entry.addKeyword("kw3", ',');
        KeywordList actual = entry.getKeywords(',');

        assertEquals(new KeywordList(new Keyword("kw"), new Keyword("kw2"), new Keyword("kw3")), actual);
    }

    @Test
    public void testGetEmptyResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(StandardField.CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        database.insertEntry(entry2);
        database.insertEntry(entry);

        KeywordList actual = entry.getResolvedKeywords(',', database);

        assertEquals(new KeywordList(), actual);
    }

    @Test
    public void testGetSingleResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(StandardField.CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        entry2.addKeyword("kw", ',');
        database.insertEntry(entry2);
        database.insertEntry(entry);

        KeywordList actual = entry.getResolvedKeywords(',', database);

        assertEquals(new KeywordList(new Keyword("kw")), actual);
    }

    @Test
    public void testGetResolvedKeywords() {
        BibDatabase database = new BibDatabase();
        BibEntry entry2 = new BibEntry();
        entry.setField(StandardField.CROSSREF, "entry2");
        entry2.setCiteKey("entry2");
        entry2.addKeyword("kw", ',');
        entry2.addKeyword("kw2", ',');
        entry2.addKeyword("kw3", ',');
        database.insertEntry(entry2);
        database.insertEntry(entry);

        KeywordList actual = entry.getResolvedKeywords(',', database);

        assertEquals(new KeywordList(new Keyword("kw"), new Keyword("kw2"), new Keyword("kw3")), actual);
    }

    @Test
    public void getResolvedFieldOrAlias() {

        //mock
        BibDatabase bibDatabase = mock(BibDatabase.class);

        Field field1 = SpecialField.PRINTED;
        Field field2 = SpecialField.QUALITY;
        Field field3 = SpecialField.RANKING;

        OrFields orFields = new OrFields(Arrays.asList(field1, field2, field3));

        ArgumentCaptor<Field> captor = ArgumentCaptor.forClass(Field.class);

        BibEntry bibEntry = spy(BibEntry.class);

        // test
        Optional<String> resolvedFieldOrAlias = bibEntry.getResolvedFieldOrAlias(orFields, bibDatabase);

        verify(bibEntry, times(3)).getResolvedFieldOrAlias(captor.capture(), any(BibDatabase.class));
        List<Field> allValues = captor.getAllValues();
        assertTrue(allValues.contains(field1));
        assertTrue(allValues.contains(field2));
        assertTrue(allValues.contains(field2));
        assertTrue(resolvedFieldOrAlias.isEmpty());
    }
}
