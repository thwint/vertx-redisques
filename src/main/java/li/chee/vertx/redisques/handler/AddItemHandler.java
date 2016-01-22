package li.chee.vertx.redisques.handler;

import li.chee.vertx.redisques.RedisQues;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

/**
 * Class AddItemHandler.
 *
 * @author baldim
 */
public class AddItemHandler implements Handler<Message<JsonObject>> {
    private Message<JsonObject> event;

    public AddItemHandler(Message<JsonObject> event) {
        this.event = event;
    }

    public void handle(Message<JsonObject> reply) {
        if (RedisQues.OK.equals(reply.body().getString(RedisQues.STATUS))) {
            event.reply(new JsonObject()
                            .putString(RedisQues.STATUS, RedisQues.OK)
            );
        } else {
            event.reply(new JsonObject()
                            .putString(RedisQues.STATUS, RedisQues.ERROR)
            );
        }
    }
}
