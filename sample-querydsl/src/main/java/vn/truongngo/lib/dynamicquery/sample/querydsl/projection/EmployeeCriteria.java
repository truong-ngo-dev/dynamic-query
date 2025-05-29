package vn.truongngo.lib.dynamicquery.sample.querydsl.projection;

import lombok.Builder;
import lombok.Data;
import vn.truongngo.lib.dynamicquery.projection.annotation.Criteria;

@Data
@Builder
public class EmployeeCriteria {

    @Criteria
    private String firstName;

    @Criteria
    private String lastName;

    @Criteria
    private String companyName;

}
