package com.boa.api.web.rest;

import com.boa.api.domain.ParamEndPoint;
import com.boa.api.repository.ParamEndPointRepository;
import com.boa.api.service.ParamEndPointService;
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
 * REST controller for managing {@link com.boa.api.domain.ParamEndPoint}.
 */
@RestController
@RequestMapping("/api")
public class ParamEndPointResource {

    private final Logger log = LoggerFactory.getLogger(ParamEndPointResource.class);

    private static final String ENTITY_NAME = "paramEndPoint";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParamEndPointService paramEndPointService;

    private final ParamEndPointRepository paramEndPointRepository;

    public ParamEndPointResource(ParamEndPointService paramEndPointService, ParamEndPointRepository paramEndPointRepository) {
        this.paramEndPointService = paramEndPointService;
        this.paramEndPointRepository = paramEndPointRepository;
    }

    /**
     * {@code POST  /param-end-points} : Create a new paramEndPoint.
     *
     * @param paramEndPoint the paramEndPoint to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paramEndPoint, or with status {@code 400 (Bad Request)} if the paramEndPoint has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/param-end-points")
    public ResponseEntity<ParamEndPoint> createParamEndPoint(@RequestBody ParamEndPoint paramEndPoint) throws URISyntaxException {
        log.debug("REST request to save ParamEndPoint : {}", paramEndPoint);
        if (paramEndPoint.getId() != null) {
            throw new BadRequestAlertException("A new paramEndPoint cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ParamEndPoint result = paramEndPointService.save(paramEndPoint);
        return ResponseEntity
            .created(new URI("/api/param-end-points/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /param-end-points/:id} : Updates an existing paramEndPoint.
     *
     * @param id the id of the paramEndPoint to save.
     * @param paramEndPoint the paramEndPoint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramEndPoint,
     * or with status {@code 400 (Bad Request)} if the paramEndPoint is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paramEndPoint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/param-end-points/{id}")
    public ResponseEntity<ParamEndPoint> updateParamEndPoint(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamEndPoint paramEndPoint
    ) throws URISyntaxException {
        log.debug("REST request to update ParamEndPoint : {}, {}", id, paramEndPoint);
        if (paramEndPoint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramEndPoint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramEndPointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        ParamEndPoint result = paramEndPointService.save(paramEndPoint);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paramEndPoint.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /param-end-points/:id} : Partial updates given fields of an existing paramEndPoint, field will ignore if it is null
     *
     * @param id the id of the paramEndPoint to save.
     * @param paramEndPoint the paramEndPoint to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paramEndPoint,
     * or with status {@code 400 (Bad Request)} if the paramEndPoint is not valid,
     * or with status {@code 404 (Not Found)} if the paramEndPoint is not found,
     * or with status {@code 500 (Internal Server Error)} if the paramEndPoint couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/param-end-points/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<ParamEndPoint> partialUpdateParamEndPoint(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody ParamEndPoint paramEndPoint
    ) throws URISyntaxException {
        log.debug("REST request to partial update ParamEndPoint partially : {}, {}", id, paramEndPoint);
        if (paramEndPoint.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paramEndPoint.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!paramEndPointRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<ParamEndPoint> result = paramEndPointService.partialUpdate(paramEndPoint);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, paramEndPoint.getId().toString())
        );
    }

    /**
     * {@code GET  /param-end-points} : get all the paramEndPoints.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paramEndPoints in body.
     */
    @GetMapping("/param-end-points")
    public List<ParamEndPoint> getAllParamEndPoints() {
        log.debug("REST request to get all ParamEndPoints");
        return paramEndPointService.findAll();
    }

    /**
     * {@code GET  /param-end-points/:id} : get the "id" paramEndPoint.
     *
     * @param id the id of the paramEndPoint to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paramEndPoint, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/param-end-points/{id}")
    public ResponseEntity<ParamEndPoint> getParamEndPoint(@PathVariable Long id) {
        log.debug("REST request to get ParamEndPoint : {}", id);
        Optional<ParamEndPoint> paramEndPoint = paramEndPointService.findOne(id);
        return ResponseUtil.wrapOrNotFound(paramEndPoint);
    }

    /**
     * {@code DELETE  /param-end-points/:id} : delete the "id" paramEndPoint.
     *
     * @param id the id of the paramEndPoint to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/param-end-points/{id}")
    public ResponseEntity<Void> deleteParamEndPoint(@PathVariable Long id) {
        log.debug("REST request to delete ParamEndPoint : {}", id);
        paramEndPointService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
