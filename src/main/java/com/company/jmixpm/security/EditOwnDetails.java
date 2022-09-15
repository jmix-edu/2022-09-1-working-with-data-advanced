package com.company.jmixpm.security;

import com.company.jmixpm.entity.EmployeeDetails;
import com.company.jmixpm.entity.User;
import io.jmix.core.security.CurrentAuthentication;
import io.jmix.security.model.RowLevelBiPredicate;
import io.jmix.security.model.RowLevelPolicyAction;
import io.jmix.security.role.annotation.PredicateRowLevelPolicy;
import io.jmix.security.role.annotation.RowLevelRole;
import org.springframework.context.ApplicationContext;

@RowLevelRole(name = "Edit Own Details", code = "edit-own-details")
public interface EditOwnDetails {

    @PredicateRowLevelPolicy(entityClass = EmployeeDetails.class,
            actions = {
                    RowLevelPolicyAction.UPDATE
            }
    )
    default RowLevelBiPredicate<EmployeeDetails, ApplicationContext> allowEditOnlyOwnDetails() {
        return (details, applicationContext) -> {
            CurrentAuthentication currentAuthentication = applicationContext.getBean(CurrentAuthentication.class);
            User currentUser = ((User) currentAuthentication.getUser());

            return currentUser.equals(details.getEmployee().getUser());
        };
    }
}
