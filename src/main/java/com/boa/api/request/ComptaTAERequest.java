package com.boa.api.request;

import lombok.Data;

@Data
public class ComptaTAERequest {

    private String langue;
    private String country;
    private String idDossier;
    private String codeAssur;
    private String noOper;
    private String codeOper;
    private String refrel;

    private String ncgClient;
    private String compteClient;
    private String ncgAssur;
    private String ncgFiscal;
    private String cptAssur;
    private String ncgAssurPrdt;
    private String cptAssurPrdt;
    private String cptFiscal;
    private Double mntAssur;
    private Double mntFiscal;
    private Double tauxCions;
    private Double mntPret;

    private String nomClient;
    private String client;
}
