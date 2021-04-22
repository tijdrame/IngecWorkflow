package com.boa.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.boa.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParamEndPointTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ParamEndPoint.class);
        ParamEndPoint paramEndPoint1 = new ParamEndPoint();
        paramEndPoint1.setId(1L);
        ParamEndPoint paramEndPoint2 = new ParamEndPoint();
        paramEndPoint2.setId(paramEndPoint1.getId());
        assertThat(paramEndPoint1).isEqualTo(paramEndPoint2);
        paramEndPoint2.setId(2L);
        assertThat(paramEndPoint1).isNotEqualTo(paramEndPoint2);
        paramEndPoint1.setId(null);
        assertThat(paramEndPoint1).isNotEqualTo(paramEndPoint2);
    }
}
