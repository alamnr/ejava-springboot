package info.ejava.examples.app.testing.testbasics.grading.dao;

import java.util.List;

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;

public interface GradeRepository {
    public void save(StudentGrade studentGrade);
    public List<StudentGrade> findAll(String student);
}
