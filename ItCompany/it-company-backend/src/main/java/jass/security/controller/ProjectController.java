package jass.security.controller;

import jakarta.servlet.http.HttpServletRequest;
import jass.security.dto.project.*;
import jass.security.exception.InvalidDateException;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Project;
import jass.security.service.implementations.ProjectService;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IProjectService;
import jass.security.utils.IPUtils;
import jass.security.utils.ObjectMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("project")
public class ProjectController {

    private final IProjectService _projectService;
    private final IAccountService _accountService;
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    public ProjectController(IProjectService projectService, IAccountService accountService) {
        _projectService = projectService;
        _accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('createProject')")
    public ResponseEntity<Project> Create(@RequestBody CreateProjectDto dto) {
        var result = _projectService.save(ObjectMapperUtils.map(dto, Project.class));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('getAllProject')")
    public ResponseEntity<List<Project>> GetAll() {
        var result = _projectService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("{id}/update-project")
    @PreAuthorize("hasAuthority('updateProjectInfo')")
    public ResponseEntity<?> updateProject(@RequestBody UpdateProjectDto dto, @PathVariable("id") UUID projectId,
                                           HttpServletRequest request) {
        try {
            var updatedProjectInfo = _projectService.update(projectId, dto);
            logger.info("User successfully updated a project with an ID: " + projectId + ", from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(
                    ObjectMapperUtils.map(updatedProjectInfo, UpdateProjectDto.class),
                    HttpStatus.OK
            );
        } catch (NotFoundException e) {
            logger.warn("User failed to update a project, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: project with an ID: " + projectId + " does not exist");

            return new ResponseEntity<>(
                    "cannot find project info",
                    HttpStatus.NOT_FOUND
            );
        } catch (InvalidDateException e) {
            logger.warn("User failed to update a project, from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: project end date must be after it's start date");

            return new ResponseEntity<>(
                    "Project end date must be after it's start date",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @PatchMapping("{id}/add-sw-engineer")
    @PreAuthorize("hasAuthority('addSwEngineerToProject')")
    public ResponseEntity<?> AddSwEngineerToProject(@RequestBody AddSwEngineerToProjectDto dto,
                                                    @PathVariable("id") UUID projectId,
                                                    HttpServletRequest request) {
        try {
            _projectService.AddSwEngineerToProject(dto, projectId);
            logger.info("User successfully added an engineer with an ID: " + dto.getSwEngineerId() +
                    " to project with an ID: " + projectId + ", from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to add an engineer to a project with an ID: " + projectId + ", from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: the project does not exist");

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/add-pr-manager")
    @PreAuthorize("hasAuthority('addPrManagerToProject')")
    public ResponseEntity<?> AddPrManagerToProject(@RequestBody AddProjectMangerToProjectDto dto,
                                                   @PathVariable("id") UUID projectId,
                                                   HttpServletRequest request) {
        try {
            _projectService.AddPrManagerToProject(dto, projectId);
            logger.info("User successfully added a project manager with an ID: " + dto.getPrManagerId() +
                    " to project with an ID: " + projectId + ", from IP: " +
                    IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to remove a project manager from a project with an ID: " + projectId + ", from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: the project does not exist");

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/dismiss-sw-engineer")
    @PreAuthorize("hasAuthority('dismissSwEngineerFromProject')")
    public ResponseEntity<?> DismissSwEngineerFromProject(@RequestBody DismissWorkerFromProjectDto dto, @PathVariable("id") UUID projectId,
                                                          HttpServletRequest request) {
        try {
            _projectService.DismissSwEngineerFromProject(dto.getWorkerId(), projectId);
            logger.info("User successfully dismissed an engineer with an ID: " + dto.getWorkerId() +
                            ", from a project with an ID: " + projectId +
                            ", from IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to dismiss an engineer with an ID: " + dto.getWorkerId() +
                    ", from a project with an ID: " + projectId +
                    ", from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: the project does not exist");

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/dismiss-pr-manager")
    @PreAuthorize("hasAuthority('dismissPrManagerFromProject')")
    public ResponseEntity<?> DismissPrManagerFromProject(@RequestBody DismissWorkerFromProjectDto dto,
                                                         @PathVariable("id") UUID projectId,
                                                         HttpServletRequest request) {
        try {
            _projectService.DismissPrManagerFromProject(dto.getWorkerId(), projectId);
            logger.info("User successfully dismissed a project manager with an ID: " + dto.getWorkerId() +
                    ", from a project with an ID: " + projectId +
                    ", from IP: " + IPUtils.getIPAddressFromHttpRequest(request));

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to dismiss a project manager with an ID: " + dto.getWorkerId() +
                            ", from a project with an ID: " + projectId +
                            ", from IP: " + IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: the project does not exist");

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/sw-engineers")
    @PreAuthorize("hasAuthority('getSwEngineersOnProject')")
    public ResponseEntity<?> GetSwEngineersOnProject(@PathVariable("id") UUID projectId) {
        var result = _projectService.GetSwEngineersOnProject(projectId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("{id}/pr-managers")
    @PreAuthorize("hasAuthority('getPrManagersOnProject')")
    public ResponseEntity<?> GetPrManagersOnProject(@PathVariable("id") UUID projectId) {
        var result = _projectService.GetPrManagersOnProject(projectId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("pr-manager")
    @PreAuthorize("hasAuthority('getPrManagersProjects')")
    public ResponseEntity<?> GetPrManagersProjects(Principal principal) throws NotFoundException {
        String projectManagerEmail = principal.getName();
        Account prManager = _accountService.findByEmail(projectManagerEmail);

        var projects = _projectService.GetPrManagersProjects(prManager.getEmployeeId());
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("pr-manager/{id}")
    @PreAuthorize("hasAuthority('getPrManagersProjectsById')")
    public ResponseEntity<?> GetPrManagersProjectsById(@PathVariable("id") UUID managerId) {
        var projects = _projectService.GetPrManagersProjects(managerId);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }


    @GetMapping("sw-engineer")
    @PreAuthorize("hasAuthority('getSwEngineersProjects')")
    public ResponseEntity<?> GetSwEngineersProjects(Principal principal) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }
        var projects = _projectService.GetSwEngineersProjects(swEngineer.getEmployeeId());
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("sw-engineer/{id}")
    @PreAuthorize("hasAuthority('getSwEngineersProjectsById')")
    public ResponseEntity<?> GetSwEngineersProjectsById(@PathVariable("id") UUID swEngineerId) {
        var projects = _projectService.GetSwEngineersProjects(swEngineerId);
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }


    @PatchMapping("{id}/sw-engineer")
    @PreAuthorize("hasAuthority('changeSwEngineersJobDescription')")
    public ResponseEntity<?> ChangeSwEngineersJobDescription(Principal principal,
                                                             @RequestBody SwEngineerChangeJobDescriptionDto dto,
                                                             @PathVariable("id") UUID projectId,
                                                             HttpServletRequest request) {
        String swEngineerEmail = principal.getName();
        Account swEngineer = null;
        try {
            swEngineer = _accountService.findByEmail(swEngineerEmail);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("This account does not exist!");
        }

        try {
            _projectService.ChangeSwEngineersJobDescription(projectId, swEngineer.getEmployeeId(), dto.getNewJobDescription());
            logger.warn("User with an account ID: " + swEngineer.getId() + ", from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request) +
                    " successfully changed their job description on a project with an ID: " +
                    projectId);

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            logger.warn("User failed to change their job description, from IP: " +
                            IPUtils.getIPAddressFromHttpRequest(request) +
                    " reason: an engineer with a given email does not exist");

            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
