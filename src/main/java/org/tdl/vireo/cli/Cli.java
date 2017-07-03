package org.tdl.vireo.cli;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.tdl.vireo.config.constant.ConfigurationName;
import org.tdl.vireo.controller.SubmissionController;
import org.tdl.vireo.enums.AppRole;
import org.tdl.vireo.model.Configuration;
import org.tdl.vireo.model.FieldPredicate;
import org.tdl.vireo.model.FieldValue;
import org.tdl.vireo.model.Organization;
import org.tdl.vireo.model.Submission;
import org.tdl.vireo.model.SubmissionFieldProfile;
import org.tdl.vireo.model.SubmissionState;
import org.tdl.vireo.model.SubmissionWorkflowStep;
import org.tdl.vireo.model.User;
import org.tdl.vireo.model.repo.ConfigurationRepo;
import org.tdl.vireo.model.repo.FieldValueRepo;
import org.tdl.vireo.model.repo.OrganizationRepo;
import org.tdl.vireo.model.repo.SubmissionRepo;
import org.tdl.vireo.model.repo.SubmissionStateRepo;
import org.tdl.vireo.model.repo.UserRepo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import edu.tamu.framework.model.Credentials;


/*
 * A command line runner for Vireo development. Accessed via: 
 * $ mvn -Drun.arguments="console" spring-boot:run
 * 
 */
@Component
public class Cli implements CommandLineRunner {

	@Autowired
	SubmissionRepo submissionRepo;

	@Autowired
	UserRepo userRepo;

	@Autowired
	OrganizationRepo organizationRepo;

	@Autowired
	SubmissionStateRepo submissionStateRepo;

	@Autowired
	FieldValueRepo fieldValueRepo;
	
	@Autowired
	ConfigurationRepo configurationRepo;
	
	@Override
	public void run(String... arg0) throws Exception {
		boolean runConsole = false;
		for(String s : arg0) {
			if(s.equals("console")) {
					runConsole = true;
					break;
			}
		}

		Boolean running = runConsole ? true : false ;

		if(running)
		{
			final String PROMPT = "\n"+(char)27 + "[36mvireo>"+(char)27 + "[37m ";

			Scanner reader = new Scanner(System.in);  // Reading from System.in

			System.out.print(PROMPT);

			int itemsGenerated = 0;

			while(running && reader.hasNextLine()) {

				String n = reader.nextLine();

				n=n.trim();

				String[] commandTokens = n.split("\\s+");
				List<String> commandArgs = new ArrayList<String>();

				String command = null;
				int num = 0;

				if(commandTokens.length > 0)
					command = commandTokens[0];
				if(commandTokens.length > 1) {
					for(int i = 1; i < commandTokens.length; i++) {
						commandArgs.add(commandTokens[i]);
					}

				}


				switch (command) {
					case "exit":
						System.out.println("\nGoodbye.");
						running=false;
						break;

					case "generate":

						Organization org = organizationRepo.findAll().get(0);

						SubmissionState state = submissionStateRepo.findAll().get(0);
						
						if(commandArgs.size() > 0 ) {
							try {
								num = Integer.parseInt(commandArgs.get(0));
							} catch (Exception e) {
								System.err.println("unable to parse as a number of items: " + commandArgs.get(0));
							}
						}


						for(int i = itemsGenerated; i < num + itemsGenerated; i++) {
							String email = "bob" + (i+1) + "@boring.bob";
							AppRole role = AppRole.STUDENT;

							// get shib headers out of DB
							String netIdHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_NETID, "netid");
							String firstNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_FIRST_NAME, "firstName");
							String lastNameHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_LAST_NAME, "lastName");
							String emailHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_EMAIL, "email");
							String institutionalIdentifierHeader = configurationRepo.getValue(ConfigurationName.APPLICATION_AUTH_SHIB_ATTRIBUTE_INSTITUTIONAL_IDENTIFIER, "uin");
							
							Map<String, String> allCredentials = new HashMap<String, String>();
							
							allCredentials.put(firstNameHeader, "Bob");
							allCredentials.put(lastNameHeader, "Boring");
							allCredentials.put(emailHeader, email);
							allCredentials.put("role", role.toString());
							allCredentials.put(institutionalIdentifierHeader, "12345678"+i);
							allCredentials.put(netIdHeader,"bboring"+i);
							
							Credentials credentials = new Credentials(allCredentials);
							
							User submitter = userRepo.create(credentials.getEmail(), credentials.getFirstName(), credentials.getLastName(), role);
							
							submitter.setNetid(credentials.getNetid());
							submitter.setUin(credentials.getUin());
							userRepo.saveAndFlush(submitter);
							
							Submission sub = submissionRepo.create(userRepo.findByEmail(credentials.getEmail()), org, state, credentials);

							for(SubmissionWorkflowStep step : sub.getSubmissionWorkflowSteps() ) {
								for(SubmissionFieldProfile fp : step.getAggregateFieldProfiles()) {
									Configuration mappedShibAttribute = fp.getMappedShibAttribute();
									FieldPredicate pred = fp.getFieldPredicate();
									if(! pred.getDocumentTypePredicate() && mappedShibAttribute == null) {
										FieldValue val = fieldValueRepo.create(pred);
										val.setValue("test value " + i);
										fp.addFieldValue(val);
									}
								}
							}
							submissionRepo.saveAndFlush(sub);
							
							System.out.print("\r" + (i-itemsGenerated) + " of " + num + " generated...");

						}

						System.out.println("\rGenerated " + num + " submissions.");
						itemsGenerated += num;
						break;


					case "":
						break;

					default:
						System.out.println("Unknown command " + n);
					}

				if(running)
					System.out.print(PROMPT);

			}
			reader.close();

		}
	}
}
