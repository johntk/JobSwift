package models;

import java.util.List;

import javax.persistence.*;

import play.db.ebean.Model;
import play.db.ebean.Model.Finder;
import play.data.validation.Constraints;

@Entity
public class InterviewQuestionModel extends Model {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	public Long question_id;
	@Lob
	@Constraints.Required
	public String question;

	@ManyToOne
	public JobListingModel job;
	
	// Default Constructor
	public InterviewQuestionModel() {	
	}
	
	// Overloaded Constructor
	public InterviewQuestionModel(JobListingModel job, String question) {	
		this.job = job;
		this.question = question;
	}
	
	public static Finder<Long, InterviewQuestionModel> find = new Finder<Long, InterviewQuestionModel>(Long.class, InterviewQuestionModel.class);
	
	public static List<InterviewQuestionModel> findAllQuestionsByJobListing(JobListingModel jlm){
		List<InterviewQuestionModel> temp = find.where().eq("job.job_id", jlm.job_id).findList();
		return temp;
	}
}
