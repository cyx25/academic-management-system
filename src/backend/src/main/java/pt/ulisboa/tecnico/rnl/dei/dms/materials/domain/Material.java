package pt.ulisboa.tecnico.rnl.dei.dms.materials.domain;



import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pt.ulisboa.tecnico.rnl.dei.dms.curriculumunit.domain.CurriculumUnit;
import pt.ulisboa.tecnico.rnl.dei.dms.files.File;


@Getter
@Setter
@ToString(exclude = "files")
@EqualsAndHashCode(exclude = "files")
@Entity
@NoArgsConstructor
@Table(name = "material_tabs")
public class Material {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "icon_name", nullable = false)
    private String iconName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_unit_id", nullable = false)
    private CurriculumUnit curriculumUnit;
 
    @OneToMany(mappedBy = "material", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<File> files = new HashSet<>();




    public Material(String name, String iconName, CurriculumUnit curriculumUnit) {

        this.name = name;
        this.iconName = iconName;
        this.curriculumUnit = curriculumUnit;

    }

    public void addFile(File file) {
        files.add(file);
        file.setMaterial(this);
    }
    
    public void removeFile(File file) {
        files.remove(file);
        file.setMaterial(null);
    }

}
