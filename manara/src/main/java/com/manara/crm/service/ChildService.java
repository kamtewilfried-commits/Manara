package com.manara.crm.service;

import com.manara.crm.entity.Child;
import com.manara.crm.repos.ChildRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChildService {

    @Autowired private ChildRepository childRepository;

    public Child findById(Long id) {
        return childRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfant introuvable : " + id));
    }

    public List<Child> findByParentId(Long parentId) {
        return childRepository.findByParentId(parentId);
    }

    public long countByParentId(Long parentId) {
        return childRepository.countByParentId(parentId);
    }

    public Child save(Child child) {
        return childRepository.save(child);
    }

    public void deleteById(Long id) {
        childRepository.deleteById(id);
    }

    // Vérifie que l'enfant appartient bien au parent
    public boolean belongsToParent(Long childId, Long parentId) {
        return childRepository.existsByIdAndParentId(childId, parentId);
    }
}