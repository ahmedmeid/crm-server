package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.ContactTestSamples.*;
import static com.ahmedmeid.crm.domain.DepartmentTestSamples.*;
import static com.ahmedmeid.crm.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class EmployeeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = getEmployeeSample1();
        Employee employee2 = new Employee();
        assertThat(employee1).isNotEqualTo(employee2);

        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);

        employee2 = getEmployeeSample2();
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    void contactTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        Contact contactBack = getContactRandomSampleGenerator();

        employee.addContact(contactBack);
        assertThat(employee.getContacts()).containsOnly(contactBack);

        employee.removeContact(contactBack);
        assertThat(employee.getContacts()).doesNotContain(contactBack);

        employee.contacts(new HashSet<>(Set.of(contactBack)));
        assertThat(employee.getContacts()).containsOnly(contactBack);

        employee.setContacts(new HashSet<>());
        assertThat(employee.getContacts()).doesNotContain(contactBack);
    }

    @Test
    void departmentTest() throws Exception {
        Employee employee = getEmployeeRandomSampleGenerator();
        Department departmentBack = getDepartmentRandomSampleGenerator();

        employee.setDepartment(departmentBack);
        assertThat(employee.getDepartment()).isEqualTo(departmentBack);

        employee.department(null);
        assertThat(employee.getDepartment()).isNull();
    }
}
