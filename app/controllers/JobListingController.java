package controllers;

import models.*;
import play.mvc.*;

import java.util.*;

import play.data.Form;

public class JobListingController extends Controller {
	
	private static final Form<JobListingModel> jobForm = Form.form(JobListingModel.class);
	
	public static Result newJobListing()
	{
		return ok(views.html.Recruiter.JobDetailsForm.render(jobForm));
	}
	
	
	// Method to add a job listing to the database
	// submitted from the job listing form
	public static Result addJobListing()
	{
		Form<JobListingModel> boundForm = jobForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Recruiter.JobDetailsForm.render(boundForm));
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

    	job.delete();
    	return redirect(routes.Application.dashboard());
    }
	
		// Creates a list of sectors for the new job listing form
		public static final List<String> sectorList() {
			List<String> list = new ArrayList<String>();
			list.add("");
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

}
