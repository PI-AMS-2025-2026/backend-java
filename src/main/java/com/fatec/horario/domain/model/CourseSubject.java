package com.fatec.horario.domain.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "course_subject")
public class CourseSubject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @Column(nullable = false)
    private Integer semesterNumber;

    private Integer practicalLessonsCount;

    public CourseSubject() {
    }

    public CourseSubject(Course course, Subject subject, Integer semesterNumber, Integer practicalLessonsCount) {
        this.course = course;
        this.subject = subject;
        this.semesterNumber = semesterNumber;
        this.practicalLessonsCount = practicalLessonsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getSemesterNumber() {
        return semesterNumber;
    }

    public void setSemesterNumber(Integer semesterNumber) {
        this.semesterNumber = semesterNumber;
    }

    public Integer getPracticalLessonsCount() {
        return practicalLessonsCount;
    }

    public void setPracticalLessonsCount(Integer practicalLessonsCount) {
        this.practicalLessonsCount = practicalLessonsCount;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CourseSubject other = (CourseSubject) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
