package com.fatec.horario.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_availability")
public class UserAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long id;
    private Integer weekday;
    private Integer lessonNumber;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public UserAvailability(Long id, Integer weekday, Integer lessonNumber) {
        this.id = id;
        this.weekday = weekday;
        this.lessonNumber = lessonNumber;

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserAvailability() {

    }

    public Long getId() {
        return id;
        
    }

    public void setId(Long id) {
        this.id = id;

    }

    public Integer getWeekday() {
        return weekday;
    }

    public void setWeekday(Integer weekday) {
        this.weekday = weekday;

    }

    public Integer getLessonNumber() {
        return lessonNumber;

    }

    public void setLessonNumber(Integer lessonNumber) {
        this.lessonNumber = lessonNumber;

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
        UserAvailability other = (UserAvailability) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }


    

}