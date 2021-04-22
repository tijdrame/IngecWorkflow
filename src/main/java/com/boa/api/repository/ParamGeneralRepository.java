package com.boa.api.repository;

import com.boa.api.domain.ParamGeneral;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ParamGeneral entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParamGeneralRepository extends JpaRepository<ParamGeneral, Long> {}
