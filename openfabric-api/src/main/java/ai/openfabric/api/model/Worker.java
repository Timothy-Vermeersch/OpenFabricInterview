package ai.openfabric.api.model;


import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.*;

@Entity()
public class Worker extends Datable implements Serializable {

    @Id
    @Getter
    @Setter
    public String id;

    public String name;

    public String ports;

    public String status;

    public String state;

    public String image;
}
