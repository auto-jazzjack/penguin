package com.example.penguinql.acl;

import com.example.penguinql.acl.exception.NotAuthorizationException;
import com.example.penguinql.core.Query;

public interface AclProvider {

    Query getAclAuth(String consumer);

    void append(String consumer, String content) throws NotAuthorizationException;

    Query parse(String content) throws NotAuthorizationException;
}
