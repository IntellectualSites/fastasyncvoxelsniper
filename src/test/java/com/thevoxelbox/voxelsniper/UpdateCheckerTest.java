package com.thevoxelbox.voxelsniper;

import com.thevoxelbox.voxelsniper.update.UpdateCheckerParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateCheckerTest {

    @Test
    public void extractsLastName() {
        String response = "[{\"name\":\"FastAsyncVoxelSniper 3.3.0\"},{\"name\":\"FastAsyncVoxelSniper 3.4.1\"}]";
        String last = UpdateCheckerParser.extractLatestNameFromResponse(response);
        assertEquals("FastAsyncVoxelSniper 3.4.1", last);
    }

    @Test
    public void returnsNullWhenNoName() {
        String response = "{\"id\":1}";
        String last = UpdateCheckerParser.extractLatestNameFromResponse(response);
        assertNull(last);
    }

    @Test
    public void updateCheckFromResponseParsesVersion() {
        String response = "[{\"name\":\"FastAsyncVoxelSniper 3.4.1\"}]";
        double result = UpdateCheckerParser.versionFromResponse(33.0, response);
        assertEquals(34.1, result, 0.0001);
        // parser doesn't set plugin static title; ensure parser parsed the name correctly
        assertEquals("FastAsyncVoxelSniper 3.4.1", UpdateCheckerParser.extractLatestNameFromResponse(response));
    }

    @Test
    public void updateCheckFromResponseReturnsCurrentWhenNoName() {
        String response = "[]";
        double result = UpdateCheckerParser.versionFromResponse(33.0, response);
        assertEquals(33.0, result, 0.0001);
    }
}
