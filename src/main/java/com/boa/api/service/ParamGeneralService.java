package com.boa.api.service;

import com.boa.api.domain.ParamGeneral;
import com.boa.api.repository.ParamGeneralRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ParamGeneral}.
 */
@Service
@Transactional
public class ParamGeneralService {

    private final Logger log = LoggerFactory.getLogger(ParamGeneralService.class);

    private final ParamGeneralRepository paramGeneralRepository;

    public ParamGeneralService(ParamGeneralRepository paramGeneralRepository) {
        this.paramGeneralRepository = paramGeneralRepository;
    }

    /**
     * Save a paramGeneral.
     *
     * @param paramGeneral the entity to save.
     * @return the persisted entity.
     */
    public ParamGeneral save(ParamGeneral paramGeneral) {
        log.debug("Request to save ParamGeneral : {}", paramGeneral);
        return paramGeneralRepository.save(paramGeneral);
    }

    /**
     * Partially update a paramGeneral.
     *
     * @param paramGeneral the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ParamGeneral> partialUpdate(ParamGeneral paramGeneral) {
        log.debug("Request to partially update ParamGeneral : {}", paramGeneral);

        return paramGeneralRepository
            .findById(paramGeneral.getId())
            .map(
                existingParamGeneral -> {
                    if (paramGeneral.getCode() != null) {
                        existingParamGeneral.setCode(paramGeneral.getCode());
                    }
                    if (paramGeneral.getPays() != null) {
                        existingParamGeneral.setPays(paramGeneral.getPays());
                    }
                    if (paramGeneral.getVarString1() != null) {
                        existingParamGeneral.setVarString1(paramGeneral.getVarString1());
                    }
                    if (paramGeneral.getVarString2() != null) {
                        existingParamGeneral.setVarString2(paramGeneral.getVarString2());
                    }
                    if (paramGeneral.getVarString3() != null) {
                        existingParamGeneral.setVarString3(paramGeneral.getVarString3());
                    }
                    if (paramGeneral.getVarInteger1() != null) {
                        existingParamGeneral.setVarInteger1(paramGeneral.getVarInteger1());
                    }
                    if (paramGeneral.getVarInteger2() != null) {
                        existingParamGeneral.setVarInteger2(paramGeneral.getVarInteger2());
                    }
                    if (paramGeneral.getVarInteger3() != null) {
                        existingParamGeneral.setVarInteger3(paramGeneral.getVarInteger3());
                    }
                    if (paramGeneral.getVarDouble1() != null) {
                        existingParamGeneral.setVarDouble1(paramGeneral.getVarDouble1());
                    }
                    if (paramGeneral.getVarDouble2() != null) {
                        existingParamGeneral.setVarDouble2(paramGeneral.getVarDouble2());
                    }
                    if (paramGeneral.getVarDouble3() != null) {
                        existingParamGeneral.setVarDouble3(paramGeneral.getVarDouble3());
                    }
                    if (paramGeneral.getVarInstant() != null) {
                        existingParamGeneral.setVarInstant(paramGeneral.getVarInstant());
                    }
                    if (paramGeneral.getVarDate() != null) {
                        existingParamGeneral.setVarDate(paramGeneral.getVarDate());
                    }
                    if (paramGeneral.getVarBoolean() != null) {
                        existingParamGeneral.setVarBoolean(paramGeneral.getVarBoolean());
                    }

                    return existingParamGeneral;
                }
            )
            .map(paramGeneralRepository::save);
    }

    /**
     * Get all the paramGenerals.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ParamGeneral> findAll() {
        log.debug("Request to get all ParamGenerals");
        return paramGeneralRepository.findAll();
    }

    /**
     * Get one paramGeneral by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ParamGeneral> findOne(Long id) {
        log.debug("Request to get ParamGeneral : {}", id);
        return paramGeneralRepository.findById(id);
    }

    /**
     * Delete the paramGeneral by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ParamGeneral : {}", id);
        paramGeneralRepository.deleteById(id);
    }
}
