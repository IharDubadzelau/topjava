package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
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

        System.out.println("------------------ Speed of execution of methods is 10,000 times ----------------------------------");
        Date startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
        System.out.println("Execution time (Stream) = "+((new Date()).getTime()-startDate.getTime()));

        startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded_Cycle(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
        System.out.println("Execution time (Cyrcle) = "+((new Date()).getTime()-startDate.getTime()));

        System.out.println("------------------ Result of the method based on the STREAM ----------------------------------");
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000)
                .stream()
                .forEach(System.out::println);

        System.out.println("------------------ Result of the method based on the CYCLES ----------------------------------");
        getFilteredWithExceeded_Cycle(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000)
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

            listUMWE = mealList.stream()
                    .filter(um -> um.isBetweenTime(startTime,endTime))
                    .map(um -> new UserMealWithExceed(um, ( mapDateCalories.getOrDefault(um.getDT_toLocalDate(),Integer.MIN_VALUE)>caloriesPerDay )))
                    .collect(Collectors.toList());
        }

        return listUMWE;
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded_Cycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> listUMWE = new ArrayList<>();

        if ( mealList !=null ) {
            Map<LocalDate, Integer> mapDateCalories = new HashMap<>();

            for (int i=0; i<mealList.size(); i++) {
                UserMeal userMeal = mealList.get(i);
                if (userMeal!=null && userMeal.isBetweenTime(startTime, endTime) ) {
                        LocalDate localDate = userMeal.getDT_toLocalDate();
                        Integer calories = mapDateCalories.get(localDate);
                        if (calories == null) {
                            calories = userMeal.getCalories();
                            for (int j = i+1; j <mealList.size(); j++) {
                                UserMeal userMealAdd = mealList.get(j);
                                if ( userMealAdd!=null && userMealAdd.getDT_toLocalDate().compareTo(localDate)==0 )
                                    calories += userMealAdd.getCalories();
                            }
                            mapDateCalories.put(userMeal.getDT_toLocalDate(),calories);
                        }
                }
            }

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
