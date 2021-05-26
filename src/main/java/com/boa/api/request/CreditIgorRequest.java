package com.boa.api.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class CreditIgorRequest {

    private String country;
    private String nooperIngec;
    private Integer njandb;
    private String devise;
    private LocalDate dateOper;
    private LocalDate dateDep;
    private LocalDate datePremRemb;
    private Double montantPret;
    private String valide;
    private Double txtax;
    private String moddif;
    private String ncgprt;
    private String client;
    private String compte;
    private String compteDeb;
    private String perint;
    private String typrmb;
    private String intdif;
    private String perrmb;
    private String xrecalc;
    private String xcomdec;
    private String typcalc;
    private String modrmb;
    private String typetaux;
    private String xrecalcd;
    private String modass;
    private String xdos;
    private String fraisDossier;
    private String txt1;
    private Double txavg;
    private String xtaxdos;
    private Double mnttaxdos;
    private Double txfongar;
    private Double mntfongar;
    private Double txfonass;
    private Double mntfonass;
    private Double txassini;
    private Double mntnetcli;
    private Double mtassur;
    private String datrmbfm;
    private Double cvmnttaxdos;
    private String cptass;
    private Integer duree;
    private Double txannuel;
    private LocalDate datcontr;
    private String langue;
}
