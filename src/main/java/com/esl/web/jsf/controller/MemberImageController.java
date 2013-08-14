package com.esl.web.jsf.controller;


/*
 * Application Controller for all member image related thing
 */
public class MemberImageController {
	public static String DEFAULT_IMAGE = "default.gif";
	public static String IMAGE_FOLDER = "/images/memberimage";
	public static String DEFAULT_FOLDER = "/default";
	public static String UPLOAD_FOLDER = "/upload";

	// ============== Setter / Getter ================//
	public String getDefaultImage() { return DEFAULT_IMAGE; }
	public void setDefaultImage(String defaultImage) { DEFAULT_IMAGE = defaultImage; }

	public String getImageFolder() { return IMAGE_FOLDER; }
	public void setImageFolder(String imagePath) { IMAGE_FOLDER = imagePath; }

	// ============== Getter Function ===================//
	public String getDefaultImageURI() { return IMAGE_FOLDER + DEFAULT_FOLDER + "//" + DEFAULT_IMAGE; }


	// ============== Constructor ================//
	public MemberImageController() {}
}
