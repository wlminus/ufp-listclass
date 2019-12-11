package com.wlminus.ufp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Schedule.
 */
@Entity
@Table(name = "schedule")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Schedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "week_value")
    private String weekValue;

    @Column(name = "week_day_value")
    private String weekDayValue;

    @Column(name = "period_value")
    private String periodValue;

    @Column(name = "location")
    private String location;

    @ManyToOne
    @JsonIgnoreProperties("schedules")
    @JsonIgnore
    private Course course;

    public Schedule() {
    }

    public Schedule(String weekValue, String weekDayValue, String periodValue, String location) {
        this.weekValue = weekValue;
        this.weekDayValue = weekDayValue;
        this.periodValue = periodValue;
        this.location = location;
    }

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWeekValue() {
        return weekValue;
    }

    public Schedule weekValue(String weekValue) {
        this.weekValue = weekValue;
        return this;
    }

    public void setWeekValue(String weekValue) {
        this.weekValue = weekValue;
    }

    public String getWeekDayValue() {
        return weekDayValue;
    }

    public Schedule weekDayValue(String weekDayValue) {
        this.weekDayValue = weekDayValue;
        return this;
    }

    public void setWeekDayValue(String weekDayValue) {
        this.weekDayValue = weekDayValue;
    }

    public String getPeriodValue() {
        return periodValue;
    }

    public Schedule periodValue(String periodValue) {
        this.periodValue = periodValue;
        return this;
    }

    public void setPeriodValue(String periodValue) {
        this.periodValue = periodValue;
    }

    public String getLocation() {
        return location;
    }

    public Schedule location(String location) {
        this.location = location;
        return this;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Course getCourse() {
        return course;
    }

    public Schedule course(Course course) {
        this.course = course;
        return this;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Schedule)) {
            return false;
        }
        return id != null && id.equals(((Schedule) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Schedule{" +
            "id=" + getId() +
            ", weekValue='" + getWeekValue() + "'" +
            ", weekDayValue='" + getWeekDayValue() + "'" +
            ", periodValue='" + getPeriodValue() + "'" +
            ", location='" + getLocation() + "'" +
            "}";
    }


}
