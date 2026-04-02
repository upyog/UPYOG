package com.ticket.controller;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.ticket.configuration.FileStore;
import com.ticket.configuration.S3Bucket;
import com.ticket.daoImpl.CommentDaoImpl;
import com.ticket.model.Comment;

@Controller
public class CommentController {
	
	private static final String BUCKET_DIR = "https://pmidc-ticket-images.s3.ap-south-1.amazonaws.com/comment-images/";


	@Autowired
	CommentDaoImpl commentDao;
	
	@Autowired
	FileStore filestore;
	

	@ResponseBody
	@RequestMapping(value = { "/get-comments" }, method = RequestMethod.GET)
	public List<Comment> getComments(HttpServletRequest request, @RequestParam("tkt_id") int tktId) {
		// HttpSession session = request.getSession();
		List<Comment> list = new ArrayList<Comment>();
		try {

			list = commentDao.getCommentDetails(tktId);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@ResponseBody
	@RequestMapping(value = { "/add-comment" }, method = RequestMethod.POST)
	public int addComment(@ModelAttribute("comment") Comment comment) {
		String imgPath = null;
		String imageUrl = null;
		int result = 0;
		MultipartFile commentFile = null;
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyyMMddHHmmss");
	    Date now = new Date();
	    String strDate = sdfDate.format(now);
		if(comment.getAttachment().equalsIgnoreCase("no"))
		{
				result = commentDao.addComment(comment);
			 }
			 else
			 {
				 
					try {
						commentFile = 	comment.getCommentFile();
						byte[] bytes = commentFile.getBytes();
						imgPath = strDate+"_"+commentFile.getOriginalFilename();
						Path path = Paths.get(imgPath);
						Files.write(path, bytes);
						//new S3Bucket().uploadFileS3Bucket(imgPath,path.toString(),"comment");
						List<MultipartFile> fileList = Arrays.asList(commentFile);
						imageUrl = filestore.uploadToFileStore(fileList, "ticket","comment-images");
					} catch (IOException e) {
						e.printStackTrace();
					}
					comment.setAttachment(imgPath);
					comment.setImageUrl(imageUrl);
					result = commentDao.addCommentWithAttachment(comment);
					
			 }
		
		
		try {
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	@RequestMapping(value = { "/download-cmt/{attachment:.+}" }, method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadAllResource(HttpServletRequest request, @PathVariable String attachment,
			HttpServletResponse response) throws IOException {
		ByteArrayOutputStream downloadInputStream = new S3Bucket().downloadFile(attachment, "comment");
		  
	    return ResponseEntity.ok()
	          .contentType(contentType(attachment))
	          .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + attachment + "\"")
	          .body(downloadInputStream.toByteArray());  
	  }
	
	private MediaType contentType(String keyname) {
	    String[] arr = keyname.split("\\.");
	    String type = arr[arr.length-1];
	    switch(type) {
	      case "txt": return MediaType.TEXT_PLAIN;
	      case "png": return MediaType.IMAGE_PNG;
	      case "jpg": return MediaType.IMAGE_JPEG;
	      default: return MediaType.APPLICATION_OCTET_STREAM;
	    }
		
	}
	
	@RequestMapping(value = { "/comment-image-url/{imageName:.+}" }, method = RequestMethod.GET)
	@ResponseBody

	public HttpEntity<byte[]> getPhoto(@PathVariable String imageName, HttpServletRequest request ) throws IOException {
		final String STATIC_IMG_DIR = request.getSession().getServletContext().getRealPath("/static/images")+"/";
		
		String path = "";
		if(imageName==null || imageName.isEmpty() || imageName=="null" || imageName.contains("null"))
		{
			path = STATIC_IMG_DIR + "no-image.png";
		}
		else
		{
			String extension = imageName.substring(imageName.lastIndexOf(".") + 1);
		if (extension.equalsIgnoreCase("pdf")) {
			path = STATIC_IMG_DIR + "pdf.png";
		} else if (extension.equalsIgnoreCase("zip")) {
			path = STATIC_IMG_DIR + "zip.png";
		} else if (extension.equalsIgnoreCase("xlsx") || extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("csv")) {
			path = STATIC_IMG_DIR + "xls-icon.png";
		}else {
			path = BUCKET_DIR +imageName;

		}
		}
		

		byte[] image = org.apache.commons.io.FileUtils.readFileToByteArray(new File(path));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		headers.setContentLength(image.length);
		return new HttpEntity<byte[]>(image, headers);
	}
	
}
