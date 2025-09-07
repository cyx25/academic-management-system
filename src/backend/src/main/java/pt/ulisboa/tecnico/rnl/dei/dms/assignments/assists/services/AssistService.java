package pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.services;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.domain.Assist;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.dto.AssistDto;
import pt.ulisboa.tecnico.rnl.dei.dms.assignments.assists.repository.AssistRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.repository.CurriculumUnitRepository;
import pt.ulisboa.tecnico.rnl.dei.dms.email.EmailService;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.DEIException;
import pt.ulisboa.tecnico.rnl.dei.dms.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.rnl.dei.dms.person.domain.Person;
import pt.ulisboa.tecnico.rnl.dei.dms.person.repository.PersonRepository;

@Service
@Transactional
public class AssistService {

    private final EmailService emailService;
    private final AssistRepository assistRepository;
    private final CurriculumUnitRepository curriculumUnitRepository;
    private final PersonRepository personRepository;

    public AssistService(
            AssistRepository assistRepository,
            CurriculumUnitRepository curriculumUnitRepository,
            PersonRepository personRepository,
            EmailService emailService) {
        this.assistRepository = assistRepository;
        this.curriculumUnitRepository = curriculumUnitRepository;
        this.personRepository = personRepository;
        this.emailService = emailService;
    }

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

        emailService.notifyAssistantAssigned(
            assistant.getEmail(),
            assistant.getName(),
            curriculumUnit.getName()
        );
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