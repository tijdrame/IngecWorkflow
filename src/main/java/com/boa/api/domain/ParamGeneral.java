package com.boa.api.domain;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParamGeneral.
 */
@Entity
@Table(name = "param_general")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ParamGeneral implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "pays")
    private String pays;

    @Column(name = "var_string_1")
    private String varString1;

    @Column(name = "var_string_2")
    private String varString2;

    @Column(name = "var_string_3")
    private String varString3;

    @Column(name = "var_integer_1")
    private Integer varInteger1;

    @Column(name = "var_integer_2")
    private Integer varInteger2;

    @Column(name = "var_integer_3")
    private Integer varInteger3;

    @Column(name = "var_double_1")
    private Double varDouble1;

    @Column(name = "var_double_2")
    private Double varDouble2;

    @Column(name = "var_double_3")
    private Double varDouble3;

    @Column(name = "var_instant")
    private Instant varInstant;

    @Column(name = "var_date")
    private LocalDate varDate;

    @Column(name = "var_boolean")
    private Boolean varBoolean;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParamGeneral id(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public ParamGeneral code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPays() {
        return this.pays;
    }

    public ParamGeneral pays(String pays) {
        this.pays = pays;
        return this;
    }

    public void setPays(String pays) {
        this.pays = pays;
    }

    public String getVarString1() {
        return this.varString1;
    }

    public ParamGeneral varString1(String varString1) {
        this.varString1 = varString1;
        return this;
    }

    public void setVarString1(String varString1) {
        this.varString1 = varString1;
    }

    public String getVarString2() {
        return this.varString2;
    }

    public ParamGeneral varString2(String varString2) {
        this.varString2 = varString2;
        return this;
    }

    public void setVarString2(String varString2) {
        this.varString2 = varString2;
    }

    public String getVarString3() {
        return this.varString3;
    }

    public ParamGeneral varString3(String varString3) {
        this.varString3 = varString3;
        return this;
    }

    public void setVarString3(String varString3) {
        this.varString3 = varString3;
    }

    public Integer getVarInteger1() {
        return this.varInteger1;
    }

    public ParamGeneral varInteger1(Integer varInteger1) {
        this.varInteger1 = varInteger1;
        return this;
    }

    public void setVarInteger1(Integer varInteger1) {
        this.varInteger1 = varInteger1;
    }

    public Integer getVarInteger2() {
        return this.varInteger2;
    }

    public ParamGeneral varInteger2(Integer varInteger2) {
        this.varInteger2 = varInteger2;
        return this;
    }

    public void setVarInteger2(Integer varInteger2) {
        this.varInteger2 = varInteger2;
    }

    public Integer getVarInteger3() {
        return this.varInteger3;
    }

    public ParamGeneral varInteger3(Integer varInteger3) {
        this.varInteger3 = varInteger3;
        return this;
    }

    public void setVarInteger3(Integer varInteger3) {
        this.varInteger3 = varInteger3;
    }

    public Double getVarDouble1() {
        return this.varDouble1;
    }

    public ParamGeneral varDouble1(Double varDouble1) {
        this.varDouble1 = varDouble1;
        return this;
    }

    public void setVarDouble1(Double varDouble1) {
        this.varDouble1 = varDouble1;
    }

    public Double getVarDouble2() {
        return this.varDouble2;
    }

    public ParamGeneral varDouble2(Double varDouble2) {
        this.varDouble2 = varDouble2;
        return this;
    }

    public void setVarDouble2(Double varDouble2) {
        this.varDouble2 = varDouble2;
    }

    public Double getVarDouble3() {
        return this.varDouble3;
    }

    public ParamGeneral varDouble3(Double varDouble3) {
        this.varDouble3 = varDouble3;
        return this;
    }

    public void setVarDouble3(Double varDouble3) {
        this.varDouble3 = varDouble3;
    }

    public Instant getVarInstant() {
        return this.varInstant;
    }

    public ParamGeneral varInstant(Instant varInstant) {
        this.varInstant = varInstant;
        return this;
    }

    public void setVarInstant(Instant varInstant) {
        this.varInstant = varInstant;
    }

    public LocalDate getVarDate() {
        return this.varDate;
    }

    public ParamGeneral varDate(LocalDate varDate) {
        this.varDate = varDate;
        return this;
    }

    public void setVarDate(LocalDate varDate) {
        this.varDate = varDate;
    }

    public Boolean getVarBoolean() {
        return this.varBoolean;
    }

    public ParamGeneral varBoolean(Boolean varBoolean) {
        this.varBoolean = varBoolean;
        return this;
    }

    public void setVarBoolean(Boolean varBoolean) {
        this.varBoolean = varBoolean;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParamGeneral)) {
            return false;
        }
        return id != null && id.equals(((ParamGeneral) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParamGeneral{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", pays='" + getPays() + "'" +
            ", varString1='" + getVarString1() + "'" +
            ", varString2='" + getVarString2() + "'" +
            ", varString3='" + getVarString3() + "'" +
            ", varInteger1=" + getVarInteger1() +
            ", varInteger2=" + getVarInteger2() +
            ", varInteger3=" + getVarInteger3() +
            ", varDouble1=" + getVarDouble1() +
            ", varDouble2=" + getVarDouble2() +
            ", varDouble3=" + getVarDouble3() +
            ", varInstant='" + getVarInstant() + "'" +
            ", varDate='" + getVarDate() + "'" +
            ", varBoolean='" + getVarBoolean() + "'" +
            "}";
    }
}
