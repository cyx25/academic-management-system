package pt.ulisboa.tecnico.rnl.dei.dms.courses.domain;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;


@Data
@Entity
@Table(name = "courses")
public class Course {



	@Id
	@GeneratedValue
	private Long id;  // long overkill para num de cursos

    // será que isto fica unique tambem? 
    @Column(name = "name", nullable = false) 
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    // in years
    @Column(name = "duration", nullable = false)
    private String duration;

    @ManyToMany
    @JoinTable(
        name = "course_curriculum_units",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "curriculum_unit_id")
    )
    private Set<CurriculumUnit> curriculumUnits = new HashSet<>();



    protected Course() {
        
    }

    public Course(String name, String code, String duration) {
        this.name = name;
        this.code = code;
        this.duration = duration;
    }

    public Course( CourseDto courseDto) {
        this.name = courseDto.name();
        this.code = courseDto.code();
        this.duration = courseDto.duration();
        System.out.println("CourseDto: " + courseDto);

    }

}
