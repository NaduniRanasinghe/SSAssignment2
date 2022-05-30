package com.ssd.assignmentws.service;

import org.springframework.web.multipart.MultipartFile;

public interface DriveAssignmentService {

	public void uploadFile(MultipartFile multipartFile) throws Exception;
}
