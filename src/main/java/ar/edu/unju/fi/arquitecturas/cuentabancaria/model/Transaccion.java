package ar.edu.unju.fi.arquitecturas.cuentabancaria.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Time;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Transaccion {
    private Date fecha;
    private Time hora;
    private float monto;
    private TipoTransaccion tipo;
    private EstadoTransaccion estado_transaccion;

    public void consultarTransaccion() {}

}
