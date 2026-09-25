package org.egov.dx.service;

import org.springframework.stereotype.Service;

/**
 * Service class for generating DIGIPIN codes based on geographical coordinates (latitude and longitude).
 * DIGIPIN is a grid-based location encoding system.
 */
@Service
public class DiginpinService {

    /**
     * The 4x4 grid used to map coordinate subdivisions to specific characters.
     */
    private static final char[][] DIGIPIN_GRID = {
        {'F', 'C', '9', '8'},
        {'J', '3', '2', '7'},
        {'K', '4', '5', '6'},
        {'L', 'M', 'P', 'T'}
    };

    // Bounding box for the region supported by DIGIPIN
    private static final double MIN_LAT = 2.5;
    private static final double MAX_LAT = 38.5;
    private static final double MIN_LON = 63.5;
    private static final double MAX_LON = 99.5;

    /**
     * Generates a 10-level DIGIPIN code for the given latitude and longitude.
     * The generated PIN includes hyphens at specific levels (after 3rd and 6th characters) for readability.
     *
     * @param lat The latitude of the location.
     * @param lon The longitude of the location.
     * @return A string representing the DIGIPIN code.
     * @throws IllegalArgumentException if the provided coordinates fall outside the supported bounding box.
     */
    public String generate(double lat, double lon) {
        // Validate that coordinates fall within the supported bounding box
        if (lat < MIN_LAT || lat > MAX_LAT)
            throw new IllegalArgumentException("Latitude out of range: must be between " + MIN_LAT + " and " + MAX_LAT);
        if (lon < MIN_LON || lon > MAX_LON)
            throw new IllegalArgumentException("Longitude out of range: must be between " + MIN_LON + " and " + MAX_LON);

        // Initialize the current bounding box to the maximum supported bounds
        double minLat = MIN_LAT, maxLat = MAX_LAT;
        double minLon = MIN_LON, maxLon = MAX_LON;
        StringBuilder digipin = new StringBuilder();

        // Generate DIGIPIN up to 10 precision levels
        for (int level = 1; level <= 10; level++) {
            // Calculate the dimensions of each grid cell at the current level
            double latDiv = (maxLat - minLat) / 4;
            double lonDiv = (maxLon - minLon) / 4;

            // Determine the row and column in the 4x4 grid for the current coordinates
            int row = 3 - (int) Math.floor((lat - minLat) / latDiv);
            int col = (int) Math.floor((lon - minLon) / lonDiv);

            // Ensure indices stay within the 0-3 bounds to prevent ArrayIndexOutOfBoundsException
            row = Math.max(0, Math.min(row, 3));
            col = Math.max(0, Math.min(col, 3));

            // Append the character corresponding to the calculated grid cell
            digipin.append(DIGIPIN_GRID[row][col]);

            // Add formatting hyphens after the 3rd and 6th characters
            if (level == 3 || level == 6)
                digipin.append('-');

            // Refine the bounding box to the selected grid cell for the next level
            maxLat = minLat + latDiv * (4 - row);
            minLat = minLat + latDiv * (3 - row);
            minLon = minLon + lonDiv * col;
            maxLon = minLon + lonDiv;
        }

        return digipin.toString();
    }
}
