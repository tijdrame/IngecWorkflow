package com.boa.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.boa.api.IntegrationTest;
import com.boa.api.domain.ParamEndPoint;
import com.boa.api.repository.ParamEndPointRepository;
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
 * Integration tests for the {@link ParamEndPointResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ParamEndPointResourceIT {

    private static final String DEFAULT_CODE_PARAM = "AAAAAAAAAA";
    private static final String UPDATED_CODE_PARAM = "BBBBBBBBBB";

    private static final String DEFAULT_END_POINTS = "AAAAAAAAAA";
    private static final String UPDATED_END_POINTS = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_01 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_01 = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_02 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_02 = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_03 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_03 = "BBBBBBBBBB";

    private static final String DEFAULT_ATTRIBUTE_04 = "AAAAAAAAAA";
    private static final String UPDATED_ATTRIBUTE_04 = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/param-end-points";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParamEndPointRepository paramEndPointRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restParamEndPointMockMvc;

    private ParamEndPoint paramEndPoint;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParamEndPoint createEntity(EntityManager em) {
        ParamEndPoint paramEndPoint = new ParamEndPoint()
            .codeParam(DEFAULT_CODE_PARAM)
            .endPoints(DEFAULT_END_POINTS)
            .attribute01(DEFAULT_ATTRIBUTE_01)
            .attribute02(DEFAULT_ATTRIBUTE_02)
            .attribute03(DEFAULT_ATTRIBUTE_03)
            .attribute04(DEFAULT_ATTRIBUTE_04);
        return paramEndPoint;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ParamEndPoint createUpdatedEntity(EntityManager em) {
        ParamEndPoint paramEndPoint = new ParamEndPoint()
            .codeParam(UPDATED_CODE_PARAM)
            .endPoints(UPDATED_END_POINTS)
            .attribute01(UPDATED_ATTRIBUTE_01)
            .attribute02(UPDATED_ATTRIBUTE_02)
            .attribute03(UPDATED_ATTRIBUTE_03)
            .attribute04(UPDATED_ATTRIBUTE_04);
        return paramEndPoint;
    }

    @BeforeEach
    public void initTest() {
        paramEndPoint = createEntity(em);
    }

    @Test
    @Transactional
    void createParamEndPoint() throws Exception {
        int databaseSizeBeforeCreate = paramEndPointRepository.findAll().size();
        // Create the ParamEndPoint
        restParamEndPointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paramEndPoint)))
            .andExpect(status().isCreated());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeCreate + 1);
        ParamEndPoint testParamEndPoint = paramEndPointList.get(paramEndPointList.size() - 1);
        assertThat(testParamEndPoint.getCodeParam()).isEqualTo(DEFAULT_CODE_PARAM);
        assertThat(testParamEndPoint.getEndPoints()).isEqualTo(DEFAULT_END_POINTS);
        assertThat(testParamEndPoint.getAttribute01()).isEqualTo(DEFAULT_ATTRIBUTE_01);
        assertThat(testParamEndPoint.getAttribute02()).isEqualTo(DEFAULT_ATTRIBUTE_02);
        assertThat(testParamEndPoint.getAttribute03()).isEqualTo(DEFAULT_ATTRIBUTE_03);
        assertThat(testParamEndPoint.getAttribute04()).isEqualTo(DEFAULT_ATTRIBUTE_04);
    }

    @Test
    @Transactional
    void createParamEndPointWithExistingId() throws Exception {
        // Create the ParamEndPoint with an existing ID
        paramEndPoint.setId(1L);

        int databaseSizeBeforeCreate = paramEndPointRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restParamEndPointMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paramEndPoint)))
            .andExpect(status().isBadRequest());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllParamEndPoints() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        // Get all the paramEndPointList
        restParamEndPointMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(paramEndPoint.getId().intValue())))
            .andExpect(jsonPath("$.[*].codeParam").value(hasItem(DEFAULT_CODE_PARAM)))
            .andExpect(jsonPath("$.[*].endPoints").value(hasItem(DEFAULT_END_POINTS)))
            .andExpect(jsonPath("$.[*].attribute01").value(hasItem(DEFAULT_ATTRIBUTE_01)))
            .andExpect(jsonPath("$.[*].attribute02").value(hasItem(DEFAULT_ATTRIBUTE_02)))
            .andExpect(jsonPath("$.[*].attribute03").value(hasItem(DEFAULT_ATTRIBUTE_03)))
            .andExpect(jsonPath("$.[*].attribute04").value(hasItem(DEFAULT_ATTRIBUTE_04)));
    }

    @Test
    @Transactional
    void getParamEndPoint() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        // Get the paramEndPoint
        restParamEndPointMockMvc
            .perform(get(ENTITY_API_URL_ID, paramEndPoint.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(paramEndPoint.getId().intValue()))
            .andExpect(jsonPath("$.codeParam").value(DEFAULT_CODE_PARAM))
            .andExpect(jsonPath("$.endPoints").value(DEFAULT_END_POINTS))
            .andExpect(jsonPath("$.attribute01").value(DEFAULT_ATTRIBUTE_01))
            .andExpect(jsonPath("$.attribute02").value(DEFAULT_ATTRIBUTE_02))
            .andExpect(jsonPath("$.attribute03").value(DEFAULT_ATTRIBUTE_03))
            .andExpect(jsonPath("$.attribute04").value(DEFAULT_ATTRIBUTE_04));
    }

    @Test
    @Transactional
    void getNonExistingParamEndPoint() throws Exception {
        // Get the paramEndPoint
        restParamEndPointMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewParamEndPoint() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();

        // Update the paramEndPoint
        ParamEndPoint updatedParamEndPoint = paramEndPointRepository.findById(paramEndPoint.getId()).get();
        // Disconnect from session so that the updates on updatedParamEndPoint are not directly saved in db
        em.detach(updatedParamEndPoint);
        updatedParamEndPoint
            .codeParam(UPDATED_CODE_PARAM)
            .endPoints(UPDATED_END_POINTS)
            .attribute01(UPDATED_ATTRIBUTE_01)
            .attribute02(UPDATED_ATTRIBUTE_02)
            .attribute03(UPDATED_ATTRIBUTE_03)
            .attribute04(UPDATED_ATTRIBUTE_04);

        restParamEndPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedParamEndPoint.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedParamEndPoint))
            )
            .andExpect(status().isOk());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
        ParamEndPoint testParamEndPoint = paramEndPointList.get(paramEndPointList.size() - 1);
        assertThat(testParamEndPoint.getCodeParam()).isEqualTo(UPDATED_CODE_PARAM);
        assertThat(testParamEndPoint.getEndPoints()).isEqualTo(UPDATED_END_POINTS);
        assertThat(testParamEndPoint.getAttribute01()).isEqualTo(UPDATED_ATTRIBUTE_01);
        assertThat(testParamEndPoint.getAttribute02()).isEqualTo(UPDATED_ATTRIBUTE_02);
        assertThat(testParamEndPoint.getAttribute03()).isEqualTo(UPDATED_ATTRIBUTE_03);
        assertThat(testParamEndPoint.getAttribute04()).isEqualTo(UPDATED_ATTRIBUTE_04);
    }

    @Test
    @Transactional
    void putNonExistingParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, paramEndPoint.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paramEndPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(paramEndPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(paramEndPoint)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateParamEndPointWithPatch() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();

        // Update the paramEndPoint using partial update
        ParamEndPoint partialUpdatedParamEndPoint = new ParamEndPoint();
        partialUpdatedParamEndPoint.setId(paramEndPoint.getId());

        partialUpdatedParamEndPoint.endPoints(UPDATED_END_POINTS);

        restParamEndPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParamEndPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParamEndPoint))
            )
            .andExpect(status().isOk());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
        ParamEndPoint testParamEndPoint = paramEndPointList.get(paramEndPointList.size() - 1);
        assertThat(testParamEndPoint.getCodeParam()).isEqualTo(DEFAULT_CODE_PARAM);
        assertThat(testParamEndPoint.getEndPoints()).isEqualTo(UPDATED_END_POINTS);
        assertThat(testParamEndPoint.getAttribute01()).isEqualTo(DEFAULT_ATTRIBUTE_01);
        assertThat(testParamEndPoint.getAttribute02()).isEqualTo(DEFAULT_ATTRIBUTE_02);
        assertThat(testParamEndPoint.getAttribute03()).isEqualTo(DEFAULT_ATTRIBUTE_03);
        assertThat(testParamEndPoint.getAttribute04()).isEqualTo(DEFAULT_ATTRIBUTE_04);
    }

    @Test
    @Transactional
    void fullUpdateParamEndPointWithPatch() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();

        // Update the paramEndPoint using partial update
        ParamEndPoint partialUpdatedParamEndPoint = new ParamEndPoint();
        partialUpdatedParamEndPoint.setId(paramEndPoint.getId());

        partialUpdatedParamEndPoint
            .codeParam(UPDATED_CODE_PARAM)
            .endPoints(UPDATED_END_POINTS)
            .attribute01(UPDATED_ATTRIBUTE_01)
            .attribute02(UPDATED_ATTRIBUTE_02)
            .attribute03(UPDATED_ATTRIBUTE_03)
            .attribute04(UPDATED_ATTRIBUTE_04);

        restParamEndPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedParamEndPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedParamEndPoint))
            )
            .andExpect(status().isOk());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
        ParamEndPoint testParamEndPoint = paramEndPointList.get(paramEndPointList.size() - 1);
        assertThat(testParamEndPoint.getCodeParam()).isEqualTo(UPDATED_CODE_PARAM);
        assertThat(testParamEndPoint.getEndPoints()).isEqualTo(UPDATED_END_POINTS);
        assertThat(testParamEndPoint.getAttribute01()).isEqualTo(UPDATED_ATTRIBUTE_01);
        assertThat(testParamEndPoint.getAttribute02()).isEqualTo(UPDATED_ATTRIBUTE_02);
        assertThat(testParamEndPoint.getAttribute03()).isEqualTo(UPDATED_ATTRIBUTE_03);
        assertThat(testParamEndPoint.getAttribute04()).isEqualTo(UPDATED_ATTRIBUTE_04);
    }

    @Test
    @Transactional
    void patchNonExistingParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, paramEndPoint.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paramEndPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(paramEndPoint))
            )
            .andExpect(status().isBadRequest());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamParamEndPoint() throws Exception {
        int databaseSizeBeforeUpdate = paramEndPointRepository.findAll().size();
        paramEndPoint.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restParamEndPointMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(paramEndPoint))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ParamEndPoint in the database
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteParamEndPoint() throws Exception {
        // Initialize the database
        paramEndPointRepository.saveAndFlush(paramEndPoint);

        int databaseSizeBeforeDelete = paramEndPointRepository.findAll().size();

        // Delete the paramEndPoint
        restParamEndPointMockMvc
            .perform(delete(ENTITY_API_URL_ID, paramEndPoint.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ParamEndPoint> paramEndPointList = paramEndPointRepository.findAll();
        assertThat(paramEndPointList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
