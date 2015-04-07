package controllers;
import models.ApplicantModel;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;
 

public class AndroidLoginController extends Controller {
	
	// Process the applicant login form from the homepage
	@BodyParser.Of(BodyParser.Json.class)
	public static Result login() {
		
		String email, password;
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();
		boolean error;
		
	    if(json == null) {
	    	error = true;
	    	System.out.println("Json null");
	    	result.put("error", error);
	    	result.put("error_msg", "No JSon");
	        return ok(result);
	    } else {
	        email = json.findPath("email").textValue();
	        password = json.findPath("password").textValue();
	        System.out.println(email);
	        ApplicantModel app = ApplicantModel.authenticateApplicant(email, password);
	        if(app == null) {
	        	error = true;
	        	result.put("error",error);
	        	result.put("error_msg", "User Not Found");
	            return ok(result);
	        } else {
	        	error = false;
	        	result.put("error", false);
	        	result.put("uid", "1234");
	        	result.put("user", app.applicant_firstName);
	        	result.put("user", app.applicant_email);
	        	result.put("user", "");
	        	result.put("user", "");
	            return ok(result);
	        }
	    }
	}
}
