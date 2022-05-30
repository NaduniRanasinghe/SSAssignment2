package com.ssd.assignmentws.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.ssd.assignmentws.constant.ApplicationAssignmentConstant;
import com.ssd.assignmentws.service.AuthorizationAssignmentService;
import com.ssd.assignmentws.util.ApplicationAssignmentConfig;

@Service
public class AuthorizationAssignmentServiceImpl implements AuthorizationAssignmentService {

	private Logger logger = LoggerFactory.getLogger(AuthorizationAssignmentServiceImpl.class);
	private GoogleAuthorizationCodeFlow flow;
	private FileDataStoreFactory fileDataStoreFactory;

	@Autowired
	private ApplicationAssignmentConfig applicationAssignmentConfig;

	@PostConstruct
	public void init() throws Exception {
		InputStreamReader reader = new InputStreamReader(applicationAssignmentConfig.getDriveSecretKeys().getInputStream());
		fileDataStoreFactory = new FileDataStoreFactory(applicationAssignmentConfig.getCredentialsFolder().getFile());

		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(ApplicationAssignmentConstant.JSON_FACTORY, reader);
		flow = new GoogleAuthorizationCodeFlow.Builder(ApplicationAssignmentConstant.HTTP_TRANSPORT, ApplicationAssignmentConstant.JSON_FACTORY, clientSecrets,
				ApplicationAssignmentConstant.SCOPES).setDataStoreFactory(fileDataStoreFactory).build();
	}

	@Override
	public boolean isAssignmentUserAuthenticated() throws Exception {
		Credential credential = getAssignmentCredentials();
		if (credential != null) {
			boolean isTokenValid = credential.refreshToken();
			logger.debug("isTokenValid, " + isTokenValid);
			return isTokenValid;
		}
		return false;
	}

	@Override
	public Credential getAssignmentCredentials() throws IOException {
		return flow.loadCredential(ApplicationAssignmentConstant.USER_ASSIGNMENT_IDENTIFIER_KEY);
	}

	@Override
	public String authenticateAssignmentUserViaGoogle() throws Exception {
		GoogleAuthorizationCodeRequestUrl url = flow.newAuthorizationUrl();
		String redirectUrl = url.setRedirectUri(applicationAssignmentConfig.getCALLBACK_URI()).setAccessType("offline").build();
		logger.debug("redirectUrl, " + redirectUrl);
		return redirectUrl;
	}

	@Override
	public void exchangeAssignmentCodeForTokens(String code) throws Exception {
		// exchange the code against the access token and refresh token
		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(applicationAssignmentConfig.getCALLBACK_URI()).execute();
		flow.createAndStoreCredential(tokenResponse, ApplicationAssignmentConstant.USER_ASSIGNMENT_IDENTIFIER_KEY);
	}

	@Override
	public void removeAssignmentUserSession(HttpServletRequest request) throws Exception {
		fileDataStoreFactory.getDataStore(applicationAssignmentConfig.getCredentialsFolder().getFilename()).clear();
	}

}
