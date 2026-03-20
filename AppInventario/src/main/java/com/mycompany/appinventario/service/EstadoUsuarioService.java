package com.mycompany.appinventario.service;

import com.mycompany.appinventario.config.AppConfig;
import java.time.LocalTime;

public final class EstadoUsuarioService {

    private EstadoUsuarioService() {
    }

    public static boolean estaActivoPorHorarioSistema() {
        LocalTime ahora = LocalTime.now();
        return !ahora.isBefore(AppConfig.HORARIO_INICIO) && !ahora.isAfter(AppConfig.HORARIO_FIN);
    }
}
