package com.manara.crm.service;

import com.manara.crm.dto.*;
import com.manara.crm.repos.ActivityRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.repos.SessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatsService {

    private final RegistrationRepository registrationRepository;
    private final SessionRepository sessionRepository;
    private final ActivityRepository activityRepository;

    public StatsService(
            RegistrationRepository registrationRepository,
            SessionRepository sessionRepository,
            ActivityRepository activityRepository
    ) {
        this.registrationRepository = registrationRepository;
        this.sessionRepository = sessionRepository;
        this.activityRepository = activityRepository;
    }

    public List<ActivityRegistrationStatsDTO> getRegistrationsByActivity() {
        return registrationRepository.countRegistrationsByActivity();
    }

    public List<ActivityRevenueDTO> getRevenueByActivity() {
        return registrationRepository.revenueByActivity();
    }

    public List<MonthlyRevenueDTO> getRevenueByMonth() {
        return registrationRepository.revenueByMonth();
    }

    public List<StatusStatsDTO> getRegistrationsByStatus() {
        return registrationRepository.countByStatus();
    }

    public List<SessionTimeStatsDTO> getSessionsByMonth() {
        return sessionRepository.sessionsByMonth();
    }

    public List<AnimatorSessionStatsDTO> getSessionsByAnimator() {
        return sessionRepository.sessionsByAnimator();
    }

    public List<ActivityCategoryStatsDTO> getActivitiesByCategorie() {
        return activityRepository.countActivitiesByCategorie();
    }

    public List<ActivityCategoryPriceDTO> getAveragePriceByCategorie() {
        return activityRepository.averagePriceByCategorie();
    }

    public long countActiveActivities() {
        return activityRepository.countByActiveTrue();
    }

    public long countInactiveActivities() {
        return activityRepository.countByActiveFalse();
    }
}