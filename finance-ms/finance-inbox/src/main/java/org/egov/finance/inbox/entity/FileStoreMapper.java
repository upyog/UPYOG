
package org.egov.finance.inbox.entity;

import static org.egov.finance.inbox.entity.FileStoreMapper.SEQ_FILESTOREMAPPER;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;

@Table(name = "eg_filestoremap")
@Entity
@SequenceGenerator(name = SEQ_FILESTOREMAPPER, sequenceName = SEQ_FILESTOREMAPPER, allocationSize = 1)
public class FileStoreMapper implements Serializable {
    public static final String SEQ_FILESTOREMAPPER = "SEQ_EG_FILESTOREMAP";
    private static final long serialVersionUID = -2997164207274266823L;
    @Id
    @GeneratedValue(generator = SEQ_FILESTOREMAPPER, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotNull
    @Length(max = 36)
    @Column(length = 36, unique = true, nullable = false)
    private String fileStoreId;

    @NotNull
    @Length(max = 100)
    @SafeHtml
    private String fileName;

    @Length(max = 100)
    @SafeHtml
    private String contentType;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();

    protected FileStoreMapper() {
        // For JPA
    }

    public FileStoreMapper(String fileStoreId, String fileName) {
        this.fileStoreId = fileStoreId;
        this.fileName = fileName;
    }

    

    public String getFileStoreId() {
        return fileStoreId;
    }

    public void setFileStoreId(String fileStoreId) {
        this.fileStoreId = fileStoreId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof FileStoreMapper))
            return false;
        final FileStoreMapper that = (FileStoreMapper) other;
        return that.id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}