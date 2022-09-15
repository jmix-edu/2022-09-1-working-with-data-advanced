package com.company.jmixpm.screen.employee;

import com.company.jmixpm.entity.EmployeeDetails;
import io.jmix.core.AccessManager;
import io.jmix.core.Metadata;
import io.jmix.core.accesscontext.InMemoryCrudEntityContext;
import io.jmix.ui.component.DateField;
import io.jmix.ui.component.TextField;
import io.jmix.ui.model.DataContext;
import io.jmix.ui.model.InstanceContainer;
import io.jmix.ui.model.InstancePropertyContainer;
import io.jmix.ui.screen.*;
import com.company.jmixpm.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

@UiController("Employee.edit")
@UiDescriptor("employee-edit.xml")
@EditedEntityContainer("employeeDc")
public class EmployeeEdit extends StandardEditor<Employee> {

    @Autowired
    private DataContext dataContext;

    @Autowired
    private AccessManager accessManager;
    @Autowired
    private InstancePropertyContainer<EmployeeDetails> employeeDetailsDc;
    @Autowired
    private TextField<String> passportNumberField;
    @Autowired
    private DateField<LocalDate> expiryDateField;
    @Autowired
    private Metadata metadata;

    @Subscribe
    public void onInitEntity(InitEntityEvent<Employee> event) {
        EmployeeDetails employeeDetails = dataContext.create(EmployeeDetails.class);
        event.getEntity().setEmployeeDetails(employeeDetails);
    }

    @Subscribe
    public void onBeforeShow(BeforeShowEvent event) {
        /*EmployeeDetails employeeDetails = getEditedEntity().getEmployeeDetails();
        if (employeeDetails == null) {
            passportNumberField.setVisible(false);
            expiryDateField.setVisible(false);
        }*/

        InMemoryCrudEntityContext inMemoryCrudEntityContext =
                new InMemoryCrudEntityContext(metadata.getClass(EmployeeDetails.class), getApplicationContext());
        accessManager.applyRegisteredConstraints(inMemoryCrudEntityContext);

        boolean updatePermitted = inMemoryCrudEntityContext.isUpdatePermitted(getEditedEntity().getEmployeeDetails());
        passportNumberField.setVisible(updatePermitted);
        expiryDateField.setVisible(updatePermitted);
    }
}