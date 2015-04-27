package controllers;

import models.*;
import play.mvc.*;

import java.util.*;

import org.mindrot.jbcrypt.BCrypt;

import controllers.Application.Login;
import play.data.Form;

public class ApplicantController extends Controller {
	
	private static final Form<ApplicantModel> appForm = Form.form(ApplicantModel.class);
	private static List<ApplicantModel> tempApps = new ArrayList<ApplicantModel>();
	
	// Method to retrieve all applicants in the database
	@Security.Authenticated(RecruiterSecured.class)
	public static Result listApplicants() {
    	List<ApplicantModel> applicants = ApplicantModel.findAll();
    	return ok(views.html.Applicant.ApplicantList.render(applicants, Form.form(ApplicantController.ApplicantSearch.class)));
    }
	
	// Displays all job applciations in the recruiters dashboard
	@Security.Authenticated(RecruiterSecured.class)
	public static Result listAllJobApplications() {
    	List<JobApplicationModel> jobApps = JobApplicationModel.findAll();
    	return ok(views.html.Recruiter.JobApplicationList.render(jobApps));
    }
    
    public static Result newApplicant() {
    	return ok(views.html.Applicant.ApplicantDetails.render(appForm));
    }
    
    public static Result details(String email) {
    	final ApplicantModel app = ApplicantModel.findByEmail(email);
    	if (app == null) {
    		return notFound(String.format("Applicant %s does not exist.", email));
    	}
    	
    	Form<ApplicantModel> filledForm = appForm.fill(app);
    	return ok(views.html.Applicant.WebsiteApplicantDetails.render(filledForm, Form.form(Login.class)));
    }
    
    public static Result viewApplicantProfile(String email) {
    	ApplicantModel app = ApplicantModel.findByEmail(email);
    	List<JobApplicationModel> appList = JobApplicationModel.findAllApplicationsByUser(app);
    	List<InterviewQuestionModel> iqList = new ArrayList<InterviewQuestionModel>();
    	
    	for(int i=0; i < appList.size(); i++) {
    		if(appList.get(i).interviewDone == 1) {
    			List<InterviewQuestionModel> tempiq = InterviewQuestionModel.findAllQuestionsByJobListing(appList.get(i).job);
    			if(tempiq != null) {
    				for(int j=0; j< tempiq.size(); j++) {
    					iqList.add(tempiq.get(j));
    				}
    			}
    		}
    	}
    	
    	return ok(views.html.Recruiter.ViewApplicantProfile.render(app, appList, iqList));
    }
    
    // Form to update applicant details
    public static Result updateApplicant() {
        Form<ApplicantModel> boundForm = appForm.bindFromRequest();
        if(boundForm.hasErrors())
        {
          flash("error", "Please correct the form below.");
          return badRequest(views.html.Applicant.WebsiteApplicantDetails.render(boundForm, Form.form(Login.class) ));
        }

        ApplicantModel app = boundForm.get();
        app.applicant_password = BCrypt.hashpw(boundForm.get().applicant_password, BCrypt.gensalt());
        
        if(app.applicant_id != null)
        {
        	app.update();
            flash("success", String.format("Successfully updated user %s", app));
            return redirect(routes.Application.applicantProfile());
        } else return redirect(routes.Application.applicantProfile());
    }
    
 // Delete method to delete user from recruiters dashboard
    public static Result deleteUser(String email) {
    	final ApplicantModel app = ApplicantModel.findByEmail(email);
    	if(app == null) {
    		return notFound(String.format("Applicant %s does not exist.", email));
    	}
    	
    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByUser(app);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	app.delete();
    	return redirect(routes.ApplicantController.listApplicants());
    }
    
    // Method to delete user from user profile
    @Security.Authenticated(Secured.class)
    public static Result deleteCurrentUser() {
    	ApplicantModel app = Application.getCurrentUser();
    	Application.logOut();
    	List<JobApplicationModel> jam = JobApplicationModel.findAllApplicationsByUser(app);
    	if(jam != null) {
	    	for(int i=0; i<jam.size(); i++) {
	    		jam.get(i).delete();
	    	}
    	}
    	app.delete();
    	
    	flash("success","Account Deleted");
    	return redirect(routes.Application.index());
    }
    
    // inner class to for applicant search method
    public static class ApplicantSearch {
    	public String applicant_firstName;
    	public String applicant_lastName;
    	public String applicant_city;
    }
    
    public static Result applicantSearchResult() {
		Form<ApplicantSearch> jobForm = Form.form(ApplicantSearch.class).bindFromRequest();
		String firstName = jobForm.get().applicant_firstName;
		String lastName = jobForm.get().applicant_lastName;
		String city = jobForm.get().applicant_city;
		
		tempApps = ApplicantModel.findByForm(firstName, lastName, city);
		
		return ok(views.html.Applicant.ApplicantList.render(tempApps, Form.form(ApplicantController.ApplicantSearch.class)));
	}

}
