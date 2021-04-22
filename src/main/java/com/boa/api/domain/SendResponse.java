package com.boa.api.domain;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A SendResponse.
 */
@Entity
@Table(name = "send_response")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SendResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false)
    private String code;

    @NotNull
    @Column(name = "libelle_service", nullable = false)
    private String libelleService;

    @NotNull
    @Column(name = "message_fr", nullable = false)
    private String messageFr;

    @NotNull
    @Column(name = "message_en", nullable = false)
    private String messageEn;

    @Column(name = "attribute_1")
    private String attribute1;

    @Column(name = "attribute_2")
    private String attribute2;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SendResponse id(Long id) {
        this.id = id;
        return this;
    }

    public String getCode() {
        return this.code;
    }

    public SendResponse code(String code) {
        this.code = code;
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelleService() {
        return this.libelleService;
    }

    public SendResponse libelleService(String libelleService) {
        this.libelleService = libelleService;
        return this;
    }

    public void setLibelleService(String libelleService) {
        this.libelleService = libelleService;
    }

    public String getMessageFr() {
        return this.messageFr;
    }

    public SendResponse messageFr(String messageFr) {
        this.messageFr = messageFr;
        return this;
    }

    public void setMessageFr(String messageFr) {
        this.messageFr = messageFr;
    }

    public String getMessageEn() {
        return this.messageEn;
    }

    public SendResponse messageEn(String messageEn) {
        this.messageEn = messageEn;
        return this;
    }

    public void setMessageEn(String messageEn) {
        this.messageEn = messageEn;
    }

    public String getAttribute1() {
        return this.attribute1;
    }

    public SendResponse attribute1(String attribute1) {
        this.attribute1 = attribute1;
        return this;
    }

    public void setAttribute1(String attribute1) {
        this.attribute1 = attribute1;
    }

    public String getAttribute2() {
        return this.attribute2;
    }

    public SendResponse attribute2(String attribute2) {
        this.attribute2 = attribute2;
        return this;
    }

    public void setAttribute2(String attribute2) {
        this.attribute2 = attribute2;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SendResponse)) {
            return false;
        }
        return id != null && id.equals(((SendResponse) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "SendResponse{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", libelleService='" + getLibelleService() + "'" +
            ", messageFr='" + getMessageFr() + "'" +
            ", messageEn='" + getMessageEn() + "'" +
            ", attribute1='" + getAttribute1() + "'" +
            ", attribute2='" + getAttribute2() + "'" +
            "}";
    }
}
