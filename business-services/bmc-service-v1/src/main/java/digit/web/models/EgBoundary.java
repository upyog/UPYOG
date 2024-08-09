package digit.web.models;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "eg_boundary")
public class EgBoundary {
    @Id
    @GeneratedValue(strategy =GenerationType.IDENTITY)
     private Long id;
     @Column(name = "boundarynum")
     private Long boundarynum;
     @Column(name = "parent")
     private Long parent;
     @Column(name = "name")
     private String name;
     @Column(name = "boundarytype")
     private Long boundarytype;
     @Column(name = "localname")
     private String localname;
     @Column(name = "bndry_name_old")
     private String bndry_name_old;
     @Column(name = "bndry_name_old_local")
     private String bndry_name_old_local;
     @Column(name = "fromdate")
     private Date fromdate;
     @Column(name = "todate")
     private Date todate;
     @Column(name = "bndryid")
     private Long bndryid;
     @Column(name = "longitude")
     private Double longitude;
     @Column(name = "latitude")
     private Double latitude;
     @Column(name = "materializedpath")
     private String materializedpath;
     @Column(name = "ishistory")
     private Boolean ishistory;
     @Column(name = "createddate")
     private Date createddate;
     @Column(name = "lastmodifieddate")
     private Date lastmodifieddate;
     @Column(name = "createdby")
     private Long createdby;
     @Column(name = "lastmodifiedby")
     private Long lastmodifiedby;
     @Column(name = "version")
     private Long version;
     @Column(name = "tenantid")
     private String tenantid;
     @Column(name = "code")
     private String code;

}
