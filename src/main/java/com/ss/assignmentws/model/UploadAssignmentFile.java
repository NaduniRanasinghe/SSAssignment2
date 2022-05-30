package com.ssd.assignmentws.model;

import org.springframework.web.multipart.MultipartFile;

public class UploadAssignmentFile {

	private MultipartFile multipartFile;

	public MultipartFile getMultipartFile() {
		return multipartFile;
	}

	public void setMultipartFile(MultipartFile multipartFile) {
		this.multipartFile = multipartFile;
	}
}
