package com.boa.api.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class RemboursementTAERequest {

    private String langue;
    private String country;
    private String dateRembAnt;
    private String nooper;
    private String numSeq;
    private Double mntRestDu;
    private Double mntRembAnt;
    private Double mntPenalite;
    private Double mntTaxePen;
    private Double tauxPenalite;
}
