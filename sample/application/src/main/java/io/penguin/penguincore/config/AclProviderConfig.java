package io.penguin.penguincore.config;

import com.example.penguinql.acl.AclProvider;
import com.example.penguinql.acl.providerimpl.GqlAclProvider;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AclProviderConfig {

    public AclProvider aclProvider() {

        return new GqlAclProvider();
    }
}
