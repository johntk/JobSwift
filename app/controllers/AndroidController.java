package controllers;

import play.mvc.*;

import java.util.Iterator;
import java.util.Map;

import play.mvc.Http.RequestBody;

import java.io.File;

import models.ApplicantModel;
import models.JobApplicationModel;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.mvc.Controller;
import play.mvc.Result;

public class AndroidController extends Controller {		

	
	// Add video path and user details to DB, save video to server
	// MultipartRequest Post update
	public static Result update(){
			
		//get the body of the request
		RequestBody body = request().body();
		//Assign the type as MultipartFormData
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		// Pull the video from the MultipartFormData
		Http.MultipartFormData.FilePart videoFile = multippartBody.getFile("fileKey");
		// Pull the hashMap from the MultipartFormData
		Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
		
		String email ="";
		String first_name ="";
		String last_name ="";
		String city ="";
		
		ApplicantModel Applicant;
		Iterator<String> myVeryOwnIterator = myMap.keySet().iterator();
	    while(myVeryOwnIterator.hasNext()) {
	        String key=(String)myVeryOwnIterator.next();
	        String[] value= myMap.get(key);
	        if(key.equals("email")){
	        	 email = value[0];
	        }
	        if(key.equals("first_name")){
	        	first_name = value[0];	
	        }
	        if(key.equals("last_name")){
	        	last_name = value[0];	
	        }
	        if(key.equals("city")){
	        	city = value[0];	
	        } 
	    }
	    
	    Applicant = ApplicantModel.findByEmail(email);
	    Applicant.applicant_city = city;
	    Applicant.applicant_email = email;
	    Applicant.applicant_firstName = first_name;
	    Applicant.applicant_lastName = last_name;
	    Applicant.update();

		// Create a directory for the Video and add the video to it
			if(videoFile != null) {
				File file = videoFile.getFile(); 
				String fileName = videoFile.getFilename();
				FileUploadController.createUserFolder(email);
				File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath()+ email);
				File fileOld = new File(newDir, fileName);
	        	if(fileOld.delete()){
	    			System.out.println(fileOld.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
		        if(!newDir.isDirectory()){
		            newDir.mkdirs();
		        }
		        if(newDir.canWrite()){
		        	file.renameTo(new File(newDir, fileName));
		        }

		        
		        Applicant.introVideoPath = "globalUploadFolder/" + Applicant.applicant_email + "/intro.mp4";
		        Applicant.update();

		        return ok("uploaded");
			} 
			else {
				return badRequest("failed");
		}
	}
// Add Profile image path to DB and save image on server
public static Result updateProfileImage(){
		
		//get the body of the request
		RequestBody body = request().body();
		//Assign the type as MultipartFormData
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		// Pull the video from the MultipartFormData
		Http.MultipartFormData.FilePart imageFile = multippartBody.getFile("fileKey");
		// Pull the hashMap from the MultipartFormData
		Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
		
		
		String email ="";
		ApplicantModel Applicant;
		Iterator<String> myVeryOwnIterator = myMap.keySet().iterator();
	    while(myVeryOwnIterator.hasNext()) {
	        String key=(String)myVeryOwnIterator.next();
	        String[] value= myMap.get(key);
	        if(key.equals("email")){
	        	 email = value[0];
	        }
	        
	    }
	    
	    
	   
		// Create a directory for the Profile image and add the .jpg to it
			if(imageFile != null) {
				File file = imageFile.getFile(); 
				String fileName = imageFile.getFilename();
				Applicant = ApplicantModel.findByEmail(email);
			    Applicant.profileImage = "globalUploadFolder/" + Applicant.applicant_email + "/" +  fileName;
			    Applicant.update();
				FileUploadController.createUserFolder(email);
				File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath()+ email);
		        if(!newDir.isDirectory()){
		            newDir.mkdirs();
		        }
		        if(newDir.canWrite()){
		        	File fileOld = new File(newDir, fileName);
		        	if(fileOld.delete()){
		    			System.out.println(fileOld.getName() + " is deleted!");
		    		}else{
		    			System.out.println("Delete operation is failed.");
		    		}
		        	file.renameTo(new File(newDir, fileName));
		        }
		        return ok("uploaded");
			} 
			else {
				return badRequest("failed");
		}
	}

// Save cv path to DB and add CV to server
public static Result updateCV(){
	
	//get the body of the request
	RequestBody body = request().body();
	//Assign the type as MultipartFormData
	Http.MultipartFormData multippartBody = body.asMultipartFormData();
	// Pull the video from the MultipartFormData
	Http.MultipartFormData.FilePart imageFile = multippartBody.getFile("fileKey");
	// Pull the hashMap from the MultipartFormData
	Map<String, String[]> myMap  = multippartBody.asFormUrlEncoded();
	
	
	String email ="";
	ApplicantModel Applicant;
	Iterator<String> myVeryOwnIterator = myMap.keySet().iterator();
    while(myVeryOwnIterator.hasNext()) {
        String key=(String)myVeryOwnIterator.next();
        String[] value= myMap.get(key);
        if(key.equals("email")){
        	 email = value[0];
        }
        
    }
    
    
   
	// Create a directory for the CV  and add the file to it
		if(imageFile != null) {
			File file = imageFile.getFile(); 
			String fileName = imageFile.getFilename();
			System.out.println(fileName);
			Applicant = ApplicantModel.findByEmail(email);
		    Applicant.cvFilePath = "globalUploadFolder/" + Applicant.applicant_email + "/" +  fileName;
		    Applicant.cvFileName = fileName;
		    Applicant.update();
			FileUploadController.createUserFolder(email);
			File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath()+ email);
	        if(!newDir.isDirectory()){
	            newDir.mkdirs();
	        }
	        if(newDir.canWrite()){
	        	File fileOld = new File(newDir, fileName);
	        	if(fileOld.delete()){
	    			System.out.println(fileOld.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
	        	file.renameTo(new File(newDir, fileName));
	        }
	        return ok();
		} 
		else {
			return badRequest();
	}
}
	
// Return all user details to application
	public static Result refresh() {
		String email;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();

	    if(json == null) {
	    	result.put("error", true);
	    	result.put("error_msg", "No JSon");
	        return badRequest(result);
	    } else {
	        email = json.findPath("email").textValue();
	        
	        ApplicantModel app = ApplicantModel.findByEmail(email);
	        if(app == null) {
	        	result.put("error",true);
	        	result.put("error_msg", "User Not Found");
	            return ok(result);
	        } else {
	        	result.put("error", false);
	        	result.put("app_id", app.applicant_id);
	        	result.put("first_name", app.applicant_firstName);
	        	result.put("last_name", app.applicant_lastName);
	        	result.put("email", app.applicant_email);
	        	result.put("city", app.applicant_city);
	        	result.put("cvFilePath", app.cvFilePath);
	        	result.put("profileImage", app.profileImage);
	        	result.put("introVideoPath", app.introVideoPath);
	            return ok(result);
	        }
	    }
	}
	
	// Save interview video path to DB and video saved to server
	public static Result saveInterviewVideos() {
		
		//get the body of the request
		RequestBody body = request().body();
		//Assign the type as MultipartFormData
		Http.MultipartFormData multippartBody = body.asMultipartFormData();
		// Pull the video from the MultipartFormData
		Http.MultipartFormData.FilePart videoFile = multippartBody.getFile("fileKey");
		// Pull the hashMap from the MultipartFormData
		Map<String, String[]> intVideoMap  = multippartBody.asFormUrlEncoded();
		
		String[] em = intVideoMap.get("email");
		String[] aid = intVideoMap.get("applicationId");
		String email = em[0];
		String applicationId = aid[0];

		// Create a directory for the interview videos
			if(videoFile != null) {
				File file = videoFile.getFile(); 
				String fileName = videoFile.getFilename();
				FileUploadController.createUserFolder(email);
				
				File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath()+ email + 
						"/InterviewVideos" + "/JobApplication" + applicationId);
				File fileOld = new File(newDir, fileName);
	        	if(fileOld.delete()){
	    			System.out.println(fileOld.getName() + " is deleted!");
	    		}else{
	    			System.out.println("Delete operation is failed.");
	    		}
		        if(!newDir.isDirectory()){
		            newDir.mkdirs();
		        }
		        if(newDir.canWrite()){
		        	file.renameTo(new File(newDir, fileName));
		        }
		        
		        return ok();
			} 
			else {
				return badRequest();
		}
	}
	
	public static Result interviewVideosComplete() {
		JsonNode json = request().body().asJson();
		
		JobApplicationModel jobApp = JobApplicationModel.findById(json.findPath("applicationId").asLong());
		
		if(jobApp != null) {
			jobApp.interviewDone = 1;
			jobApp.status = "complete";
			jobApp.update();
			return ok();
		} else {
			return badRequest();
		}
	}
	
}

