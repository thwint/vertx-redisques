package org.swisspush.redisques.util;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.swisspush.redisques.util.RedisquesConfiguration.*;

/**
 * Tests for {@link RedisquesConfiguration} class.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class RedisquesConfigurationTest {

    @Test
    public void testDefaultConfiguration(TestContext testContext) {
        RedisquesConfiguration config = new RedisquesConfiguration();
        testContext.assertEquals(config.getAddress(), "redisques");
        testContext.assertEquals(config.getConfigurationUpdatedAddress(), "redisques-configuration-updated");
        testContext.assertEquals(config.getRedisPrefix(), "redisques:");
        testContext.assertEquals(config.getProcessorAddress(), "redisques-processor");
        testContext.assertEquals(config.getRefreshPeriod(), 10);
        testContext.assertEquals(config.getRedisHost(), "localhost");
        testContext.assertEquals(config.getRedisPort(), 6379);
        testContext.assertEquals(config.getRedisEnableTls(), false);
        testContext.assertEquals(config.getRedisReconnectAttempts(), 0);
        testContext.assertEquals(config.getRedisReconnectDelaySec(), 30);
        testContext.assertEquals(config.getCheckInterval(), 60);
        testContext.assertEquals(config.getProcessorTimeout(), 240000);
        testContext.assertEquals(config.getProcessorDelayMax(), 0L);
        testContext.assertFalse(config.getHttpRequestHandlerEnabled());
        testContext.assertFalse(config.getHttpRequestHandlerAuthenticationEnabled());
        testContext.assertEquals(config.getHttpRequestHandlerPrefix(), "/queuing");
        testContext.assertNull(config.getHttpRequestHandlerUsername());
        testContext.assertNull(config.getHttpRequestHandlerPassword());
        testContext.assertEquals(config.getHttpRequestHandlerPort(), 7070);
        testContext.assertEquals(config.getHttpRequestHandlerUserHeader(), "x-rp-usr");
        testContext.assertEquals(config.getQueueConfigurations().size(), 0);
        testContext.assertEquals(config.getQueueSpeedIntervalSec(),60);
        testContext.assertEquals(config.getMemoryUsageLimitPercent(),100);
    }

    @Test
    public void testOverrideConfiguration(TestContext testContext) {

        RedisquesConfiguration config = with()
                .address("new_address")
                .configurationUpdatedAddress("config_updated")
                .redisHost("anotherhost")
                .redisPort(1234)
                .redisEnableTls(true)
                .redisReconnectAttempts(15)
                .redisReconnectDelaySec(60)
                .checkInterval(5)
                .processorTimeout(10)
                .processorDelayMax(50)
                .httpRequestHandlerEnabled(true)
                .httpRequestHandlerAuthenticationEnabled(true)
                .httpRequestHandlerPrefix("/queuing/test")
                .httpRequestHandlerUsername("foo")
                .httpRequestHandlerPassword("bar")
                .httpRequestHandlerPort(7171)
                .httpRequestHandlerUserHeader("x-custom-user-header")
                .queueConfigurations(Collections.singletonList(
                        new QueueConfiguration().withPattern("vehicle-.*").withRetryIntervals(10, 20, 30, 60)
                ))
                .queueSpeedIntervalSec(1)
                .memoryUsageLimitPercent(80)
                .build();

        // default values
        testContext.assertEquals(config.getRedisPrefix(), "redisques:");
        testContext.assertEquals(config.getProcessorAddress(), "redisques-processor");
        testContext.assertEquals(config.getRefreshPeriod(), 10);

        // overridden values
        testContext.assertEquals(config.getAddress(), "new_address");
        testContext.assertEquals(config.getConfigurationUpdatedAddress(), "config_updated");
        testContext.assertEquals(config.getRedisHost(), "anotherhost");
        testContext.assertEquals(config.getRedisPort(), 1234);
        testContext.assertEquals(config.getRedisEnableTls(), true);
        testContext.assertEquals(config.getRedisReconnectAttempts(), 15);
        testContext.assertEquals(config.getRedisReconnectDelaySec(), 60);
        testContext.assertEquals(config.getCheckInterval(), 5);
        testContext.assertEquals(config.getProcessorTimeout(), 10);
        testContext.assertEquals(config.getProcessorDelayMax(), 50L);
        testContext.assertTrue(config.getHttpRequestHandlerEnabled());
        testContext.assertTrue(config.getHttpRequestHandlerAuthenticationEnabled());
        testContext.assertEquals(config.getHttpRequestHandlerPrefix(), "/queuing/test");
        testContext.assertEquals(config.getHttpRequestHandlerUsername(), "foo");
        testContext.assertEquals(config.getHttpRequestHandlerPassword(), "bar");
        testContext.assertEquals(config.getHttpRequestHandlerPort(), 7171);
        testContext.assertEquals(config.getHttpRequestHandlerUserHeader(), "x-custom-user-header");
        testContext.assertEquals(config.getQueueSpeedIntervalSec(), 1);
        testContext.assertEquals(config.getMemoryUsageLimitPercent(), 80);
        // queue configurations
        testContext.assertEquals(config.getQueueConfigurations().size(), 1);
        QueueConfiguration queueConfiguration = config.getQueueConfigurations().get(0);
        testContext.assertEquals(queueConfiguration.getPattern(), "vehicle-.*");
        testContext.assertTrue(Arrays.equals(queueConfiguration.getRetryIntervals(), new int[]{10, 20, 30, 60}));
    }

    @Test
    public void testGetDefaultAsJsonObject(TestContext testContext) {
        RedisquesConfiguration config = new RedisquesConfiguration();
        JsonObject json = config.asJsonObject();

        testContext.assertEquals(json.getString(PROP_ADDRESS), "redisques");
        testContext.assertEquals(json.getString(PROP_CONFIGURATION_UPDATED_ADDRESS), "redisques-configuration-updated");
        testContext.assertEquals(json.getString(PROP_REDIS_PREFIX), "redisques:");
        testContext.assertEquals(json.getString(PROP_PROCESSOR_ADDRESS), "redisques-processor");
        testContext.assertEquals(json.getInteger(PROP_REFRESH_PERIOD), 10);
        testContext.assertEquals(json.getString(PROP_REDIS_HOST), "localhost");
        testContext.assertEquals(json.getInteger(PROP_REDIS_PORT), 6379);
        testContext.assertFalse(json.getBoolean(PROP_REDIS_ENABLE_TLS));
        testContext.assertEquals(json.getInteger(PROP_REDIS_RECONNECT_ATTEMPTS), 0);
        testContext.assertEquals(json.getInteger(PROP_REDIS_RECONNECT_DELAY_SEC), 30);
        testContext.assertEquals(json.getInteger(PROP_CHECK_INTERVAL), 60);
        testContext.assertEquals(json.getInteger(PROP_PROCESSOR_TIMEOUT), 240000);
        testContext.assertEquals(json.getInteger(PROP_PROCESSOR_DELAY_MAX), 0);
        testContext.assertFalse(json.getBoolean(PROP_HTTP_REQUEST_HANDLER_ENABLED));
        testContext.assertFalse(json.getBoolean(PROP_HTTP_REQUEST_HANDLER_AUTH_ENABLED));
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_PREFIX), "/queuing");
        testContext.assertNull(json.getString(PROP_HTTP_REQUEST_HANDLER_USERNAME));
        testContext.assertNull(json.getString(PROP_HTTP_REQUEST_HANDLER_PASSWORD));
        testContext.assertEquals(json.getInteger(PROP_HTTP_REQUEST_HANDLER_PORT), 7070);
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_USER_HEADER), "x-rp-usr");
        testContext.assertEquals(json.getJsonArray(PROP_QUEUE_CONFIGURATIONS).getList().size(), 0);
        testContext.assertEquals(json.getInteger(PROP_QUEUE_SPEED_INTERVAL_SEC), 60);
        testContext.assertEquals(json.getInteger(PROP_MEMORY_USAGE_LIMIT_PCT), 100);
    }

    @Test
    public void testGetOverriddenAsJsonObject(TestContext testContext) {

        RedisquesConfiguration config = with()
                .address("new_address")
                .configurationUpdatedAddress("config_updated")
                .redisHost("anotherhost")
                .redisPort(1234)
                .redisEnableTls(true)
                .redisReconnectAttempts(22)
                .redisReconnectDelaySec(55)
                .checkInterval(5)
                .processorTimeout(20)
                .processorDelayMax(50)
                .httpRequestHandlerPort(7171)
                .httpRequestHandlerAuthenticationEnabled(true)
                .httpRequestHandlerUsername("foo")
                .httpRequestHandlerPassword("bar")
                .httpRequestHandlerUserHeader("x-custom-user-header")
                .queueConfigurations(Collections.singletonList(
                        new QueueConfiguration().withPattern("vehicle-.*").withRetryIntervals(10, 20, 30, 60)
                ))
                .queueSpeedIntervalSec(1)
                .memoryUsageLimitPercent(55)
                .build();

        JsonObject json = config.asJsonObject();

        // default values
        testContext.assertEquals(json.getString(PROP_REDIS_PREFIX), "redisques:");
        testContext.assertEquals(json.getString(PROP_PROCESSOR_ADDRESS), "redisques-processor");
        testContext.assertEquals(json.getInteger(PROP_REFRESH_PERIOD), 10);
        testContext.assertFalse(json.getBoolean(PROP_HTTP_REQUEST_HANDLER_ENABLED));
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_PREFIX), "/queuing");

        // overridden values
        testContext.assertEquals(json.getString(PROP_ADDRESS), "new_address");
        testContext.assertEquals(json.getString(PROP_CONFIGURATION_UPDATED_ADDRESS), "config_updated");
        testContext.assertEquals(json.getString(PROP_REDIS_HOST), "anotherhost");
        testContext.assertEquals(json.getInteger(PROP_REDIS_PORT), 1234);
        testContext.assertTrue(json.getBoolean(PROP_REDIS_ENABLE_TLS));
        testContext.assertEquals(json.getInteger(PROP_REDIS_RECONNECT_ATTEMPTS), 22);
        testContext.assertEquals(json.getInteger(PROP_REDIS_RECONNECT_DELAY_SEC), 55);
        testContext.assertEquals(json.getInteger(PROP_CHECK_INTERVAL), 5);
        testContext.assertEquals(json.getInteger(PROP_PROCESSOR_TIMEOUT), 20);
        testContext.assertEquals(json.getInteger(PROP_PROCESSOR_DELAY_MAX), 50);
        testContext.assertEquals(json.getInteger(PROP_HTTP_REQUEST_HANDLER_PORT), 7171);
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_USERNAME), "foo");
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_PASSWORD), "bar");
        testContext.assertTrue(json.getBoolean(PROP_HTTP_REQUEST_HANDLER_AUTH_ENABLED));
        testContext.assertEquals(json.getString(PROP_HTTP_REQUEST_HANDLER_USER_HEADER), "x-custom-user-header");
        testContext.assertEquals(json.getInteger(PROP_QUEUE_SPEED_INTERVAL_SEC), 1);
        testContext.assertEquals(json.getInteger(PROP_MEMORY_USAGE_LIMIT_PCT), 55);

        // queue configurations
        JsonArray queueConfigurationsJsonArray = json.getJsonArray(PROP_QUEUE_CONFIGURATIONS);
        List<JsonObject> queueConfigurationJsonObjects = queueConfigurationsJsonArray.getList();
        testContext.assertEquals(queueConfigurationJsonObjects.size(), 1);
        JsonObject queueConfigurationJsonObject = queueConfigurationJsonObjects.get(0);
        testContext.assertEquals(queueConfigurationJsonObject.getString("pattern"), "vehicle-.*");
        testContext.assertEquals(queueConfigurationJsonObject.getJsonArray("retryIntervals").getList(), Arrays.asList(10, 20, 30, 60));
    }

    @Test
    public void testGetDefaultFromJsonObject(TestContext testContext) {
        JsonObject json = new RedisquesConfiguration().asJsonObject();
        RedisquesConfiguration config = fromJsonObject(json);

        testContext.assertEquals(config.getAddress(), "redisques");
        testContext.assertEquals(config.getConfigurationUpdatedAddress(), "redisques-configuration-updated");
        testContext.assertEquals(config.getRedisPrefix(), "redisques:");
        testContext.assertEquals(config.getProcessorAddress(), "redisques-processor");
        testContext.assertEquals(config.getRefreshPeriod(), 10);
        testContext.assertEquals(config.getRedisHost(), "localhost");
        testContext.assertEquals(config.getRedisPort(), 6379);
        testContext.assertFalse(config.getRedisEnableTls());
        testContext.assertEquals(config.getRedisReconnectAttempts(), 0);
        testContext.assertEquals(config.getRedisReconnectDelaySec(), 30);
        testContext.assertEquals(config.getCheckInterval(), 60);
        testContext.assertEquals(config.getProcessorTimeout(), 240000);
        testContext.assertEquals(config.getProcessorDelayMax(), 0L);
        testContext.assertFalse(config.getHttpRequestHandlerEnabled());
        testContext.assertFalse(config.getHttpRequestHandlerAuthenticationEnabled());
        testContext.assertEquals(config.getHttpRequestHandlerPrefix(), "/queuing");
        testContext.assertNull(config.getHttpRequestHandlerUsername());
        testContext.assertNull(config.getHttpRequestHandlerPassword());
        testContext.assertEquals(config.getHttpRequestHandlerPort(), 7070);
        testContext.assertEquals(config.getHttpRequestHandlerUserHeader(), "x-rp-usr");
        testContext.assertEquals(config.getQueueConfigurations().size(), 0);
        testContext.assertEquals(config.getQueueSpeedIntervalSec(), 60);
        testContext.assertEquals(config.getMemoryUsageLimitPercent(), 100);
    }

    @Test
    public void testGetOverriddenFromJsonObject(TestContext testContext) {

        JsonObject json = new JsonObject();
        json.put(PROP_ADDRESS, "new_address");
        json.put(PROP_CONFIGURATION_UPDATED_ADDRESS, "config_updated");
        json.put(PROP_REDIS_PREFIX, "new_redis-prefix");
        json.put(PROP_PROCESSOR_ADDRESS, "new_processor-address");
        json.put(PROP_REFRESH_PERIOD, 99);
        json.put(PROP_REDIS_HOST, "newredishost");
        json.put(PROP_REDIS_PORT, 4321);
        json.put(PROP_REDIS_ENABLE_TLS, true);
        json.put(PROP_REDIS_RECONNECT_DELAY_SEC, -5);
        json.put(PROP_CHECK_INTERVAL, 5);
        json.put(PROP_PROCESSOR_TIMEOUT, 30);
        json.put(PROP_PROCESSOR_DELAY_MAX, 99);
        json.put(PROP_HTTP_REQUEST_HANDLER_ENABLED, Boolean.TRUE);
        json.put(PROP_HTTP_REQUEST_HANDLER_AUTH_ENABLED, Boolean.TRUE);
        json.put(PROP_HTTP_REQUEST_HANDLER_PREFIX, "/queuing/test123");
        json.put(PROP_HTTP_REQUEST_HANDLER_USERNAME, "foo");
        json.put(PROP_HTTP_REQUEST_HANDLER_PASSWORD, "bar");
        json.put(PROP_HTTP_REQUEST_HANDLER_PORT, 7171);
        json.put(PROP_HTTP_REQUEST_HANDLER_USER_HEADER, "x-custom-user-header");
        json.put(PROP_QUEUE_SPEED_INTERVAL_SEC, 1);
        json.put(PROP_MEMORY_USAGE_LIMIT_PCT, 75);
        json.put(PROP_QUEUE_CONFIGURATIONS, new JsonArray(Collections.singletonList(
                new QueueConfiguration().withPattern("vehicle-.*")
                        .withRetryIntervals(10, 20, 30, 60)
                        .asJsonObject()
        )));

        RedisquesConfiguration config = fromJsonObject(json);
        testContext.assertEquals(config.getAddress(), "new_address");
        testContext.assertEquals(config.getConfigurationUpdatedAddress(), "config_updated");
        testContext.assertEquals(config.getRedisPrefix(), "new_redis-prefix");
        testContext.assertEquals(config.getProcessorAddress(), "new_processor-address");
        testContext.assertEquals(config.getRefreshPeriod(), 99);
        testContext.assertEquals(config.getRedisHost(), "newredishost");
        testContext.assertEquals(config.getRedisPort(), 4321);
        testContext.assertTrue(config.getRedisEnableTls());
        testContext.assertEquals(config.getRedisReconnectDelaySec(), 1);
        testContext.assertEquals(config.getCheckInterval(), 5);
        testContext.assertEquals(config.getProcessorTimeout(), 30);
        testContext.assertEquals(config.getProcessorDelayMax(), 99L);
        testContext.assertTrue(config.getHttpRequestHandlerEnabled());
        testContext.assertTrue(config.getHttpRequestHandlerAuthenticationEnabled());
        testContext.assertEquals(config.getHttpRequestHandlerPort(), 7171);
        testContext.assertEquals(config.getHttpRequestHandlerPrefix(), "/queuing/test123");
        testContext.assertEquals(config.getHttpRequestHandlerUsername(), "foo");
        testContext.assertEquals(config.getHttpRequestHandlerPassword(), "bar");
        testContext.assertEquals(config.getHttpRequestHandlerUserHeader(), "x-custom-user-header");
        testContext.assertEquals(config.getQueueSpeedIntervalSec(), 1);
        testContext.assertEquals(config.getMemoryUsageLimitPercent(), 75);

        // queue configurations
        testContext.assertEquals(config.getQueueConfigurations().size(), 1);
        QueueConfiguration queueConfiguration = config.getQueueConfigurations().get(0);
        testContext.assertEquals(queueConfiguration.getPattern(), "vehicle-.*");
        testContext.assertTrue(Arrays.equals(queueConfiguration.getRetryIntervals(), new int[]{10, 20, 30, 60}));
    }

    @Test
    public void testProcessorDelay(TestContext testContext) {
        RedisquesConfiguration config = with().processorDelayMax(5).build();
        testContext.assertEquals(5L, config.getProcessorDelayMax());

        config = with().processorDelayMax(0).build();
        testContext.assertEquals(0L, config.getProcessorDelayMax());

        // test negative value
        config = with().processorDelayMax(-50).build();
        testContext.assertEquals(0L, config.getProcessorDelayMax());
        config = with().processorDelayMax(Integer.MIN_VALUE).build();
        testContext.assertEquals(0L, config.getProcessorDelayMax());

        config = with().processorDelayMax(Long.MAX_VALUE).build();
        testContext.assertEquals(Long.MAX_VALUE, config.getProcessorDelayMax());
    }

    @Test
    public void testCleanupInterval(TestContext testContext) {
        RedisquesConfiguration config = with().checkInterval(5).build();
        testContext.assertEquals(5, config.getCheckInterval());
        testContext.assertEquals(add500ms(2500), config.getCheckIntervalTimerMs());

        config = with().checkInterval(1).build();
        testContext.assertEquals(1, config.getCheckInterval());
        testContext.assertEquals(add500ms(500), config.getCheckIntervalTimerMs());

        config = with().checkInterval(2).build();
        testContext.assertEquals(2, config.getCheckInterval());
        testContext.assertEquals(add500ms(1000), config.getCheckIntervalTimerMs());

        config = with().checkInterval(3).build();
        testContext.assertEquals(3, config.getCheckInterval());
        testContext.assertEquals(add500ms(1500), config.getCheckIntervalTimerMs());

        config = with().checkInterval(7).build();
        testContext.assertEquals(7, config.getCheckInterval());
        testContext.assertEquals(add500ms(3500), config.getCheckIntervalTimerMs());

        config = with().checkInterval(0).build();
        testContext.assertEquals(60, config.getCheckInterval());
        testContext.assertEquals(add500ms(30000), config.getCheckIntervalTimerMs());

        config = with().checkInterval(-5).build();
        testContext.assertEquals(60, config.getCheckInterval());
        testContext.assertEquals(add500ms(30000), config.getCheckIntervalTimerMs());

        config = with().checkInterval(60).build();
        testContext.assertEquals(60, config.getCheckInterval());
        testContext.assertEquals(add500ms(30000), config.getCheckIntervalTimerMs());

        JsonObject json = new JsonObject();
        json.put(PROP_CHECK_INTERVAL, 5);
        config = fromJsonObject(json);
        testContext.assertEquals(5, config.getCheckInterval());
        testContext.assertEquals(add500ms(2500), config.getCheckIntervalTimerMs());
    }

    @Test
    public void testMemoryUsageLimit(TestContext testContext) {
        testContext.assertEquals(100, with().memoryUsageLimitPercent(-30).build().getMemoryUsageLimitPercent()); // negative values are not allowed
        testContext.assertEquals(100, with().memoryUsageLimitPercent(150).build().getMemoryUsageLimitPercent()); // values over 100 are not allowed
        testContext.assertEquals(0, with().memoryUsageLimitPercent(0).build().getMemoryUsageLimitPercent());
        testContext.assertEquals(1, with().memoryUsageLimitPercent(1).build().getMemoryUsageLimitPercent());
        testContext.assertEquals(99, with().memoryUsageLimitPercent(99).build().getMemoryUsageLimitPercent());
        testContext.assertEquals(100, with().memoryUsageLimitPercent(100).build().getMemoryUsageLimitPercent());
    }

    @Test
    public void testRedisReconnectDelaySec(TestContext testContext) {
        testContext.assertEquals(1, with().redisReconnectDelaySec(-30).build().getRedisReconnectDelaySec()); // negative values are not allowed
        testContext.assertEquals(1, with().redisReconnectDelaySec(0).build().getRedisReconnectDelaySec());
        testContext.assertEquals(2, with().redisReconnectDelaySec(2).build().getRedisReconnectDelaySec());
        testContext.assertEquals(50, with().redisReconnectDelaySec(50).build().getRedisReconnectDelaySec());
        testContext.assertEquals(3600, with().redisReconnectDelaySec(3600).build().getRedisReconnectDelaySec());
    }

    private int add500ms(int interval) {
        return interval + 500;
    }
}
