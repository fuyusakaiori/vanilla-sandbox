package com.vanillaware.sandbox.redis.redisson.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.Objects;

public class RedissonClientFactory {

    private volatile static RedissonClient redissonClient;


    public static RedissonClient getRedissonClient() {
        if (Objects.isNull(redissonClient)) {
            synchronized (RedissonClient.class) {
                if (Objects.isNull(redissonClient)) {
                    Config config = new Config();
                    config.useSingleServer()
                            .setAddress("127.0.0.1:6379");
                    return redissonClient = Redisson.create(config);
                }
            }
        }
        return redissonClient;
    }

}
