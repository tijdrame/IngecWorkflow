package com.boa.api.request;

import java.time.LocalDate;
import lombok.Data;

@Data
public class LoanRequest {

    private String loantype;
    private String client;
    private String currentaccount;
    private String currency;
    private Double loanamount;
    private LocalDate firstpaymentday;
    private LocalDate contractdate;
    private Integer duration;
    private String deferredmode;
    private String endmonthAnniversary;
    private String calculationtype;
    private String reimbursementmode;
    private String interestperiodicity;
    private String reimbursementperiodicity;
    private String ratetype;
    private Double interesttaxrate;
    private String advancematurity;
    private String interetsrecovery;
    private String insurancecalculationmode;
    private String interestsrecalculation;
    private String deferredrate;
    private Double margin;
    private Double filefeesamount;
    private String filefeestaxflag;
    private Double stampfeesamount;
    private String variousfeesamount;
    private String variousfeestaxflag;
    private Double insurancefeesamount;
    private String insurancefeestaxflag;
    private String loanobjectif;
    private String loanbiendesc1;
    private String loanbiendesc2;
    private String loansimulation;
    private String usercode;
    private String langue;
    private String referenceIn;
    private Integer sequence;
    private String country;
}
