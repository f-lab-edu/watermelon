package com.project.watermelon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.watermelon.dto.CommonBackendResponseDto;
import com.project.watermelon.dto.reservation.ReservationIdResponseDto;
import com.project.watermelon.dto.reservation.ReservationRankResponseDto;
import com.project.watermelon.enumeration.ReservationStatus;
import com.project.watermelon.exception.InvalidIdException;
import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
import com.project.watermelon.model.Reservation;
import com.project.watermelon.repository.ReservationRedisRepository;
import com.project.watermelon.repository.ReservationRepository;
import com.project.watermelon.vo.ReservationIdVo;
import com.project.watermelon.vo.ReservationRankVo;
import jakarta.annotation.PostConstruct;
import com.project.watermelon.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationService {

    private final ReservationRedisRepository reservationRedisRepository;
    private final ReservationRepository reservationRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final SecurityUtil securityUtil;

    @Value("${kafka.reservationMessageTopic}")
    private String reservationMessageTopic;

    @Transactional
    public CommonBackendResponseDto<String> produceReservationMessage(Long concertMappingId) {
        String memberEmail = securityUtil.getCurrentMemberUsername();
        String stringConcertMappingId = Long.toString(concertMappingId);

        if (isMemberExists(stringConcertMappingId, memberEmail)) {
            String message = "Member " + memberEmail + " is already registered for concert: " + stringConcertMappingId;
            log.warn(message);
            CommonBackendResponseDto<String> errorResponse = new CommonBackendResponseDto<>();
            errorResponse.markAsFailed(message);
            return errorResponse;
        }

        try {
            ProducerRecord<String, String> record = transformMessageStringToJson(memberEmail, stringConcertMappingId);
            CompletableFuture<SendResult<String, String>> completableFuture = kafkaTemplate.send(record);
            ListenableFuture<SendResult<String, String>> listenableFuture = toListenableFuture(completableFuture);

            listenableFuture.addCallback(result -> {
                reservationRedisRepository.storeUserIdWithDefaultState(memberEmail, stringConcertMappingId);
                log.info("Message sent to topic {} with offset {}", result.getRecordMetadata().topic(), result.getRecordMetadata().offset());
            }, ex -> {
                log.error("Failed to send message", ex);
            });
        } catch (Exception e) {
            log.error("Exception while sending message", e);
            throw new RuntimeException("Exception occurred during message production", e);
        }

        return new CommonBackendResponseDto<>();
    }

    private ProducerRecord<String, String> transformMessageStringToJson(String memberEmail, String stringConcertMappingId) {
        ObjectMapper objectMapper = new ObjectMapper();

        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("concertMappingId", stringConcertMappingId);
        messageMap.put("memberEmail", memberEmail);

        try {
            String messageValue = objectMapper.writeValueAsString(messageMap);
            return new ProducerRecord<>(reservationMessageTopic, stringConcertMappingId, messageValue);
        } catch (Exception e) {
            log.error("Failed to serialize message to JSON", e);
            throw new RuntimeException("Failed to serialize message to JSON", e);
        }
    }

    private Boolean isMemberExists(String stringConcertMappingId, String memberEmail) {
        HashOperations<String, String, String> hashOps = stringRedisTemplate.opsForHash();
        String key = "concertMappingId:" + stringConcertMappingId + ":memberStatus";

        boolean isMemberExists = hashOps.hasKey(key, memberEmail);

        if (!isMemberExists) {
            boolean isReservationExist = reservationRepository.existsByMemberEmailAndConcertMappingConcertMappingId(memberEmail, Long.parseLong(stringConcertMappingId));
            if (isReservationExist) {
                reservationRedisRepository.storeUserIdWithDefaultState(memberEmail, stringConcertMappingId);
                isMemberExists = true;
            }
        }
        return isMemberExists;
    }

    public ReservationIdResponseDto retrieveReservationId(Long concertMappingId, String email) {
        Reservation reservation = reservationRepository.findByConcertMapping_ConcertMappingIdAndMember_Email(concertMappingId, email).orElseThrow(
                () -> new InvalidIdException("Invalid concertMappingId.")
        );
        Long reservationId = reservation.getReservationId();

        ReservationIdVo reservationIdVo = new ReservationIdVo(reservationId);
        return new ReservationIdResponseDto(
                reservationIdVo
        );
    }

    public ReservationRankResponseDto retrieveReservationRank(Long concertMappingId, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId).orElseThrow(
                () -> new InvalidIdException("Invalid reservationId.")
        );
        Long reservationRank = reservation.getReservationRank();
        int nonWaitingCount = reservationRepository.countByConcertMapping_ConcertMappingIdAndStatusNotAndReservationRankLessThan(
                concertMappingId,
                ReservationStatus.WAIT,
                reservationRank
        );
        int currentRank = Math.toIntExact(reservationRank - nonWaitingCount);
        ReservationStatus reservationStatus = reservation.getStatus();

        ReservationRankVo reservationRankVo = new ReservationRankVo(currentRank, reservationStatus);
        return new ReservationRankResponseDto(
                reservationRankVo
        );

    private <T> ListenableFuture<T> toListenableFuture(CompletableFuture<T> completableFuture) {
        SettableListenableFuture<T> listenableFuture = new SettableListenableFuture<>();
        completableFuture.whenComplete((result, ex) -> {
            if (ex == null) {
                listenableFuture.set(result);
            } else {
                listenableFuture.setException(ex);
            }
        });
        return listenableFuture;
    }
}
