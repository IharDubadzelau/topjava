package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000)
                .stream()
                .forEach(System.out::println);

//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> listUMWE = new ArrayList<>();

        if ( mealList !=null ) {
            Map<LocalDate, Integer> mapDateCalories = mealList.stream()
                    .collect(Collectors.groupingBy( UserMeal::getDT_toLocalDate, Collectors.summingInt(UserMeal::getCalories)));

            for (UserMeal userMeal:mealList) {
                if ( userMeal.isBetweenTime(startTime,endTime) ) {
                    Integer sumCalories = mapDateCalories.get(userMeal.getDT_toLocalDate());
                    if (sumCalories!=null )
                        listUMWE.add(new UserMealWithExceed(userMeal,sumCalories>caloriesPerDay));
                }
            }
        }

        return listUMWE;
    }
}
