package tcc.impl.security.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import tcc.impl.security.controller.dto.NewGradeDto;
import tcc.impl.security.entities.Grade;
import tcc.impl.security.entities.User;
import tcc.impl.security.repository.GradeRepository;
import tcc.impl.security.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
public class GradesController {

    private final GradeRepository gradeRepository;

    private final UserRepository userRepository;

    public GradesController(GradeRepository gradeRepository, UserRepository userRepository) {
        this.gradeRepository = gradeRepository;
        this.userRepository = userRepository;
    }

    public static String formatUUID(String input) {
        if (input == null || input.length() != 32) {
            throw new IllegalArgumentException("String must have 32 characters");
        }

        return input.substring(0, 8) + "-" +
                input.substring(8, 12) + "-" +
                input.substring(12, 16) + "-" +
                input.substring(16, 20) + "-" +
                input.substring(20);
    }

    @GetMapping("/grade")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<Grade>> getAllGrades() {
        List<Grade> grades = gradeRepository.findAll();
        if (grades.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(grades);
    }

    @PostMapping("/grade")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> assignGrade(@RequestBody NewGradeDto newGradeDto) {
        String formattedUserId = formatUUID(newGradeDto.userId());
        Optional<User> userOptional = userRepository.findById(UUID.fromString(formattedUserId));
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }
        User user = userOptional.get();
        Optional<Grade> existingGrade = gradeRepository.findByUser(user);
        if (existingGrade.isPresent()) {
            return ResponseEntity.badRequest().
                    body("User already has a grade assigned. Please delete the existing grade " +
                            "and assign a new one.");
        }
        if (newGradeDto.grade() < 0 || newGradeDto.grade() > 100) {
            return ResponseEntity.badRequest().body("Grade must be between 0 and 100");
        }
        Grade grade = new Grade();
        grade.setUser(user);
        grade.setGrade(newGradeDto.grade());
        gradeRepository.save(grade);
        return ResponseEntity.ok("Grade assigned successfully.");
    }

    @DeleteMapping("/grade/{gradeId}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<String> deleteGrade(@PathVariable String gradeId) {
        if (!gradeRepository.existsById(Long.valueOf(gradeId))) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Grade not found.");
        }
        gradeRepository.deleteById(Long.valueOf(gradeId));
        return ResponseEntity.ok("Grade deleted successfully.");
    }

    @GetMapping("/my-grade")
    public ResponseEntity<Object> myGrade(JwtAuthenticationToken token) {
        UUID userId = UUID.fromString(token.getName());
        var userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        var user = userOptional.get();
        Optional<Grade> gradeOptional = gradeRepository.findByUser(user);
        if (gradeOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("You have no grade.");
        }
        return ResponseEntity.ok(gradeOptional.get());
    }
}
