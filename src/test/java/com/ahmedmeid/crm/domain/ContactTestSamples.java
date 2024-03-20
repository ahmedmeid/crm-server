package com.ahmedmeid.crm.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ContactTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Contact getContactSample1() {
        return new Contact()
            .id(1L)
            .contactName("contactName1")
            .jobTitle("jobTitle1")
            .emailAddress("emailAddress1")
            .phoneNo("phoneNo1")
            .addressNumber(1)
            .addressStreet("addressStreet1")
            .addressCity("addressCity1")
            .leadSource("leadSource1");
    }

    public static Contact getContactSample2() {
        return new Contact()
            .id(2L)
            .contactName("contactName2")
            .jobTitle("jobTitle2")
            .emailAddress("emailAddress2")
            .phoneNo("phoneNo2")
            .addressNumber(2)
            .addressStreet("addressStreet2")
            .addressCity("addressCity2")
            .leadSource("leadSource2");
    }

    public static Contact getContactRandomSampleGenerator() {
        return new Contact()
            .id(longCount.incrementAndGet())
            .contactName(UUID.randomUUID().toString())
            .jobTitle(UUID.randomUUID().toString())
            .emailAddress(UUID.randomUUID().toString())
            .phoneNo(UUID.randomUUID().toString())
            .addressNumber(intCount.incrementAndGet())
            .addressStreet(UUID.randomUUID().toString())
            .addressCity(UUID.randomUUID().toString())
            .leadSource(UUID.randomUUID().toString());
    }
}
