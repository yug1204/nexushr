package com.nexushr.recruitment.repository;

import com.nexushr.recruitment.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, String> {

    Optional<Candidate> findByEmail(String email);

    List<Candidate> findByRequisitionIdOrderByAiMatchScoreDesc(String requisitionId);

    List<Candidate> findByPipelineStageOrderByCreatedAtDesc(Candidate.PipelineStage stage);

    @Query("SELECT c.pipelineStage, COUNT(c) FROM Candidate c WHERE c.deleted = false GROUP BY c.pipelineStage")
    List<Object[]> getPipelineDistribution();

    @Query("SELECT c.source, COUNT(c) FROM Candidate c WHERE c.deleted = false GROUP BY c.source")
    List<Object[]> getSourceDistribution();

    @Query("SELECT c FROM Candidate c WHERE c.deleted = false ORDER BY c.aiMatchScore DESC")
    List<Candidate> findTopCandidatesByAiScore();
}
