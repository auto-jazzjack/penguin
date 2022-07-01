package com.example.penguinql.core;


import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;

public class GqlParser {

    /**
     * Since there isn't any state, this method will be thread-safe
     */
    public Query parseFrom(String gql) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        Query query = queryGen(gql, atomicInteger);

        skipBlank(gql, atomicInteger);
        if (atomicInteger.get() != gql.length()) {
            throw new RuntimeException("Invalid Query exception. Some token is not read");
        }
        return query;
    }

    private Query queryGen(String list, AtomicInteger idx) {
        Query query = new Query();
        query.setFields(new HashSet<>());
        query.setQueryByResolverName(new HashMap<>());

        if (list.charAt(idx.getAndIncrement()) != '{') {
            throw new RuntimeException("Query should start with '{' character");
        }

        String before = "";
        while (list.length() > idx.get()) {
            String word1 = getWord(list, idx);

            //This means that we need to move into +1 depth
            if (word1.equals("{")) {
                idx.decrementAndGet();

                if (before.isEmpty()) {
                    throw new RuntimeException("Invalid Query exception near " + idx.get());
                }
                //Without getting two world, we cannot determine whether it is leaf node or not.
                //So in case of +1 depth case, let's remove before world
                query.getFields().remove(before);
                query.getQueryByResolverName().put(before, queryGen(list, idx));
            } else if (word1.equals("}")) {
                if (before.isEmpty()) {
                    throw new RuntimeException("Invalid Query exception near " + idx.get());
                }
                //end of current function
                break;
            } else {
                //leaf node
                query.getFields().add(word1);
                before = word1;
            }

        }
        return query;

    }

    //This me method get string and index.
    //Return the first world exist between idx to next whitespace.
    private String getWord(String list, AtomicInteger idx) {
        StringBuilder retv = new StringBuilder();
        skipBlank(list, idx);
        while (list.length() > idx.get()) {
            char c = list.charAt(idx.getAndIncrement());
            switch (c) {
                case '\n':
                case '\t':
                case ' ':
                    return retv.toString();
                case '{':
                case '}':
                    if (retv.toString().length() > 0) {
                        idx.decrementAndGet();
                        return String.valueOf(retv);
                    } else {
                        return String.valueOf(c);
                    }
                default:
                    retv.append(c);
            }

        }
        return retv.toString();
    }

    //helper to skip blank
    private void skipBlank(String list, AtomicInteger idx) {
        while (list.length() > idx.get()) {
            switch (list.charAt(idx.getAndIncrement())) {
                case '\n':
                case '\t':
                case ' ':
                    break;
                default:
                    idx.decrementAndGet();
                    return;
            }
        }
    }
}

