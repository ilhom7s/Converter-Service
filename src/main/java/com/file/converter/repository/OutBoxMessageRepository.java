package com.file.converter.repository;

import com.file.converter.entity.OutBoxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutBoxMessageRepository extends JpaRepository<OutBoxMessage, UUID> {
    List<OutBoxMessage> findBySentFalseAndRetryCountLessThan(int maxRetry);
}