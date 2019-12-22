package com.wlminus.ufp.repository;

import com.wlminus.ufp.domain.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Course entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query(value = "select c from Course c left join c.subject left join c.schedules where c.courseCode like %:query% or c.subject.subjectCode like %:query% or c.subject.subjectName like %:query%",
        countQuery = "select count(c) from Course c left join c.subject left join c.schedules where c.courseCode like %:query% or c.subject.subjectCode like %:query% or c.subject.subjectName like %:query%")
    Page<Course> searchCourse(@Param("query") String query, Pageable pageable);
}
