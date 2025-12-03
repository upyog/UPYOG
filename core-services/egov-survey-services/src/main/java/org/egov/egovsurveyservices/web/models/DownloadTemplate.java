package org.egov.egovsurveyservices.web.models;

import lombok.*;
import org.springframework.http.HttpHeaders;

import java.io.ByteArrayOutputStream;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DownloadTemplate {
    private ByteArrayOutputStream byteArrayOutputStream;
    private HttpHeaders httpHeaders;
}
