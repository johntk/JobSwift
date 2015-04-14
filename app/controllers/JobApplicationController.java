package controllers;

import java.util.List;

import models.*;
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
    	return redirect(routes.ApplicantController.listAllJobApplications());
    }
	
	public static Result setApplicationStatus(Long id, String status) {
		
		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		return redirect(routes.ApplicantController.listAllJobApplications());
	}
	
	public static Result setApplicationStatusUserProfile(Long id, String status) {
		
		JobApplicationModel jam = JobApplicationModel.findById(id);
		jam.status = status;
		jam.update();
		return redirect(routes.ApplicantController.viewApplicantProfile(jam.app.applicant_email));
	}

}
