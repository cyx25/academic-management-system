package pt.ulisboa.tecnico.rnl.dei.dms.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service for sending email notifications for academic events
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${app.mail.from:sistema-ams@tecnico.ulisboa.pt}")
    private String fromEmail;

    @Value("${app.mail.enabled:true}")
    private boolean emailEnabled;

    /**
     * Send simple email notification
     */
    private void sendNotification(String to, String subject, String message) {
        if (!emailEnabled) {
            System.out.println("[EMAIL DISABLED] Would send to: " + to + " | Subject: " + subject);
            return;
        }

        try {
            SimpleMailMessage email = new SimpleMailMessage();
            email.setFrom(fromEmail);
            email.setTo(to);
            email.setSubject(subject);
            email.setText(message);
            
            emailSender.send(email);
            System.out.println("[EMAIL SENT] Notification sent to: " + to + " | Subject: " + subject);
            
        } catch (Exception e) {
            System.err.println("[EMAIL ERROR] Failed to send notification to: " + to + " - " + e.getMessage());
        }
    }


    /**
     * Notify student when a test grade is assigned
     */
    public void notifyTestGradeAssigned(String studentEmail, String studentName, 
                                      String curriculumUnitName, String testTitle, Double grade) {
        String subject = "Nova Classificação - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "Foi atribuída uma nova classificação em teste:\n\n" +
            "Unidade Curricular: %s\n" +
            "Teste: %s\n" +
            "Classificação: %.1f valores\n\n" +
            "Pode consultar os detalhes no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName, testTitle, grade
        );
        
        sendNotification(studentEmail, subject, message);
    }

    /**
     * Notify student when a project grade is assigned
     */
    public void notifyProjectGradeAssigned(String studentEmail, String studentName,
                                         String curriculumUnitName, String projectTitle, Double grade) {
        String subject = "Nova Classificação - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "Foi atribuída uma nova classificação em projeto:\n\n" +
            "Unidade Curricular: %s\n" +
            "Projeto: %s\n" +
            "Classificação: %.1f valores\n\n" +
            "Pode consultar os detalhes no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName, projectTitle, grade
        );
        
        sendNotification(studentEmail, subject, message);
    }

    // === PROJECT SUBMISSION NOTIFICATIONS ===

    /**
     * Notify student when project submission is successful
     */
    public void notifyProjectSubmitted(String studentEmail, String studentName,
                                     String curriculumUnitName, String projectTitle) {
        String subject = "Projeto Submetido - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "A sua submissão de projeto foi recebida com sucesso:\n\n" +
            "Unidade Curricular: %s\n" +
            "Projeto: %s\n" +
            "Data de submissão: %s\n\n" +
            "A classificação será atribuída em breve.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName, projectTitle, 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        sendNotification(studentEmail, subject, message);
    }

    /**
     * Notify teachers when a project is submitted
     */
    public void notifyTeachersProjectSubmitted(String teacherEmail, String teacherName,
                                             String studentName, String curriculumUnitName, String projectTitle) {
        String subject = "Nova Submissão de Projeto - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) Professor(a) %s,\n\n" +
            "Foi submetido um novo projeto para classificação:\n\n" +
            "Estudante: %s\n" +
            "Unidade Curricular: %s\n" +
            "Projeto: %s\n" +
            "Data de submissão: %s\n\n" +
            "Aguarda classificação no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            teacherName, studentName, curriculumUnitName, projectTitle,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        sendNotification(teacherEmail, subject, message);
    }

    // === REVISION REQUEST NOTIFICATIONS ===

    /**
     * Notify student when revision request is submitted
     */
    public void notifyRevisionRequested(String studentEmail, String studentName,
                                      String curriculumUnitName, String testTitle) {
        String subject = "Pedido de Revisão Submetido - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "O seu pedido de revisão foi submetido com sucesso:\n\n" +
            "Unidade Curricular: %s\n" +
            "Teste: %s\n" +
            "Data do pedido: %s\n\n" +
            "O pedido será analisado pelos professores e receberá uma resposta em breve.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName, testTitle,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        sendNotification(studentEmail, subject, message);
    }

    /**
     * Notify teachers when a revision request is submitted
     */
    public void notifyTeachersRevisionRequested(String teacherEmail, String teacherName,
                                              String studentName, String curriculumUnitName, String testTitle) {
        String subject = "Novo Pedido de Revisão - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) Professor(a) %s,\n\n" +
            "Foi submetido um novo pedido de revisão:\n\n" +
            "Estudante: %s\n" +
            "Unidade Curricular: %s\n" +
            "Teste: %s\n" +
            "Data do pedido: %s\n\n" +
            "Aguarda análise no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            teacherName, studentName, curriculumUnitName, testTitle,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        sendNotification(teacherEmail, subject, message);
    }

    /**
     * Notify student when revision request status changes
     */
    public void notifyRevisionStatusChanged(String studentEmail, String studentName,
                                          String curriculumUnitName, String testTitle, String status) {
        String subject = "Resposta ao Pedido de Revisão - " + curriculumUnitName;
        String statusText = "APPROVED".equals(status) ? "APROVADO" : "REJEITADO";
        
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "O seu pedido de revisão foi analisado:\n\n" +
            "Unidade Curricular: %s\n" +
            "Teste: %s\n" +
            "Estado: %s\n" +
            "Pode consultar os detalhes no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName, testTitle, statusText
    
        );
        
        sendNotification(studentEmail, subject, message);
    }

    // === ENROLLMENT NOTIFICATIONS ===

    /**
     * Notify student when enrolled in a curriculum unit
     */
    public void notifyStudentEnrolled(String studentEmail, String studentName, String curriculumUnitName) {
        String subject = "Inscrição em Unidade Curricular - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) %s,\n\n" +
            "Foi inscrito(a) na seguinte unidade curricular:\n\n" +
            "Unidade Curricular: %s\n" +
            "Data de inscrição: %s\n\n" +
            "Pode já aceder aos materiais e informações no sistema académico.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            studentName, curriculumUnitName,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        
        sendNotification(studentEmail, subject, message);
    }

    /**
     * Notify assistant when assigned to a curriculum unit
     */
    public void notifyAssistantAssigned(String assistantEmail, String assistantName, String curriculumUnitName) {
        String subject = "Atribuição como Assistente - " + curriculumUnitName;
        String message = String.format(
            "Caro(a) Professor(a) %s,\n\n" +
            "Foi atribuído(a) como assistente na seguinte unidade curricular:\n\n" +
            "Unidade Curricular: %s\n" +
            "Data de atribuição: %s\n\n" +
            "Pode já aceder ao sistema para apoiar na gestão académica.\n\n" +
            "Cumprimentos,\n" +
            "Sistema de Gestão Académica DEI",
            assistantName, curriculumUnitName,
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        
        sendNotification(assistantEmail, subject, message);
    }

   
}