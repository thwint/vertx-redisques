package org.swisspush.redisques.action;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.redis.client.RedisAPI;
import io.vertx.redis.client.impl.types.BulkType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.swisspush.redisques.util.MemoryUsageProvider;
import org.swisspush.redisques.util.QueueStatisticsCollector;
import org.swisspush.redisques.util.RedisProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link EnqueueAction} class.
 *
 * @author https://github.com/mcweba [Marc-Andre Weber]
 */
@RunWith(VertxUnitRunner.class)
public class EnqueueActionTest {

    private RedisAPI redisAPI;
    private RedisProvider redisProvider;

    private Vertx vertx;

    private MemoryUsageProvider memoryUsageProvider;

    private Message<JsonObject> message;
    private EnqueueAction action;

    @Before
    public void setup(TestContext context) {
        redisAPI = Mockito.mock(RedisAPI.class);
        redisProvider = Mockito.mock(RedisProvider.class);
        when(redisProvider.redis()).thenReturn(Future.succeededFuture(redisAPI));

        memoryUsageProvider = Mockito.mock(MemoryUsageProvider.class);
        when(memoryUsageProvider.currentMemoryUsagePercentage()).thenReturn(Optional.empty());
        vertx = Vertx.vertx();

        message = Mockito.mock(Message.class);

        action = new EnqueueAction(vertx, redisProvider,
                "addr", "q-", "prefix-", "c-", "l-",
                new ArrayList<>(), Mockito.mock(QueueStatisticsCollector.class), Mockito.mock(Logger.class), memoryUsageProvider, 80);
    }

    @Test
    public void testDontEnqueueWhenMemoryUsageLimitIsReached(TestContext context){
        when(message.body()).thenReturn(new JsonObject(Buffer.buffer("{\"operation\":\"enqueue\",\"payload\":{\"queuename\":\"someQueue\"},\"message\":\"hello\"}")));
        when(memoryUsageProvider.currentMemoryUsagePercentage()).thenReturn(Optional.of(85));

        action.execute(message);

        verify(message, times(1)).reply(eq(new JsonObject(Buffer.buffer("{\"status\":\"error\",\"message\":\"memory usage limit reached\"}"))));
        verifyZeroInteractions(redisAPI);
    }

    @Test
    public void testDontEnqueueWhenUpdateTimestampFails(TestContext context){
        when(message.body()).thenReturn(new JsonObject(Buffer.buffer("{\"operation\":\"enqueue\",\"payload\":{\"queuename\":\"updateTimestampFail\"},\"message\":\"hello\"}")));

        when(redisAPI.zadd(anyList()))
                .thenReturn(Future.failedFuture("Booom"));

        action.execute(message);

        verify(message, times(1)).reply(eq(new JsonObject(Buffer.buffer("{\"status\":" +
                "\"error\",\"message\":\"RedisQues QUEUE_ERROR: Error while enqueueing message into " +
                "queue updateTimestampFail\"}"))));
        verify(redisAPI, never()).rpush(anyList());
    }

    @Test
    public void testEnqueueWhenUpdateTimestampSucceeds(TestContext context){
        when(message.body()).thenReturn(new JsonObject(Buffer.buffer("{\"operation\":\"enqueue\",\"payload\":{\"queuename\":\"someQueue\"},\"message\":\"hello\"}")));

        when(redisAPI.zadd(anyList()))
                .thenReturn(Future.succeededFuture());
        when(redisAPI.rpush(anyList())).thenReturn(Future.succeededFuture(BulkType.create(Buffer.buffer("1"), false)));

        action.execute(message);

        verify(message, times(1)).reply(eq(new JsonObject(Buffer.buffer("{\"status\":\"ok\",\"message\":\"enqueued\"}"))));
        verify(redisAPI, times(1)).rpush(eq(Arrays.asList("prefix-someQueue", "hello")));
    }
}
