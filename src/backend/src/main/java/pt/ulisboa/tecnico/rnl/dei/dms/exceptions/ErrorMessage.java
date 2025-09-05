package pt.ulisboa.tecnico.rnl.dei.dms.exceptions;

import org.postgresql.translation.messages_bg;

public enum ErrorMessage {
	// TODO: mudar para ist id

	
	NO_SUCH_PERSON("Não existe nenhuma pessoa com o ID %s", 1001),
	PERSON_NAME_NOT_VALID("O nome da pessoa especificado não é válido.", 1002),
	PERSON_ALREADY_EXISTS("Já existe uma pessoa com o ID %s", 1003),
	INVALID_EMAIL_FORMAT("O formato do email especificado não é válido.", 1004),
	INVALID_PERSON_TYPE("O tipo de pessoa especificado não é válido.", 1005),
	INVALID_PERSON_ID("O ID da pessoa especificado não é válido.", 1006),
	PERSON_NAME_REQUIRED("O nome da pessoa é obrigatório.", 1007),
	PERSON_IST_ID_REQUIRED("O IST ID da pessoa é obrigatório.", 1008),
	PERSON_TYPE_REQUIRED("O tipo da pessoa é obrigatório.", 1009),
	PERSON_IST_ID_NOT_VALID("O IST ID da pessoa especificado não é válido.", 1010),
	NO_SUCH_COURSE("Não existe nenhum curso com o ID %s", 2001),
	INVALID_COURSE_DURATION("A duração do curso especificado não é válida.", 2002),
	COURSE_CODE_ALREADY_EXISTS("Já existe um curso com o código %s", 2003),
	COURSE_NAME_REQUIRED("O nome do curso é obrigatório.", 2004),
	COURSE_CODE_REQUIRED("O código do curso é obrigatório.", 2005),
	COURSE_DURATION_REQUIRED("A duração do curso é obrigatória.", 2006),
	COURSE_CODE_TOO_LONG("O código especificado é demasiado longo.", 2007),
	COURSE_CODE_NOT_VALID("O código especificado não é válido.", 2008),
	NO_SUCH_CU("Não existe unidade curricular com o ID %s", 3001),
	CU_INVALID_SEMESTER("O semestre da unidade curricular especificado não é válido.", 3002),
	CU_INVALID_ECTS("Os ECTS da unidade curricular especificada não são válidos.", 3003),
	CU_CODE_ALREADY_EXISTS("Já existe uma unidade curricular com o código %s", 3004),
	CU_NAME_REQUIRED("O nome da unidade curricular é obrigatório.", 3005),
	CU_CODE_REQUIRED("O código da unidade curricular é obrigatório.", 3006),
	CU_SEMESTER_REQUIRED("O semestre da unidade curricular é obrigatório.", 3007),
	CU_ECTS_REQUIRED("Os ECTS da unidade curricular são obrigatórios.", 3008),
	CU_MAIN_TEACHER_REQUIRED("O professor regente da unidade curricular é obrigatório.", 3009),
	CU_CODE_TOO_LONG("O código especificado é demasiado longo.", 3010),
	CU_CODE_NOT_VALID("O código especificado não é válido.", 3011),
	ENROLLMENT_ALREADY_EXISTS("O estudante com o ID %s já está inscrito na unidade curricular com o ID %s", 4001),
	NO_SUCH_ENROLLMENT("Não existe matrícula com o aluno ID %s", 4002),
	ASSIST_ALREADY_EXISTS("O assistente com o ID %s já está associado à unidade curricular", 5001),
	NO_SUCH_ASSIST("Não existe assistência do professor com o ID %s", 5002),
	NO_SUCH_PROJECT("Não existe nenhum projeto com o ID %s", 6001),
	FILE_REQUIRED("É necessário fazer upload de um ficheiro.", 7001),
	NO_SUCH_FILE("Não existe nenhum ficheiro com o ID %s", 7002),
	EMPTY_FILE("O ficheiro está vazio.", 7003),
	STUDENT_NOT_IN_GROUP("O estudante não pertence a nenhum grupo do projeto", 8001),
	NO_SUCH_GROUP("Não existe nenhum grupo com o ID %s", 8002),
	INVALID_GRADE("A nota especificada não é válida.", 8003),
	NO_SUCH_SUBMISSION("Não existe nenhuma submissão com o ID %s", 9001),
	SUBMISSION_DATE_EXPIRED("A data limite para submissão do projeto já expirou.", 9002),
	NO_SUCH_MATERIAL("Não existe nenhum material com o ID %s", 10001);




	private final String label;
	private final int code;

	ErrorMessage(String label, int code) {
		this.label = label;
		this.code = code;
	}

	public String getLabel() {
		return this.label;
	}

	public int getCode() {
		return this.code;
	}
}
