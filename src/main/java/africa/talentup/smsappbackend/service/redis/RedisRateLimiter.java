package africa.talentup.smsappbackend.service.redis;


import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisRateLimiter {

//    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimiter.class);

    @Autowired
    private StringRedisTemplate stringTemplate;

    private static final int REQUESTS_PER_24_HOURS = 50;

    public boolean isAllowed(String phoneNumber) {
        final int hour = LocalDateTime.now().getHour();
        String key = phoneNumber + ":" + hour;
        ValueOperations<String, String> operations = stringTemplate.opsForValue();
        String requests = operations.get(key);
        if(StringUtils.isNotBlank(requests) && Integer.parseInt(requests) >= REQUESTS_PER_24_HOURS) return false;

        List<Object> txResults = stringTemplate.execute(new SessionCallback<>() {
            @Override
            public <K, V> List<Object> execute(@NonNull RedisOperations<K, V> operations) throws DataAccessException {
                final StringRedisTemplate redisTemplate = (StringRedisTemplate) operations;
                final ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
                operations.multi();
                valueOperations.increment(key);
                redisTemplate.expire(key, 24, TimeUnit.HOURS);

                return operations.exec();
            }
        });
//        assert txResults != null;
//        logger.info("Current request count: " + txResults.get(0));
        return true;
    }
}
