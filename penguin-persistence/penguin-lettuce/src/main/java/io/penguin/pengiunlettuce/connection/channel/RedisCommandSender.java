package io.penguin.pengiunlettuce.connection.channel;

import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.output.ArrayOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.BaseRedisCommandBuilder;
import io.lettuce.core.protocol.Command;

import java.util.List;

import static io.lettuce.core.protocol.CommandType.PING;
import static io.lettuce.core.protocol.CommandType.ROLE;

//https://redis.io/commands/
public class RedisCommandSender<K, V> extends BaseRedisCommandBuilder<K, V> {

    public RedisCommandSender(RedisCodec<K, V> codec) {
        super(codec);
    }

    public Command<K, V, List<Object>> role() {
        return createCommand(ROLE, new ArrayOutput<>(codec));
    }

    public Command<K, V, String> ping() {
        return createCommand(PING, new StatusOutput<>(codec));
    }
}