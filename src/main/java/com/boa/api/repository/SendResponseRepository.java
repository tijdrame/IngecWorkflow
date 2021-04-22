package com.boa.api.repository;

import com.boa.api.domain.SendResponse;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the SendResponse entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SendResponseRepository extends JpaRepository<SendResponse, Long> {}
