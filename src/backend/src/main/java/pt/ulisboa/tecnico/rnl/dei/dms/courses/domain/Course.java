package pt.ulisboa.tecnico.rnl.dei.dms.courses.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import pt.ulisboa.tecnico.rnl.dei.dms.courses.dto.CourseDto;

@Data
@Entity
@Table(name = "courses")
public class Course {



	@Id
	@GeneratedValue
	private Integer id;  // long overkill para num de cursos

    // será que isto fica unique tambem? 
    @Column(name = "name", nullable = false) 
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    // in years
    @Column(name = "duration", nullable = false)
    private int duration;


    protected Course() {
        
    }

    public Course(String name, String code, int duration) {
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
