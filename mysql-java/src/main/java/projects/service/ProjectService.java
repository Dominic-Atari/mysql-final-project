package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;


 //This class implements the service layer.
public class ProjectService {
  private ProjectDao projectDao = new ProjectDao();

  
   // This method simply calls the DAO class to insert a project row.
  public Project addProject(Project project) {
    return projectDao.insertProject(project);
  }

  
   //This method calls the project DAO to retrieve all project rows.
  public List<Project> fetchAllProjects() {
    return projectDao.fetchAllProjects();
  }

  
   //This method calls the project DAO to get all project details.
  public Project fetchProjectById(Integer projectId) {
    return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
        "Project with project ID=" + projectId + " does not exist."));
  }
  
  //This method calls the project DAO to modify the project details row in the project table.
  public void modifyProjectDetails(Project project) {
    if(!projectDao.modifyProjectDetails(project)) {
      throw new DbException("Project with ID=" + project.getProjectId() + " does not exist.");
    }
  }

  
   //This method calls the project DAO to delete the project with the given project ID.
  public void deleteProject(Integer projectId) {
    if(!projectDao.deleteProject(projectId)) {
      throw new DbException("Project with ID=" + projectId + " does not exist.");
    }
  }
}
