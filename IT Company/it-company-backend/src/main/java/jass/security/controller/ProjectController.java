package jass.security.controller;

import jass.security.dto.project.*;
import jass.security.exception.NotFoundException;
import jass.security.model.Account;
import jass.security.model.Project;
import jass.security.model.ProjectManager;
import jass.security.service.interfaces.IAccountService;
import jass.security.service.interfaces.IProjectService;
import jass.security.utils.ObjectMapperUtils;
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


    @Autowired
    public ProjectController(IProjectService projectService, IAccountService accountService) {
        _projectService = projectService;
        _accountService = accountService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('createProject')")
    public ResponseEntity<Project> Create(@RequestBody CreateProjectDto dto){
        var result = _projectService.save(ObjectMapperUtils.map(dto, Project.class));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    @GetMapping
    @PreAuthorize("hasAuthority('getAllProject')")
    public ResponseEntity<List<Project>> GetAll(){
        var result = _projectService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @PatchMapping("{id}/add-sw-engineer")
    @PreAuthorize("hasAuthority('addSwEngineerToProject')")
    public ResponseEntity<?> AddSwEngineerToProject(@RequestBody AddSwEngineerToProjectDto dto, @PathVariable("id") UUID projectId){
        try {
            _projectService.AddSwEngineerToProject(dto, projectId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/add-pr-manager")
    @PreAuthorize("hasAuthority('addPrManagerToProject')")
    public ResponseEntity<?> AddPrManagerToProject(@RequestBody AddProjectMangerToProjectDto dto, @PathVariable("id") UUID projectId){
        try {
            _projectService.AddPrManagerToProject(dto, projectId);
            return new ResponseEntity<>( HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/dismiss-sw-engineer")
    @PreAuthorize("hasAuthority('dismissSwEngineerFromProject')")
    public ResponseEntity<?> DismissSwEngineerFromProject(@RequestBody DismissWorkerFromProjectDto dto, @PathVariable("id") UUID projectId){
        try {
            _projectService.DismissSwEngineerFromProject(dto.getWorkerId(), projectId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("{id}/dismiss-pr-manager")
    @PreAuthorize("hasAuthority('dismissPrManagerFromProject')")
    public ResponseEntity<?> DismissPrManagerFromProject(@RequestBody DismissWorkerFromProjectDto dto, @PathVariable("id") UUID projectId){
        try {
            _projectService.DismissPrManagerFromProject(dto.getWorkerId(), projectId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("{id}/sw-engineers")
    @PreAuthorize("hasAuthority('getSwEngineersOnProject')")
    public ResponseEntity<?> GetSwEngineersOnProject(@PathVariable("id") UUID projectId){
        var result = _projectService.GetSwEngineersOnProject(projectId);
        return new ResponseEntity<>(result ,HttpStatus.OK);
    }

    @GetMapping("{id}/pr-managers")
    @PreAuthorize("hasAuthority('getPrManagersOnProject')")
    public ResponseEntity<?> GetPrManagersOnProject(@PathVariable("id") UUID projectId){
        var result = _projectService.GetPrManagersOnProject(projectId);
        return new ResponseEntity<>(result ,HttpStatus.OK);
    }

    @GetMapping("pr-manager")
    @PreAuthorize("hasAuthority('getPrManagersProjects')")
    public ResponseEntity<?> GetPrManagersProjects(Principal principal){
        String projectManagerEmail = principal.getName();
        Account prManager = _accountService.findByEmail(projectManagerEmail);

        var projects = _projectService.GetPrManagersProjects(prManager.getEmployeeId());
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @GetMapping("sw-engineer")
    @PreAuthorize("hasAuthority('getSwEngineersProjects')")
    public ResponseEntity<?> GetSwEngineersProjects(Principal principal){
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        var projects = _projectService.GetSwEngineersProjects(swEngineer.getEmployeeId());
        return new ResponseEntity<>(projects, HttpStatus.OK);
    }

    @PatchMapping("{id}/sw-engineer")
    @PreAuthorize("hasAuthority('changeSwEngineersJobDescription')")
    public ResponseEntity<?> ChangeSwEngineersJobDescription(Principal principal, @RequestBody SwEngineerChangeJobDescriptionDto dto, @PathVariable("id") UUID projectId){
        String swEngineerEmail = principal.getName();
        Account swEngineer = _accountService.findByEmail(swEngineerEmail);

        try {
            _projectService.ChangeSwEngineersJobDescription(projectId, swEngineer.getEmployeeId(), dto.getNewJobDescription());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}
