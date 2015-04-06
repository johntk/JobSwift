package controllers;

import models.*;
import play.mvc.*;

public class JobApplicationController extends Controller {
	
	public static Result applyForJob(Long id) {
		if(session().get("email") != null) {
			
			ApplicantModel app = ApplicantModel.findByEmail(session().get("email"));
			if(app.cvFileName != null) {
				JobListingModel jlm = JobListingModel.findById(id);
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
}
