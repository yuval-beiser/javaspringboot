package com.handson.basic.repo;


import com.handson.basic.models.StudentGrade;
import org.springframework.data.repository.CrudRepository;

public interface StudentGradeRepository extends CrudRepository<StudentGrade, Long> {

}