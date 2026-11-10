package com.manara.crm.Rest;

import com.manara.crm.dto.*;
import com.manara.crm.service.StatsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class StatsApiController {

    private final StatsService statsService;

    public StatsApiController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/registrations/by-activity")
    public List<ActivityRegistrationStatsDTO> registrationsByActivity() {
        return statsService.getRegistrationsByActivity();
    }

    @GetMapping("/revenue/by-activity")
    public List<ActivityRevenueDTO> revenueByActivity() {
        return statsService.getRevenueByActivity();
    }

    @GetMapping("/revenue/by-month")
    public List<MonthlyRevenueDTO> revenueByMonth() {
        return statsService.getRevenueByMonth();
    }

    @GetMapping("/registrations/by-status")
    public List<StatusStatsDTO> registrationsByStatus() {
        return statsService.getRegistrationsByStatus();
    }

    @GetMapping("/sessions/by-month")
    public List<SessionTimeStatsDTO> sessionsByMonth() {
        return statsService.getSessionsByMonth();
    }

    @GetMapping("/sessions/by-animator")
    public List<AnimatorSessionStatsDTO> sessionsByAnimator() {
        return statsService.getSessionsByAnimator();
    }

    @GetMapping("/activities/by-category")
    public List<ActivityCategoryStatsDTO> activitiesByCategory() {
        return statsService.getActivitiesByCategorie();
    }

    @GetMapping("/activities/average-price-by-category")
    public List<ActivityCategoryPriceDTO> averagePriceByCategory() {
        return statsService.getAveragePriceByCategorie();
    }

    @GetMapping("/activities/active/count")
    public long activeActivitiesCount() {
        return statsService.countActiveActivities();
    }

    @GetMapping("/activities/inactive/count")
    public long inactiveActivitiesCount() {
        return statsService.countInactiveActivities();
    }
}