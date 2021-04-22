package com.boa.api.web.rest;

import com.boa.api.domain.SendResponse;
import com.boa.api.repository.SendResponseRepository;
import com.boa.api.service.SendResponseService;
import com.boa.api.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.boa.api.domain.SendResponse}.
 */
@RestController
@RequestMapping("/api")
public class SendResponseResource {

    private final Logger log = LoggerFactory.getLogger(SendResponseResource.class);

    private static final String ENTITY_NAME = "sendResponse";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SendResponseService sendResponseService;

    private final SendResponseRepository sendResponseRepository;

    public SendResponseResource(SendResponseService sendResponseService, SendResponseRepository sendResponseRepository) {
        this.sendResponseService = sendResponseService;
        this.sendResponseRepository = sendResponseRepository;
    }

    /**
     * {@code POST  /send-responses} : Create a new sendResponse.
     *
     * @param sendResponse the sendResponse to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new sendResponse, or with status {@code 400 (Bad Request)} if the sendResponse has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/send-responses")
    public ResponseEntity<SendResponse> createSendResponse(@Valid @RequestBody SendResponse sendResponse) throws URISyntaxException {
        log.debug("REST request to save SendResponse : {}", sendResponse);
        if (sendResponse.getId() != null) {
            throw new BadRequestAlertException("A new sendResponse cannot already have an ID", ENTITY_NAME, "idexists");
        }
        SendResponse result = sendResponseService.save(sendResponse);
        return ResponseEntity
            .created(new URI("/api/send-responses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /send-responses/:id} : Updates an existing sendResponse.
     *
     * @param id the id of the sendResponse to save.
     * @param sendResponse the sendResponse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sendResponse,
     * or with status {@code 400 (Bad Request)} if the sendResponse is not valid,
     * or with status {@code 500 (Internal Server Error)} if the sendResponse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/send-responses/{id}")
    public ResponseEntity<SendResponse> updateSendResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody SendResponse sendResponse
    ) throws URISyntaxException {
        log.debug("REST request to update SendResponse : {}, {}", id, sendResponse);
        if (sendResponse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sendResponse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sendResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        SendResponse result = sendResponseService.save(sendResponse);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sendResponse.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /send-responses/:id} : Partial updates given fields of an existing sendResponse, field will ignore if it is null
     *
     * @param id the id of the sendResponse to save.
     * @param sendResponse the sendResponse to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated sendResponse,
     * or with status {@code 400 (Bad Request)} if the sendResponse is not valid,
     * or with status {@code 404 (Not Found)} if the sendResponse is not found,
     * or with status {@code 500 (Internal Server Error)} if the sendResponse couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/send-responses/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<SendResponse> partialUpdateSendResponse(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody SendResponse sendResponse
    ) throws URISyntaxException {
        log.debug("REST request to partial update SendResponse partially : {}, {}", id, sendResponse);
        if (sendResponse.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, sendResponse.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!sendResponseRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<SendResponse> result = sendResponseService.partialUpdate(sendResponse);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, sendResponse.getId().toString())
        );
    }

    /**
     * {@code GET  /send-responses} : get all the sendResponses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of sendResponses in body.
     */
    @GetMapping("/send-responses")
    public ResponseEntity<List<SendResponse>> getAllSendResponses(Pageable pageable) {
        log.debug("REST request to get a page of SendResponses");
        Page<SendResponse> page = sendResponseService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /send-responses/:id} : get the "id" sendResponse.
     *
     * @param id the id of the sendResponse to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the sendResponse, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/send-responses/{id}")
    public ResponseEntity<SendResponse> getSendResponse(@PathVariable Long id) {
        log.debug("REST request to get SendResponse : {}", id);
        Optional<SendResponse> sendResponse = sendResponseService.findOne(id);
        return ResponseUtil.wrapOrNotFound(sendResponse);
    }

    /**
     * {@code DELETE  /send-responses/:id} : delete the "id" sendResponse.
     *
     * @param id the id of the sendResponse to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/send-responses/{id}")
    public ResponseEntity<Void> deleteSendResponse(@PathVariable Long id) {
        log.debug("REST request to delete SendResponse : {}", id);
        sendResponseService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
