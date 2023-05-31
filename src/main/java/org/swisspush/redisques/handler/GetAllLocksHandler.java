package org.swisspush.redisques.handler;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.client.Response;
import org.swisspush.redisques.util.HandlerUtil;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.swisspush.redisques.util.RedisquesAPI.*;

/**
 * Class GetAllLocksHandler.
 *
 * @author baldim
 */
public class GetAllLocksHandler implements Handler<AsyncResult<Response>> {

    private final Message<JsonObject> event;
    private final Optional<Pattern> filterPattern;

    public GetAllLocksHandler(Message<JsonObject> event, Optional<Pattern> filterPattern) {
        this.event = event;
        this.filterPattern = filterPattern;
    }

    @Override
    public void handle(AsyncResult<Response> reply) {
        if (reply.succeeded() && reply.result() != null) {
            JsonObject result = new JsonObject();
            Response locks = reply.result();
            List<String> filteredLocks = HandlerUtil.filterByPattern(locks, filterPattern);
            result.put("locks", new JsonArray(filteredLocks));
            event.reply(new JsonObject().put(STATUS, OK).put(VALUE, result));
        } else {
            event.reply(new JsonObject().put(STATUS, ERROR));
        }
    }
}