package controllers;

import models.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;

import org.mindrot.jbcrypt.BCrypt;


public class AndroidUserController extends Controller {
	
	// Process the applicant login form from the homepage
	public static Result login() {
		String email, password, gcmId;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();

	    if(json == null) {
	    	result.put("error", true);
	    	result.put("error_msg", "No JSon");
	        return badRequest(result);
	    } else {
	        email = json.findPath("email").textValue();
	        password = json.findPath("password").textValue();
	        gcmId = json.findPath("gcmid").textValue();
	        
	        ApplicantModel app = ApplicantModel.authenticateApplicant(email, password);
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
	        	result.put("cvFileName", app.cvFileName);
	        	
	        	app.gcm_id = gcmId;
	        	app.update();
	        	
	            return ok(result);
	        }
	    }
	}
	
	public static Result storeGCMId() {
		String email, gcmId;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
	    if(json == null) {
	    	result.put("error", true);
	    	result.put("error_msg", "No JSon");
	        return badRequest(result);
	    } else {
	        email = json.findPath("email").textValue();
	        gcmId = json.findPath("gcmid").textValue();
	        
	        ApplicantModel app = ApplicantModel.findByEmail(email);
	        if(app == null) {
	        	
	        	result.put("error",true);
	        	result.put("error_msg", "User Not Found");
	            return ok(result);
	        } else {
	        	result.put("error", false);
	        	
	        	app.gcm_id = gcmId;
	        	
	        	// Check if the user has logged into the app before
	        	// If not, send them a welcome message push notification
	        	if(app.loggedIntoApp == 0) {
		        	GCMController.sendNotification(app.gcm_id, "Thank you for choosing JobSwift.\n\n"
		    				+ "Make sure to complete your profile by adding a short introduction video that tells us all about your skills & work experience.\n"
		    				+ "When your profile is complete, visit our website to start applying for jobs.");
		    		app.loggedIntoApp = 1;
	        	}
	        	app.update();
	        	
	            return ok(result);
	        }
	    }
	}
	
	public static Result register() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		
		if(json == null) {
			System.out.println("no json");
	    	result.put("error", true);
	    	result.put("error_msg", "No JSon");
	        return badRequest(result);
	    } else {

	    	ApplicantModel app = new ApplicantModel();
	    	app.applicant_title = json.findPath("title").textValue();
	    	app.applicant_email = json.findPath("email").textValue();
	    	app.applicant_firstName = json.findPath("first_name").textValue();
	    	app.applicant_lastName = json.findPath("last_name").textValue();
	    	app.applicant_city = json.findPath("city").textValue();
	    	app.applicant_password = BCrypt.hashpw(json.findPath("password").textValue(), BCrypt.gensalt());
	    	
	    	if (app.applicant_id == null) {
				if (ApplicantModel.findByEmail(app.applicant_email) != null) {
					result.put("error", true);
			    	result.put("error_msg", "User Is Already Registered!");
			        return ok(result);
				}else {
					app.save();
				}
	    	}
	    	
	    	if(ApplicantModel.findByEmail(json.findPath("email").textValue()) != null) {
	    		result.put("error", false);
		    	result.put("error_msg", "Signup Successful");
		        return ok(result);
	    	} else {
	    		result.put("error", true);
		    	result.put("error_msg", "Something Went Wrong");
		        return ok(result);
	    	}
	    }
	}
	
}