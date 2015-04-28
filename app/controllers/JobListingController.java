package controllers;

import models.*;
import play.mvc.*;

import java.util.*;

import controllers.Application.Login;
import play.data.Form;

public class JobListingController extends Controller {
	
	private static final Form<JobListingModel> jobForm = Form.form(JobListingModel.class);
	private static List<JobListingModel> tempJobs = new ArrayList<JobListingModel>();
	
	// Render the job listings view for the main website
	public static Result jobListings() {
		return ok(views.html.MainWebsite.WebsiteJobList.render
				(tempJobs,Form.form(JobListingController.JobSearch.class), Form.form(Login.class), JobListingController.locationList(), jobTypeList(), JobListingController.sectorList() ));
	}
	
	@Security.Authenticated(RecruiterSecured.class)
	public static Result newJobListing()
	{
		return ok(views.html.Recruiter.JobDetailsForm.render(jobForm, locationList(), jobTypeList(), sectorList()));
	}
	
	// Method to add a job listing to the database
	// submitted from the job listing form
	@Security.Authenticated(RecruiterSecured.class)
	public static Result addJobListing() {
		
		Form<JobListingModel> boundForm = jobForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Recruiter.JobDetailsForm.render(boundForm, locationList(), jobTypeList(), sectorList()));
        }

        JobListingModel job = boundForm.get();
        
        if(job.job_id == null)
        {
			job.save();
        	
        	for(int i = 0; i < job.questions.size(); i++) {
        		if(job.questions.get(i) != null){
        			job.interviewQuestions.add(new InterviewQuestionModel(job, job.questions.get(i)));
        			job.interviewQuestions.get(i).save();
        		}
        	}
			
			flash("success", String.format("Successfully added job listing %s", job));
			return redirect(routes.JobListingController.listJobs());
        } else {
          job.update();
          flash("success", String.format("Successfully updated job listing %s", job));
          return redirect(routes.JobListingController.listJobs());
        }
	}
	
	@Security.Authenticated(RecruiterSecured.class)
	public static Result delete(Long id) {
    	final JobListingModel job = JobListingModel.findById(id);
    	if(job == null) {
    		return notFound(String.format("Job %s does not exist.", id));
    	}
    	
    	List<InterviewQuestionModel> temp = InterviewQuestionModel.findAllQuestionsByJobListing(job);
    	for(int i = 0; i< temp.size(); i++) {
    		temp.get(i).delete();
    	}
    	
    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByJobListing(job);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	
    	job.delete();
    	return redirect(routes.RecruiterController.dashboard());
    }
	
	public static class JobSearch {
		public String job_location;
		public String job_sector;
		public String job_type;
	}
	
	// Redirect to main website with list of job results
	public static Result jobSearchResult() {
		Form<JobSearch> jobForm = Form.form(JobSearch.class).bindFromRequest();
		String location = jobForm.get().job_location;
		String sector = jobForm.get().job_sector;
		String jobType = jobForm.get().job_type;
		
		tempJobs = JobListingModel.findByForm(location, sector, jobType);
		
		return redirect(routes.JobListingController.jobListings());
	}
	
	@Security.Authenticated(RecruiterSecured.class)
	public static Result jobSearchResultRecruiter() {
		Form<JobSearch> jobForm = Form.form(JobSearch.class).bindFromRequest();
		String location = jobForm.get().job_location;
		String sector = jobForm.get().job_sector;
		String jobType = jobForm.get().job_type;
		
		tempJobs = JobListingModel.findByForm(location, sector, jobType);
		
		return ok(views.html.Recruiter.JobList.render(tempJobs,jobForm, locationList(), jobTypeList(), sectorList()));
	}
	
	// Displays all jobs in recruiters dashboard
	@Security.Authenticated(RecruiterSecured.class)
	public static Result listJobs() {
		List<JobListingModel> jobs = JobListingModel.findAll();
    	return ok(views.html.Recruiter.JobList.render(jobs,Form.form(JobSearch.class), locationList(), jobTypeList(), sectorList()));
	}
	
	public static Result editJobListing(Long id) {
		JobListingModel jlm = JobListingModel.findById(id);
		List<InterviewQuestionModel> iqList = InterviewQuestionModel.findAllQuestionsByJobListing(jlm);
		
		Form<JobListingModel> filledForm = jobForm.fill(jlm);
		
		List<String> tempQuestions = new ArrayList<String>();
		
		for(int i=0; i<iqList.size(); i++) {
			tempQuestions.add(iqList.get(i).question);
		}
		
		return ok(views.html.Recruiter.EditJobListing.render(filledForm, tempQuestions, locationList(), jobTypeList(), sectorList()));
	}
	
	// Update the job listing from the job profile screen
	public static Result updateJobListing() {
		Form<JobListingModel> boundForm = jobForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return redirect(routes.JobListingController.listJobs());
        }

        JobListingModel job = boundForm.get();
        
        if(job.job_id != null)
        {
        	job.update();
        	List<InterviewQuestionModel> iqList = InterviewQuestionModel.findAllQuestionsByJobListing(job);
        	
        	for(int i=0; i<job.questions.size(); i++) {
        		iqList.get(i).question = job.questions.get(i);
        		iqList.get(i).update();
        	}
        	
            flash("success", String.format("Successfully updated job %s", job));
            return redirect(routes.JobListingController.editJobListing(job.job_id));
        } else return redirect(routes.JobListingController.editJobListing(job.job_id));
	}
	
	
	public static void clearJobResults() {
		tempJobs = null;
	}
	
	public static final List<String> jobTypeList() {
		List<String> list = new ArrayList<String>();
		list.add("Permanent");
		list.add("Temporary");
		list.add("Contract");
		return list;
	}
	
	// Creates a list of sectors for the new job listing form
	public static final List<String> sectorList() {
		List<String> list = new ArrayList<String>();
		list.add("Accountancy");
		list.add("Banking & Finance");
		list.add("Sales & Marketing");
		list.add("Office Support");
		list.add("Production & Manufacturing");
		list.add("Call Centres & Multilingual");
		list.add("Transport & Logistics");
		list.add("Engineering");
		list.add("IT");
		list.add("Digital & Online");
		list.add("Science & Pharmaceutical");
		return list;
	}
		
	// Creates a list of locations for the new job listing form
	public static final List<String> locationList() {
		List<String> list = new ArrayList<String>();
		list.add("Carlow");
		list.add("Cavan");
		list.add("Clare");
		list.add("Cork");
		list.add("Donegal");
		list.add("Dublin");
		list.add("Galway");
		list.add("Kerry");
		list.add("Kildare");
		list.add("Kilkenny");
		list.add("Laois");
		list.add("Leitrim");
		list.add("Limerick");
		list.add("Longford");
		list.add("Louth");
		list.add("Mayo");
		list.add("Meath");
		list.add("Monaghan");
		list.add("Offaly");
		list.add("Roscommon");
		list.add("Sligo");
		list.add("Tipperary");
		list.add("Waterford");
		list.add("Westmeath");
		list.add("Wexford");
		list.add("Wicklow");
		return list;
	}
	
	public static Result jobListingProfile(Long id) {
		JobListingModel jlm = JobListingModel.findById(id);
		List<JobApplicationModel> jobAppList = JobApplicationModel.findAllApplicationsByJobListing(jlm);
		
		return ok(views.html.Recruiter.JobListingProfile.render(jlm, jobAppList));
	}
}
