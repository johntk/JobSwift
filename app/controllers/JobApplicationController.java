package controllers;

import models.*;
import play.mvc.*;

public class JobApplicationController extends Controller {
	
	@Security.Authenticated(Secured.class)
	public static Result applyForJob(Long id) {
		if(Application.getCurrentUser() != null) {
			ApplicantModel app = Application.getCurrentUser();
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
