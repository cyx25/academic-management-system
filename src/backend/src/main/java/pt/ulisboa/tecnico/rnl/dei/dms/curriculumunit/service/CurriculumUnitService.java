package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.domain.Course;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.repository.CourseRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;


@Transactional
@Service
public class CurriculumUnitService {

    @Autowired
    private CurriculumUnitRepository curriculumUnitRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;


    private CurriculumUnit fetchCurriculumUnitOrThrow(long id) {
		return curriculumUnitRepository.findById(id)
				.orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(id)));
	}


    @Transactional
    public List<CurriculumUnitDto> getCurriculumUnits() {
        return curriculumUnitRepository.findAll().stream()
                .map(CurriculumUnitDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CurriculumUnitDto saveCurriculumUnit(Long id, CurriculumUnitDto curriculumUnitDto) {
        Person mainTeacher = personRepository.findById(curriculumUnitDto.mainTeacher().id())
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON));
                
        List<Course> courses = curriculumUnitDto.courses().stream()
                .map(courseDto -> courseRepository.findById(courseDto.id())
                        .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_COURSE, Long.toString(courseDto.id()))))
                .collect(Collectors.toList());
              
        CurriculumUnit curriculumUnit = new CurriculumUnit(curriculumUnitDto, mainTeacher, courses);
        curriculumUnit.setId(id);
        return new CurriculumUnitDto( curriculumUnitRepository.save(curriculumUnit));
    }

    @Transactional
    public CurriculumUnitDto createCurriculumUnit(CurriculumUnitDto curriculumUnitDto) {
        return saveCurriculumUnit(null, curriculumUnitDto);
    }

    @Transactional
	public CurriculumUnitDto getCurriculumUnit(long id) {
		return new CurriculumUnitDto(fetchCurriculumUnitOrThrow(id));
	}

    @Transactional
    public CurriculumUnitDto updateCurriculumUnit(long curriculumUnitId, CurriculumUnitDto curriculumUnitDto) {
        fetchCurriculumUnitOrThrow(curriculumUnitId);
        return saveCurriculumUnit(curriculumUnitId, curriculumUnitDto);
    }



    @Transactional
    public void deleteCurriculumUnit(long curriculumUnitId) {
        fetchCurriculumUnitOrThrow(curriculumUnitId);
        curriculumUnitRepository.deleteById(curriculumUnitId);
    }


}
