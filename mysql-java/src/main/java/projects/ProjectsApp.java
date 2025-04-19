package projects;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import projects.entity.Project;
import projects.exception.DbException;
import projects.service.ProjectService;

public class ProjectsApp {
	private Scanner scanner = new Scanner(System.in);
	private ProjectService projectService = new ProjectService();
	private Project curProject;

	// @formatter:off
  private List<String> operations = List.of(
      "1) Add a project",
      "2) List projects",
      "3) Select a project",
      "4) Update project details",
      "5) Delete a project"
  );
  // @formatter:on

	public static void main(String[] args) {
		new ProjectsApp().processUserSelections();
	}

	private void processUserSelections() {
		boolean done = false;

		while (!done) {
			try {
				int selection = getUserSelection();

				switch (selection) {
				case -1:
					done = exitMenu();
					break;

				case 1:
					createProject();
					break;

				case 2:
					listProjects();
					break;

				case 3:
					selectProject();
					break;

				case 4:
					updateProjectDetails();
					break;

				case 5:
					deleteProject();
					break;

				default:
					System.out.println("\n" + selection + " is not a valid selection. Try again.");
					break;
				}
			} catch (Exception e) {
				System.out.println("\nError: " + e + " Try again.");
			}
		}
	}

	// This method deletes a project and all project child records
	private void deleteProject() {
		listProjects();

		Integer projectId = getIntInput("Enter the ID of the project to delete");

		projectService.deleteProject(projectId);
		System.out.println("Project " + projectId + " was deleted successfully.");

		if (Objects.nonNull(curProject) && curProject.getProjectId().equals(projectId)) {
			curProject = null;
		}
	}

	// This method allows the user to modify project details.
	private void updateProjectDetails() {
		/* If there is no current project selected, return to the menu. */
		if (Objects.isNull(curProject)) {
			System.out.println("\nPlease select a project.");
			return;
		}

		/*
		 * Collect input from the user. If the user presses Enter without entering a
		 * value, the local variable will be null.
		 */
		String projectName = getStringInput("Enter the project name [" + curProject.getProjectName() + "]");

		BigDecimal estimatedHours = getDecimalInput(
				"Enter the estimated hours [" + curProject.getEstimatedHours() + "]");

		BigDecimal actualHours = getDecimalInput("Enter the actual hours + [" + curProject.getActualHours() + "]");

		Integer difficulty = getIntInput("Enter the project difficulty (1-5) [" + curProject.getDifficulty() + "]");

		String notes = getStringInput("Enter the project notes [" + curProject.getNotes() + "]");

		/*
		 * Create and populate a Project object. Use the value supplied by the user if
		 * the user entered something. If the user did not enter a value, set the value
		 * to that which is in the currently selected project.
		 */
		Project project = new Project();

		project.setProjectId(curProject.getProjectId());
		project.setProjectName(Objects.isNull(projectName) ? curProject.getProjectName() : projectName);

		project.setEstimatedHours(Objects.isNull(estimatedHours) ? curProject.getEstimatedHours() : estimatedHours);

		project.setActualHours(Objects.isNull(actualHours) ? curProject.getActualHours() : actualHours);
		project.setDifficulty(Objects.isNull(difficulty) ? curProject.getDifficulty() : difficulty);
		project.setNotes(Objects.isNull(notes) ? curProject.getNotes() : notes);

		/* Call the project service to update the project details. */
		projectService.modifyProjectDetails(project);

		/* Re-read the current project, which will display the new details. */
		curProject = projectService.fetchProjectById(curProject.getProjectId());
	}

	// This method allows the user to select a "current" project.

	private void selectProject() {
		listProjects();
		Integer projectId = getIntInput("Enter a project ID to select a project");

		// Unselect the current project.

		curProject = null;

		/* This will throw an exception if an invalid project ID is entered. */
		curProject = projectService.fetchProjectById(projectId);
	}

	// This method calls the project service to retrieve a list of projects from the
	// projects table.
	private void listProjects() {
		List<Project> projects = projectService.fetchAllProjects();

		System.out.println("\nProjects:");

		projects.forEach(
				project -> System.out.println("   " + project.getProjectId() + ": " + project.getProjectName()));
	}

	// 2 Gather user input for a project row then call the project service to create
	// the row.

	private void createProject() {
		String projectName = getStringInput("Enter the project name");
		BigDecimal estimatedHours = getDecimalInput("Enter the estimated hours");
		BigDecimal actualHours = getDecimalInput("Enter the actual hours");
		Integer difficulty = getIntInput("Enter the project difficulty (1-5)");
		String notes = getStringInput("Enter the project notes");

		Project project = new Project();

		project.setProjectName(projectName);
		project.setEstimatedHours(estimatedHours);
		project.setActualHours(actualHours);
		project.setDifficulty(difficulty);
		project.setNotes(notes);

		Project dbProject = projectService.addProject(project);
		System.out.println("You have successfully created project: " + dbProject);
	}

	// Gets the user's input from the console and converts it to a BigDecimal.
	private BigDecimal getDecimalInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			// Create the BigDecimal object and set it to two decimal places (the scale). */
			return new BigDecimal(input).setScale(2);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid decimal number.");
		}
	}

	private boolean exitMenu() {
		System.out.println("Exiting the menu.");
		return true;
	}

	
	//This method prints the available menu selections.
	private int getUserSelection() {
		printOperations();

		Integer input = getIntInput("Enter a menu selection");

		return Objects.isNull(input) ? -1 : input;
	}

	
	 //Prints a prompt on the console and then gets the user's input from the console.
	private Integer getIntInput(String prompt) {
		String input = getStringInput(prompt);

		if (Objects.isNull(input)) {
			return null;
		}

		try {
			return Integer.valueOf(input);
		} catch (NumberFormatException e) {
			throw new DbException(input + " is not a valid number.");
		}
	}

	
	 //Prints a prompt on the console and then gets the user's input from the console.
	private String getStringInput(String prompt) {
		System.out.print(prompt + ": ");
		String input = scanner.nextLine();

		return input.isBlank() ? null : input.trim();
	}

	
	 //Print the given line on the console. Indent the line by three spaces.
	private void printWithIndent(String line) {
		System.out.println("   " + line);
	}

	
	 // Print the menu selections, one per line.
	private void printOperations() {
		System.out.println("\nThese are the available selections. Press the Enter key to quit:");

		/* Print with a Lambda expression */
		operations.forEach(line -> printWithIndent(line));

		/*
		 * Print with a method reference. Note that there is no way to use
		 * System.out::println directly and have the line indented. Method references
		 * can be used to replace a simple one-line Lambda expression. They can and
		 * should be used if they provide clarity and conciseness.
		 */
		// operations.forEach(this::printWithIndent);

		/* With enhanced for loop */
		// for(String line : operations) {
		// printWithIndent(line);
		// }

		if (Objects.isNull(curProject)) {
			System.out.println("\nYou are not working with a project.");
		} else {
			System.out.println("\nYou are working with project: " + curProject);
		}
	}
}
