package com.fatec.horario.domain.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
@Table(name = "class_group") // Nome da tabela
public class ClassGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // ID gerado automaticamente
    private Long id;

    private Integer studentCount; // Quantidade de alunos na turma

    // Muitas turmas pertencem a um curso
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false) // Chave estrangeira para Course
    private Course course;

    // Construtor obrigatório para o JPA
    public ClassGroup() {
    }

    // Construtor completo
    public ClassGroup(Long id, Integer studentCount, Course course) {
        this.id = id;
        this.studentCount = studentCount;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public Course getCourse() {
        return course;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    // Comparação baseada no ID
    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ClassGroup)) return false;
        ClassGroup other = (ClassGroup) obj;
        return id != null && id.equals(other.id);
    }
}
