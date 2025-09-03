package pt.ulisboa.tecnico.rnl.dei.dms.assignments.enrollments;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private CurriculumUnitRepository curriculumUnitRepository;

    @Autowired
    private PersonRepository personRepository;

    public EnrollmentDto createEnrollment(long curriculumUnitId, long studentId) {
        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(curriculumUnitId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(curriculumUnitId)));

        Person student = personRepository.findById(studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(studentId)));

        enrollmentRepository.findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId)
                .ifPresent(e -> {
                    throw new DEIException(ErrorMessage.ENROLLMENT_ALREADY_EXISTS, Long.toString(studentId));
                });

        Enrollment enrollment = new Enrollment(student, curriculumUnit);
        enrollmentRepository.save(enrollment);

        return new EnrollmentDto(enrollment);
    }

    public void deleteEnrollment(long curriculumUnitId, long studentId) {
        Enrollment enrollment = enrollmentRepository.findByCurriculumUnitIdAndStudentId(curriculumUnitId, studentId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_ENROLLMENT, Long.toString(studentId)));

        enrollmentRepository.delete(enrollment);
    }


    @Transactional(readOnly = true)
    public List<EnrollmentDto> getEnrollments(long curriculumUnitId) {
        // Validate that the curriculum unit exists
        if (!curriculumUnitRepository.existsById(curriculumUnitId)) {
            throw new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(curriculumUnitId));
        }

        return enrollmentRepository.findByCurriculumUnitId(curriculumUnitId)
                .stream()
                .map(EnrollmentDto::new)
                .collect(Collectors.toList());
    }
}