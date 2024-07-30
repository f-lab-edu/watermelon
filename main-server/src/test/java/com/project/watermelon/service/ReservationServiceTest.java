//package com.project.watermelon.service;
//
//import com.project.watermelon.dto.CommonBackendResponseDto;
//import com.project.watermelon.exception.MemberAlreadyRequestReservationException;
//import com.project.watermelon.repository.ReservationRedisRepository;
//import com.project.watermelon.repository.ReservationRepository;
//import org.apache.kafka.clients.producer.KafkaProducer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//import org.apache.kafka.clients.producer.RecordMetadata;
//import org.apache.kafka.common.TopicPartition;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.HashOperations;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Future;
//import java.util.concurrent.TimeoutException;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//class ReservationServiceTest {
//
//    @Mock
//    private ReservationRedisRepository reservationRedisRepository;
//
//    @Mock
//    private ReservationRepository reservationRepository;
//
//    @Mock
//    private KafkaProducer<String, String> kafkaProducer;
//
//    @Mock
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Mock
//    private HashOperations<String, String, String> hashOperations;
//
//    @InjectMocks
//    private ReservationService reservationService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        doReturn(hashOperations).when(stringRedisTemplate).opsForHash();
//        // Set the value for the reservationMessageTopic field
//        ReflectionTestUtils.setField(reservationService, "reservationMessageTopic", "test-topic");
//
//    }
//
//    @Test
//    void testProduceReservationMessageSuccess() throws Exception {
//        String memberEmail = "test@example.com";
//        Long concertMappingId = 1L;
//        String stringConcertMappingId = Long.toString(concertMappingId);
//
//        // Mock KafkaProducer behavior
//        Future<RecordMetadata> futureMock = mock(Future.class);
//        TopicPartition topicPartition = new TopicPartition("test", 0);
//        RecordMetadata metadata = new RecordMetadata(topicPartition, 0, 0, 0L, null, 0, 0);
//        when(futureMock.get(anyLong(), any())).thenReturn(metadata);
//        when(kafkaProducer.send(any(ProducerRecord.class))).thenReturn(futureMock);
//
//        // Mock Redis behavior
//        when(hashOperations.hasKey(anyString(), anyString())).thenReturn(false);
//
//        CommonBackendResponseDto<String> response = reservationService.produceReservationMessage(memberEmail, concertMappingId);
//
//        assertNotNull(response);
//        verify(kafkaProducer, times(1)).send(any(ProducerRecord.class));
//        verify(kafkaProducer, times(1)).beginTransaction();
//        verify(kafkaProducer, times(1)).commitTransaction();
//    }
//
//    @Test
//    void testProduceReservationMessageMemberAlreadyExists() {
//        String memberEmail = "test@example.com";
//        Long concertMappingId = 1L;
//        String stringConcertMappingId = Long.toString(concertMappingId);
//
//        // Mock Redis behavior
//        when(hashOperations.hasKey(anyString(), anyString())).thenReturn(true);
//
//        assertThrows(MemberAlreadyRequestReservationException.class, () -> {
//            reservationService.produceReservationMessage(memberEmail, concertMappingId);
//        });
//
//        verify(kafkaProducer, never()).send(any(ProducerRecord.class));
//        verify(kafkaProducer, never()).beginTransaction();
//        verify(kafkaProducer, never()).commitTransaction();
//    }
//
//    @Test
//    void testProduceReservationMessageKafkaTimeout() throws Exception {
//        String memberEmail = "test@example.com";
//        Long concertMappingId = 1L;
//        String stringConcertMappingId = Long.toString(concertMappingId);
//
//        // Mock KafkaProducer behavior
//        Future<RecordMetadata> futureMock = mock(Future.class);
//        when(futureMock.get(anyLong(), any())).thenThrow(new ExecutionException(new TimeoutException()));
//        when(kafkaProducer.send(any(ProducerRecord.class))).thenReturn(futureMock);
//
//        // Mock Redis behavior
//        when(hashOperations.hasKey(anyString(), anyString())).thenReturn(false);
//
//        CommonBackendResponseDto<String> response = reservationService.produceReservationMessage(memberEmail, concertMappingId);
//
//        assertNotNull(response);
//        verify(kafkaProducer, times(1)).send(any(ProducerRecord.class));
//        verify(kafkaProducer, times(1)).beginTransaction();
//        verify(kafkaProducer, times(1)).abortTransaction();
//    }
//}
