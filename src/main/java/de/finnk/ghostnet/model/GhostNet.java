package de.finnk.ghostnet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "ghost_nets")
public class GhostNet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Bitte einen Breitengrad eingeben")
    @DecimalMin(
            value = "-90.0",
            message = "Der Breitengrad muss mindestens -90 sein"
    )
    @DecimalMax(
            value = "90.0",
            message = "Der Breitengrad muss höchstens 90 sein"
    )
    private Double latitude;

    @NotNull(message = "Bitte einen Längengrad eingeben")
    @DecimalMin(
            value = "-180.0",
            message = "Der Längengrad muss mindestens -180 sein"
    )
    @DecimalMax(
            value = "180.0",
            message = "Der Längengrad muss höchstens 180 sein"
    )
    private Double longitude;

    @NotNull(message = "Bitte eine geschätzte Größe eingeben")
    @Positive(message = "Die geschätzte Größe muss größer als null sein")
    private Double estimatedSize;

    @Enumerated(EnumType.STRING)
    private GhostNetStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    private ReportingPerson reportingPerson;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "recovering_person_id")
    private RecoveringPerson recoveringPerson;

    @ManyToOne(cascade = CascadeType.ALL)
    private ReportingPerson missingReportingPerson;

    public GhostNet() {
        this.status = GhostNetStatus.REPORTED;
    }

    public GhostNet(
            Double latitude,
            Double longitude,
            Double estimatedSize) {

        this.latitude = latitude;
        this.longitude = longitude;
        this.estimatedSize = estimatedSize;
        this.status = GhostNetStatus.REPORTED;
    }

    public Long getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getEstimatedSize() {
        return estimatedSize;
    }

    public void setEstimatedSize(Double estimatedSize) {
        this.estimatedSize = estimatedSize;
    }

    public GhostNetStatus getStatus() {
        return status;
    }

    public void setStatus(GhostNetStatus status) {
        this.status = status;
    }

    public ReportingPerson getReportingPerson() {
        return reportingPerson;
    }

    public void setReportingPerson(ReportingPerson reportingPerson) {
        this.reportingPerson = reportingPerson;
    }

    public RecoveringPerson getRecoveringPerson() {
        return recoveringPerson;
    }

    public void setRecoveringPerson(RecoveringPerson recoveringPerson) {
        this.recoveringPerson = recoveringPerson;
    }

    public ReportingPerson getMissingReportingPerson() {
        return missingReportingPerson;
    }

    public void setMissingReportingPerson(
            ReportingPerson missingReportingPerson) {

        this.missingReportingPerson = missingReportingPerson;
    }
}