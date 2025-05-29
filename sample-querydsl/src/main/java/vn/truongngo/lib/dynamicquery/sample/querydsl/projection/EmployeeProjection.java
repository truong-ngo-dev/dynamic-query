package vn.truongngo.lib.dynamicquery.sample.querydsl.projection;

import lombok.Data;
import lombok.NoArgsConstructor;
import vn.truongngo.lib.dynamicquery.projection.annotation.Column;
import vn.truongngo.lib.dynamicquery.projection.annotation.Join;
import vn.truongngo.lib.dynamicquery.projection.annotation.Projection;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Company;
import vn.truongngo.lib.dynamicquery.sample.querydsl.entity.Employee;

@Data
@NoArgsConstructor
@Projection(entity = Employee.class, alias = "e")
@Join(target = Company.class, targetAlias = "c", targetColumn = "id", sourceColumn = "companyId")
public class EmployeeProjection {
    @Column
    private String firstName;
    @Column
    private String lastName;
    @Column(name = "name", from = "c")
    private String companyName;
}
