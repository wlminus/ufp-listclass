package com.wlminus.ufp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * A Course.
 */
@Entity
@Table(name = "course")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "course_code")
    private String courseCode;

    @Column(name = "max_slot")
    private Long maxSlot;

    @Column(name = "current_slot")
    private Long currentSlot;

    @Column(name = "status")
    private String status;

    @OneToMany(mappedBy = "course", fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    private Set<Schedule> schedules = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties("courses")
    private Subject subject;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public Course courseCode(String courseCode) {
        this.courseCode = courseCode;
        return this;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public Long getMaxSlot() {
        return maxSlot;
    }

    public Course maxSlot(Long maxSlot) {
        this.maxSlot = maxSlot;
        return this;
    }

    public void setMaxSlot(Long maxSlot) {
        this.maxSlot = maxSlot;
    }

    public Long getCurrentSlot() {
        return currentSlot;
    }

    public void setCurrentSlot(Long currentSlot) {
        this.currentSlot = currentSlot;
    }

    public Course currentSlot(Long currentSlot) {
        this.currentSlot = currentSlot;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Course status(String status) {
        this.status = status;
        return this;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Schedule> getSchedules() {
        return schedules;
    }

    public Course schedules(Set<Schedule> schedules) {
        this.schedules = schedules;
        return this;
    }

    public Course addSchedule(Schedule schedule) {
        this.schedules.add(schedule);
        schedule.setCourse(this);
        return this;
    }

    public Course removeSchedule(Schedule schedule) {
        this.schedules.remove(schedule);
        schedule.setCourse(null);
        return this;
    }

    public void setSchedules(Set<Schedule> schedules) {
        this.schedules = schedules;
    }

    public Subject getSubject() {
        return subject;
    }

    public Course subject(Subject subject) {
        this.subject = subject;
        return this;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Course)) {
            return false;
        }
        return id != null && id.equals(((Course) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Course{" +
            "id=" + getId() +
            ", courseCode='" + getCourseCode() + "'" +
            ", maxSlot=" + getMaxSlot() +
            ", currentSlot=" + getCurrentSlot() +
            ", status='" + getStatus() + "'" +
            "}";
    }
}
