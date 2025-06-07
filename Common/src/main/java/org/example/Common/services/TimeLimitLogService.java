package org.example.Common.services;

import org.example.Common.entities.TimeLimitExceedLog;
import org.example.Common.repositories.TimeLimitExceedLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TimeLimitLogService {

    public final TimeLimitExceedLogRepository timeLimitExceedLogRepository;


    public TimeLimitLogService(TimeLimitExceedLogRepository timeLimitExceedLogRepository) {
        this.timeLimitExceedLogRepository = timeLimitExceedLogRepository;
    }

    public void saveRow(String methodName, Long executionTime ,  Long timeLimit){
        TimeLimitExceedLog timeLimitExceedLog = new TimeLimitExceedLog();
        timeLimitExceedLog.setMethodName(methodName);
        timeLimitExceedLog.setExecutionTime(executionTime );
        timeLimitExceedLog.setTimeLimit(timeLimit);
        timeLimitExceedLog.setTimestamp(LocalDateTime.now());
        timeLimitExceedLogRepository.save(timeLimitExceedLog);
    }
}
