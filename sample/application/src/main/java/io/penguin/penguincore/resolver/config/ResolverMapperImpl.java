package io.penguin.penguincore.resolver.config;

import com.example.penguinql.core.Resolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))

public class ResolverMapperImpl implements com.example.penguinql.core.ResolverMapper {

    private final List<Resolver> resolvers;
    private Map<Class<? extends Resolver>, Resolver> mapper;

    @PostConstruct
    public void hello() {
        mapper = resolvers.stream().collect(Collectors.toMap(Resolver::getClass, i -> i));
    }

    @Override
    public Resolver toInstant(Class<? extends Resolver> resolver) {
        return mapper.get(resolver);
    }
}
