package org.egov.web.notification.sms.models;


import org.hibernate.validator.constraints.SafeHtml;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Report {
    @SafeHtml
    private String jobno;

    @SafeHtml
    private int messagestatus;

    @SafeHtml
    private String DoneTime;

    @SafeHtml
    private String usernameHash;
}
