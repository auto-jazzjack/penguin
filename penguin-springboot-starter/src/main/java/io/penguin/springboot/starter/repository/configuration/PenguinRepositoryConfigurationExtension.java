package io.penguin.springboot.starter.repository.configuration;

import io.penguin.springboot.starter.Penguin;
import io.penguin.springboot.starter.repository.support.PenguinRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationExtensionSupport;
import org.springframework.data.repository.core.RepositoryMetadata;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class PenguinRepositoryConfigurationExtension extends RepositoryConfigurationExtensionSupport {

    @Override
    public String getModuleName() {
        return "Penguin";
    }

    @Override
    protected String getModulePrefix() {
        return "penguin";
    }

    @Override
    public String getRepositoryFactoryBeanClassName() {
        return PenguinRepositoryFactoryBean.class.getName();
    }

    @Override
    protected Collection<Class<?>> getIdentifyingTypes() {
        return Stream.of(Penguin.class).collect(Collectors.toSet());
    }

    @Override
    protected boolean useRepositoryConfiguration(RepositoryMetadata metadata) {
        return metadata.isReactiveRepository();
    }


}
