package com.boa.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.boa.api.IntegrationTest;
import com.boa.api.domain.SendResponse;
import com.boa.api.repository.SendResponseRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link SendResponseResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class SendResponseResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_LIBELLE_SERVICE = "AAAAAAAAAA";
    private static final String UPDATED_LIBELLE_SERVICE = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_FR = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_FR = "BBBBBBBBBB";

    private static final String DEFAULT_MESSAGE_EN = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE_EN = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_2 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/send-responses";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SendResponseRepository sendResponseRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restSendResponseMockMvc;

    private SendResponse sendResponse;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SendResponse createEntity(EntityManager em) {
        SendResponse sendResponse = new SendResponse()
            .code(DEFAULT_CODE)
            .libelleService(DEFAULT_LIBELLE_SERVICE)
            .messageFr(DEFAULT_MESSAGE_FR)
            .messageEn(DEFAULT_MESSAGE_EN)
            .attribute1(DEFAULT_ATTRIBUTE_1)
            .attribute2(DEFAULT_ATTRIBUTE_2);
        return sendResponse;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SendResponse createUpdatedEntity(EntityManager em) {
        SendResponse sendResponse = new SendResponse()
            .code(UPDATED_CODE)
            .libelleService(UPDATED_LIBELLE_SERVICE)
            .messageFr(UPDATED_MESSAGE_FR)
            .messageEn(UPDATED_MESSAGE_EN)
            .attribute1(UPDATED_ATTRIBUTE_1)
            .attribute2(UPDATED_ATTRIBUTE_2);
        return sendResponse;
    }

    @BeforeEach
    public void initTest() {
        sendResponse = createEntity(em);
    }

    @Test
    @Transactional
    void createSendResponse() throws Exception {
        int databaseSizeBeforeCreate = sendResponseRepository.findAll().size();
        // Create the SendResponse
        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isCreated());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeCreate + 1);
        SendResponse testSendResponse = sendResponseList.get(sendResponseList.size() - 1);
        assertThat(testSendResponse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSendResponse.getLibelleService()).isEqualTo(DEFAULT_LIBELLE_SERVICE);
        assertThat(testSendResponse.getMessageFr()).isEqualTo(DEFAULT_MESSAGE_FR);
        assertThat(testSendResponse.getMessageEn()).isEqualTo(DEFAULT_MESSAGE_EN);
        assertThat(testSendResponse.getAttribute1()).isEqualTo(DEFAULT_ATTRIBUTE_1);
        assertThat(testSendResponse.getAttribute2()).isEqualTo(DEFAULT_ATTRIBUTE_2);
    }

    @Test
    @Transactional
    void createSendResponseWithExistingId() throws Exception {
        // Create the SendResponse with an existing ID
        sendResponse.setId(1L);

        int databaseSizeBeforeCreate = sendResponseRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isBadRequest());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = sendResponseRepository.findAll().size();
        // set the field null
        sendResponse.setCode(null);

        // Create the SendResponse, which fails.

        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isBadRequest());

        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLibelleServiceIsRequired() throws Exception {
        int databaseSizeBeforeTest = sendResponseRepository.findAll().size();
        // set the field null
        sendResponse.setLibelleService(null);

        // Create the SendResponse, which fails.

        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isBadRequest());

        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageFrIsRequired() throws Exception {
        int databaseSizeBeforeTest = sendResponseRepository.findAll().size();
        // set the field null
        sendResponse.setMessageFr(null);

        // Create the SendResponse, which fails.

        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isBadRequest());

        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMessageEnIsRequired() throws Exception {
        int databaseSizeBeforeTest = sendResponseRepository.findAll().size();
        // set the field null
        sendResponse.setMessageEn(null);

        // Create the SendResponse, which fails.

        restSendResponseMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isBadRequest());

        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllSendResponses() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        // Get all the sendResponseList
        restSendResponseMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(sendResponse.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].libelleService").value(hasItem(DEFAULT_LIBELLE_SERVICE)))
            .andExpect(jsonPath("$.[*].messageFr").value(hasItem(DEFAULT_MESSAGE_FR)))
            .andExpect(jsonPath("$.[*].messageEn").value(hasItem(DEFAULT_MESSAGE_EN)))
            .andExpect(jsonPath("$.[*].attribute1").value(hasItem(DEFAULT_ATTRIBUTE_1)))
            .andExpect(jsonPath("$.[*].attribute2").value(hasItem(DEFAULT_ATTRIBUTE_2)));
    }

    @Test
    @Transactional
    void getSendResponse() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        // Get the sendResponse
        restSendResponseMockMvc
            .perform(get(ENTITY_API_URL_ID, sendResponse.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(sendResponse.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.libelleService").value(DEFAULT_LIBELLE_SERVICE))
            .andExpect(jsonPath("$.messageFr").value(DEFAULT_MESSAGE_FR))
            .andExpect(jsonPath("$.messageEn").value(DEFAULT_MESSAGE_EN))
            .andExpect(jsonPath("$.attribute1").value(DEFAULT_ATTRIBUTE_1))
            .andExpect(jsonPath("$.attribute2").value(DEFAULT_ATTRIBUTE_2));
    }

    @Test
    @Transactional
    void getNonExistingSendResponse() throws Exception {
        // Get the sendResponse
        restSendResponseMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewSendResponse() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();

        // Update the sendResponse
        SendResponse updatedSendResponse = sendResponseRepository.findById(sendResponse.getId()).get();
        // Disconnect from session so that the updates on updatedSendResponse are not directly saved in db
        em.detach(updatedSendResponse);
        updatedSendResponse
            .code(UPDATED_CODE)
            .libelleService(UPDATED_LIBELLE_SERVICE)
            .messageFr(UPDATED_MESSAGE_FR)
            .messageEn(UPDATED_MESSAGE_EN)
            .attribute1(UPDATED_ATTRIBUTE_1)
            .attribute2(UPDATED_ATTRIBUTE_2);

        restSendResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedSendResponse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedSendResponse))
            )
            .andExpect(status().isOk());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
        SendResponse testSendResponse = sendResponseList.get(sendResponseList.size() - 1);
        assertThat(testSendResponse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSendResponse.getLibelleService()).isEqualTo(UPDATED_LIBELLE_SERVICE);
        assertThat(testSendResponse.getMessageFr()).isEqualTo(UPDATED_MESSAGE_FR);
        assertThat(testSendResponse.getMessageEn()).isEqualTo(UPDATED_MESSAGE_EN);
        assertThat(testSendResponse.getAttribute1()).isEqualTo(UPDATED_ATTRIBUTE_1);
        assertThat(testSendResponse.getAttribute2()).isEqualTo(UPDATED_ATTRIBUTE_2);
    }

    @Test
    @Transactional
    void putNonExistingSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, sendResponse.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sendResponse))
            )
            .andExpect(status().isBadRequest());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(sendResponse))
            )
            .andExpect(status().isBadRequest());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(sendResponse)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateSendResponseWithPatch() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();

        // Update the sendResponse using partial update
        SendResponse partialUpdatedSendResponse = new SendResponse();
        partialUpdatedSendResponse.setId(sendResponse.getId());

        partialUpdatedSendResponse.libelleService(UPDATED_LIBELLE_SERVICE).messageFr(UPDATED_MESSAGE_FR).attribute1(UPDATED_ATTRIBUTE_1);

        restSendResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSendResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSendResponse))
            )
            .andExpect(status().isOk());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
        SendResponse testSendResponse = sendResponseList.get(sendResponseList.size() - 1);
        assertThat(testSendResponse.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testSendResponse.getLibelleService()).isEqualTo(UPDATED_LIBELLE_SERVICE);
        assertThat(testSendResponse.getMessageFr()).isEqualTo(UPDATED_MESSAGE_FR);
        assertThat(testSendResponse.getMessageEn()).isEqualTo(DEFAULT_MESSAGE_EN);
        assertThat(testSendResponse.getAttribute1()).isEqualTo(UPDATED_ATTRIBUTE_1);
        assertThat(testSendResponse.getAttribute2()).isEqualTo(DEFAULT_ATTRIBUTE_2);
    }

    @Test
    @Transactional
    void fullUpdateSendResponseWithPatch() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();

        // Update the sendResponse using partial update
        SendResponse partialUpdatedSendResponse = new SendResponse();
        partialUpdatedSendResponse.setId(sendResponse.getId());

        partialUpdatedSendResponse
            .code(UPDATED_CODE)
            .libelleService(UPDATED_LIBELLE_SERVICE)
            .messageFr(UPDATED_MESSAGE_FR)
            .messageEn(UPDATED_MESSAGE_EN)
            .attribute1(UPDATED_ATTRIBUTE_1)
            .attribute2(UPDATED_ATTRIBUTE_2);

        restSendResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedSendResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedSendResponse))
            )
            .andExpect(status().isOk());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
        SendResponse testSendResponse = sendResponseList.get(sendResponseList.size() - 1);
        assertThat(testSendResponse.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testSendResponse.getLibelleService()).isEqualTo(UPDATED_LIBELLE_SERVICE);
        assertThat(testSendResponse.getMessageFr()).isEqualTo(UPDATED_MESSAGE_FR);
        assertThat(testSendResponse.getMessageEn()).isEqualTo(UPDATED_MESSAGE_EN);
        assertThat(testSendResponse.getAttribute1()).isEqualTo(UPDATED_ATTRIBUTE_1);
        assertThat(testSendResponse.getAttribute2()).isEqualTo(UPDATED_ATTRIBUTE_2);
    }

    @Test
    @Transactional
    void patchNonExistingSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, sendResponse.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sendResponse))
            )
            .andExpect(status().isBadRequest());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(sendResponse))
            )
            .andExpect(status().isBadRequest());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamSendResponse() throws Exception {
        int databaseSizeBeforeUpdate = sendResponseRepository.findAll().size();
        sendResponse.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restSendResponseMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(sendResponse))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the SendResponse in the database
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteSendResponse() throws Exception {
        // Initialize the database
        sendResponseRepository.saveAndFlush(sendResponse);

        int databaseSizeBeforeDelete = sendResponseRepository.findAll().size();

        // Delete the sendResponse
        restSendResponseMockMvc
            .perform(delete(ENTITY_API_URL_ID, sendResponse.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<SendResponse> sendResponseList = sendResponseRepository.findAll();
        assertThat(sendResponseList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
