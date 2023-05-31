package org.swisspush.redisques.action;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.swisspush.redisques.handler.GetQueueItemsCountHandler;
import org.swisspush.redisques.lua.LuaScriptManager;
import org.swisspush.redisques.util.QueueConfiguration;
import org.swisspush.redisques.util.QueueStatisticsCollector;
import org.swisspush.redisques.util.RedisAPIProvider;

import java.util.List;

import static org.swisspush.redisques.util.RedisquesAPI.PAYLOAD;
import static org.swisspush.redisques.util.RedisquesAPI.QUEUENAME;

/**
 * Retrieve the number of items of the requested queue
 */
public class GetQueueItemsCountAction extends AbstractQueueAction {

    public GetQueueItemsCountAction(Vertx vertx, LuaScriptManager luaScriptManager, RedisAPIProvider redisAPIProvider, String address, String queuesKey, String queuesPrefix,
                                    String consumersPrefix, String locksKey, List<QueueConfiguration> queueConfigurations,
                                    QueueStatisticsCollector queueStatisticsCollector, Logger log) {
        super(vertx, luaScriptManager, redisAPIProvider, address, queuesKey, queuesPrefix, consumersPrefix, locksKey, queueConfigurations,
                queueStatisticsCollector, log);
    }

    @Override
    public void execute(Message<JsonObject> event) {
        String queue = event.body().getJsonObject(PAYLOAD).getString(QUEUENAME);
        redisAPIProvider.redisAPI().onSuccess(redisAPI -> redisAPI.llen(queuesPrefix + queue, new GetQueueItemsCountHandler(event)));
    }
}
