package com.boa.api.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AutorisationRequest {

    private String langue;
    private String country;
    private String idDossier;
    private String codDossier;
    private String expl;
    private String client;
    private String ncg;
    private String devise;
    private Double montant;
    private LocalDate dateDebut;
    private LocalDate dateEch;
    private String compte;
    private String valide;
}
