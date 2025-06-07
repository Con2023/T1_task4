import org.example.Common.TaskApplication;
import org.example.Common.entities.TimeLimitExceedLog;
import org.example.Common.repositories.TimeLimitExceedLogRepository;
import org.example.Common.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertFalse;


@SpringBootTest(classes = TaskApplication.class)
@TestPropertySource(properties = {
        "metric.time-limit-millis=1000"
})
public class MetricAspectTest {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TimeLimitExceedLogRepository timeLimitExceedLogRepository;

    @Test
    void testMetricAspect() throws InterruptedException {
        transactionService.slowMethod();
        Thread.sleep(100);
        List<TimeLimitExceedLog> logs = timeLimitExceedLogRepository.findAll();
        assertFalse(logs.isEmpty(), "В БД должна быть запись о превышении времени");
    }
}
