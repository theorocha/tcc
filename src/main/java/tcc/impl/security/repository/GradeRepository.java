package tcc.impl.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tcc.impl.security.entities.Grade;
import tcc.impl.security.entities.User;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByUser(User user);
}
