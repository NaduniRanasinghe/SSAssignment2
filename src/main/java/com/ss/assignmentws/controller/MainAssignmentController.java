package com.ssd.assignmentws.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import com.ssd.assignmentws.model.UploadAssignmentFile;
import com.ssd.assignmentws.service.AuthorizationAssignmentService;
import com.ssd.assignmentws.service.DriveAssignmentService;

@Controller
public class MainAssignmentController {

	private Logger logger = LoggerFactory.getLogger(MainAssignmentController.class);

	@Autowired
	AuthorizationAssignmentService authorizationAssignmentService;

	@Autowired
	DriveAssignmentService driveAssignmentService;

	@GetMapping("/")
	public String showAssignmentHomePage() throws Exception {
		if (authorizationAssignmentService.isAssignmentUserAuthenticated()) {
			logger.debug("User is authenticated. Redirecting to home...");
			return "redirect:/home";
		} else {
			logger.debug("User is not authenticated. Redirecting to sso...");
			return "redirect:/login";
		}
	}

	@GetMapping("/login")
	public String goToLogin() {
		return "index.html";
	}

	@GetMapping("/home")
	public String goAssignmentToHome() {
		return "home.html";
	}

	@GetMapping("/googlesignin")
	public void doAssignmentGoogleSignIn(HttpServletResponse response) throws Exception {
		logger.debug("SSO Called...");
		response.sendRedirect(authorizationAssignmentService.authenticateAssignmentUserViaGoogle());
	}

	@GetMapping("/oauth/callback")
	public String saveAssignmentAuthorizationCode(HttpServletRequest request) throws Exception {
		logger.debug("SSO Callback invoked...");
		String code = request.getParameter("code");
		logger.debug("SSO Callback Code Value..., " + code);

		if (code != null) {
			authorizationAssignmentService.exchangeAssignmentCodeForTokens(code);
			return "redirect:/home";
		}
		return "redirect:/login";
	}

	@GetMapping("/logout")
	public String logoutAssignment(HttpServletRequest request) throws Exception {
		logger.debug("Logout invoked...");
		authorizationAssignmentService.removeAssignmentUserSession(request);
		return "redirect:/login";
	}

	@PostMapping("/upload")
	public String uploadAssignmentFile(HttpServletRequest request, @ModelAttribute UploadAssignmentFile uploadedFile) throws Exception {
		MultipartFile multipartFile = uploadedFile.getMultipartFile();
		driveAssignmentService.uploadFile(multipartFile);
		return "redirect:/home?status=success";
	}
}
