package com.boa.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.boa.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParamGeneralTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParamGeneral.class);
        ParamGeneral paramGeneral1 = new ParamGeneral();
        paramGeneral1.setId(1L);
        ParamGeneral paramGeneral2 = new ParamGeneral();
        paramGeneral2.setId(paramGeneral1.getId());
        assertThat(paramGeneral1).isEqualTo(paramGeneral2);
        paramGeneral2.setId(2L);
        assertThat(paramGeneral1).isNotEqualTo(paramGeneral2);
        paramGeneral1.setId(null);
        assertThat(paramGeneral1).isNotEqualTo(paramGeneral2);
    }
}
