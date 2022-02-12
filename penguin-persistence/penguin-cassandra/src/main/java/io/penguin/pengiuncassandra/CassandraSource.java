package io.penguin.pengiuncassandra;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.penguin.penguincore.reader.Reader;
import reactor.core.publisher.Mono;

import java.util.Optional;

public abstract class CassandraSource<K, V> implements Reader<K, V> {

    private final Class<V> valueType;
    private final MappingManager mappingManager;
    private final PreparedStatement statement;
    private final Session session;

    public CassandraSource(CassandraConfig cassandraConfig, MappingManager mappingManager) {
        valueType = (Class<V>) cassandraConfig.getValueType();
        this.mappingManager = mappingManager;
        this.statement = cassandraConfig.getStatement();
        this.session = this.mappingManager.getSession();

    }

    @Override
    public Mono<V> findOne(K key) {

        ListenableFuture<Result<V>> resultListenableFuture = mappingManager.mapper(valueType)
                .mapAsync(session.executeAsync(statement.bind(key)));

        return Mono.create(i -> Futures.addCallback(resultListenableFuture, new FutureCallback<>() {
            @Override
            public void onSuccess(Result<V> result) {

                V v = Optional.ofNullable(result)
                        .map(Result::one)
                        .orElse(null);

                if (v != null) {
                    i.success(v);
                } else {
                    i.success();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                i.error(t);
            }
        }));
    }
}
