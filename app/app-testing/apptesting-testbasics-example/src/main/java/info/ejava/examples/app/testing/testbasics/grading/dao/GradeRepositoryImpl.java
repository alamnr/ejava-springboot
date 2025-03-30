package info.ejava.examples.app.testing.testbasics.grading.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;

public class GradeRepositoryImpl implements GradeRepository {

    Map<String, List<StudentGrade>> grades = new HashMap<>();

    @Override
    public void save(StudentGrade studentGrade) {

        List<StudentGrade> studentGrades = grades.get(studentGrade.getStudent());
        if(studentGrades == null){
            studentGrades = new ArrayList<>();
            grades.put(studentGrade.getStudent(), studentGrades);
        }
        studentGrades.add(studentGrade);        
    }

    @Override
    public List<StudentGrade> findAll(String student) {
        List<StudentGrade> studentGrades = grades.get(student);
        return null == studentGrades ? Collections.emptyList() : studentGrades;
    }
    
}
