package models;

import javax.persistence.*;

import play.db.ebean.Model;
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
	JobListingModel jlm;
	
	// Default Constructor
	public InterviewQuestionModel() {	
	}
	
	// Default Constructor
	public InterviewQuestionModel(String question) {	
		this.question = question;
	}
}
