package controllers;
import java.util.List;

import models.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.libs.Json;
import play.mvc.*;

import org.mindrot.jbcrypt.BCrypt;
 

public class AndroidUserController extends Controller {
	
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
	
	public static Result getInterviews() {
		JsonNode json = request().body().asJson();
		ObjectNode singleJobInterview = Json.newObject();
		ObjectNode jobInterviewColection = Json.newObject();
		
		if(json != null) {
	        return badRequest();
	    } else {
	    	ApplicantModel app = ApplicantModel.findByEmail("e@e");
	    	
	    	if(app != null) {
	    		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(app);
	    		
	    		for(int i=0; i< jobAppList.size(); i++) {
	    			JobListingModel jlm = jobAppList.get(i).job;
	    			singleJobInterview.put("job_id", jlm.job_id);
	    			singleJobInterview.put("company", jlm.job_company);
	    			singleJobInterview.put("title", jlm.job_title);
	    			
	    			List<InterviewQuestionModel> iqList = InterviewQuestionModel.findAllQuestionsByJobListing(jlm);
	    			for(int j=0; j < iqList.size(); j++) {
	    				singleJobInterview.put("question"+j, iqList.get(j).question);
	    			}
	    			jobInterviewColection.put("jobInterview"+i, singleJobInterview);
	    		}
	    	}
	    	return ok(jobInterviewColection);
	    }
	}
	
//	public static Result print() {
//		
//		ApplicantModel app = ApplicantModel.findByEmail("e@e");
//    	if(app != null) {
//    		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(app);
//    		for(int i=0; i< jobAppList.size(); i++) {
//    			JobListingModel jlm = jobAppList.get(i).job;
//    			System.out.println("Questions for job: " + jlm.job_title);
//    			List<InterviewQuestionModel> iqList = InterviewQuestionModel.findAllQuestionsByJobListing(jlm);
//    			for(int j=0; j < iqList.size(); j++) {
//    				System.out.println(iqList.get(j).question);
//    			}
//    		}
//    		return ok();
//    	}else {
//    		return ok();
//    	}
//	}
}