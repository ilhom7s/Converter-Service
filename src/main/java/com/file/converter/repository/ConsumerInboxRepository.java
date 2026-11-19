package com.file.converter.repository;
import com.file.converter.entity.InboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ConsumerInboxRepository extends JpaRepository<InboxMessage, UUID> {
    boolean existsByMessageId(String messageId);
    List<InboxMessage> findByProceedFalseAndRetryCountLessThan(int maxRetry);
}