package io.penguin.pengiunlettuce.compress;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Strategy {

    NONE(0),

    GZIP(1);

    final int mode;
}
