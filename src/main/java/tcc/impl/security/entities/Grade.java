package tcc.impl.security.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "grade_id")
    private Long gradeId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Integer grade;

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
