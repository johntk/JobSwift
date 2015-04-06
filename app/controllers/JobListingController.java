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
				(tempJobs,Form.form(JobListingController.JobSearch.class), Form.form(Login.class), JobListingController.locationList(), JobListingController.sectorList() ));
	}
	
	public static Result newJobListing()
	{
		return ok(views.html.Recruiter.JobDetailsForm.render(jobForm, locationList(), sectorList()));
	}
	
	
	// Method to add a job listing to the database
	// submitted from the job listing form
	public static Result addJobListing() {
		
		Form<JobListingModel> boundForm = jobForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Recruiter.JobDetailsForm.render(boundForm, locationList(), sectorList()));
        }

        JobListingModel job = boundForm.get();
        
        if(job.job_id == null)
        {
			job.save();
			flash("success", String.format("Successfully added job listing %s", job));
			return redirect(routes.ApplicantController.listApplicants());
        } else {
          job.update();
          flash("success", String.format("Successfully updated job listing %s", job));
          return redirect(routes.ApplicantController.listApplicants());
        }
	}
	
	public static Result delete(Long id) {
    	final JobListingModel job = JobListingModel.findById(id);
    	if(job == null) {
    		return notFound(String.format("Job %s does not exist.", id));
    	}

    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByJobListing(job);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	
    	job.delete();
    	return redirect(routes.Application.dashboard());
    }
	
	public static class JobSearch {
		public String job_location;
		public String job_sector;
	}
	
	public static Result jobSearchResult() {
		Form<JobSearch> jobForm = Form.form(JobSearch.class).bindFromRequest();
		String location = jobForm.get().job_location;
		String sector = jobForm.get().job_sector;
		
		tempJobs = JobListingModel.findByForm(location, sector);
		
		return redirect(routes.JobListingController.jobListings());
	}
	
	public static void clearJobResults() {
		tempJobs = null;
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

}
