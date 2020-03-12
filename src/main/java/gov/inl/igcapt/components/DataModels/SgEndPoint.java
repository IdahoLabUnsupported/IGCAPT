package gov.inl.igcapt.components.DataModels;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "endPoint")
public class SgEndPoint {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    protected String endpoint;
}
