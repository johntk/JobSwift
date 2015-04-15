package controllers;

import models.*;

import java.io.File;

import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import play.Play;

public class FileUploadController extends Controller {
	
	public static String getGlobalUploadFolderAbolutePath() {
		String root = Play.application().path().toString();
		String globalFolderPath = root + "/public/globalUploadFolder/";
		File globalFolder = new File(globalFolderPath);
		
		// Check if the main upload folder exists first, if not create it
		if(!globalFolder.exists()) {
			try{
				globalFolder.mkdir();
			}catch(SecurityException se){
				System.out.println("cannot create global directory");
		    }
		}
		return globalFolderPath;
	}
	
	/*******************************
	 * Create a folder for a user
	 ******************************/
	public static void createUserFolder(String email) {
		String userFolderName = getGlobalUploadFolderAbolutePath() + email;
		File userFolder = new File(userFolderName);
		
		// Check if the user upload folder exists first, if not create it
		if(!userFolder.exists()) {
			try{
				userFolder.mkdir();
			}catch(SecurityException se){
				System.out.println("cant create directory");
		    } 
		}
	}

	/*******************************************
	 * CV File upload from website user profile
	 ******************************************/
	@Security.Authenticated(Secured.class)
	public static Result uploadCV() {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart cvFile = body.getFile("cvfile");
		
		if (cvFile != null) {
			// Create variables for folder creation
			
			String userFolderName = getGlobalUploadFolderAbolutePath() + Application.getCurrentUser().applicant_email;
			File userFolder = new File(userFolderName);
			
			// Check if the user upload folder exists first, if not create it
			if(!userFolder.exists()) {
				try{
					userFolder.mkdir();
				}catch(SecurityException se){
					System.out.println("cannot create directory");
			    } 
			}
			
//			String newFileName = Application.getCurrentUser().applicant_firstName+"_"+Application.getCurrentUser().applicant_lastName+"_CV";
			String newFileName = cvFile.getFilename();
			
			try {
        		File oldFile = new File(userFolderName, cvFile.getFilename());
        		oldFile.delete(); } catch(Exception e) {
        		e.printStackTrace();
        	}
			
			File file = cvFile.getFile();
			file.renameTo(new File(userFolderName, newFileName));
			
			System.out.println(userFolderName);
			
			ApplicantModel app = Application.getCurrentUser();
			app.cvFilePath = "globalUploadFolder/"+ app.applicant_email +"/"+ newFileName;
			app.cvFileName = newFileName;
			app.update();
			
			flash("success", "File Uploaded.");
			return redirect(routes.Application.applicantProfile());
		} else {
			flash("error", "File Error.");
			return redirect(routes.Application.applicantProfile());
		}
	}
	
	/*************************************************
	 * Profile image upload from website user profile
	 ************************************************/
	@Security.Authenticated(Secured.class)
	public static Result uploadProfileImage() {
		MultipartFormData body = request().body().asMultipartFormData();
		FilePart imageFile = body.getFile("profileImage");
		
		if (imageFile != null) {
			// Create variables for folder creation
			
			String userFolderName = getGlobalUploadFolderAbolutePath() + Application.getCurrentUser().applicant_email;
			File userFolder = new File(userFolderName);
			
			// Check if the user upload folder exists first, if not create it
			if(!userFolder.exists()) {
				try{
					userFolder.mkdir();
				}catch(SecurityException se){
					System.out.println("cannot create directory");
			    } 
			}
			
			String newFileName = imageFile.getFilename();
			File file = imageFile.getFile();
			
        	try {
        		File oldFile = new File(userFolderName, imageFile.getFilename());
        		oldFile.delete(); } catch(Exception e) {
        		e.printStackTrace();
        	}
			
			file.renameTo(new File(userFolderName, newFileName));
			
			ApplicantModel app = Application.getCurrentUser();
			app.profileImage = "globalUploadFolder/"+ app.applicant_email +"/"+ newFileName;
			System.out.println(app.profileImage);
			app.update();
			
			flash("success", "File Uploaded.");
			return redirect(routes.Application.applicantProfile());
		} else {
			flash("error", "File Error.");
			return redirect(routes.Application.applicantProfile());
		}
	}
}
	
	