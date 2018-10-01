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
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );

        System.out.println("------------------ Speed of execution of methods is 10,000 times ----------------------------------");
        Date startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("Execution time (Stream) = " + ((new Date()).getTime() - startDate.getTime()));

        startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded_Cycle(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("Execution time (Cycle)  = " + ((new Date()).getTime() - startDate.getTime()));

        startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded_Fast(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("Execution time (fast)  = " + ((new Date()).getTime() - startDate.getTime()));

        startDate = new Date();
        for (int i = 0; i < 10000; i++)
            getFilteredWithExceeded_Fast2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000);
        System.out.println("Execution time (fast2)  = " + ((new Date()).getTime() - startDate.getTime()));

        System.out.println("------------------ Result of the method based on the STREAM ----------------------------------");
        getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .stream()
                .forEach(System.out::println);

        System.out.println("------------------ Result of the method based on the CYCLES ----------------------------------");
        getFilteredWithExceeded_Cycle(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .stream()
                .forEach(System.out::println);
        System.out.println("------------------ Result of the method based on the FAST ----------------------------------");
        getFilteredWithExceeded_Fast(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .stream()
                .forEach(System.out::println);
        System.out.println("------------------ Result of the method based on the FAST2 ----------------------------------");
        getFilteredWithExceeded_Fast2(mealList, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .stream()
                .forEach(System.out::println);
//        .toLocalDate();
//        .toLocalTime();
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapDateCalories = mealList.stream()
                .collect(Collectors.groupingBy(UserMeal::toLocalDate, Collectors.summingInt(UserMeal::getCalories)));

        List<UserMealWithExceed> listUMWE = mealList.stream()
                .filter(um -> TimeUtil.isBetween(um.toLocalTime(), startTime, endTime))
                .map(um -> new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), (mapDateCalories.get(um.toLocalDate()) > caloriesPerDay)))
                .collect(Collectors.toList());

        return listUMWE;
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded_Cycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        List<UserMealWithExceed> listUMWE = new ArrayList<>();

        if (mealList != null) {
            Map<LocalDate, Integer> mapDateCalories = new HashMap<>();

            for (UserMeal userMeal : mealList) {
                mapDateCalories.merge(userMeal.toLocalDate(), userMeal.getCalories(), (um1, um2) -> um1 + um2);
            }

            for (UserMeal um : mealList) {
                if (TimeUtil.isBetween(um.toLocalTime(), startTime, endTime)) {
                    listUMWE.add(new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(),
                            mapDateCalories.get(um.toLocalDate()) > caloriesPerDay));
                }
            }
        }

        return listUMWE;
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded_Fast(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapDateCalories = new HashMap<>();

        for (int i = 0; i < mealList.size(); i++) {
            UserMeal userMeal = mealList.get(i);
            if (TimeUtil.isBetween(userMeal.toLocalTime(), startTime, endTime)) {
                LocalDate localDate = userMeal.toLocalDate();
                Integer calories = mapDateCalories.get(localDate);
                if (calories == null) {
                    calories = userMeal.getCalories();
                    for (int j = i + 1; j < mealList.size(); j++) {
                        UserMeal userMealAdd = mealList.get(j);
                        if (userMealAdd.toLocalDate().compareTo(localDate) == 0)
                            calories += userMealAdd.getCalories();
                    }
                    mapDateCalories.put(localDate, calories);
                }
            }
        }

        List<UserMealWithExceed> listUMWE = new ArrayList<>();
        for (UserMeal um : mealList) {
            if (TimeUtil.isBetween(um.toLocalTime(), startTime, endTime)) {
                listUMWE.add(new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(),
                        mapDateCalories.get(um.toLocalDate()) > caloriesPerDay));
            }
        }

        return listUMWE;
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded_Fast2(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> mapDateCalories = new HashMap<>();

        for (int i = 0; i < mealList.size(); i++) {
            UserMeal userMeal = mealList.get(i);
            LocalDate localDate = userMeal.toLocalDate();
            Integer calories = mapDateCalories.get(localDate);
            if (calories == null) {
                calories = userMeal.getCalories();
                for (int j = i + 1; j < mealList.size(); j++) {
                    UserMeal userMealAdd = mealList.get(j);
                    if (userMealAdd.toLocalDate().compareTo(localDate) == 0)
                        calories += userMealAdd.getCalories();
                }
                mapDateCalories.put(localDate, calories);
            }
        }

        List<UserMealWithExceed> listUMWE = new ArrayList<>();
        for (UserMeal um : mealList) {
            if (TimeUtil.isBetween(um.toLocalTime(), startTime, endTime)) {
                listUMWE.add(new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(),
                        mapDateCalories.get(um.toLocalDate()) > caloriesPerDay));
            }
        }

        return listUMWE;
    }
}
