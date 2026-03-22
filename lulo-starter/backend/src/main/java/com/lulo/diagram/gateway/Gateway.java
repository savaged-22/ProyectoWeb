package com.lulo.diagram.gateway;

import com.lulo.diagram.node.Nodo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "gateway")
@DiscriminatorValue("gateway")
@PrimaryKeyJoinColumn(name = "nodo_id")
public class Gateway extends Nodo {

    @Column(name = "tipo_gateway", nullable = false)
    private String tipoGateway;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "config_json", columnDefinition = "jsonb")
    private String configJson;
}
