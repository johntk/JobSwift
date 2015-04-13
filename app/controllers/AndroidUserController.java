package controllers;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

import models.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import play.libs.Json;
import play.mvc.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
	
	public static Result getInterviews() throws JSONException, FileNotFoundException, UnsupportedEncodingException {
		JsonNode json = request().body().asJson();
		ObjectNode jsonApp;
		ArrayNode appList = new ArrayNode(null);
		
		if(json == null) {
	        return badRequest();
	    } else {
	    	ApplicantModel app = ApplicantModel.findByEmail(json.findPath("email").asText());
	    	if(app != null) {
	    		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(app);
	    		
	    		if(jobAppList != null) {
	    			for(int i =0; i< jobAppList.size(); i++) {
	    				jsonApp = Json.newObject();
	    				jsonApp.put("app_id", jobAppList.get(i).job_application_id);
	    				jsonApp.put("job_id", jobAppList.get(i).job.job_id);
	    				jsonApp.put("job_title", jobAppList.get(i).job.job_title);
	    				jsonApp.put("job_description", jobAppList.get(i).job.job_description);
	    				jsonApp.put("job_location", jobAppList.get(i).job.job_location);
	    				appList.add(jsonApp);
	    			}
	    		}
	    	}
	    	System.out.println(appList);
	    	return ok(appList);
	    }
	}
	
//	public static Result getInterviews() throws JSONException, FileNotFoundException, UnsupportedEncodingException {
//		JsonNode json = request().body().asJson();
//		ObjectNode jsonApp = Json.newObject();
//		String jsonString = null;
//		
//		if(json == null) {
//	        return badRequest();
//	    } else {
//	    	ApplicantModel app = ApplicantModel.findByEmail(json.findPath("email").asText());
//	    	if(app != null) {
//	    		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(app);
//	    		jsonString = "[";
//	    		
//	    		if(jobAppList != null) {
//	    			for(int i=0; i< jobAppList.size(); i++) {
//			    		jsonString += "{\"job\":{";
//			    		jsonString += "\"app_id\":\"" + jobAppList.get(i).job_application_id + "\", ";
//			    		jsonString += "\"job_id\":\"" + jobAppList.get(i).job.job_id + "\", ";
//			    		jsonString += "\"job_title\":\"" + jobAppList.get(i).job.job_title + "\", ";
//			    		jsonString += "\"job_description\":\"" + jobAppList.get(i).job.job_description + "\", ";
//			    		jsonString += "\"job_location\":\"" + jobAppList.get(i).job.job_location + "\"";
//			    		if(i == (jobAppList.size()-1)){
//			    			jsonString += "}}";
//			    		}else {
//			    			jsonString += "}},";
//			    		}
//	    			}
//	    			jsonString += "]";
//	    		}
//	    	}
//	    	
////	    	PrintWriter writer = new PrintWriter("json.txt", "UTF-8");
////	    	writer.println(jsonString);
////	    	writer.close();
//	    	jsonApp = (ObjectNode) Json.parse(jsonString);
//	    	System.out.println(jsonApp.toString());
//	    	return ok();
//	    }
//	}
	
//	public static Result getInterviews() {
//		JsonNode json = request().body().asJson();
//		JSONArray questionArray = new JSONArray();
//		JSONArray jobInterviewCollection = new JSONArray();
//		
//		if(json == null) {
//	        return badRequest();
//	    } else {
//	    	ApplicantModel app = ApplicantModel.findByEmail(json.findPath("email").asText());
//	    	if(app != null) {
//	    		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByUser(app);
//	    		
//	    		if(jobAppList != null) {
//		    		for(int i=0; i< jobAppList.size(); i++) {
//		    			JobListingModel jlm = jobAppList.get(i).job;
//		    			ObjectNode singleJobInterview = Json.newObject();
//		    			singleJobInterview.put("job_id", jlm.job_id);
//		    			singleJobInterview.put("title", jlm.job_title);
//		    			singleJobInterview.put("location", jlm.job_location);
//		    			
//		    			List<InterviewQuestionModel> iqList = InterviewQuestionModel.findAllQuestionsByJobListing(jlm);
//		    			for(int j=0; j < iqList.size(); j++) {
//		    				ObjectNode question = Json.newObject();;
//		    				question.put("question_id", iqList.get(j).question_id);
//		    				question.put("question", iqList.get(j).question);
//		    				question.put("job_id", iqList.get(j).job.job_id);
//		    				questionArray.put(question);
//		    			}
//		    			singleJobInterview.put("questions", questionArray.toString());
//		    			jobInterviewCollection.put(singleJobInterview);
//		    		}
//	    		}
//	    	}
//	    	System.out.println(jobInterviewCollection);
//	    	return ok(jobInterviewCollection.toString());
//	    }
//	}

}