package de.learny.domain;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TestScore {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@ManyToMany
	private Set<Answer> checkedAnswers = new HashSet<Answer>();

	@ManyToMany
	private Set<Answer> uncheckedAnswers = new HashSet<Answer>();

	@ManyToOne
	private Account account;

	@ManyToOne
	private Test test;

	private Timestamp timestamp;
	
	private int score;

	public TestScore(Test test, Account account, Set<Answer> checkedAnswers) {
		setTest(test);
		this.account = account;
		this.checkedAnswers = checkedAnswers;
		uncheckedAnswers = calculateUncheckedAnswers(checkedAnswers, test);
		java.util.Date currentDate = new java.util.Date();
		this.timestamp = new Timestamp(currentDate.getTime());
	}

	public TestScore() {
		// TODO Auto-generated constructor stub
	}

	// @JsonIgnore
	public Set<Answer> getCheckedAnswers() {
		return checkedAnswers;
	}

	public Set<Answer> getUncheckedAnswers() {
		return uncheckedAnswers;
	}

	public void setCheckedAnswers(Set<Answer> answers) {
		this.checkedAnswers = answers;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	@JsonIgnore
	public Test getTest() {
		return test;
	}

	public void setTest(Test test) {
		this.test = test;
		if (!this.test.getTestScores().contains(this)) {
			test.addTestScore(this);
		}
	}

	public long getId() {
		return id;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	private Set<Answer> calculateUncheckedAnswers(Set<Answer> checkedAnswers, Test test) {
		Set<Answer> resultSet = new HashSet<>();
		test.getQuestions().forEach(question -> {
			question.getAnswers().forEach(answer -> {
				if (!isInCheckedAnswers(answer)) {
					resultSet.add(answer);
				}
			});
		});
		return resultSet;
	}

	private boolean isInCheckedAnswers(Answer answer) {
		boolean result = false;
		for (Answer checkedAnswer : checkedAnswers) {
			if (answer.getId() == checkedAnswer.getId()) {
				result = true;
			}
		}
		return result;
	}

}
