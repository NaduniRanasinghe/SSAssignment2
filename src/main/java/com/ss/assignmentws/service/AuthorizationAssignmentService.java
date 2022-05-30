package com.ssd.assignmentws.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.google.api.client.auth.oauth2.Credential;

public interface AuthorizationAssignmentService {

	public boolean isAssignmentUserAuthenticated() throws Exception;

	public Credential getAssignmentCredentials() throws IOException;

	public String authenticateAssignmentUserViaGoogle() throws Exception;

	public void exchangeAssignmentCodeForTokens(String code) throws Exception;
	
	public void removeAssignmentUserSession(HttpServletRequest request) throws Exception;
}
