package ch.heig.dai.lab.smtp;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for the Parser class.
 *
 * @author Aubry Mangold <aubry.mangold@heig-vd.ch>
 * @author Hugo Germano <hugo.germano@heig-vd.ch>
 * @see Parser
 */
public class ParserTest {
    /**
     * Test that the parser exits correctly when the input files does not exist.
     */
    @Test
    public void missingFileThrowsTest() {
        assertThrows(FileNotFoundException.class, () -> new Parser("empty.txt", "missing.txt", 1));
    }

    /**
     * Test that the parser exits correctly when the input files are empty.
     */
    @Test
    public void emptyFileThrowsTest() {
        assertThrows(Exception.class, () -> new Parser("empty.txt", "empty.txt", 1));
    }

    /**
     * Test that the parser correctly forms groups.
     */
    @Test
    public void groupFormationTest() throws FileNotFoundException {
        // Run a few iterations to make sure the groups are of correct length.
        for (int i = 0; i < 20; i++) {
            Parser parser = new Parser("victims.txt", "messages.txt", 1);
            Mail[] groups = parser.getGroups();
            assertEquals(1, groups.length);
            assertNotNull(groups[0].sender());
            assertTrue(groups[0].receivers().length >= 2 && groups[0].receivers().length <= 5);
            assertNotNull(groups[0].message());
        }
    }

    /**
     * Test that the shuffle works correctly
     */
    @Test
    public void shuffleListTest() throws FileNotFoundException {
        Parser parser1 = new Parser("victims.txt", "messages.txt", 5);
        Mail[] groups1 = parser1.getGroups();
        Parser parser2 = new Parser("victims.txt", "messages.txt", 5);
        Mail[] groups2 = parser2.getGroups();
        assertTrue(!Arrays.deepEquals(groups1, groups2));
    }
}