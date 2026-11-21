package com.file.converter.services.scheduler;

import com.file.converter.constants.Constants;
import com.file.converter.entity.InboxMessage;
import com.file.converter.entity.OutBoxMessage;
import com.file.converter.repository.ConsumerInboxRepository;
import com.file.converter.repository.OutBoxMessageRepository;
import com.file.converter.services.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConvertScheduler {
    private final FileService fileService;
    private final ConsumerInboxRepository consumerInboxRepository;
    private final OutBoxMessageRepository outBoxMessageRepository;
    private final KafkaTemplate<String,String> kafkaTemplate;
    @Value("${app.kafka.topics.convert-success-result}")
    private String convertSuccessTopic;


    @Scheduled(fixedDelayString = "${app.scheduler.rate-ms}")
    @SchedulerLock(name = "InboxPublisher", lockAtMostFor = "PT30S", lockAtLeastFor = "PT1S")
    public void handleEventToConvert(){
        List<InboxMessage> listOfNotProceedEvents = consumerInboxRepository.findByProceedFalseAndRetryCountLessThan(Constants.MAX_RETRY_CONSUMER, PageRequest.of(0,Constants.BATCHSIZE));
        for(InboxMessage inboxMessage: listOfNotProceedEvents){
            try{
                fileService.convertFile(inboxMessage.getPayLoad());
                inboxMessage.setProceed(true);
                inboxMessage.setProcessedAt(Instant.now());
            }
            catch (Exception exception){
                log.error("Не удалось обработать сообщения {}",inboxMessage.getMessageId(),exception);
                inboxMessage.setRetryCount(inboxMessage.getRetryCount()+1);
            }
            consumerInboxRepository.save(inboxMessage);
        }
    }
    @Scheduled(fixedDelayString = "${app.scheduler.rate-ms}")
    @SchedulerLock(name = "outboxPublisher", lockAtMostFor = "PT30S", lockAtLeastFor = "PT1S")
    public void handleEventToSend(){
        List<OutBoxMessage> outBoxMessageList = outBoxMessageRepository.findBySentFalseAndRetryCountLessThan(
                Constants.MAX_RETRY_CONSUMER,PageRequest.of(0,Constants.BATCHSIZE));

        for(OutBoxMessage outBoxMessage: outBoxMessageList){
            try{
                String key = String.format("User-%d",outBoxMessage.getUserId());
                kafkaTemplate.send(convertSuccessTopic,key,outBoxMessage.getPlayLoad()).get(5, TimeUnit.SECONDS);
                outBoxMessage.setSendAt(Instant.now());
                outBoxMessage.setSent(true);
                outBoxMessage.setPublishAt(Instant.now());
                outBoxMessageRepository.save(outBoxMessage);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                markFailed(outBoxMessage);
                return;

            } catch (ExecutionException | TimeoutException e) {
                markFailed(outBoxMessage);
                log.error("Не удалось отправить outbox-сообщение {} в Kafka", outBoxMessage.getId(), e);
            }
        }
    }
    private void markFailed(OutBoxMessage outBoxMessage){
        outBoxMessage.setRetry(outBoxMessage.getRetry()+1);
        outBoxMessage.setLastRetryAttempt(Instant.now());
        outBoxMessageRepository.save(outBoxMessage);
    }
}
