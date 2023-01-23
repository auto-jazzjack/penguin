package io.penguin.penguinql.acl;

import io.penguin.penguinql.core.Query;

public interface AclProvider {

    Query getAclAuth(String consumer);

    void parseAndPut(String content) throws Throwable;
}
