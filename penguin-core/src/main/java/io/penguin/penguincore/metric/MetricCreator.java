package io.penguin.penguincore.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

public class MetricCreator {

    private static final MeterRegistry METRICS_REGISTRY = Metrics.globalRegistry;
    private static final double[] buckets = {0.5, 0.9, 0.95, 0.99};

    public static Timer timer(String name, String... tags) {
        return Timer.builder(name)
                .tags(tags)
                .publishPercentiles(buckets)
                .register(METRICS_REGISTRY);
    }

    public static Counter counter(String name, String... tags) {
        return Counter.builder(name)
                .tags(tags)
                .register(Metrics.globalRegistry);
    }

}
