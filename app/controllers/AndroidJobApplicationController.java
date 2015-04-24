package controllers;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import models.*;

import org.json.JSONException;

import play.libs.Json;
import play.mvc.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AndroidJobApplicationController extends Controller {
	
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
	    				jsonApp.put("status", jobAppList.get(i).status);
	    				appList.add(jsonApp);
	    			}
	    		}
	    	}
	    	return ok(appList);
	    }
	}
	
	public static Result getInterviewQuestions() {
		JsonNode json = request().body().asJson();
		ObjectNode jsonApp;
		ArrayNode qList = new ArrayNode(null);
		
		if(json == null) {
	        return badRequest();
	    } else {
	    	JobListingModel app = JobListingModel.findById(json.findPath("job_id").asLong());
	    	if(app != null) {
	    		List<InterviewQuestionModel> questionList = InterviewQuestionModel.findAllQuestionsByJobListing(app);
	    		
	    		if(questionList != null) {
	    			for(int i =0; i< questionList.size(); i++) {
	    				jsonApp = Json.newObject();
	    				jsonApp.put("question_id", questionList.get(i).question_id);
	    				jsonApp.put("question", questionList.get(i).question);
	    				jsonApp.put("job_id", questionList.get(i).job.job_id);
	    				qList.add(jsonApp);
	    			}
	    		}
	    	}
	    	return ok(qList);
	    }
	}

}
