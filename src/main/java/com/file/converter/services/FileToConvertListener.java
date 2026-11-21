package com.file.converter.services;
import com.file.converter.entity.InboxMessage;
import com.file.converter.repository.ConsumerInboxRepository;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class FileToConvertListener {
    private final ConsumerInboxRepository consumerInboxRepository;
    @KafkaListener(topics = "convert-request",groupId = "convert-group")
    @Transactional
    public void receivedFileToConvert(ConsumerRecord<String,String> consumerRecord){
        String messageId = String.format("%s - %s - %s",consumerRecord.topic(),consumerRecord.partition(),consumerRecord.offset());
        if(consumerInboxRepository.existsByMessageId(messageId))return;
        InboxMessage inboxMessage = new InboxMessage();
        inboxMessage.setMessageId(messageId);
        inboxMessage.setTopic(consumerRecord.topic());
        inboxMessage.setPayload(consumerRecord.value());
        consumerInboxRepository.save(inboxMessage);
    }
}
