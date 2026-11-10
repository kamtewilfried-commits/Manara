package com.manara.crm.Rest;

import com.manara.crm.dto.ChildDTO;
import com.manara.crm.entity.Child;
import com.manara.crm.mapper.ChildMapper;
import com.manara.crm.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/children")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class ChildApiController {

    @Autowired
    private ChildService childService;

    @GetMapping("/{id}")
    public ChildDTO getChildById(@PathVariable Long id) {
        return ChildMapper.toDTO(childService.findById(id));
    }

    @GetMapping("/parent/{parentId}")
    public List<ChildDTO> getChildrenByParentId(@PathVariable Long parentId) {
        return ChildMapper.toDTOList(childService.findByParentId(parentId));
    }

    @GetMapping("/count/parent/{parentId}")
    public long countChildrenByParentId(@PathVariable Long parentId) {
        return childService.countByParentId(parentId);
    }

    @PostMapping
    public ChildDTO createChild(@RequestBody Child child) {
        Child savedChild = childService.save(child);
        return ChildMapper.toDTO(savedChild);
    }

    @PutMapping("/{id}")
    public ChildDTO updateChild(@PathVariable Long id, @RequestBody Child child) {
        Child existingChild = childService.findById(id);

        existingChild.setFirstName(child.getFirstName());
        existingChild.setLastName(child.getLastName());
        existingChild.setBirthDate(child.getBirthDate());
        existingChild.setParent(child.getParent());
        existingChild.setAllergies(child.getAllergies());
        existingChild.setMedicalNotes(child.getMedicalNotes());

        Child updatedChild = childService.save(existingChild);
        return ChildMapper.toDTO(updatedChild);
    }

    @DeleteMapping("/{id}")
    public void deleteChild(@PathVariable Long id) {
        childService.deleteById(id);
    }

    @GetMapping("/{childId}/belongs-to-parent/{parentId}")
    public boolean belongsToParent(@PathVariable Long childId, @PathVariable Long parentId) {
        return childService.belongsToParent(childId, parentId);
    }
}