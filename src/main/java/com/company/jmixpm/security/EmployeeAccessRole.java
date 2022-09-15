package com.company.jmixpm.security;

import com.company.jmixpm.entity.Employee;
import com.company.jmixpm.entity.EmployeeDetails;
import io.jmix.security.model.EntityAttributePolicyAction;
import io.jmix.security.model.EntityPolicyAction;
import io.jmix.security.role.annotation.EntityAttributePolicy;
import io.jmix.security.role.annotation.EntityPolicy;
import io.jmix.security.role.annotation.ResourceRole;
import io.jmix.securityui.role.annotation.MenuPolicy;
import io.jmix.securityui.role.annotation.ScreenPolicy;

@ResourceRole(name = "EmployeeAccessRole", code = "employee-access-role")
public interface EmployeeAccessRole {
    @EntityAttributePolicy(entityClass = Employee.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = Employee.class, actions = EntityPolicyAction.ALL)
    void employee();

    @EntityAttributePolicy(entityClass = EmployeeDetails.class, attributes = "*", action = EntityAttributePolicyAction.MODIFY)
    @EntityPolicy(entityClass = EmployeeDetails.class, actions = EntityPolicyAction.ALL)
    void employeeDetails();

    @MenuPolicy(menuIds = "Employee.browse")
    @ScreenPolicy(screenIds = {"Employee.browse", "Employee.edit"})
    void screens();
}