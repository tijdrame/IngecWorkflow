package com.boa.api.domain;

import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A ParamEndPoint.
 */
@Entity
@Table(name = "param_end_point")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ParamEndPoint implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "code_param")
    private String codeParam;

    @Column(name = "end_points")
    private String endPoints;

    @Column(name = "attribute_01")
    private String attribute01;

    @Column(name = "attribute_02")
    private String attribute02;

    @Column(name = "attribute_03")
    private String attribute03;

    @Column(name = "attribute_04")
    private String attribute04;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParamEndPoint id(Long id) {
        this.id = id;
        return this;
    }

    public String getCodeParam() {
        return this.codeParam;
    }

    public ParamEndPoint codeParam(String codeParam) {
        this.codeParam = codeParam;
        return this;
    }

    public void setCodeParam(String codeParam) {
        this.codeParam = codeParam;
    }

    public String getEndPoints() {
        return this.endPoints;
    }

    public ParamEndPoint endPoints(String endPoints) {
        this.endPoints = endPoints;
        return this;
    }

    public void setEndPoints(String endPoints) {
        this.endPoints = endPoints;
    }

    public String getAttribute01() {
        return this.attribute01;
    }

    public ParamEndPoint attribute01(String attribute01) {
        this.attribute01 = attribute01;
        return this;
    }

    public void setAttribute01(String attribute01) {
        this.attribute01 = attribute01;
    }

    public String getAttribute02() {
        return this.attribute02;
    }

    public ParamEndPoint attribute02(String attribute02) {
        this.attribute02 = attribute02;
        return this;
    }

    public void setAttribute02(String attribute02) {
        this.attribute02 = attribute02;
    }

    public String getAttribute03() {
        return this.attribute03;
    }

    public ParamEndPoint attribute03(String attribute03) {
        this.attribute03 = attribute03;
        return this;
    }

    public void setAttribute03(String attribute03) {
        this.attribute03 = attribute03;
    }

    public String getAttribute04() {
        return this.attribute04;
    }

    public ParamEndPoint attribute04(String attribute04) {
        this.attribute04 = attribute04;
        return this;
    }

    public void setAttribute04(String attribute04) {
        this.attribute04 = attribute04;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ParamEndPoint)) {
            return false;
        }
        return id != null && id.equals(((ParamEndPoint) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ParamEndPoint{" +
            "id=" + getId() +
            ", codeParam='" + getCodeParam() + "'" +
            ", endPoints='" + getEndPoints() + "'" +
            ", attribute01='" + getAttribute01() + "'" +
            ", attribute02='" + getAttribute02() + "'" +
            ", attribute03='" + getAttribute03() + "'" +
            ", attribute04='" + getAttribute04() + "'" +
            "}";
    }
}
