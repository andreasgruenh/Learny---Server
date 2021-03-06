package de.learny.domain;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import de.learny.JsonView.View;

@Entity
public class Test {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private String name;

	@ManyToOne
	private Subject subject;

	@ManyToMany(mappedBy = "requiredForTests")
	private Set<Test> requiredTests = new HashSet<Test>();

	@ManyToMany
	private Set<Test> requiredForTests = new HashSet<Test>();

	@JsonView(View.Summary.class)
	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Question> questions = new HashSet<Question>();

	@OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TestScore> testScores = new HashSet<TestScore>();

	public Test(String name, Subject subject) {
		this.name = name;
		this.subject = subject;
	}

	public Test() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	public long getId() {
		return id;
	}

	public Set<Test> getRequiredTests() {
		return requiredTests;
	}
	
	@JsonIgnore
	public Set<Test> getRequiredForTests() {
		return requiredForTests;
	}

	@JsonIgnore
	public Set<Question> getQuestions() {
		return questions;
	}
	
	@JsonProperty
	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}

	@JsonIgnore
	public Set<TestScore> getTestScores() {
		return Collections.unmodifiableSet(testScores);
	}
	
	public boolean addQuestion(Question quest) {
		this.getQuestions().add(quest);
		if(quest.getTest() != this) {
			quest.setTest(this);
		}
		return true;
	}
	
	public boolean removeQuestion(Question quest) {
		this.getQuestions().remove(quest);
		if(quest.getTest() == this) {
			quest.setTest(null);
		}
		return true;
	}
	
	public boolean addTestScore(TestScore testScore) {
		this.testScores.add(testScore);
		if(!testScore.getTest().equals(this)) {
			testScore.setTest(this);;
		}
		return true;
	}
	
	public boolean removeTestScore(TestScore testScore) {
		this.testScores.remove(testScore);
		if(testScore.getTest().equals(this)) {
			testScore.setTest(null);
		}
		return true;
	}

}
