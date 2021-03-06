package de.learny.controller;

import io.swagger.annotations.Api;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import de.learny.controller.exception.NotEnoughPermissionsException;
import de.learny.controller.exception.ResourceNotFoundException;
import de.learny.dataaccess.AccountRepository;
import de.learny.dataaccess.SubjectRepository;
import de.learny.dataaccess.TestRepository;
import de.learny.domain.Account;
import de.learny.domain.Answer;
import de.learny.domain.Question;
import de.learny.domain.Subject;
import de.learny.domain.Test;
import de.learny.security.service.LoggedInAccountService;

@Api(value = "Subjects", description = "Fächer verwalten. Ein- und austragen aus Fächern. Tests von einem Fach einsehen", produces = "application/json")
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

	@Autowired
	private SubjectRepository subjectRepo;
	
	@Autowired
	private AccountRepository accountRepo;
	
	@Autowired
	private TestRepository testRepo;
	
	@Autowired
	private LoggedInAccountService userToAccountService;

	@RequestMapping(method = RequestMethod.GET)
	Iterable<Subject> getAllSubject() {
		return subjectRepo.findAll();
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	Subject getSubject(@PathVariable("id") long id) {
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		return subject;
	}

	@RequestMapping(value = "/{id}/tests", method = RequestMethod.GET)
	Iterable<Test> getAllTestsForSubject(@PathVariable("id") long id) {
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		return subject.getTests();
	}
	
	@RequestMapping(value = "/{id}/tests", method = RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE})
	boolean addTestForSubject(@PathVariable("id") long id, @RequestBody Test test) {
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		boolean var = false;
		if(permitted(id)){
			Set<Question> questions = test.getQuestions();
			for(Question question: questions) {
				question.setTest(test);
				Set<Answer> answers = question.getAnswers();
				for(Answer answer: answers) {
					answer.setQuestion(question);
				}
			}
			var = subject.addTest(test);
			subjectRepo.save(subject);
		}
		return var;
	}
	
	@RequestMapping(method = RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE})
	long create(@RequestBody Subject subject){
		Account loggedInAccount = userToAccountService.getLoggedInAccount();
		if (!loggedInAccount.hasRole("admin")) {
			throw new NotEnoughPermissionsException("Nicht genug Rechte, um das auszuführen.");
		}
		Subject saved = subjectRepo.save(subject);
		return saved.getId();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void delete(@PathVariable("id") long id){
		//Übeprüft ob Subject vorhaden
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException(
					"Ein Fach mit dieser id existiert nicht");
		if (permitted(id)) {
			this.subjectRepo.delete(id);
		}
	}
	
	// Überprüft ob Account Admin oder verantwortlich für Subject
	private boolean permitted(long id){
		Subject subject = subjectRepo.findById(id);
		Account loggedInAccount = userToAccountService.getLoggedInAccount();
		boolean inCharge = false;
		inCharge = subject.getAccountsInCharge().contains(loggedInAccount);
		if (inCharge || loggedInAccount.hasRole("admin")) {
			return true;
		} else {
			throw new NotEnoughPermissionsException("Nicht genug Rechte, um das auszuführen.");
		}
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT, consumes={MediaType.APPLICATION_JSON_VALUE})
	Subject update(@PathVariable("id") long id, @RequestBody Subject updateSubject){
		Subject oldSubject = subjectRepo.findById(id);
		if (oldSubject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		if (permitted(id)) {
			oldSubject.setName(updateSubject.getName());
			oldSubject.setDescription(updateSubject.getDescription());
		}
		return this.subjectRepo.save(oldSubject);
	}
	
	@RequestMapping(value = "/{id}/responsibles", method = RequestMethod.GET)
	Iterable<Account> getResponsibles(@PathVariable("id") long id) {
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		return subject.getAccountsInCharge();
	}
	
	@RequestMapping(value = "/{id}/responsibles", method = RequestMethod.POST, consumes={MediaType.APPLICATION_JSON_VALUE})
	boolean addResponsible(@PathVariable("id") long id, @RequestBody Account account) {
		Account newResponsible = accountRepo.findFirstByAccountName(account.getAccountName());
		Subject subject = subjectRepo.findById(id);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		boolean var = false;
		if(permitted(id)){
			var = subject.addAccountInCharge(newResponsible);
			subjectRepo.save(subject);
		}
		return var;
	}
	
	@RequestMapping(value = "{subjectId}/responsibles/{userId}", method = RequestMethod.DELETE)
	boolean removeResponsible(@PathVariable("subjectId") long subjectId, @PathVariable("userId") long userId){
		if (!permitted(subjectId)) {
			throw new NotEnoughPermissionsException("Nicht genug Rechte, um das auszuführen.");
		}
		
		Account toRemoveAccount = accountRepo.findById(userId);
		if (toRemoveAccount == null)
			throw new ResourceNotFoundException("Ein Account mit dieser id existiert nicht");
		
		Subject subject = subjectRepo.findById(subjectId);
		if (subject == null)
			throw new ResourceNotFoundException("Ein Fach mit dieser id existiert nicht");
		boolean var = subject.removeAccountInCharge(toRemoveAccount);
		subjectRepo.save(subject);
		return var;
	}
}
