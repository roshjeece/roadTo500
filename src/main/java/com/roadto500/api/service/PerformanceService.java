package com.roadto500.api.service;

import com.roadto500.api.dto.AftScoreRequestDTO;
import com.roadto500.api.dto.AftScoreResponseDTO;
import com.roadto500.api.dto.GapAnalysisDTO;
import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import com.roadto500.api.service.strategy.RuleBasedPrescriptionStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.roadto500.api.model.PriorityLevel.*;

@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final AftEventRepository aftEventRepository;
    private final SoldierRepository soldierRepository;
    private final AftTestResultRepository aftTestResultRepository;
    private final EventScoreRepository eventScoreRepository;
    private final ImprovementSuggestionRepository improvementSuggestionRepository;
    private final RuleBasedPrescriptionStrategy ruleBasedPrescriptionStrategy;

    public AftScoreResponseDTO submitAftScores(AftScoreRequestDTO aftScoreRequestDTO) {
        int totalScore = 0;
        AftScoreResponseDTO aftScoreResponseDTO = new AftScoreResponseDTO();
        Soldier soldier = soldierRepository.findById(aftScoreRequestDTO.getSoldierId()).orElseThrow();
        AftTestResult aftTestResult = new AftTestResult();
        aftTestResult.setSoldier(soldier);
        aftTestResult.setTestDate(aftScoreRequestDTO.getDateOfTest());
        aftScoreResponseDTO.setName(soldier.getName());
        aftTestResult.setTotalScore(0);
        aftTestResultRepository.save(aftTestResult);

        for (Map.Entry<String, Integer> entry : aftScoreRequestDTO.getEventScore().entrySet()) {
            AftEvent aftEvent = aftEventRepository.findByAbbreviation(entry.getKey()).orElseThrow();
            EventScore eventScore = new EventScore();
            eventScore.setAftEvent(aftEvent);
            eventScore.setAftTestResult(aftTestResult);
            eventScore.setPointsEarned(entry.getValue());
            totalScore += entry.getValue();
            eventScoreRepository.save(eventScore);
        }

        aftTestResult.setTotalScore(totalScore);
        aftTestResultRepository.save(aftTestResult);
        aftScoreResponseDTO.setEventScore(aftScoreRequestDTO.getEventScore());
        aftScoreResponseDTO.setDateOfTest(aftScoreRequestDTO.getDateOfTest());
        updateImprovementSuggestions(soldier.getId());

        return aftScoreResponseDTO;

    }

    public Map<String, Integer> getCurrentScoresForSoldier(Long soldierId) {

        Map<String, Integer> currentScores = new HashMap<>();
        List<String> abbreviations = List.of("MDL", "HRP", "SDC", "PLK", "2MR");

        for (String event : abbreviations) {
            currentScores.put(event, eventScoreRepository
                    .findTopByAftTestResult_Soldier_IdAndAftEvent_AbbreviationOrderByAftTestResult_TestDateDesc(soldierId, event)
                    .map(EventScore::getPointsEarned)
                    .orElse(0));
        }
        return currentScores;

    }

    public void updateImprovementSuggestions(Long soldierId) {
        Soldier soldier = soldierRepository.findById(soldierId).orElseThrow();
        Map<String, Integer> currentScores = getCurrentScoresForSoldier(soldierId);
        GapAnalysisDTO gapAnalysisDTO = ruleBasedPrescriptionStrategy.analyze(currentScores);
        for (String abbreviation : gapAnalysisDTO.getGapPerEvent().keySet()) {
            int gap = gapAnalysisDTO.getGapPerEvent().get(abbreviation);
            ImprovementSuggestion suggestion = improvementSuggestionRepository
                    .findBySoldier_IdAndAftEvent_Abbreviation(soldierId, abbreviation)
                    .orElse(new ImprovementSuggestion());
            suggestion.setSoldier(soldier);
            suggestion.setAftEvent(aftEventRepository.findByAbbreviation(abbreviation).orElseThrow());
            suggestion.setScore(currentScores.get(abbreviation));
            suggestion.setGap(gapAnalysisDTO.getGapPerEvent().get(abbreviation));
            suggestion.setCheckInFrequency(gapAnalysisDTO.getCheckInFrequency().get(abbreviation));
            if(gap > 40) suggestion.setPriorityLevel(HIGH);
            else if(gap > 20) suggestion.setPriorityLevel(MEDIUM);
            else if(gap > 0) suggestion.setPriorityLevel(LOW);
            else suggestion.setPriorityLevel(MAINTENANCE);
            suggestion.setSuggestionDate(LocalDate.now());
            suggestion.setSource(ImprovementGenerationSource.RULE_ENGINE);
            improvementSuggestionRepository.save(suggestion);
        }
    }
}
