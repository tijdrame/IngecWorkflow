package com.boa.api.request;

import lombok.Data;

@Data
public class InfoAnticipationRequest {

    private String operation;
    private String dateAnticipation;
    private String idDossier;
    private Double montantRestantDu;
    private String nooper;
    private String ncg;
    private Double taxeInteret;
    private Double tauxPenalite;
    private Double mensualite;
    private String createdBy;
    private String statut;
    private String country;
    private String langue;
}
