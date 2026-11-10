package com.manara.crm.dto;

public class AnimatorSessionStatsDTO {
    private String animatorName;
    private Long sessionsCount;

    public AnimatorSessionStatsDTO(String firstName, String lastName, Long sessionsCount) {
        this.animatorName = firstName + " " + lastName;
        this.sessionsCount = sessionsCount;
    }

    public String getAnimatorName() { return animatorName; }
    public Long getSessionsCount() { return sessionsCount; }
}