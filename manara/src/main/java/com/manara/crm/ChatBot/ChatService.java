package com.manara.crm.ChatBot;

import com.manara.crm.entity.Child;
import com.manara.crm.entity.Registration;
import com.manara.crm.entity.Session;
import com.manara.crm.entity.User;
import com.manara.crm.repos.ChildRepository;
import com.manara.crm.repos.RegistrationRepository;
import com.manara.crm.repos.SessionRepository;
import com.manara.crm.repos.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Service
public class ChatService {

    private final SessionRepository sessionRepository;
    private final OpenAiService openAiService;
    private final UserRepository userRepository;
    private final ChildRepository childRepository;
    private final RegistrationRepository registrationRepository;

    public ChatService(
            SessionRepository sessionRepository,
            UserRepository userRepository,
            ChildRepository childRepository,
            RegistrationRepository registrationRepository,
            OpenAiService openAiService
    ) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.childRepository = childRepository;
        this.registrationRepository = registrationRepository;
        this.openAiService = openAiService;
    }

    public String processMessage(String message) {

        if (message == null || message.isBlank()) {
            return "Veuillez écrire un message.";
        }

        String normalized = message.toLowerCase(Locale.ROOT).trim();

        if (normalized.contains("passé")
                || normalized.contains("passées")
                || normalized.contains("ancienne")) {

            return handlePastSessions();
        }

        if (normalized.contains("activité")
                || normalized.contains("activite")) {

            return handleActivities();
        }

        if (isSessionQuestion(normalized)) {
            return handleSessionsQuestion();
        }
        if (normalized.contains("animateur") || normalized.contains("animateurs")) {
            return handleUsersByRole("ANIMATEUR", "animateurs");
        }

        if (normalized.contains("admin") || normalized.contains("administrateur")) {
            return handleUsersByRole("ADMIN", "administrateurs");
        }

        if (normalized.contains("parent") || normalized.contains("parents")) {
            return handleUsersByRole("PARENT", "parents");
        }

        if (normalized.contains("enfant") || normalized.contains("enfants")) {
            return handleChildren();
        }

        if (normalized.contains("inscrit") || normalized.contains("inscription") || normalized.contains("inscriptions")) {
            return handleRegistrations();
        }

        return openAiService.askChatGPT(message);
    }

    private String handleUsersByRole(String role, String label) {
        List<User> users = userRepository.findByRole(role);

        if (users == null || users.isEmpty()) {
            return "Aucun " + label + " trouvé.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Voici les ").append(label).append(" :\n\n");

        users.stream().limit(10).forEach(u -> {
            sb.append("• ")
                    .append(u.getFirstName() != null ? u.getFirstName() : "")
                    .append(" ")
                    .append(u.getLastName() != null ? u.getLastName() : "")
                    .append(" — ")
                    .append(u.getEmail() != null ? u.getEmail() : "courriel non précisé")
                    .append("\n");
        });

        return sb.toString();
    }
    private String handleChildren() {
        List<Child> children = childRepository.findAll();

        if (children == null || children.isEmpty()) {
            return "Aucun enfant trouvé.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Voici les enfants enregistrés :\n\n");

        children.stream().limit(10).forEach(c -> {
            sb.append("• ")
                    .append(c.getFirstName())
                    .append(" ")
                    .append(c.getLastName())
                    .append("\n");
        });

        return sb.toString();
    }
    private String handleRegistrations() {
        List<Registration> registrations = registrationRepository.findAll();

        if (registrations == null || registrations.isEmpty()) {
            return "Aucune inscription trouvée.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Il y a ")
                .append(registrations.size())
                .append(" inscription(s) au total.\n\n");

        registrations.stream().limit(10).forEach(r -> {
            sb.append("• ");

            if (r.getChild() != null) {
                sb.append(r.getChild().getFirstName())
                        .append(" ")
                        .append(r.getChild().getLastName());
            } else {
                sb.append("Enfant non précisé");
            }

            if (r.getSession() != null && r.getSession().getActivity() != null) {
                sb.append(" — ")
                        .append(r.getSession().getActivity().getName());
            }

            sb.append("\n");
        });

        return sb.toString();
    }
    private String handlePastSessions() {

        List<Session> sessions =
                sessionRepository.findByStartDateBeforeOrderByStartDateDesc(
                        LocalDateTime.now()
                );

        if (sessions.isEmpty()) {
            return "Aucune session passée.";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm", Locale.FRENCH);

        StringBuilder sb = new StringBuilder();
        sb.append("Voici les dernières sessions :\n\n");

        sessions.stream()
                .limit(5)
                .forEach(s -> {

                    String activityName =
                            s.getActivity() != null
                                    ? s.getActivity().getName()
                                    : "Activité";

                    sb.append("• ")
                            .append(activityName)
                            .append(" — ")
                            .append(s.getStartDate().format(formatter))
                            .append("\n");
                });

        return sb.toString();
    }
    private String handleActivities() {

        List<Session> sessions = sessionRepository.findAll();

        if (sessions.isEmpty()) {
            return "Aucune activité n’est disponible.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Voici quelques activités disponibles :\n\n");

        sessions.stream()
                .map(Session::getActivity)
                .filter(a -> a != null)
                .distinct()
                .limit(8)
                .forEach(activity -> {

                    sb.append("• ")
                            .append(activity.getName());

                    if (activity.getCategorie() != null) {
                        sb.append(" — ")
                                .append(activity.getCategorie());
                    }

                    sb.append("\n");
                });

        return sb.toString();
    }

    private boolean isSessionQuestion(String message) {
        return message.contains("activité")
                || message.contains("activite")
                || message.contains("session")
                || message.contains("horaire")
                || message.contains("horaires")
                || message.contains("programme")
                || message.contains("prochaine activité")
                || message.contains("prochaine activite")
                || message.contains("disponible")
                || message.contains("places")
                || message.contains("prix")
                || message.contains("coût")
                || message.contains("cout");
    }

    private String handleSessionsQuestion() {
        List<Session> sessions =
                sessionRepository.findByStartDateAfterOrderByStartDateAsc(LocalDateTime.now());

        if (sessions == null || sessions.isEmpty()) {
            return "Aucune session à venir n’est disponible pour le moment.";
        }

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd MMM yyyy à HH:mm", Locale.FRENCH);

        StringBuilder sb = new StringBuilder();
        sb.append("Voici les prochaines sessions disponibles :\n\n");

        int limit = Math.min(sessions.size(), 8);

        for (int i = 0; i < limit; i++) {
            Session s = sessions.get(i);

            String activityName = "Activité";
            String categorie = "Catégorie non précisée";
            String prix = "Prix non précisé";

            if (s.getActivity() != null) {
                if (s.getActivity().getName() != null) {
                    activityName = s.getActivity().getName();
                }

                if (s.getActivity().getCategorie() != null) {
                    categorie = s.getActivity().getCategorie();
                }

                prix = s.getActivity().getPrixHoraire() + " $ / h";
            }

            String location = s.getLocation() != null && !s.getLocation().isBlank()
                    ? s.getLocation()
                    : "Lieu à confirmer";

            String startDate = s.getStartDate() != null
                    ? s.getStartDate().format(formatter)
                    : "Date à confirmer";

            String availableSpots = "Places non précisées";
            try {
                availableSpots = s.getAvailableSpots() + " place(s) disponible(s)";
            } catch (Exception ignored) {
            }

            sb.append("• ")
                    .append(activityName)
                    .append(" — ")
                    .append(categorie)
                    .append("\n")
                    .append("  Date : ")
                    .append(startDate)
                    .append("\n")
                    .append("  Lieu : ")
                    .append(location)
                    .append("\n")
                    .append("  Prix : ")
                    .append(prix)
                    .append("\n")
                    .append("  ")
                    .append(availableSpots)
                    .append("\n\n");
        }

        if (sessions.size() > limit) {
            sb.append("Il y a aussi d’autres sessions disponibles. Consultez la programmation pour tout voir.");
        }

        return sb.toString();
    }

}