package com.boa.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.boa.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SendResponseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SendResponse.class);
        SendResponse sendResponse1 = new SendResponse();
        sendResponse1.setId(1L);
        SendResponse sendResponse2 = new SendResponse();
        sendResponse2.setId(sendResponse1.getId());
        assertThat(sendResponse1).isEqualTo(sendResponse2);
        sendResponse2.setId(2L);
        assertThat(sendResponse1).isNotEqualTo(sendResponse2);
        sendResponse1.setId(null);
        assertThat(sendResponse1).isNotEqualTo(sendResponse2);
    }
}
