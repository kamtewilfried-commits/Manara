package com.manara.crm.Rest;

import com.manara.crm.dto.RegistrationDTO;
import com.manara.crm.mapper.RegistrationMapper;
import com.manara.crm.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/registrations")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
//@CrossOrigin(origins = "https://localhost:4200", allowCredentials = "true")
public class RegistrationApiController {

    @Autowired
    private RegistrationService registrationService;

    @GetMapping("/{id}")
    public RegistrationDTO getRegistrationById(@PathVariable Long id) {
        return RegistrationMapper.toDTO(registrationService.findById(id));
    }

    @GetMapping
    public List<RegistrationDTO> getAllRegistrations() {
        return RegistrationMapper.toDTOList(registrationService.findAll());
    }

    @GetMapping("/parent/{parentId}")
    public List<RegistrationDTO> getRegistrationsByParent(@PathVariable Long parentId) {
        return RegistrationMapper.toDTOList(registrationService.findByParentId(parentId));
    }

    @GetMapping(value = "/animateur/{animateurId}", produces = "application/json")
    public List<RegistrationDTO> getRegistrationsByAnimateur(@PathVariable Long animateurId) {
        return RegistrationMapper.toDTOList(
                registrationService.findByAnimateurId(animateurId)
        );
    }
    @GetMapping("/session/{sessionId}")
    public List<RegistrationDTO> getRegistrationsBySession(@PathVariable Long sessionId) {
        return RegistrationMapper.toDTOList(registrationService.findBySessionId(sessionId));
    }

    @GetMapping("/recent")
    public List<RegistrationDTO> getRecentRegistrations(@RequestParam(defaultValue = "5") int limit) {
        return RegistrationMapper.toDTOList(registrationService.findRecent(limit));
    }


    @GetMapping("/count/active/parent/{parentId}")
    public long countActives(@PathVariable Long parentId) {
        return registrationService.countActives(parentId);
    }

    @GetMapping("/count/pending/parent/{parentId}")
    public long countPending(@PathVariable Long parentId) {
        return registrationService.countPending(parentId);
    }

    @GetMapping("/count/all")
    public long countAll() {
        return registrationService.countAll();
    }

    @GetMapping("/count/pending/all")
    public long countPendingAll() {
        return registrationService.countPendingAll();
    }

    @GetMapping("/count/animateur/{animateurId}")
    public long countByAnimateur(@PathVariable Long animateurId) {
        return registrationService.countByAnimateurId(animateurId);
    }

    @GetMapping("/upcoming/parent/{parentId}")
    public List<RegistrationDTO> getUpcomingByParent(@PathVariable Long parentId) {
        return RegistrationMapper.toDTOList(registrationService.findUpcoming(parentId));
    }

    @PostMapping("/inscrire")
    public RegistrationDTO inscrire(
            @RequestParam Long childId,
            @RequestParam Long sessionId
    ) {
        return RegistrationMapper.toDTO(
                registrationService.inscrire(childId, sessionId)
        );
    }

    @PatchMapping("/{id}/confirm")
    public void confirmer(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        registrationService.confirmer(id, adminId);
    }

    @PatchMapping("/{id}/cancel")
    public void annuler(@PathVariable Long id) {
        registrationService.cancel(id);
    }

    @PatchMapping("/{id}/promote")
    public void promouvoir(
            @PathVariable Long id,
            @RequestParam Long adminId
    ) {
        registrationService.promouvoir(id, adminId);
    }

    @PostMapping("/presences/{sessionId}")
    public void savePresences(
            @PathVariable Long sessionId,
            @RequestBody List<Long> childPresents
    ) {
        registrationService.savePresences(sessionId, childPresents);
    }

    @GetMapping("/planning-events/{parentId}")
    public List<Map<String, Object>> getPlanningEvents(
            @PathVariable Long parentId,
            @RequestParam(required = false) String semaine
    ) {
        return registrationService.getPlanningEvents(parentId, semaine);
    }

    @PatchMapping("/{id}/confirm-animateur")
    public void confirmerByAnimateur(
            @PathVariable Long id,
            @RequestParam Long animateurId
    ) {
        registrationService.confirmerByAnimateur(id, animateurId);
    }
}