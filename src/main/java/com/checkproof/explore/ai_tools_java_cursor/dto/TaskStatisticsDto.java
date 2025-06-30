package com.checkproof.explore.ai_tools_java_cursor.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for task statistics
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskStatisticsDto {

    private long totalTasks;
    private long pendingTasks;
    private long inProgressTasks;
    private long completedTasks;
    private long cancelledTasks;
    private long overdueTasks;
    private long upcomingTasks;

    /**
     * Calculate completion rate as a percentage
     */
    public double getCompletionRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) completedTasks / totalTasks * 100;
    }

    /**
     * Calculate overdue rate as a percentage
     */
    public double getOverdueRate() {
        if (totalTasks == 0) {
            return 0.0;
        }
        return (double) overdueTasks / totalTasks * 100;
    }

    /**
     * Get active tasks count (pending + in progress)
     */
    public long getActiveTasks() {
        return pendingTasks + inProgressTasks;
    }

    /**
     * Get inactive tasks count (completed + cancelled)
     */
    public long getInactiveTasks() {
        return completedTasks + cancelledTasks;
    }

    /**
     * Check if there are any overdue tasks
     */
    public boolean hasOverdueTasks() {
        return overdueTasks > 0;
    }

    /**
     * Check if there are any upcoming tasks
     */
    public boolean hasUpcomingTasks() {
        return upcomingTasks > 0;
    }
} 