package com.studenthub.repository;
import com.studenthub.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface StudentRepository extends JpaRepository<Student,Long> {
List<Student> findByNameContainingIgnoreCaseOrCourseContainingIgnoreCaseOrderByIdDesc(String name,String course);
List<Student> findAllByOrderByIdDesc();
}
