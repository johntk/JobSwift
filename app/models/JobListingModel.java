package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;
import play.data.validation.Constraints;

@Entity
public class JobListingModel extends Model{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long job_id;
	
	@Constraints.Required
	public String job_sector;
	@Constraints.Required
	public String job_company;
	@Constraints.Required
	public String job_title;
	@Constraints.Required
	public String job_type;
	@Constraints.Required
	public String job_location;
	public double job_salary;
	@Lob
	@Constraints.Required
	public String job_description;
	@Lob
	@Constraints.Required
	public String job_criteria;
	
	@OneToMany
	public List<JobApplicationModel> applicationList;
	
	@Constraints.Required
	public List<String> questions;
	
	@OneToMany
	public List<InterviewQuestionModel> interviewQuestions;
	
	// Default Constructor
	public JobListingModel(){}
	
	// Overloaded Constructor
	public JobListingModel(String job_sector, String job_company, String job_title, String job_type, String job_location, double job_salary, String job_description, String job_criteria)
	{
		this.job_sector = job_sector;
		this.job_company = job_company;
		this.job_title = job_title;
		this.job_type = job_type;
		this.job_location = job_location;
		this.job_salary = job_salary;
		this.job_description = job_description;
		this.job_criteria = job_criteria;
	}
	
	public String toString() {
		return String.format("%s", job_title);
	}
	
	public static Finder<Long, JobListingModel> find = new Finder<Long, JobListingModel>(Long.class, JobListingModel.class);
	
	// Find a job listing by it's id (primary key)
	public static JobListingModel findById(Long id) {
		return find.where().eq("job_id", id).findUnique();
	}

	// Query database for all job listings
	public static List<JobListingModel> findAll() {
		return JobListingModel.find.all();
	}
	
	// Query database for job listings based on search values
	public static List<JobListingModel> findByForm(String location, String sector, String jobType) {
		List<JobListingModel> tempJobs = new ArrayList<JobListingModel>();
		if(location != null && sector != null && jobType != null) {
			
			tempJobs = find.where().ilike("job_location", location).ilike("job_sector", sector).ilike("job_type", jobType).findList();
		}
		return tempJobs;
	}
}
