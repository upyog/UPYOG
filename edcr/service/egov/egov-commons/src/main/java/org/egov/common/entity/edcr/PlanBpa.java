package org.egov.common.entity.edcr;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import org.egov.common.entity.bpa.SubOccupancy;
import org.egov.common.entity.bpa.Usage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the complete building plan data used during EDCR scrutiny
 * and BPA processing.
 *
 * <p>This is the root domain object that aggregates all information
 * required for plan validation, scrutiny, and report generation,
 * including plot details, plan information, building blocks,
 * occupancies, scrutiny status, and tenant-specific data.</p>
 *
 * <p>The object is commonly used for exchanging plan details through
 * EDCR APIs, including the {@code /edcrdetails} endpoint, and serves
 * as the central model passed through the scrutiny workflow.</p>
 *
 * <p>It contains both user-provided plan information and data generated
 * during scrutiny, such as rule validation results, occupancy mappings,
 * and plan status.</p>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanBpa implements Serializable {

    private static final long serialVersionUID = 7276648029097296311L;

    /**
     * Plan scrutiny report status. Values true mean "Accepted" and False mean "Not Accepted". Default value false. On plan
     * scrutiny, if all the rules are success then value is true.
     */
    Map<String, String> planInfoProperties = new HashMap<>();

    private Boolean edcrPassed = false;
    // Submission date of plan scrutiny.
    private Date applicationDate;
    /**
     * decides on what date scrutiny should be done
     */
    private Date asOnDate;
    private Plot plot;

    /**
     * Planinformation captures the declarations of the plan.Plan information captures the boundary, building location
     * details,surrounding building NOC's etc. User will assert the details about the plot. The same will be used to print in plan
     * report.
     */
    private PlanInformation planInformation;


    // Single plan contain multiple block/building information. Records Existing and proposed block information.
    private List<Block> blocks = new ArrayList<>();

    private String tenantId;

    // List of occupancies present in the plot including all the blocks.
    private List<Occupancy> occupancies = new ArrayList<>();
    @JsonIgnore
    private transient Map<Integer, org.egov.common.entity.bpa.Occupancy> occupanciesMaster = new HashMap<>();
    @JsonIgnore
    private transient Map<Integer, SubOccupancy> subOccupanciesMaster = new HashMap<>();

    // coverage Overall Coverage of all the block. Total area of all the floor/plot area.



    public List<Occupancy> getOccupancies() {
        return occupancies;
    }

    public void setOccupancies(List<Occupancy> occupancies) {
        this.occupancies = occupancies;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public Block getBlockByName(String blockName) {
        for (Block block : getBlocks()) {
            if (block.getName().equalsIgnoreCase(blockName))
                return block;
        }
        return null;
    }


    public Boolean getEdcrPassed() {
        return edcrPassed;
    }

    public void setEdcrPassed(Boolean edcrPassed) {
        this.edcrPassed = edcrPassed;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }


    public PlanInformation getPlanInformation() {
        return planInformation;
    }

    public void setPlanInformation(PlanInformation planInformation) {
        this.planInformation = planInformation;
    }

    public Plot getPlot() {

        return plot;
    }

    public void setPlot(Plot plot) {
        this.plot = plot;
    }



    public void sortBlockByName() {
        if (!blocks.isEmpty())
            Collections.sort(blocks, Comparator.comparing(Block::getNumber));
    }

    public void sortSetBacksByLevel() {
        for (Block block : blocks)
            Collections.sort(block.getSetBacks(), Comparator.comparing(SetBack::getLevel));
    }


    public Map<Integer, org.egov.common.entity.bpa.Occupancy> getOccupanciesMaster() {
        return occupanciesMaster;
    }

    public void setOccupanciesMaster(Map<Integer, org.egov.common.entity.bpa.Occupancy> occupanciesMaster) {
        this.occupanciesMaster = occupanciesMaster;
    }

    public Map<Integer, SubOccupancy> getSubOccupanciesMaster() {
        return subOccupanciesMaster;
    }

    public void setSubOccupanciesMaster(Map<Integer, SubOccupancy> subOccupanciesMaster) {
        this.subOccupanciesMaster = subOccupanciesMaster;
    }



    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }



    public Map<String, String> getPlanInfoProperties() {
        return planInfoProperties;
    }

    public void setPlanInfoProperties(Map<String, String> planInfoProperties) {
        this.planInfoProperties = planInfoProperties;
    }



}
