package com.example.penguinql.acl;

import com.example.penguinql.core.Query;

public interface AclProvider {

    Query getAclAuth(String consumer);

    void parseAndPut(String content) throws Throwable;
}
