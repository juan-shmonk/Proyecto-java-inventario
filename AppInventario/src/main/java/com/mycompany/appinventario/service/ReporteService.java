package com.mycompany.appinventario.service;

import com.mycompany.appinventario.model.RegistroMaterialTarea;
import com.mycompany.appinventario.model.Tarea;
import com.mycompany.appinventario.model.Usuario;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JTextArea;

public final class ReporteService {

    private static final DateTimeFormatter FILE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    private ReporteService() {
    }

    public static String generarReporteTarea(Tarea tarea, Usuario colaborador, List<RegistroMaterialTarea> registros) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== REPORTE FINAL DE TAREA ===\n");
        sb.append("ID Tarea: ").append(tarea.getIdTarea()).append('\n');
        sb.append("Titulo: ").append(tarea.getTitulo()).append('\n');
        sb.append("Estado: ").append(tarea.getEstado().getDbValue()).append('\n');
        sb.append("Fecha Entrega: ").append(tarea.getFechaEntrega()).append('\n');
        sb.append("Colaborador: ").append(colaborador != null ? colaborador.getNombre() : "Sin asignar").append('\n');
        sb.append("Descripcion final:\n").append(tarea.getDescripcion() == null ? "" : tarea.getDescripcion()).append("\n\n");

        sb.append("--- Materiales registrados en la tarea ---\n");
        if (registros.isEmpty()) {
            sb.append("Sin registros de materiales.\n");
        } else {
            for (RegistroMaterialTarea registro : registros) {
                sb.append("Codigo: ").append(registro.getCodigoProducto())
                        .append(" | Nombre: ").append(registro.getNombreProducto())
                        .append(" | Cantidad: ").append(registro.getCantidad())
                        .append(" | Fecha ingreso: ").append(registro.getFechaIngreso())
                        .append(" | Proveedor: ").append(registro.getProveedor())
                        .append(" | Defectos: ").append(registro.getDefectosVisibles())
                        .append('\n');
            }
        }

        return sb.toString();
    }

    public static Path guardarReporteEnArchivo(String reporte, int idTarea) throws IOException {
        Path carpetaReportes = Path.of("reportes");
        Files.createDirectories(carpetaReportes);

        String timestamp = LocalDateTime.now().format(FILE_FORMAT);
        Path archivo = carpetaReportes.resolve("tarea_" + idTarea + "_" + timestamp + ".txt");
        Files.writeString(archivo, reporte, StandardCharsets.UTF_8);
        return archivo;
    }

    public static void imprimirReporte(String reporte) throws PrinterException {
        JTextArea textArea = new JTextArea(reporte);
        textArea.print();
    }
}
