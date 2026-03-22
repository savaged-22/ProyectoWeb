package com.lulo.diagram.activity;

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
@Table(name = "actividad")
@DiscriminatorValue("actividad")
@PrimaryKeyJoinColumn(name = "nodo_id")
public class Actividad extends Nodo {

    @Column(name = "tipo_actividad")
    private String tipoActividad;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "props_json", columnDefinition = "jsonb")
    private String propsJson;
}
