package controllers;

import play.Play;
import play.mvc.*;

import java.util.Iterator;
import java.util.Map;

import play.mvc.Http.RequestBody;

import java.io.File;

import models.ApplicantModel;
import play.libs.Json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndroidController extends Controller {

			

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
	        	 System.out.println(value[0]);
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
				File newDir = new File(FileUploadController.getGlobalUploadFolderAbolutePath(),"videos");
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
	            return ok(result);
	        }
	    }
	}
}