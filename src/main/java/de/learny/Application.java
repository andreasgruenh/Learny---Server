package de.learny;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import de.learny.dataaccess.AccountRepository;
import de.learny.dataaccess.AnswerRepository;
import de.learny.dataaccess.QuestionRepository;
import de.learny.dataaccess.RoleRepository;
import de.learny.dataaccess.SubjectRepository;
import de.learny.dataaccess.TestRepository;
import de.learny.domain.Account;
import de.learny.domain.Answer;
import de.learny.domain.Question;
import de.learny.domain.Role;
import de.learny.domain.Subject;
import de.learny.domain.Test;
import de.learny.security.service.PasswordGeneratorService;
import de.learny.swagger.SwaggerConfig;

/**
 * Acts as a servlet initializer and start class.
 * 
 * @author andi
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class Application extends SpringBootServletInitializer implements CommandLineRunner {

	@Autowired
	AccountRepository accountRepo;

	@Autowired
	RoleRepository roleRepo;

	@Autowired
	TestRepository testRepo;

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	QuestionRepository questionRepo;

	@Autowired
	PasswordGeneratorService passwordGenerator;

	@Autowired
	AnswerRepository answerRepo;

	@Autowired
	private Environment environment;
	
	@Autowired
	private SwaggerConfig swaggerConfig;

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		String[] envVars = environment.getActiveProfiles();
		if (!(envVars.length > 0 && envVars[0].equals("demo"))) {
			Role adminRole = new Role("admin");
			Role dozentRole = new Role("dozent");
			Role userRole = new Role("user");

			roleRepo.save(adminRole);
			roleRepo.save(dozentRole);
			roleRepo.save(userRole);

			Subject sub1 = new Subject("Fach1");
			Subject sub2 = new Subject("Fach2");
			subjectRepo.save(sub1);
			subjectRepo.save(sub2);
			Test test1 = new Test("test1", sub1);
			Test test3 = new Test("test3", sub1);
			Test test2 = new Test("test2", sub2);
			testRepo.save(test1);
			testRepo.save(test3);
			testRepo.save(test2);

			Question quest1 = new Question("frage1", test1);
			questionRepo.save(quest1);
			Answer answer1 = new Answer("antwort1", quest1, true);
			Answer answer2 = new Answer("antwort2", quest1, false);
			answerRepo.save(answer1);
			answerRepo.save(answer2);
			
			Question quest2 = new Question("frage2", test3);
			questionRepo.save(quest2);
			Answer answer3 = new Answer("antwort3", quest2, true);
			Answer answer4 = new Answer("antwort4", quest2, false);
			answerRepo.save(answer3);
			answerRepo.save(answer4);

			Account student = new Account("student", passwordGenerator.hashPassword("student"));
			student.setFirstname("Conrad");
			student.setLastname("Reuter");
			student.setEmail("a@rtline.de");

			Account admin = new Account("admin", passwordGenerator.hashPassword("admin"));
			admin.setFirstname("Andreas");
			admin.setLastname("Roth");
			admin.setEmail("andreas.roth@rtline.de");

			Account dozent = new Account("dozent", passwordGenerator.hashPassword("dozent"));
			dozent.setFirstname("Martin");
			dozent.setLastname("Burwitz");
			dozent.setEmail("art_martinburwitz@rtline.de");

			student.addRole(userRole);
			admin.addRole(adminRole);
			dozent.addRole(dozentRole);

			accountRepo.save(admin);
			accountRepo.save(student);
			accountRepo.save(dozent);

			sub1.addAccountInCharge(dozent);
			subjectRepo.save(sub1);
			student.addJoinedSubject(sub1);
			accountRepo.save(student);
		}
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}
}
