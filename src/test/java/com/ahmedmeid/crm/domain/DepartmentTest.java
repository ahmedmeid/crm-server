package com.ahmedmeid.crm.domain;

import static com.ahmedmeid.crm.domain.DepartmentTestSamples.*;
import static com.ahmedmeid.crm.domain.EmployeeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.ahmedmeid.crm.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DepartmentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Department.class);
        Department department1 = getDepartmentSample1();
        Department department2 = new Department();
        assertThat(department1).isNotEqualTo(department2);

        department2.setId(department1.getId());
        assertThat(department1).isEqualTo(department2);

        department2 = getDepartmentSample2();
        assertThat(department1).isNotEqualTo(department2);
    }

    @Test
    void employeeTest() throws Exception {
        Department department = getDepartmentRandomSampleGenerator();
        Employee employeeBack = getEmployeeRandomSampleGenerator();

        department.addEmployee(employeeBack);
        assertThat(department.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getDepartment()).isEqualTo(department);

        department.removeEmployee(employeeBack);
        assertThat(department.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getDepartment()).isNull();

        department.employees(new HashSet<>(Set.of(employeeBack)));
        assertThat(department.getEmployees()).containsOnly(employeeBack);
        assertThat(employeeBack.getDepartment()).isEqualTo(department);

        department.setEmployees(new HashSet<>());
        assertThat(department.getEmployees()).doesNotContain(employeeBack);
        assertThat(employeeBack.getDepartment()).isNull();
    }
}
