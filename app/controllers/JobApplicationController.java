package controllers;

import java.util.List;

import controllers.RecruiterController.EmpLogin;
import models.*;
import play.data.Form;
import play.mvc.*;

public class JobApplicationController extends Controller {
	
	public static Result applyForJob(Long id) {
		if(session().get("email") != null) {
			
			ApplicantModel app = ApplicantModel.findByEmail(session().get("email"));
			List<JobListingModel> currentApps = JobApplicationModel.findJobsAppliedForByUser(app);
			
			if(app.cvFileName != null) {
				JobListingModel jlm = JobListingModel.findById(id);
				
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
	
	public static Result delete(Long id) {
    	final JobApplicationModel jobApp = JobApplicationModel.findById(id);
    	if(jobApp == null) {
    		return notFound(String.format("Job %s does not exist.", id));
    	}
    	
    	jobApp.delete();
    	return redirect(routes.JobApplicationController.listAllJobApplications());
    }
	
	public static Result setApplicationStatus(Long id, String status) {
		
		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		
		if(status.equals("interview")) {
			sendInterviewNotification(jam);
		}
		
		return redirect(routes.JobApplicationController.listAllJobApplications());
	}
	
	public static Result setApplicationStatusUserProfile(Long id, String status) {
		
		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		
		if(status.equals("interview")) {
			sendInterviewNotification(jam);
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
	
	@Security.Authenticated(RecruiterSecured.class)
	public static Result filterJobApplications() {
		Form<FilterJobApplication> filterForm = Form.form(FilterJobApplication.class).bindFromRequest();
		List<JobApplicationModel> jobApps = JobApplicationModel.findApplicationByStatus(filterForm.get().status);
		return ok(views.html.Recruiter.JobApplicationList.render(jobApps, Form.form(FilterJobApplication.class)));
	}
	
	public static void sendInterviewNotification(JobApplicationModel jam) {
		GCMContent content = GCMController.createContent(jam.app.gcm_id, "You're interview for " + jam.job.job_title + " in " + jam.job.job_location +
				" is now available!\nLog into your app to complete your application.");
    	GCMController.post(content);
	}

}
