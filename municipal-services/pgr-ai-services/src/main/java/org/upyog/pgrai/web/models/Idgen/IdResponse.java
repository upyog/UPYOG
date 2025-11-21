package org.upyog.pgrai.web.models.Idgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the response for a generated ID in the system.
 * This class encapsulates the details of the generated ID, including:
 * - The unique ID that was generated.
 *
 * This class is part of the ID generation module in the PGR system.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdResponse {

    private String id;

}
