package vn.truongngo.lib.dynamicquery.sample.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "companies")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String industry;
    private String address;
    private String website;
}
