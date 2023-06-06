package com.handson.basic.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.handson.basic.util.Dates;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDate;

public class StudentIn implements Serializable {

    @Length(max = 60)
    private String fullname;

    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate birthDate;

    @Min(100)
    @Max(800)
    private Integer satScore;

    private Double graduationScore;

    public Student toStudent() {
        return aStudent().birthDate(Dates.atUtc(birthDate)).fullname(fullname).satScore(satScore).graduationScore(graduationScore).build();
    }

    public void updateStudent(Student student) {
        student.setBirthDate(Dates.atUtc(birthDate));
        student.setFullname(fullname);
        student.setSatScore(satScore);
        student.setGraduationScore(graduationScore);
    }
}