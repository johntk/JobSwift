package models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import play.data.format.Formats;
import play.db.ebean.Model;

@Entity
public class JobApplicationModel extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long job_application_id;
	
	public String status;	
	@Formats.DateTime(pattern="dd/MM/yyyy")
	public Date date = new Date();
	
	@ManyToOne
	public ApplicantModel app;
	@ManyToOne
	public JobListingModel job;
	
	// Default Constructor
	public JobApplicationModel() {
	}
	
	// Overloaded Constructor
	public JobApplicationModel(ApplicantModel app, JobListingModel job) {
		this.app = app;
		this.job = job;
		status = "submitted";
		date = Calendar.getInstance().getTime();
	}
	
	public static Finder<Long, JobApplicationModel> find = new Finder<Long, JobApplicationModel>(Long.class, JobApplicationModel.class);
	
	// Find all job applications in the database
	public static List<JobApplicationModel> findAll() {
		return JobApplicationModel.find.all();
	}
	
	// Find a job application listing by it's id (primary key)
	public static JobApplicationModel findById(Long id) {
		return find.where().eq("job_application_id", id).findUnique();
	}
	
	// Find all job applications for a given user
	public static List<JobApplicationModel> findAllApplicationsByUser(ApplicantModel tempApp){
		List<JobApplicationModel> temp = find.where().eq("app.applicant_id", tempApp.applicant_id).findList();
		return temp;
	}
	
	// Find all job applications related to a given job listing
	public static List<JobApplicationModel> findAllApplicationsByJobListing(JobListingModel jlm){
		List<JobApplicationModel> temp = find.where().eq("job.job_id", jlm.job_id).findList();
		return temp;
	}
	
	// Fetch all jobs applied for by a given applicant from the database
	public static List<JobListingModel> findJobsAppliedForByUser(ApplicantModel tempApp) {
		List<JobApplicationModel> temp = new ArrayList<JobApplicationModel>();
		if(tempApp != null) {
			temp = find.where().eq("app.applicant_id", tempApp.applicant_id).findList();
		}
		List<JobListingModel> jobList = new ArrayList<JobListingModel>();
		
		if(temp != null) {
			for(int i=0; i<temp.size();i++) {
				jobList.add(temp.get(i).job);
			}
		}
	    return jobList;
	}
	
	public static List<JobApplicationModel> findAllUnprocessedApplications() {
		List<JobApplicationModel> temp = find.where().eq("status", "submitted").findList();
		return temp;
	}

}
