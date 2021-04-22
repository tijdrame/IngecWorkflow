package com.boa.api.service;

import com.boa.api.domain.SendResponse;
import com.boa.api.repository.SendResponseRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link SendResponse}.
 */
@Service
@Transactional
public class SendResponseService {

    private final Logger log = LoggerFactory.getLogger(SendResponseService.class);

    private final SendResponseRepository sendResponseRepository;

    public SendResponseService(SendResponseRepository sendResponseRepository) {
        this.sendResponseRepository = sendResponseRepository;
    }

    /**
     * Save a sendResponse.
     *
     * @param sendResponse the entity to save.
     * @return the persisted entity.
     */
    public SendResponse save(SendResponse sendResponse) {
        log.debug("Request to save SendResponse : {}", sendResponse);
        return sendResponseRepository.save(sendResponse);
    }

    /**
     * Partially update a sendResponse.
     *
     * @param sendResponse the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<SendResponse> partialUpdate(SendResponse sendResponse) {
        log.debug("Request to partially update SendResponse : {}", sendResponse);

        return sendResponseRepository
            .findById(sendResponse.getId())
            .map(
                existingSendResponse -> {
                    if (sendResponse.getCode() != null) {
                        existingSendResponse.setCode(sendResponse.getCode());
                    }
                    if (sendResponse.getLibelleService() != null) {
                        existingSendResponse.setLibelleService(sendResponse.getLibelleService());
                    }
                    if (sendResponse.getMessageFr() != null) {
                        existingSendResponse.setMessageFr(sendResponse.getMessageFr());
                    }
                    if (sendResponse.getMessageEn() != null) {
                        existingSendResponse.setMessageEn(sendResponse.getMessageEn());
                    }
                    if (sendResponse.getAttribute1() != null) {
                        existingSendResponse.setAttribute1(sendResponse.getAttribute1());
                    }
                    if (sendResponse.getAttribute2() != null) {
                        existingSendResponse.setAttribute2(sendResponse.getAttribute2());
                    }

                    return existingSendResponse;
                }
            )
            .map(sendResponseRepository::save);
    }

    /**
     * Get all the sendResponses.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<SendResponse> findAll(Pageable pageable) {
        log.debug("Request to get all SendResponses");
        return sendResponseRepository.findAll(pageable);
    }

    /**
     * Get one sendResponse by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<SendResponse> findOne(Long id) {
        log.debug("Request to get SendResponse : {}", id);
        return sendResponseRepository.findById(id);
    }

    /**
     * Delete the sendResponse by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete SendResponse : {}", id);
        sendResponseRepository.deleteById(id);
    }
}
