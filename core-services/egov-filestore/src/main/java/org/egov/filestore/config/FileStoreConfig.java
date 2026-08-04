package org.egov.filestore.config;

import java.util.List;
import java.util.Map;
import java.util.Set;


import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;

@Configuration
@Getter
public class FileStoreConfig {

	/**
	 * Comma-separated list of allowed audio/video file extensions for media uploads.
	 * Example: mp4,mp3,wav,ogg,webm,avi,mov,aac,flac,mkv,m4a,m4v
	 */
	@Value("#{'${allowed.audio.video.formats}'.split(',')}")
	private List<String> allowedAudioVideoFormats;

	/**
	 * Maximum allowed size (bytes) for direct audio/video uploads. Defaults to 100 MB.
	 */
	@Value("${media.upload.max.size.bytes:104857600}")
	private Long mediaUploadMaxSizeBytes;

	@Value("${image.charset.type}")
	private String imageCharsetType;
	
	@Value("#{${allowed.formats.map}}")
	private Map<String,List<String>> allowedFormatsMap;
	
	private Set<String> allowedKeySet;
	
	@Value("${image.small}")
	private String _small;

	@Value("${image.medium}")
	private String _medium;

	@Value("${image.large}")
	private String _large;
	
	@Value("${image.small.width}")
	private Integer smallWidth;

	@Value("${image.medium.width}")
	private Integer mediumWidth;

	@Value("${image.large.width}")
	private Integer largeWidth;
	
	@Value("${presigned.url.expiry.time.in.secs}")
	private Integer preSignedUrlTimeOut;
	
	@Value("#{'${image.formats}'.split(',')}") 
	private List<String> imageFormats;
	
	@PostConstruct
	private void enrichKeysetForFormats() {
		allowedKeySet = allowedFormatsMap.keySet();
	}

	// -------------------------------------------------------------------------
	// Explicit getters for new fields – ensures these are accessible even when
	// Lombok's annotation processor fails on newer JDKs (Java 21/23 + Lombok <1.18.24)
	// -------------------------------------------------------------------------

	public List<String> getAllowedAudioVideoFormats() {
		return allowedAudioVideoFormats;
	}

	public void setAllowedAudioVideoFormats(List<String> allowedAudioVideoFormats) {
		this.allowedAudioVideoFormats = allowedAudioVideoFormats;
	}

	public Long getMediaUploadMaxSizeBytes() {
		return mediaUploadMaxSizeBytes;
	}

	public void setMediaUploadMaxSizeBytes(Long mediaUploadMaxSizeBytes) {
		this.mediaUploadMaxSizeBytes = mediaUploadMaxSizeBytes;
	}

	public void setAllowedFormatsMap(Map<String, List<String>> allowedFormatsMap) {
		this.allowedFormatsMap = allowedFormatsMap;
	}
}
