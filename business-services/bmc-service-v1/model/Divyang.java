package digit.bmc.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@Table(name = "eg_bmc_divyang")
@NoArgsConstructor
@AllArgsConstructor
public class Divyang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "createdon")
    private Long createdOn;

    @Column(name = "modifiedon")
    private Long modifiedOn;

    @Column(name = "createdby")
    private Integer createdBy;

    @Column(name = "modifiedby")
    private Integer modifiedBy;

}
