package info.ejava.examples.app.testing.testbasics.grading.svc;

import java.util.List;

import org.springframework.stereotype.Component;

import info.ejava.examples.app.testing.testbasics.grading.ClientError;
import info.ejava.examples.app.testing.testbasics.grading.bo.StudentGrade;
import info.ejava.examples.app.testing.testbasics.grading.dao.GradeRepository;
import info.ejava.examples.app.testing.testbasics.grading.dao.GradeRepositoryImpl;

@Component
public class GradeServiceImpl implements GraderService {

    private final GradeRepository gradeRepository;
    private NameNormalizer nameNormalizer = new LowerCaseNormalizer();

    public GradeServiceImpl(GradeRepository gradeRepository){
        this.gradeRepository = gradeRepository;
    }

    public interface NameNormalizer {
        String normalizeStudentName(String name);
    }

    public static class LowerCaseNormalizer implements NameNormalizer {
        public String normalizeStudentName(String name) {
            return name==null ? null : name.trim().toLowerCase();
        }
    }


    @Override
    public int submitGrade(String student, Integer grade) throws ClientError {
        // validae input
        if(student == null){
            throw new ClientError.BadRequest("student is required");
        } else if(grade == null) {
            throw new ClientError.BadRequest("grade is required");
        }

        // normalize input 
        student = nameNormalizer.normalizeStudentName(student);
        // store the grade 
        StudentGrade studentGrade = new StudentGrade(student, grade);
        gradeRepository.save(studentGrade);
        return 0;
    }

    @Override
    public double calcGrade(String student) throws ClientError {
        // validate input
        if(student == null){
            throw new ClientError.BadRequest("student is required");
        }

         //normalize input
        student = nameNormalizer.normalizeStudentName(student);

        //obtain the grades
        List<StudentGrade> grades = gradeRepository.findAll(student);

        //calculate average
        double result= 0;
        if (!grades.isEmpty()) {
            double sum = grades.stream().mapToInt(sg->sg.getGrade()).sum();
            result = sum/grades.size();
        }

        return result;

    }

}
