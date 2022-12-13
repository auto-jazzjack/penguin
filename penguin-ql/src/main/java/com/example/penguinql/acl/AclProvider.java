package com.example.penguinql.acl;

import com.example.penguinql.acl.exception.NotAuthorizationException;

public interface AclProvider {

    AclAuth getAclAuth(String consumer);

    void append(String consumer, String content) throws NotAuthorizationException;

    AclAuth parse(String content) throws NotAuthorizationException;
}
