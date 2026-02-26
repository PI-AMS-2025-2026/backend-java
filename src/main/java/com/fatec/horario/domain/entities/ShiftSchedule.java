package com.fatec.horario.domain.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "shift_schedule")
public class ShiftSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String shiftDescription;

    @Column(nullable = false)
    private Integer startTime;

    @Column(nullable = false)
    private Integer lessonCount;

    @Column(nullable = false)
    private Integer lessonDuration;

    private Boolean includedSaturday;

    @OneToMany(mappedBy = "shiftSchedule")
    private List<ShiftScheduleDetail> details;

    public ShiftSchedule() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShiftDescription() {
        return shiftDescription;
    }

    public void setShiftDescription(String shiftDescription) {
        this.shiftDescription = shiftDescription;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getLessonCount() {
        return lessonCount;
    }

    public void setLessonCount(Integer lessonCount) {
        this.lessonCount = lessonCount;
    }

    public Integer getLessonDuration() {
        return lessonDuration;
    }

    public void setLessonDuration(Integer lessonDuration) {
        this.lessonDuration = lessonDuration;
    }

    public List<ShiftScheduleDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ShiftScheduleDetail> details) {
        this.details = details;
    }

    public Boolean getIncludedSaturday() {
        return includedSaturday;
    }

    public void setIncludedSaturday(Boolean includedSaturday) {
        this.includedSaturday = includedSaturday;
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
        ShiftSchedule other = (ShiftSchedule) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
