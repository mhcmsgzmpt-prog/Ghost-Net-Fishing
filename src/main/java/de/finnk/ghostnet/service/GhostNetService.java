package de.finnk.ghostnet.service;

import de.finnk.ghostnet.model.GhostNet;
import de.finnk.ghostnet.model.GhostNetStatus;
import de.finnk.ghostnet.model.ReportingPerson;
import de.finnk.ghostnet.model.RecoveringPerson;
import de.finnk.ghostnet.repository.GhostNetRepository;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;


@Service
public class GhostNetService {

    private final GhostNetRepository ghostNetRepository;

    public GhostNetService(GhostNetRepository ghostNetRepository) {
        this.ghostNetRepository = ghostNetRepository;
    }

    public List<GhostNet> getAllGhostNets() {
        return ghostNetRepository.findAll();
    }

    public GhostNet getGhostNetById(Long id) {
        return ghostNetRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Geisternetz mit der ID " + id + " nicht gefunden"
                        )
                );
    }

    public GhostNet createGhostNet(GhostNet ghostNet) {
        ReportingPerson reportingPerson = ghostNet.getReportingPerson();
       
        if (reportingPerson == null){
            throw new IllegalArgumentException(
                    "Für die Meldung muss eine meldende Person angegeben werden.");
        }

        if (reportingPerson.isAnonymous()) {
            reportingPerson.setName(null);
            reportingPerson.setPhoneNumber(null);
        } else {
            if (reportingPerson.getName() == null || reportingPerson.getName().isBlank()) {
                throw new IllegalArgumentException(
                        "Bitte geben Sie Ihren Namen an oder wählen Sie die anonyme Meldung."
                );
            }
            if (reportingPerson.getPhoneNumber() == null || reportingPerson.getPhoneNumber().isBlank()) {
                throw new IllegalArgumentException(
                        "Bitte geben Sie Ihre Telefonnummer an oder wählen Sie die anonyme Meldung."
                );
            }
        }
        ghostNet.setStatus(GhostNetStatus.REPORTED);
        return ghostNetRepository.save(ghostNet);
    }

    public GhostNet updateGhostNetStatus(Long id, GhostNetStatus status) {
        GhostNet ghostNet = getGhostNetById(id);

        if (ghostNet.getStatus() == GhostNetStatus.RECOVERED) {
            throw new IllegalStateException(
                    "Der Status des Geisternetzes kann nicht geändert werden."
            );
        }

        if (status == GhostNetStatus.MISSING) {
            throw new IllegalArgumentException(
                    "Der Status Vermisst kann nur über die Verschollenmeldung gesetzt werden."
            );
        }

        ghostNet.setStatus(status);
        return ghostNetRepository.save(ghostNet);
    }

    public GhostNet reportMissingGhostNet(
            Long id,
            ReportingPerson missingReportingPerson) {

        GhostNet ghostNet = getGhostNetById(id);

        if (ghostNet.getStatus() == GhostNetStatus.RECOVERED) {
            throw new IllegalStateException(
                    "Ein bereits geborgenes Netz kann nicht als verschollen gemeldet werden."
            );
        }

        if (missingReportingPerson.getName() == null
                || missingReportingPerson.getName().isBlank()) {

            throw new IllegalArgumentException(
                    "Für die Verschollenmeldung muss ein Name angegeben werden."
            );
        }

        if (missingReportingPerson.getPhoneNumber() == null
                || missingReportingPerson.getPhoneNumber().isBlank()) {

            throw new IllegalArgumentException(
                    "Für die Verschollenmeldung muss eine Telefonnummer angegeben werden."
            );
        }

        missingReportingPerson.setAnonymous(false);

        ghostNet.setStatus(GhostNetStatus.MISSING);
        ghostNet.setMissingReportingPerson(missingReportingPerson);

        return ghostNetRepository.save(ghostNet);
    }

    public GhostNet assignRecoveringPerson(Long id, RecoveringPerson recoveringPerson) {
        GhostNet ghostNet = getGhostNetById(id);

        if (ghostNet.getStatus() == GhostNetStatus.RECOVERED) {
            throw new IllegalStateException(
                    "Ein bereits geborgenes Netz kann nicht erneut übernommen werden."
            );
        }

        if (ghostNet.getRecoveringPerson() != null) {
            throw new IllegalStateException(
                    "Dieses geisternetz wurde bereits von einer bergenden Person übernommen."
            );
        }

        if (recoveringPerson.getName() == null || recoveringPerson.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Bitte geben Sie Ihren Namen an."
            );
        }

        if (recoveringPerson.getPhoneNumber() == null || recoveringPerson.getPhoneNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Bitte geben Sie Ihre Telefonnummer an."
            );
        }
        ghostNet.setRecoveringPerson(recoveringPerson);
        ghostNet.setStatus(GhostNetStatus.RECOVERY_PENDING);
        return ghostNetRepository.save(ghostNet);
    }

    public void deleteGhostNet(Long id) {
        GhostNet ghostNet = getGhostNetById(id);
        ghostNetRepository.delete(ghostNet);
    }
}
