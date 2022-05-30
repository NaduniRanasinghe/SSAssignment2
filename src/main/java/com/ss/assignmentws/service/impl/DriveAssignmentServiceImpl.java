package com.ssd.assignmentws.service.impl;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.ssd.assignmentws.constant.ApplicationAssignmentConstant;
import com.ssd.assignmentws.service.AuthorizationAssignmentService;
import com.ssd.assignmentws.service.DriveAssignmentService;
import com.ssd.assignmentws.util.ApplicationAssignmentConfig;

@Service
public class DriveAssignmentServiceImpl implements DriveAssignmentService {

	private Logger logger = LoggerFactory.getLogger(DriveAssignmentServiceImpl.class);

	private Drive drive;

	@Autowired
	AuthorizationAssignmentService authorizationAssignmentService;

	@Autowired
	ApplicationAssignmentConfig applicationAssignmentConfig;

	@PostConstruct
	public void init() throws Exception {
		Credential credential = authorizationAssignmentService.getAssignmentCredentials();
		drive = new Drive.Builder(ApplicationAssignmentConstant.HTTP_TRANSPORT, ApplicationAssignmentConstant.JSON_FACTORY, credential)
				.setApplicationName(ApplicationAssignmentConstant.APPLICATION_ASSIGNMENT_NAME).build();
	}

	@Override
	public void uploadFile(MultipartFile multipartFile) throws Exception {
		logger.debug("Inside Upload Service...");

		String path = applicationAssignmentConfig.getTemporaryFolder();
		String fileName = multipartFile.getOriginalFilename();
		String contentType = multipartFile.getContentType();

		java.io.File transferedFile = new java.io.File(path, fileName);
		multipartFile.transferTo(transferedFile);

		File fileMetadata = new File();
		fileMetadata.setName(fileName);
		
		FileContent mediaContent = new FileContent(contentType, transferedFile);
		File file = drive.files().create(fileMetadata, mediaContent).setFields("id").execute();

		logger.debug("File ID: " + file.getName() + ", " + file.getId());
	}

}
