package org.d3ifcool.alert.model;

/**
 * Created by Sholeh Hermawan on 9/13/2017.
 */

public class Schedule {
    public String dateOfSchedule, nameOfSchedule, idScheduleList;
    public String timeOfSchedule;

    public Schedule() {
    }

    public Schedule(String dateOfSchedule, String nameOfSchedule, String timeOfSchedule) {
        this.dateOfSchedule = dateOfSchedule;
        this.nameOfSchedule = nameOfSchedule;
        this.timeOfSchedule = timeOfSchedule;
    }

    public Schedule(Schedule schedule, String idScheduleList) {
        this.dateOfSchedule = schedule.getDateOfSchedule();
        this.nameOfSchedule = schedule.getNameOfSchedule();
        this.timeOfSchedule = schedule.getTimeOfSchedule();
        this.idScheduleList = idScheduleList;
    }

    public String getDateOfSchedule() {
        return dateOfSchedule;
    }

    public String getNameOfSchedule() {
        return nameOfSchedule;
    }

    public String getIdScheduleList() {
        return idScheduleList;
    }

    public String getTimeOfSchedule() {
        return timeOfSchedule;
    }
}
