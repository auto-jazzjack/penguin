package io.penguin.pengiunlettuce.connection.channel;

import io.lettuce.core.ConnectionEvents;
import io.lettuce.core.RedisChannelInitializer;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.protocol.AsyncCommand;
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

@Slf4j
public class CustomNetty<K, V> implements NettyCustomizer {

    private final RedisCommandSender<K, V> sender;

    public CustomNetty(RedisCodec<K, V> redisCodec) {
        sender = new RedisCommandSender<>(redisCodec);
    }

    @Override
    public void afterBootstrapInitialized(Bootstrap bootstrap) {
    }

    @Override
    public void afterChannelInitialized(Channel channel) {

        //As-is activator
        RedisChannelInitializer redisChannelInitializer = channel.pipeline().get(RedisChannelInitializer.class);

        //delegate it.
        channel.pipeline().replace(RedisChannelInitializer.class, "TunnedChannelActivator", new ChannelActivator(redisChannelInitializer));

        //To trigger Event
        channel.pipeline().addFirst(new IdleStateHandler(1, 1, 0));
        channel.pipeline().addLast(new ChannelDuplexHandler() {
            @Override
            public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                //IdleStateHandler will make event
                if (evt instanceof IdleStateEvent) {

                    IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
                    if (idleStateEvent.state().equals(IdleState.READER_IDLE)) {
                        ctx.channel().close();
                    } else if (idleStateEvent.state().equals(IdleState.WRITER_IDLE)) {
                        channel.writeAndFlush(sender.ping());
                    }
                }
            }

        });

    }


    static class ChannelActivator extends ChannelDuplexHandler implements RedisChannelInitializer {

        private final RedisChannelInitializer delegate;
        private final RedisCommandSender<String, String> sender;

        public ChannelActivator(RedisChannelInitializer redisChannelInitializer) {
            this.delegate = redisChannelInitializer;
            this.sender = new RedisCommandSender<>(StringCodec.UTF8);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ((ChannelInboundHandler) delegate).channelInactive(ctx);
        }

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            ((ChannelInboundHandler) delegate).userEventTriggered(ctx, evt);
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {

            AsyncCommand<String, String, List<Object>> roleAsyncCommand = new AsyncCommand<>(sender.role());
            ctx.fireUserEventTriggered(new ConnectionEvents.PingBeforeActivate(roleAsyncCommand));

            roleAsyncCommand.whenComplete((roleResult, exceptionOfRole) -> {
                log.info("role result : " + roleResult.toString());

                /*
                 * https://redis.io/commands/role/
                 * The master output is composed of the following parts:
                 * (0) The string master.
                 * (1) The current master replication offset.
                 * (2) An array composed of three elements array representing the connected replicas.
                 * */
                if ("master".equals(roleResult.get(0))) {
                    triggerChannelActive(ctx);
                    return;
                }

                /*
                 * The replica output is composed of the following parts:
                 *
                 * (0) The string slave
                 * (1) The IP of the master.
                 * (2) The port number of the master.
                 * (3) The state of the replication from the point of view of the master,
                 *      - connect (the instance needs to connect to its master)
                 *      - connecting (the master-replica connection is in progress)
                 *      - sync (the master and replica are trying to perform the synchronization)
                 *      - connected (the replica is online).
                 * (4) The amount of data received from the replica so far in terms of master replication offset.
                 * */
                if (!"connected".equals(roleResult.get(3))) {
                    delegate.channelInitialized().completeExceptionally(new IllegalStateException("this is not connected"));
                    return;
                }

                triggerChannelActive(ctx);

            });

        }

        private void triggerChannelActive(ChannelHandlerContext ctx) {
            try {
                ((ChannelInboundHandler) delegate).channelActive(ctx);
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }

        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            ((ChannelInboundHandler) delegate).exceptionCaught(ctx, cause);
        }

        @Override
        public CompletableFuture<Boolean> channelInitialized() {
            return delegate.channelInitialized();
        }
    }


}