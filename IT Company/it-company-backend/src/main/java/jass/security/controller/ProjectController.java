package jass.security.controller;

import jass.security.dto.project.AddSwEngineerToProjectDto;
import jass.security.dto.project.CreateProjectDto;
import jass.security.model.Project;
import jass.security.service.interfaces.IProjectService;
import jass.security.utils.ObjectMapperUtils;
import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("project")
public class ProjectController {

    private final IProjectService _projectService;

    @Autowired
    public ProjectController(IProjectService projectService) {
        _projectService = projectService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('createProject')")
    public ResponseEntity<Project> Create(@RequestBody CreateProjectDto dto){
        var result = _projectService.save(ObjectMapperUtils.map(dto, Project.class));
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }
    codeaction-selected)
    @PreAuthorize("hasAuthority('getAllProject')")
    public ResponseEntity<List<Project>> GetAll(){
        var result = _projectService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }



    @PostMapping("{id}/add-sw-engineer")
    @PreAuthorize("hasAuthority('addSwEngineerToProject')")
    public ResponseEntity<?> AddSwEngineerToProject(@RequestBody AddSwEngineerToProjectDto dto, @PathVariable("id") UUID projectId){
        throw new NotYetImplementedException();
    }

    @PatchMapping("{id}/dismiss-sw-engineer")
    @PreAuthorize("hasAuthority('dismissSwEngineerFromProject')")
    public ResponseEntity<?> DismissSwEngineerFromProject(@RequestBody AddSwEngineerToProjectDto dto, @PathVariable("id") UUID projectId){
        throw new NotYetImplementedException();
    }

    @GetMapping("{id}/sw-engineers")
    @PreAuthorize("hasAuthority('getSwEngineersOnProject')")
    public ResponseEntity<?> GetSwEngineersOnProject(@PathVariable("id") UUID projectId){
        throw new NotYetImplementedException();
    }
}
