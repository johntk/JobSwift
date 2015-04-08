package controllers;
import models.ApplicantModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
 

public class AndroidLoginController extends Controller {
	
	// Process the applicant login form from the homepage
	public static Result login() {
		String email, password;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();

	    if(json == null) {
	    	result.put("error", true);
	    	result.put("error_msg", "No JSon");
	        return badRequest(result);
	    } else {
	        email = json.findPath("email").textValue();
	        password = json.findPath("password").textValue();
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
	            return ok(result);
	        }
	    }
	}
}
