package com.roadto500.api.service;

import com.roadto500.api.dto.CheckInRequestDTO;
import com.roadto500.api.dto.CheckInResponseDTO;
import com.roadto500.api.model.*;
import com.roadto500.api.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInService {

    private final PlannedSessionRepository plannedSessionRepository;
    private final AftEventRepository aftEventRepository;
    private final AftTestResultRepository aftTestResultRepository;
    private final EventScoreRepository eventScoreRepository;
    private final ImprovementSuggestionRepository improvementSuggestionRepository;
    private final PerformanceService performanceService;

    private static final Map<CheckInFrequency, Integer> FREQUENCY_DAYS = Map.of(
            CheckInFrequency.EVERY_SESSION, 0,
            CheckInFrequency.WEEKLY, 7,
            CheckInFrequency.BIWEEKLY, 14,
            CheckInFrequency.MONTHLY, 30
    );

    public CheckInResponseDTO getCheckInStatus(Long sessionId) {
        PlannedSession session = plannedSessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        Long soldierId = session.getWeeklyPlan().getSoldier().getId();
        List<String> dueEvents = new ArrayList<>();

        for (String abbreviation : session.getSessionType().getEvents()) {
            if (isCheckInDue(soldierId, abbreviation)) {
                dueEvents.add(abbreviation);
            }
        }

        CheckInResponseDTO response = new CheckInResponseDTO();
        response.setCheckInRequired(!dueEvents.isEmpty());
        response.setDueEvents(dueEvents);
        return response;
    }

    public void submitCheckIn(Long sessionId, CheckInRequestDTO dto) {
        PlannedSession session = plannedSessionRepository
                .findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));

        Soldier soldier = session.getWeeklyPlan().getSoldier();
        Long soldierId = soldier.getId();

        // Create a synthetic AftTestResult to hang EventScores on
        AftTestResult checkInResult = new AftTestResult();
        checkInResult.setSoldier(soldier);
        checkInResult.setTestDate(LocalDate.now());
        checkInResult.setTotalScore(dto.getEventScores().values().stream()
                .mapToInt(Integer::intValue).sum());
        checkInResult.setNotes("Check-in — session " + sessionId);
        aftTestResultRepository.save(checkInResult);

        for (Map.Entry<String, Integer> entry : dto.getEventScores().entrySet()) {
            AftEvent aftEvent = aftEventRepository
                    .findByAbbreviation(entry.getKey())
                    .orElseThrow(() -> new IllegalArgumentException("Unknown event: " + entry.getKey()));

            EventScore eventScore = new EventScore();
            eventScore.setAftTestResult(checkInResult);
            eventScore.setAftEvent(aftEvent);
            eventScore.setPointsEarned(entry.getValue());
            eventScore.setCheckIn(true);
            eventScoreRepository.save(eventScore);
        }

        performanceService.updateImprovementSuggestions(soldierId);
        log.info("Check-in submitted for soldier {} on session {}", soldierId, sessionId);
    }

    private boolean isCheckInDue(Long soldierId, String abbreviation) {
        ImprovementSuggestion suggestion = improvementSuggestionRepository
                .findBySoldier_IdAndAftEvent_Abbreviation(soldierId, abbreviation)
                .orElse(null);

        if (suggestion == null) return false;

        CheckInFrequency frequency = suggestion.getCheckInFrequency();

        if (frequency == CheckInFrequency.EVERY_SESSION) return true;

        Optional<LocalDate> lastScoreDate = eventScoreRepository
                .findMostRecentScoreDateBySoldierAndEvent(soldierId, abbreviation);

        if (lastScoreDate.isEmpty()) return true;

        int daysRequired = FREQUENCY_DAYS.get(frequency);
        return lastScoreDate.get().plusDays(daysRequired).isBefore(LocalDate.now());
    }
}