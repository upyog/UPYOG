package org.egov.edcr.entity.blackbox;

import org.egov.common.entity.edcr.Plan;
import org.kabeja.dxf.DXFDocument;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.File;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlanDetail extends Plan {

    private static final long serialVersionUID = 76L;

    /** Original DXF on disk (used by Aspose path); not serialized. */
    @JsonIgnore
    private transient File dxfSourceFile;

    @JsonIgnore
    private DXFDocument doc;

    public File getDxfSourceFile() {
        return dxfSourceFile;
    }
    public void setDxfSourceFile(File dxfSourceFile) {
        this.dxfSourceFile = dxfSourceFile;
    }

    public DXFDocument getDoc() {
        return doc;
    }

    public void setDoc(DXFDocument doc) {
        this.doc = doc;
    }

    @JsonIgnore
    public DXFDocument getDxfDocument() {
        return doc;
    }

}
