package controllers;

import java.util.List;

import models.*;
import play.data.Form;
import play.mvc.*;

public class JobApplicationController extends Controller {
	
	// Applying for a job from main website for applicants
	public static Result applyForJob(Long id) {
		if(session().get("email") != null) {
			
			// Find applicant object based on the user currently logged in
			ApplicantModel app = ApplicantModel.findByEmail(session().get("email"));
			List<JobListingModel> currentApps = JobApplicationModel.findJobsAppliedForByUser(app);
			
			// Check if the applicant has a CV on file
			// if not redirect with a warning message
			if(app.cvFileName != null) {
				JobListingModel jlm = JobListingModel.findById(id);
				
				// Loops through the users existing applications to
				// check if they have applied for this position already
				if(currentApps != null){
					for(int i = 0; i < currentApps.size(); i++) {
						System.out.println(currentApps.get(i).job_description);
						if(jlm.job_id == currentApps.get(i).job_id) {
							flash("error", "You have already applied for this job.");
							return redirect(routes.JobListingController.jobListings());
						}
					}
				}
				
				JobApplicationModel temp = new JobApplicationModel(app, jlm);
				temp.save();
				flash("success", "Job Application Successful.");
				return redirect(routes.JobListingController.jobListings());
			} else {
				flash("error", "Please upload your CV before applying for any jobs.");
				return redirect(routes.JobListingController.jobListings());
			}
			
		} else
			flash("error", "You must be logged in first!");
			return redirect(routes.JobListingController.jobListings());
	}
	
	// Deletes a job application from the database
	public static Result delete(Long id) {
    	final JobApplicationModel jobApp = JobApplicationModel.findById(id);
    	if(jobApp == null) {
    		return notFound(String.format("Job %s does not exist.", id));
    	}
    	
    	AssignedTaskModel atm = AssignedTaskModel.findTaskByJobApplication(jobApp);
    	if(atm != null) {
    		atm.delete();
    	}
    	jobApp.delete();
    	return redirect(routes.JobApplicationController.listAllJobApplications());
    }
	
	// Secure method to set the status of 
	// job application from recruiters website
	@Security.Authenticated(RecruiterSecured.class)
	public static Result setApplicationStatus(Long id, String status) {

		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		
		if(status.equals("accepted")) {
			assignEmployeeTask(jam);
		}
		
		if(status.equals("interview")) {
			GCMController.sendNotification(jam.app.gcm_id, "You're interview for " + jam.job.job_title + " in " + jam.job.job_location +
					" is now available!\nLog into your app to complete your application.");
		}
		
		return redirect(routes.JobApplicationController.listAllJobApplications());
	}
	
	// Secure method to change the status from
	// within a users profile on the recruiters website
	// Separate method required as this one redirects to the user's profile
	@Security.Authenticated(RecruiterSecured.class)
	public static Result setApplicationStatusUserProfile(Long id, String status) {
		
		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		
		if(status.equals("accepted")) {
			assignEmployeeTask(jam);
		}
		
		if(status.equals("interview")) {
			GCMController.sendNotification(jam.app.gcm_id, "You're interview for " + jam.job.job_title + " in " + jam.job.job_location +
				" is now available!\nLog into your app to complete your application.");
		}
		
		return redirect(routes.ApplicantController.viewApplicantProfile(jam.app.applicant_email));
	}
	
	public static class FilterJobApplication {
		public String status;
	}
	
	// Displays all job applications in the recruiters dashboard
	@Security.Authenticated(RecruiterSecured.class)
	public static Result listAllJobApplications() {
    	List<JobApplicationModel> jobApps = JobApplicationModel.findAll();
    	return ok(views.html.Recruiter.JobApplicationList.render(jobApps, Form.form(FilterJobApplication.class)));
    }
	
	// filter job applications on recruiters website
	@Security.Authenticated(RecruiterSecured.class)
	public static Result filterJobApplications() {
		Form<FilterJobApplication> filterForm = Form.form(FilterJobApplication.class).bindFromRequest();
		List<JobApplicationModel> jobApps = JobApplicationModel.findApplicationByStatus(filterForm.get().status);
		return ok(views.html.Recruiter.JobApplicationList.render(jobApps, Form.form(FilterJobApplication.class)));
	}
	
	// When employee "accepts" a job application
	// this method assigns the task to them
	public static void assignEmployeeTask(JobApplicationModel jam) {
		EmployeeModel employee = EmployeeModel.findByUserName(session().get("username"));
		AssignedTaskModel atm = new AssignedTaskModel();
		atm.emp = employee;
		atm.jobApp = jam;
		atm.save();
	}

}
