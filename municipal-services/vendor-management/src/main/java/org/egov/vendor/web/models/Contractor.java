
package org.egov.vendor.web.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "contractor")
public class Contractor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "code", nullable = false, length = 50)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "correspondence_address", length = 255)
    private String correspondenceAddress;

    @Column(name = "permanent_address", length = 255)
    private String permanentAddress;

    @Column(name = "contact_person", nullable = false, length = 100)
    private String contactPerson;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "mobile_number", nullable = false, length = 15)
    private String mobileNumber;

    @Column(name = "narration", length = 500)
    private String narration;

    @Column(name = "gst_tin_no", length = 15)
    private String gstTinNo;

    @Column(name = "gst_registered_state", length = 100)
    private String gstRegisteredState;

    @Column(name = "bank", length = 100)
    private String bank;

    @Column(name = "bank_account_number", length = 20)
    private String bankAccountNumber;

    @Column(name = "ifsc_code", length = 11)
    private String ifscCode;

    @Column(name = "type", nullable = false, length = 10)
    private String type; // "Firm" or "Individual"

    @Column(name = "registration_no", length = 50)
    private String registrationNo;

    @Column(name = "status", nullable = false, length = 10)
    private String status; // "Active" or "Inactive"

    @Column(name = "pan_no", length = 10)
    private String panNo;

    @Column(name = "epf_no", length = 20)
    private String epfNo;

    @Column(name = "esi_no", length = 20)
    private String esiNo;
}
