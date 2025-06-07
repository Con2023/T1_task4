package org.example.Common;
import org.example.Common.services.TimeLimitLogService;
import org.springframework.beans.factory.annotation.Value;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Component
@Aspect
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class MetricAspect {

    private final KafkaTemplate<String, String> kafkaTemplate;
    public final TimeLimitLogService serviceTimeLimitLog;

    public MetricAspect(KafkaTemplate<String, String> kafkaTemplate, TimeLimitLogService serviceTimeLimitLog) {
        this.kafkaTemplate = kafkaTemplate;
        this.serviceTimeLimitLog = serviceTimeLimitLog;
    }

    @Value("${metric.time-limit-millis}")
    private Long timeLimitMillis;

    private final String metricsTopic = "t1_demo_metrics";

    @Around("@annotation(org.example.Common.Metric)")
    public Object methodLimitedLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        Long executionTime = System.currentTimeMillis() - startTime;
        if (executionTime > timeLimitMillis) {
            String messageError = String.format("Method %s exceeded time limit. Execution time: %d ms, Limit: %d ms",
                    joinPoint.getSignature().toShortString(), executionTime, timeLimitMillis);
            try {
                Message<String> messageToKafka = MessageBuilder
                        .withPayload(messageError)
                        .setHeader(KafkaHeaders.TOPIC, metricsTopic)
                        .setHeader(KafkaHeaders.KEY, "error-metrics-key")
                        .setHeader("errorType", "METRICS")
                        .build();

                kafkaTemplate.send(messageToKafka).get();
            }
            catch (Exception e) {
                serviceTimeLimitLog.saveRow(joinPoint.getSignature().toShortString(), executionTime, timeLimitMillis);
            }
        }
        return result;
    }
}
