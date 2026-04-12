@FilterDef(
        name = "empresaFilter",
        parameters = @ParamDef(name = "empresaId", type = UUID.class)
)
package com.lulo;

import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import java.util.UUID;
