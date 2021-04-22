package com.boa.api.web.rest;

import com.boa.api.domain.ParamGeneral;
import com.boa.api.repository.ParamGeneralRepository;
import com.boa.api.service.ParamGeneralService;
import com.boa.api.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.boa.api.domain.ParamGeneral}.
 */
@RestController
@RequestMapping("/api")
public class ParamGeneralResource {

    private final Logger log = LoggerFactory.getLogger(ParamGeneralResource.class);

    private static final String ENTITY_NAME = "paramGeneral";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParamGeneralService paramGeneralService;

    private final ParamGeneralRepository paramGeneralRepository;

    public ParamGeneralResource(ParamGeneralService paramGeneralService, ParamGeneralRepository paramGeneralRepository) {
        this.paramGeneralService = paramGeneralService;
        this.paramGeneralRepository = paramGeneralRepository;
    }

    /**
     * {@code POST  /param-generals} : Create a new paramGeneral.
     *
     * @param paramGeneral the paramGeneral to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paramGeneral, or with status {@code 400 (Bad Request)} if the paramGeneral has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/param-generals")
    public ResponseEntity<ParamGeneral> createParamGeneral(@RequestBody ParamGeneral paramGeneral) throws URISyntaxException {
        log.debug("REST request to save ParamGeneral : {}", paramGeneral);
        if (paramGeneral.getId() != null) {
            throw new BadRequestAlertException("A new paramGeneral cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParamGeneral result = paramGeneralService.save(paramGeneral);
        return ResponseEntity
            .created(new URI("/api/param-generals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /param-generals/:id} : Updates an existing paramGeneral.
     *
     * @param id the id of the paramGeneral to save.
     * @param paramGeneral the paramGeneral to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramGeneral,
     * or with status {@code 400 (Bad Request)} if the paramGeneral is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paramGeneral couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/param-generals/{id}")
    public ResponseEntity<ParamGeneral> updateParamGeneral(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamGeneral paramGeneral
    ) throws URISyntaxException {
        log.debug("REST request to update ParamGeneral : {}, {}", id, paramGeneral);
        if (paramGeneral.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramGeneral.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramGeneralRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ParamGeneral result = paramGeneralService.save(paramGeneral);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paramGeneral.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /param-generals/:id} : Partial updates given fields of an existing paramGeneral, field will ignore if it is null
     *
     * @param id the id of the paramGeneral to save.
     * @param paramGeneral the paramGeneral to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramGeneral,
     * or with status {@code 400 (Bad Request)} if the paramGeneral is not valid,
     * or with status {@code 404 (Not Found)} if the paramGeneral is not found,
     * or with status {@code 500 (Internal Server Error)} if the paramGeneral couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/param-generals/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ParamGeneral> partialUpdateParamGeneral(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamGeneral paramGeneral
    ) throws URISyntaxException {
        log.debug("REST request to partial update ParamGeneral partially : {}, {}", id, paramGeneral);
        if (paramGeneral.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramGeneral.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramGeneralRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParamGeneral> result = paramGeneralService.partialUpdate(paramGeneral);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paramGeneral.getId().toString())
        );
    }

    /**
     * {@code GET  /param-generals} : get all the paramGenerals.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paramGenerals in body.
     */
    @GetMapping("/param-generals")
    public List<ParamGeneral> getAllParamGenerals() {
        log.debug("REST request to get all ParamGenerals");
        return paramGeneralService.findAll();
    }

    /**
     * {@code GET  /param-generals/:id} : get the "id" paramGeneral.
     *
     * @param id the id of the paramGeneral to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paramGeneral, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/param-generals/{id}")
    public ResponseEntity<ParamGeneral> getParamGeneral(@PathVariable Long id) {
        log.debug("REST request to get ParamGeneral : {}", id);
        Optional<ParamGeneral> paramGeneral = paramGeneralService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paramGeneral);
    }

    /**
     * {@code DELETE  /param-generals/:id} : delete the "id" paramGeneral.
     *
     * @param id the id of the paramGeneral to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/param-generals/{id}")
    public ResponseEntity<Void> deleteParamGeneral(@PathVariable Long id) {
        log.debug("REST request to delete ParamGeneral : {}", id);
        paramGeneralService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
