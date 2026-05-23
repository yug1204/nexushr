package com.nexushr.recruitment.service;

import com.nexushr.common.exception.BusinessException;
import com.nexushr.recruitment.model.Candidate;
import com.nexushr.recruitment.model.JobRequisition;
import com.nexushr.recruitment.repository.CandidateRepository;
import com.nexushr.recruitment.repository.JobRequisitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final JobRequisitionRepository requisitionRepo;
    private final CandidateRepository candidateRepo;

    // ========== JOB REQUISITIONS ==========

    @Transactional
    public JobRequisition createRequisition(JobRequisition req) {
        req.setStatus(JobRequisition.RequisitionStatus.DRAFT);
        req.setTenantId("default");
        return requisitionRepo.save(req);
    }

    @Transactional
    public JobRequisition submitForApproval(String reqId) {
        JobRequisition req = requisitionRepo.findById(reqId)
                .orElseThrow(() -> new BusinessException("Requisition not found: " + reqId));
        if (req.getStatus() != JobRequisition.RequisitionStatus.DRAFT) {
            throw new BusinessException("Can only submit DRAFT requisitions");
        }
        req.setStatus(JobRequisition.RequisitionStatus.PENDING_APPROVAL);
        return requisitionRepo.save(req);
    }

    @Transactional
    public JobRequisition approveRequisition(String reqId, String approvedBy) {
        JobRequisition req = requisitionRepo.findById(reqId)
                .orElseThrow(() -> new BusinessException("Requisition not found: " + reqId));
        req.setStatus(JobRequisition.RequisitionStatus.OPEN);
        req.setApprovedBy(approvedBy);
        req.setApprovedAt(LocalDate.now());
        log.info("Requisition {} approved by {} — now OPEN for applications", reqId, approvedBy);
        return requisitionRepo.save(req);
    }

    public List<JobRequisition> getOpenRequisitions() {
        return requisitionRepo.findByStatusOrderByCreatedAtDesc(JobRequisition.RequisitionStatus.OPEN);
    }

    public List<JobRequisition> getByDepartment(String department) {
        return requisitionRepo.findByDepartmentOrderByCreatedAtDesc(department);
    }

    // ========== CANDIDATES ==========

    @Transactional
    public Candidate applyForJob(String requisitionId, Candidate candidate) {
        JobRequisition req = requisitionRepo.findById(requisitionId)
                .orElseThrow(() -> new BusinessException("Requisition not found"));

        if (req.getStatus() != JobRequisition.RequisitionStatus.OPEN) {
            throw new BusinessException("Position is not open for applications");
        }

        candidate.setRequisition(req);
        candidate.setPipelineStage(Candidate.PipelineStage.APPLIED);
        candidate.setTenantId("default");

        // AI match scoring (simplified — production uses NLP similarity)
        candidate.setAiMatchScore(calculateMatchScore(candidate, req));

        log.info("Candidate {} applied for {} — AI match score: {}",
                candidate.getEmail(), req.getTitle(), candidate.getAiMatchScore());

        return candidateRepo.save(candidate);
    }

    @Transactional
    public Candidate advanceCandidate(String candidateId, Candidate.PipelineStage nextStage) {
        Candidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new BusinessException("Candidate not found: " + candidateId));

        validateStageTransition(c.getPipelineStage(), nextStage);
        c.setPipelineStage(nextStage);

        if (nextStage == Candidate.PipelineStage.OFFER_SENT) {
            c.setOfferDate(LocalDate.now());
        }

        log.info("Candidate {} advanced to stage: {}", c.getEmail(), nextStage);
        return candidateRepo.save(c);
    }

    @Transactional
    public Candidate rejectCandidate(String candidateId, String reason) {
        Candidate c = candidateRepo.findById(candidateId)
                .orElseThrow(() -> new BusinessException("Candidate not found"));
        c.setPipelineStage(Candidate.PipelineStage.REJECTED);
        c.setRejectionReason(reason);
        return candidateRepo.save(c);
    }

    public List<Candidate> getCandidatesForRequisition(String requisitionId) {
        return candidateRepo.findByRequisitionIdOrderByAiMatchScoreDesc(requisitionId);
    }

    public Map<String, Object> getRecruitmentDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        dashboard.put("pipelineDistribution", candidateRepo.getPipelineDistribution());
        dashboard.put("sourceDistribution", candidateRepo.getSourceDistribution());
        dashboard.put("openPositions", requisitionRepo.getOpenPositionsByDepartment());
        dashboard.put("topCandidates", candidateRepo.findTopCandidatesByAiScore().stream().limit(10).toList());
        return dashboard;
    }

    // ========== HELPERS ==========

    private int calculateMatchScore(Candidate candidate, JobRequisition req) {
        int score = 50; // Base score
        if (candidate.getTotalExperience() != null && req.getMinExperience() != null) {
            if (candidate.getTotalExperience() >= req.getMinExperience()) score += 20;
        }
        if (candidate.getSkills() != null && req.getRequiredSkills() != null) {
            String[] reqSkills = req.getRequiredSkills().toLowerCase().split(",");
            String candidateSkills = candidate.getSkills().toLowerCase();
            int matched = 0;
            for (String skill : reqSkills) {
                if (candidateSkills.contains(skill.trim())) matched++;
            }
            score += (int) ((double) matched / reqSkills.length * 30);
        }
        return Math.min(score, 100);
    }

    private void validateStageTransition(Candidate.PipelineStage current, Candidate.PipelineStage next) {
        // Define valid transitions
        Map<Candidate.PipelineStage, Set<Candidate.PipelineStage>> valid = Map.of(
            Candidate.PipelineStage.APPLIED, Set.of(Candidate.PipelineStage.SCREENING, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.SCREENING, Set.of(Candidate.PipelineStage.PHONE_SCREEN, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.PHONE_SCREEN, Set.of(Candidate.PipelineStage.TECHNICAL_ROUND, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.TECHNICAL_ROUND, Set.of(Candidate.PipelineStage.HIRING_MANAGER_ROUND, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.HIRING_MANAGER_ROUND, Set.of(Candidate.PipelineStage.HR_ROUND, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.HR_ROUND, Set.of(Candidate.PipelineStage.OFFER_PENDING, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.OFFER_PENDING, Set.of(Candidate.PipelineStage.OFFER_SENT, Candidate.PipelineStage.REJECTED),
            Candidate.PipelineStage.OFFER_SENT, Set.of(Candidate.PipelineStage.OFFER_ACCEPTED, Candidate.PipelineStage.OFFER_DECLINED)
        );

        if (!valid.getOrDefault(current, Set.of()).contains(next)) {
            throw new BusinessException("Invalid stage transition: " + current + " → " + next);
        }
    }
}
