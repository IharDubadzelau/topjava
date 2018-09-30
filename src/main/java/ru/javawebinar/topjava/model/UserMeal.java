package ru.javawebinar.topjava.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class UserMeal {
    private final LocalDateTime dateTime;

    private final String description;

    private final int calories;

    public UserMeal(LocalDateTime dateTime, String description, int calories) {
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public LocalDate getDT_toLocalDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getDT_toLocalTime() {
        return dateTime.toLocalTime();
    }

    public boolean isBetweenTime(LocalTime startTime, LocalTime endTime)
    {
        return ((startTime==null || getDT_toLocalTime().compareTo(startTime)>=0 ) &&
                (endTime==null || getDT_toLocalTime().compareTo(endTime)<=0 ));
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    @Override
    public String toString() {
        return "UserMeal{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
