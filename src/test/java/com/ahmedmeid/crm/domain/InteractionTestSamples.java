package com.ahmedmeid.crm.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class InteractionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Interaction getInteractionSample1() {
        return new Interaction().id(1L);
    }

    public static Interaction getInteractionSample2() {
        return new Interaction().id(2L);
    }

    public static Interaction getInteractionRandomSampleGenerator() {
        return new Interaction().id(longCount.incrementAndGet());
    }
}
