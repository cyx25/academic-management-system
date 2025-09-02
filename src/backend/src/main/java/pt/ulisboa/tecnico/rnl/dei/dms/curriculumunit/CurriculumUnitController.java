package pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.service.CourseService;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.dto.CurriculumUnitDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.service.CurriculumUnitService;

@RestController
public class CurriculumUnitController {


    @Autowired
	private CurriculumUnitService curriculumUnitService;

	@GetMapping("/curriculum-units")
	public List<CurriculumUnitDto> getCurriculumUnits() {
		return curriculumUnitService.getCurriculumUnits();
	}

	@PostMapping("/curriculum-units")
	public CurriculumUnitDto createCurriculumUnit(@RequestBody CurriculumUnitDto curriculumUnitDto) {
		return curriculumUnitService.createCurriculumUnit(curriculumUnitDto);
	}

	@GetMapping("/curriculum-units/{id}")
	public CurriculumUnitDto getCurriculumUnit(@PathVariable long id) {
		return curriculumUnitService.getCurriculumUnit(id);
	}

	@PutMapping("/curriculum-units/{id}")
	public CurriculumUnitDto updateCurriculumUnit(@PathVariable long id, @RequestBody CurriculumUnitDto curriculumUnitDto) {
		return curriculumUnitService.updateCurriculumUnit(id, curriculumUnitDto);
	}

	@DeleteMapping("/curriculum-units/{id}")
	public void deleteCurriculumUnit(@PathVariable long id) {
		curriculumUnitService.deleteCurriculumUnit(id);
	}
    
}
