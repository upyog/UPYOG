package digit.bmc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
@Data
@Entity
@Table(name = "eg_bmc_sectormaster")
public class Sector {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "sector")
    private String sector;

    @Column(name = "remark")
    private String remark;

    @Column(name = "createdon")
    private Long createdOn;

    @Column(name = "modifiedon")
    private Long modifiedOn;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifiedby")
    private String modifiedBy;




}
