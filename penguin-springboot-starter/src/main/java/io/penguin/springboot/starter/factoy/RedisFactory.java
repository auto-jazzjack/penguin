package io.penguin.springboot.starter.factoy;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.penguin.pengiunlettuce.LettuceCache;
import io.penguin.pengiunlettuce.cofig.LettuceCacheConfig;
import io.penguin.pengiunlettuce.connection.LettuceResource;
import io.penguin.penguincore.reader.CacheContext;
import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerKind;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static io.penguin.springboot.starter.mapper.ContainerKind.LETTUCE_CACHE;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RedisFactory<K, V> implements ReaderFactory<K, CacheContext<V>> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Map<String, Object>> collectedResources;

    @Override
    public ContainerKind getContainerType() {
        return ContainerKind.LETTUCE_CACHE;
    }

    @Override
    public Reader<K, CacheContext<V>> generate(Map<String, Object> spec) throws Exception {
        LettuceResource lettuceResource = objectMapper.convertValue(collectedResources.get(LETTUCE_CACHE.name()), LettuceResource.class);
        LettuceCacheConfig<V> config = objectMapper.convertValue(spec, LettuceCacheConfig.class);
        return new LettuceCache<>(lettuceResource, config);
    }

}
