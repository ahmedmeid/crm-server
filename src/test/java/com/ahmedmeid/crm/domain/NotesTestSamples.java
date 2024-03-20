package com.ahmedmeid.crm.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class NotesTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notes getNotesSample1() {
        return new Notes().id(1L);
    }

    public static Notes getNotesSample2() {
        return new Notes().id(2L);
    }

    public static Notes getNotesRandomSampleGenerator() {
        return new Notes().id(longCount.incrementAndGet());
    }
}
