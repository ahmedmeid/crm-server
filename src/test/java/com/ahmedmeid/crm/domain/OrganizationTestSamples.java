package com.ahmedmeid.crm.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class OrganizationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Organization getOrganizationSample1() {
        return new Organization()
            .id(1L)
            .organizationName("organizationName1")
            .industry("industry1")
            .website("website1")
            .phoneNumber("phoneNumber1")
            .address("address1");
    }

    public static Organization getOrganizationSample2() {
        return new Organization()
            .id(2L)
            .organizationName("organizationName2")
            .industry("industry2")
            .website("website2")
            .phoneNumber("phoneNumber2")
            .address("address2");
    }

    public static Organization getOrganizationRandomSampleGenerator() {
        return new Organization()
            .id(longCount.incrementAndGet())
            .organizationName(UUID.randomUUID().toString())
            .industry(UUID.randomUUID().toString())
            .website(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .address(UUID.randomUUID().toString());
    }
}
