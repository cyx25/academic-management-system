package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists;

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
public class AssistService {

    @Autowired
    private AssistRepository assistRepository;

    @Autowired
    private CurriculumUnitRepository curriculumUnitRepository;

    @Autowired
    private PersonRepository personRepository;

    public AssistDto createAssist(long curriculumUnitId, long assistantId) {
        CurriculumUnit curriculumUnit = curriculumUnitRepository.findById(curriculumUnitId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(curriculumUnitId)));

        Person assistant = personRepository.findById(assistantId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_PERSON, Long.toString(assistantId)));

        assistRepository.findByCurriculumUnitIdAndAssistantId(curriculumUnitId, assistantId)
                .ifPresent(a -> {
                    throw new DEIException(ErrorMessage.ASSIST_ALREADY_EXISTS, Long.toString(assistantId));
                });

        Assist assist = new Assist(curriculumUnit, assistant);
        assistRepository.save(assist);

        return new AssistDto(assist);
    }

    public void deleteAssist(long curriculumUnitId, long assistantId) {
        Assist assist = assistRepository.findByCurriculumUnitIdAndAssistantId(curriculumUnitId, assistantId)
                .orElseThrow(() -> new DEIException(ErrorMessage.NO_SUCH_ASSIST, Long.toString(assistantId)));

        assistRepository.delete(assist);
    }


    @Transactional(readOnly = true)
    public List<AssistDto> getAssists(long curriculumUnitId) {
        // Validate that the curriculum unit exists
        if (!curriculumUnitRepository.existsById(curriculumUnitId)) {
            throw new DEIException(ErrorMessage.NO_SUCH_CU, Long.toString(curriculumUnitId));
        }

        return assistRepository.findByCurriculumUnitId(curriculumUnitId)
                .stream()
                .map(AssistDto::new)
                .collect(Collectors.toList());
    }
}