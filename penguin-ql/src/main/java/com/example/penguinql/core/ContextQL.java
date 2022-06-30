package com.example.penguinql.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * This is used for prepare logic
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContextQL {
    private List<String> executedResolver;

    public void addExecutedResolver(String str) {
        if (executedResolver == null) {
            executedResolver = new ArrayList<>();
        }
        executedResolver.add(str);
    }
}