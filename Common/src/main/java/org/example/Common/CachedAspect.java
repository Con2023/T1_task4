package org.example.Common;
import lombok.Getter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CachedAspect {

    @Value("${cache.expiration.time.minutes}")
    private Long timeLimit;

    @Getter
        private record CacheEntry(Object value, Long timestamp) {

    }

    public final Map<String, CacheEntry> cacheEntry = new ConcurrentHashMap<>();

    @Around("@annotation(cachedAnnotation)")
    public Object cacheMethod(ProceedingJoinPoint joinPoint, CachedAnnotation cachedAnnotation) throws Throwable {
        String key = createKey(joinPoint);
        CacheEntry entry = cacheEntry.get(key);

        if (entry != null && isExistAndOk(key, entry)) {
            return entry.value;
        }
        Object result = joinPoint.proceed();
        cacheEntry.put(key, new CacheEntry(result, System.currentTimeMillis()));
        return result;

    }
    private String createKey(ProceedingJoinPoint joinPoint){
        return joinPoint.getSignature()+ Arrays.toString(joinPoint.getArgs());
    }
    private boolean isExistAndOk(String key, CacheEntry entry){
        Long currentTime = System.currentTimeMillis();
        Long entryAgeMinutes = TimeUnit.MILLISECONDS.toMinutes(currentTime - entry.timestamp());
        if (entryAgeMinutes >= timeLimit) {
            cacheEntry.remove(key);
            return false;
        }
        return true;
    }
}
