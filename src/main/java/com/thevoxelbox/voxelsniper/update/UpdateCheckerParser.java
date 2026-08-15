package com.thevoxelbox.voxelsniper.update;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight parser for the update checker response. Intentionally avoids a JSON dependency.
 */
public final class UpdateCheckerParser {

    private UpdateCheckerParser() {
    }

    public static String extractLatestNameFromResponse(String response) {
        if (response == null) {
            return null;
        }
        Pattern p = Pattern.compile("\"name\"\\s*:\\s*\"([^\"]+)\"");
        Matcher m = p.matcher(response);
        String lastName = null;
        while (m.find()) {
            lastName = m.group(1);
        }
        return lastName;
    }

    /**
     * Parse the numeric version from the API response. Returns currentVersion when no name was found,
     * or Double.NaN when the version string couldn't be parsed.
     */
    public static double versionFromResponse(double currentVersion, String response) {
        String lastName = extractLatestNameFromResponse(response);
        if (lastName == null) {
            return currentVersion;
        }
        String newVersionTitle = lastName.replace("FastAsyncVoxelSniper", "").trim();
        try {
            return Double.parseDouble(newVersionTitle.replaceFirst("\\.", "").trim());
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }
}
