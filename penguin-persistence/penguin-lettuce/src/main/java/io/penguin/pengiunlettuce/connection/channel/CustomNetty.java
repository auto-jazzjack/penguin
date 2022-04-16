package io.penguin.pengiunlettuce.connection.channel;

import io.lettuce.core.ConnectionEvents;
import io.lettuce.core.RedisChannelInitializer;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.ArrayOutput;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.AsyncCommand;
import io.lettuce.core.protocol.BaseRedisCommandBuilder;
import io.lettuce.core.protocol.Command;
import io.lettuce.core.resource.NettyCustomizer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.lettuce.core.protocol.CommandType.PING;
import static io.lettuce.core.protocol.CommandType.ROLE;

@Slf4j
public class CustomNetty<K, V> implements NettyCustomizer {
    private PingCommandGenerator<K, V> pingCommandGenerator;

    public CustomNetty(RedisCodec<K, V> redisCodec) {
        this.pingCommandGenerator = new PingCommandGenerator<>(redisCodec);
    }

    @Override
    public void afterBootstrapInitialized(Bootstrap bootstrap) {
    }

    @Override
    public void afterChannelInitialized(Channel channel) {
        RedisChannelInitializer redisChannelInitializer = channel.pipeline().get(RedisChannelInitializer.class);

        channel.pipeline().replace(RedisChannelInitializer.class, "customChannelActivator",
                new ChannelActivator(redisChannelInitializer));

        channel.pipeline().addFirst(new IdleStateHandler(1, 1, 0));
        channel.pipeline().addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                if (evt instanceof IdleStateEvent) {

                    IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                    if (idleStateEvent.state().equals(IdleState.READER_IDLE) && !idleStateEvent.isFirst()) {
                        log.info(String.format("read inactive on this channel(%s)", ctx.channel()));
                        ctx.channel().close();

                    } else if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
                        channel.writeAndFlush(pingCommandGenerator.ping());
                    }
                }
            }

        });

    }


    static class ChannelActivator extends ChannelDuplexHandler implements RedisChannelInitializer {

        RedisChannelInitializer redisChannelInitializer;
        RoleGenerator<String, String> roleGenerator;

        public ChannelActivator(RedisChannelInitializer redisChannelInitializer) {
            this.redisChannelInitializer = redisChannelInitializer;
            this.roleGenerator = new RoleGenerator<>(StringCodec.UTF8);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ((ChannelInboundHandler) redisChannelInitializer).channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ((ChannelInboundHandler) redisChannelInitializer).userEventTriggered(ctx, evt);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {

            AsyncCommand<String, String, List<Object>> roleAsyncCommand = new AsyncCommand<>(roleGenerator.role());
            ctx.fireUserEventTriggered(new ConnectionEvents.PingBeforeActivate(roleAsyncCommand));

            roleAsyncCommand.whenComplete((roleResult, exceptionOfRole) -> {
                log.info("role result : " + roleResult.toString());

                //if it is master, then let's connect to the server without condition.
                if ("master".equals(roleResult.get(0))) {
                    triggerChannelActive(ctx);
                    return;
                }

                //if slave and not connected, pend the connection.
                if (!"connected".equals(roleResult.get(3))) {
                    redisChannelInitializer
                            .channelInitialized()
                            .completeExceptionally(new IllegalStateException("this is not connected"));
                    return;
                }

                triggerChannelActive(ctx);

            });

        }

        private void triggerChannelActive(ChannelHandlerContext ctx) {
            try {
                ((ChannelInboundHandler) redisChannelInitializer).channelActive(ctx);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ((ChannelInboundHandler) redisChannelInitializer).exceptionCaught(ctx, cause);
        }

        @Override
        public CompletableFuture<Boolean> channelInitialized() {
            return redisChannelInitializer.channelInitialized();
        }
    }

    static class RoleGenerator<K, V> extends BaseRedisCommandBuilder<K, V> {

        public RoleGenerator(RedisCodec<K, V> codec) {
            super(codec);
        }

        public Command<K, V, List<Object>> role() {
            return createCommand(ROLE, new ArrayOutput<>(codec));
        }

    }

    static class PingCommandGenerator<K, V> extends BaseRedisCommandBuilder<K, V> {

        public PingCommandGenerator(RedisCodec<K, V> codec) {
            super(codec);
        }

        public Command<K, V, String> ping() {
            return createCommand(PING, new StatusOutput<>(codec));
        }

    }
}