package io.penguin.springboot.starter.yaml;

import io.penguin.penguincore.reader.Reader;
import io.penguin.springboot.starter.mapper.ContainerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@Import(Penguin.class)
@Configuration
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Config {

    private final Penguin penguin;
    private final ContainerMapper mapper = new ContainerMapper();
    private Map<String, Reader> readers;

    @PostConstruct
    public void init() {
        System.out.println(penguin);
        Validator.validate(penguin);

        readers = new TreeMap<>();//Order maintained

        Optional.of(penguin)
                .map(Penguin::getSpec)
                .map(Penguin.Spec::getContainers)
                .orElse(Collections.emptyList())
                .forEach(i -> readers.put(i.getName(), mapper.generate(i, readers)));
    }
}
