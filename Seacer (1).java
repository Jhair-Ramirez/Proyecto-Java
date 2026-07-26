import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * SEACER - Sistema de Estimación y Alerta del Consumo Eléctrico Residencial para la ciudad de Machala.
 * Proyecto de Nivelación - UTMACH.
 */
public class Seacer {

    private static final BufferedReader ENTRADA =
            new BufferedReader(new InputStreamReader(System.in));

    private static final int MAX_DISPOSITIVOS = 20;
    private static final int DIAS_SEMANA = 7;

    private static final double SEMANAS_POR_ANIO = 52.0;
    private static final double MESES_POR_ANIO = 12.0;
    private static final double SEMANAS_POR_MES =
            SEMANAS_POR_ANIO / MESES_POR_ANIO;

    private static final int COL_POTENCIA = 0;
    private static final int COL_HORAS = 1;
    private static final int COL_DIAS = 2;
    private static final int COL_DIARIO = 3;
    private static final int COL_SEMANAL = 4;
    private static final int COL_MENSUAL = 5;
    private static final int COL_ANUAL = 6;

    private static final String[] DIAS = {
        "Lunes",
        "Martes",
        "Miércoles",
        "Jueves",
        "Viernes",
        "Sábado",
        "Domingo"
    };

    // MÉTODOS PARA VALIDAR ENTRADAS

    public static int leerEntero(String mensaje) throws IOException {

        while (true) {
            System.out.print(mensaje);
            String texto = ENTRADA.readLine();

            try {
                return Integer.parseInt(texto.trim());

            } catch (NumberFormatException e) {
                System.out.println(
                        "Entrada inválida. Debe ingresar un número entero.");
            }
        }
    }

    public static int leerEnteroEnRango(
            String mensaje,
            int minimo,
            int maximo) throws IOException {

        while (true) {
            int valor = leerEntero(mensaje);

            if (valor >= minimo && valor <= maximo) {
                return valor;
            }

            System.out.printf(
                    "Valor inválido. Ingrese un número entre %d y %d.%n",
                    minimo,
                    maximo);
        }
    }

    public static double leerDouble(String mensaje) throws IOException {

        while (true) {
            System.out.print(mensaje);

            String texto = ENTRADA.readLine()
                    .trim()
                    .replace(',', '.');

            try {
                return Double.parseDouble(texto);

            } catch (NumberFormatException e) {
                System.out.println(
                        "Entrada inválida. Debe ingresar un número.");
            }
        }
    }

    public static double leerDoubleEnRango(
            String mensaje,
            double minimo,
            double maximo) throws IOException {

        while (true) {
            double valor = leerDouble(mensaje);

            if (valor >= minimo && valor <= maximo) {
                return valor;
            }

            System.out.printf(
                    "Valor inválido. Ingrese un valor entre %.2f y %.2f.%n",
                    minimo,
                    maximo);
        }
    }

    public static String leerOpcionTexto(
            String mensaje,
            String opcion1,
            String opcion2) throws IOException {

        while (true) {
            System.out.print(mensaje);
            String valor = ENTRADA.readLine().trim();

            if (valor.equalsIgnoreCase(opcion1)) {
                return opcion1;
            }

            if (valor.equalsIgnoreCase(opcion2)) {
                return opcion2;
            }

            System.out.printf(
                    "Opción inválida. Escriba %s o %s.%n",
                    opcion1,
                    opcion2);
        }
    }

    // SELECCIÓN DE DÍAS DE USO

    /**
     * Permite seleccionar los días específicos en los que se utiliza
     * un electrodoméstico.
     *
     * No permite seleccionar un mismo día más de una vez.
     */
    public static void registrarDiasDeUso(
            double[][] consumoSemana,
            int filaDispositivo,
            int cantidadDias,
            double consumoDiario) throws IOException {

        boolean[] diasSeleccionados =
                new boolean[DIAS_SEMANA];

        /*
         * Primero se inicializa en cero toda la fila correspondiente
         * al dispositivo.
         */
        for (int dia = 0; dia < DIAS_SEMANA; dia++) {
            consumoSemana[filaDispositivo][dia] = 0;
        }

        System.out.println();
        System.out.println("--- Selección de días de uso ---");

        System.out.printf(
                "Debe seleccionar %d día(s) diferente(s).%n",
                cantidadDias);

        int diasRegistrados = 0;

        while (diasRegistrados < cantidadDias) {

            mostrarDiasDisponibles(diasSeleccionados);

            int opcionDia = leerEnteroEnRango(
                    "Seleccione el día "
                    + (diasRegistrados + 1)
                    + " de "
                    + cantidadDias
                    + " (1-7): ",
                    1,
                    DIAS_SEMANA);

            int indiceDia = opcionDia - 1;

            if (diasSeleccionados[indiceDia]) {

                System.out.printf(
                        "%nEl día %s ya fue registrado.%n",
                        DIAS[indiceDia]);

                System.out.println(
                        "No puede repetir el mismo día. "
                        + "Seleccione uno diferente.");

                mostrarDiasSeleccionados(
                        diasSeleccionados);

            } else {

                diasSeleccionados[indiceDia] = true;

                /*
                 * Se registra el consumo diario únicamente en
                 * el día seleccionado.
                 */
                consumoSemana[filaDispositivo][indiceDia] =
                        consumoDiario;

                diasRegistrados++;

                System.out.printf(
                        "%s registrado correctamente "
                        + "con %.2f kWh.%n",
                        DIAS[indiceDia],
                        consumoDiario);
            }
        }

        System.out.println();
        System.out.println("Días de uso registrados:");

        mostrarDiasSeleccionados(
                diasSeleccionados);
    }

    /**
     * Muestra los días que todavía no han sido seleccionados.
     */
    public static void mostrarDiasDisponibles(
            boolean[] diasSeleccionados) {

        System.out.println();
        System.out.println("Días disponibles:");

        for (int i = 0; i < DIAS_SEMANA; i++) {

            if (!diasSeleccionados[i]) {
                System.out.printf(
                        "%d. %s%n",
                        i + 1,
                        DIAS[i]);
            }
        }
    }

    /**
     * Muestra los días que el usuario ya seleccionó.
     */
    public static void mostrarDiasSeleccionados(
            boolean[] diasSeleccionados) {

        boolean existeSeleccion = false;

        for (int i = 0; i < DIAS_SEMANA; i++) {

            if (diasSeleccionados[i]) {
                System.out.println(
                        "- " + DIAS[i]);

                existeSeleccion = true;
            }
        }

        if (!existeSeleccion) {
            System.out.println(
                    "Todavía no se ha seleccionado ningún día.");
        }
    }

    // CATÁLOGO Y MENÚ

    public static void cargarElectrodomesticos(
            String[] nombres,
            double[] potencias) {

        String[] catalogoNombres = {
            "Televisor",
            "Refrigeradora",
            "Lavadora",
            "Microondas",
            "Plancha",
            "Aire acondicionado",
            "Computadora",
            "Foco LED",
            "Ventilador",
            "Cargador de celular"
        };

        double[] catalogoPotencias = {
            150,
            400,
            500,
            1200,
            1000,
            2000,
            300,
            10,
            75,
            5
        };

        for (int i = 0;
                i < catalogoNombres.length;
                i++) {

            nombres[i] = catalogoNombres[i];
            potencias[i] = catalogoPotencias[i];
        }
    }

    public static void mostrarMenu() {

        System.out.println();

        System.out.println(
                "======================================================");

        System.out.println(
                "       SEACER - Consumo Eléctrico Residencial");

        System.out.println(
                "======================================================");

        System.out.println(
                "1. Registrar electrodomésticos");

        System.out.println(
                "2. Ver reporte general de consumo");

        System.out.println(
                "3. Ver detalle de consumo por dispositivo");

        System.out.println(
                "4. Clasificar consumo y generar alertas");

        System.out.println(
                "5. Mostrar estadísticas y porcentajes");

        System.out.println(
                "6. Comparar escenario actual con optimizado");

        System.out.println(
                "7. Calcular intensidad de corriente y resistencia");

        System.out.println(
                "8. Salir y mostrar matrices finales");
    }

    // REGISTRO DE ELECTRODOMÉSTICOS

    public static int registrarElectrodomesticos(
            String[] nombresCatalogo,
            double[] potenciasCatalogo,
            String[] nombresSeleccionados,
            String[] estados,
            String[] prioridades,
            double[][] datos,
            double[][] consumoSemana,
            int[] contadorPorTipo,
            int cantidad) throws IOException {

        String continuar = "S";

        while (continuar.equalsIgnoreCase("S")
                && cantidad < MAX_DISPOSITIVOS) {

            System.out.println();
            System.out.println(
                    "--- Electrodomésticos disponibles ---");

            for (int i = 0;
                    i < nombresCatalogo.length;
                    i++) {

                System.out.printf(
                        "%d. %-22s %8.2f W%n",
                        i + 1,
                        nombresCatalogo[i],
                        potenciasCatalogo[i]);
            }

            int opcion = leerEnteroEnRango(
                    "Seleccione el electrodoméstico: ",
                    1,
                    nombresCatalogo.length);

            int indiceCatalogo = opcion - 1;

            double horas = leerDoubleEnRango(
                    "Horas de uso diario (0 a 24): ",
                    0,
                    24);

            int dias = leerEnteroEnRango(
                    "Cantidad de días de uso por semana (1 a 7): ",
                    1,
                    7);

            String estado = leerOpcionTexto(
                    "Estado actual (Encendido/Apagado): ",
                    "Encendido",
                    "Apagado");

            String prioridad = leerOpcionTexto(
                    "Prioridad (Esencial/No esencial): ",
                    "Esencial",
                    "No esencial");

            double diario = calcularConsumoDiario(
                    potenciasCatalogo[indiceCatalogo],
                    horas);

            double semanal = calcularConsumoSemanal(
                    diario,
                    dias);

            double mensual = calcularConsumoMensual(
                    semanal);

            double anual = calcularConsumoAnual(
                    semanal);

            contadorPorTipo[indiceCatalogo]++;

            nombresSeleccionados[cantidad] =
                    generarNombreUnico(
                            nombresCatalogo[indiceCatalogo],
                            nombresSeleccionados,
                            cantidad,
                            contadorPorTipo[indiceCatalogo]);

            estados[cantidad] = estado;
            prioridades[cantidad] = prioridad;

            datos[cantidad][COL_POTENCIA] =
                    potenciasCatalogo[indiceCatalogo];

            datos[cantidad][COL_HORAS] = horas;
            datos[cantidad][COL_DIAS] = dias;
            datos[cantidad][COL_DIARIO] = diario;
            datos[cantidad][COL_SEMANAL] = semanal;
            datos[cantidad][COL_MENSUAL] = mensual;
            datos[cantidad][COL_ANUAL] = anual;

            /*
             * El usuario selecciona los días específicos de uso.
             * No se permiten días repetidos.
             */
            registrarDiasDeUso(
                    consumoSemana,
                    cantidad,
                    dias,
                    diario);

            cantidad++;

            System.out.println();
            System.out.println(
                    "Electrodoméstico registrado correctamente.");

            System.out.printf(
                    "Consumo diario: (%.2f W x %.2f h) / 1000 "
                    + "= %.2f kWh%n",
                    potenciasCatalogo[indiceCatalogo],
                    horas,
                    diario);

            System.out.printf(
                    "Consumo semanal: %.2f kWh x %d días "
                    + "= %.2f kWh%n",
                    diario,
                    dias,
                    semanal);

            System.out.printf(
                    "Consumo mensual: %.2f kWh x %.2f semanas "
                    + "= %.2f kWh%n",
                    semanal,
                    SEMANAS_POR_MES,
                    mensual);

            System.out.printf(
                    "Consumo anual: %.2f kWh x %.0f semanas "
                    + "= %.2f kWh%n",
                    semanal,
                    SEMANAS_POR_ANIO,
                    anual);

            if (dias == 1) {
                System.out.println(
                        "Nota: el consumo diario y semanal son iguales "
                        + "porque el dispositivo se usa un solo día "
                        + "por semana.");
            }

            if (cantidad < MAX_DISPOSITIVOS) {

                continuar = leerOpcionTexto(
                        "¿Desea agregar otro electrodoméstico? (S/N): ",
                        "S",
                        "N");
            }
        }

        if (cantidad == MAX_DISPOSITIVOS) {

            System.out.printf(
                    "Se alcanzó el límite de %d registros.%n",
                    MAX_DISPOSITIVOS);
        }

        return cantidad;
    }

    // NOMBRES REPETIDOS

    public static String generarNombreUnico(
            String nombreBase,
            String[] nombresSeleccionados,
            int cantidad,
            int numeroRepeticion) {

        if (numeroRepeticion == 1) {
            return nombreBase;
        }

        /*
         * Cuando se registra por segunda vez el mismo tipo de
         * electrodoméstico, también se numera el primero.
         */
        if (numeroRepeticion == 2) {

            for (int i = 0; i < cantidad; i++) {

                if (nombresSeleccionados[i]
                        .equalsIgnoreCase(nombreBase)) {

                    nombresSeleccionados[i] =
                            nombreBase + " 1";

                    break;
                }
            }
        }

        return nombreBase + " " + numeroRepeticion;
    }

    public static boolean hayRegistros(int cantidad) {

        if (cantidad == 0) {

            System.out.println(
                    "\nNo existen electrodomésticos registrados. "
                    + "Use la opción 1.");

            return false;
        }

        return true;
    }

    // REPORTE GENERAL

    public static void reporteGeneral(
            double[][] datos,
            int cantidad,
            double tarifa) {

        if (!hayRegistros(cantidad)) {
            return;
        }

        double diario = sumarColumna(
                datos,
                cantidad,
                COL_DIARIO);

        double semanal = sumarColumna(
                datos,
                cantidad,
                COL_SEMANAL);

        double mensual = sumarColumna(
                datos,
                cantidad,
                COL_MENSUAL);

        double anual = sumarColumna(
                datos,
                cantidad,
                COL_ANUAL);

        System.out.println();
        System.out.println(
                "--- Reporte general de consumo residencial ---");

        System.out.printf(
                "Dispositivos registrados: %d%n",
                cantidad);

        System.out.printf(
                "Consumo diario total: %.2f kWh%n",
                diario);

        System.out.printf(
                "Consumo semanal total: %.2f kWh%n",
                semanal);

        System.out.printf(
                "Consumo mensual estimado: %.2f kWh%n",
                mensual);

        System.out.printf(
                "Consumo anual estimado: %.2f kWh%n",
                anual);

        System.out.printf(
                "Coste mensual estimado: $%.2f%n",
                mensual * tarifa);

        System.out.printf(
                "Coste anual estimado: $%.2f%n",
                anual * tarifa);

        System.out.printf(
                "Tarifa aplicada: $%.2f por kWh%n",
                tarifa);
    }

    // DETALLE POR DISPOSITIVO

    public static void detallePorDispositivo(
            String[] nombres,
            String[] estados,
            String[] prioridades,
            double[][] datos,
            double[][] consumoSemana,
            int cantidad,
            double tarifa) {

        if (!hayRegistros(cantidad)) {
            return;
        }

        System.out.println();
        System.out.println(
                "--- Detalle de consumo por dispositivo ---");

        for (int i = 0; i < cantidad; i++) {

            System.out.printf(
                    "%n%d. %s%n",
                    i + 1,
                    nombres[i]);

            System.out.printf(
                    "Potencia: %.2f W%n",
                    datos[i][COL_POTENCIA]);

            System.out.printf(
                    "Uso: %.2f horas diarias, %.0f días por semana%n",
                    datos[i][COL_HORAS],
                    datos[i][COL_DIAS]);

            System.out.printf(
                    "Estado: %s | Prioridad: %s%n",
                    estados[i],
                    prioridades[i]);

            System.out.printf(
                    "Consumo diario: %.2f kWh%n",
                    datos[i][COL_DIARIO]);

            System.out.printf(
                    "Consumo semanal: %.2f kWh%n",
                    datos[i][COL_SEMANAL]);

            System.out.printf(
                    "Consumo mensual: %.2f kWh%n",
                    datos[i][COL_MENSUAL]);

            System.out.printf(
                    "Consumo anual: %.2f kWh%n",
                    datos[i][COL_ANUAL]);

            System.out.printf(
                    "Coste mensual estimado: $%.2f%n",
                    datos[i][COL_MENSUAL] * tarifa);

            System.out.println("Consumo por día:");

            for (int dia = 0;
                    dia < DIAS_SEMANA;
                    dia++) {

                System.out.printf(
                        "- %-10s: %.2f kWh%n",
                        DIAS[dia],
                        consumoSemana[i][dia]);
            }
        }
    }

    // MATRICES FINALES

    public static void mostrarMatrizFinal(
            String[] nombres,
            String[] estados,
            String[] prioridades,
            double[][] datos,
            double[][] consumoSemana,
            int cantidad) {

        System.out.println();

        System.out.println(
                "================================================================");

        System.out.println(
                "                MATRIZ FINAL DE DATOS DE SEACER");

        System.out.println(
                "================================================================");

        if (!hayRegistros(cantidad)) {
            return;
        }

        System.out.printf(
                "%-4s %-24s %9s %8s %6s %10s %10s "
                + "%10s %10s %-11s %-12s%n",
                "N.º",
                "Dispositivo",
                "Pot.(W)",
                "Horas",
                "Días",
                "kWh/día",
                "kWh/sem",
                "kWh/mes",
                "kWh/año",
                "Estado",
                "Prioridad");

        System.out.println(
                "---------------------------------------------------------------"
                + "---------------------------------------------------------------");

        for (int i = 0; i < cantidad; i++) {

            System.out.printf(
                    "%-4d %-24s %9.2f %8.2f %6.0f %10.2f "
                    + "%10.2f %10.2f %10.2f %-11s %-12s%n",
                    i + 1,
                    nombres[i],
                    datos[i][COL_POTENCIA],
                    datos[i][COL_HORAS],
                    datos[i][COL_DIAS],
                    datos[i][COL_DIARIO],
                    datos[i][COL_SEMANAL],
                    datos[i][COL_MENSUAL],
                    datos[i][COL_ANUAL],
                    estados[i],
                    prioridades[i]);
        }

        System.out.println();
        System.out.println(
                "--- MATRIZ DE CONSUMO POR DÍA DE LA SEMANA (kWh) ---");

        System.out.println(
                "Cada celda contiene el consumo de ese día; "
                + "la última columna contiene el total semanal.");

        System.out.printf(
                "%-4s %-24s",
                "N.º",
                "Dispositivo");

        for (String dia : DIAS) {
            System.out.printf(
                    " %10s",
                    dia);
        }

        System.out.printf(
                " %12s%n",
                "Total sem.");

        System.out.println(
                "---------------------------------------------------------------"
                + "-------------------------------------------------------------------");

        for (int i = 0; i < cantidad; i++) {

            double totalFilaSemanal = 0;

            System.out.printf(
                    "%-4d %-24s",
                    i + 1,
                    nombres[i]);

            for (int dia = 0;
                    dia < DIAS_SEMANA;
                    dia++) {

                System.out.printf(
                        " %10.2f",
                        consumoSemana[i][dia]);

                totalFilaSemanal +=
                        consumoSemana[i][dia];
            }

            System.out.printf(
                    " %12.2f%n",
                    totalFilaSemanal);
        }
    }

    // CLASIFICACIÓN Y ALERTAS

    public static void clasificarYAlertar(
            String[] estados,
            String[] prioridades,
            double[][] datos,
            int cantidad,
            double limiteMensual) {

        if (!hayRegistros(cantidad)) {
            return;
        }

        double mensual = 0;
        int encendidos = 0;
        int altaPotencia = 0;
        int altaPotenciaEncendidos = 0;

        boolean noEsencialEncendido = false;
        boolean altoConsumo = false;

        for (int i = 0; i < cantidad; i++) {

            mensual += datos[i][COL_MENSUAL];

            boolean encendido =
                    estados[i].equalsIgnoreCase(
                            "Encendido");

            boolean potenciaAlta =
                    datos[i][COL_POTENCIA] >= 1000;

            if (encendido) {
                encendidos++;
            }

            if (potenciaAlta) {
                altaPotencia++;
            }

            if (encendido && potenciaAlta) {
                altaPotenciaEncendidos++;
            }

            if (encendido
                    && prioridades[i].equalsIgnoreCase(
                            "No esencial")) {

                noEsencialEncendido = true;
            }

            if (encendido
                    && potenciaAlta
                    && datos[i][COL_HORAS] > 5) {

                altoConsumo = true;
            }
        }

        double indice = calcularIndiceConsumo(
                mensual,
                encendidos,
                altaPotencia);

        String clasificacion =
                obtenerClasificacion(indice);

        System.out.println();
        System.out.println(
                "--- Clasificación del consumo ---");

        System.out.printf(
                "Consumo mensual: %.2f kWh%n",
                mensual);

        System.out.printf(
                "Equipos encendidos: %d%n",
                encendidos);

        System.out.printf(
                "Equipos de alta potencia: %d%n",
                altaPotencia);

        System.out.printf(
                "Índice de consumo: %.2f%n",
                indice);

        System.out.println(
                "Clasificación: " + clasificacion);

        System.out.println();
        System.out.println("--- Alertas ---");

        boolean alerta = false;

        if (mensual > limiteMensual
                && noEsencialEncendido) {

            System.out.println(
                    "ALERTA 1: Se supera el límite mensual y hay "
                    + "equipos no esenciales encendidos.");

            System.out.println(
                    "Recomendación: apagar o reducir el uso "
                    + "de esos equipos.");

            alerta = true;
        }

        if (altoConsumo) {

            System.out.println(
                    "ALERTA 2: Hay equipos de alta potencia encendidos "
                    + "más de 5 horas al día.");

            System.out.println(
                    "Recomendación: disminuir su tiempo de uso.");

            alerta = true;
        }

        if (altaPotenciaEncendidos >= 2) {

            System.out.println(
                    "ALERTA 3: Hay dos o más equipos de alta "
                    + "potencia encendidos.");

            System.out.println(
                    "Recomendación: evitar su uso simultáneo.");

            alerta = true;
        }

        if (!alerta) {

            if (mensual <= limiteMensual) {

                System.out.println(
                        "Consumo dentro del límite mensual establecido.");

            } else {

                System.out.println(
                        "No se detectaron condiciones adicionales "
                        + "de alerta.");
            }
        }
    }

    // ESTADÍSTICAS

    public static void mostrarEstadisticas(
            String[] nombres,
            double[][] datos,
            int cantidad) {

        if (!hayRegistros(cantidad)) {
            return;
        }

        double totalDiario = sumarColumna(
                datos,
                cantidad,
                COL_DIARIO);

        double totalMensual = sumarColumna(
                datos,
                cantidad,
                COL_MENSUAL);

        double totalHoras = sumarColumna(
                datos,
                cantidad,
                COL_HORAS);

        int mayor = 0;
        int menor = 0;

        for (int i = 1; i < cantidad; i++) {

            if (datos[i][COL_MENSUAL]
                    > datos[mayor][COL_MENSUAL]) {

                mayor = i;
            }

            if (datos[i][COL_MENSUAL]
                    < datos[menor][COL_MENSUAL]) {

                menor = i;
            }
        }

        System.out.println();
        System.out.println(
                "--- Estadísticas generales ---");

        System.out.printf(
                "Promedio diario por dispositivo: %.2f kWh%n",
                totalDiario / cantidad);

        System.out.printf(
                "Promedio de horas de uso: %.2f horas%n",
                totalHoras / cantidad);

        System.out.printf(
                "Mayor consumo: %s (%.2f kWh/mes)%n",
                nombres[mayor],
                datos[mayor][COL_MENSUAL]);

        System.out.printf(
                "Menor consumo: %s (%.2f kWh/mes)%n",
                nombres[menor],
                datos[menor][COL_MENSUAL]);

        System.out.println();
        System.out.println(
                "--- Participación en el consumo mensual ---");

        for (int i = 0; i < cantidad; i++) {

            double porcentaje;

            if (totalMensual == 0) {
                porcentaje = 0;

            } else {
                porcentaje =
                        datos[i][COL_MENSUAL]
                        * 100
                        / totalMensual;
            }

            System.out.printf(
                    "%-24s %7.2f%%%n",
                    nombres[i],
                    porcentaje);
        }
    }

    // COMPARACIÓN DE ESCENARIOS

    public static void compararEscenarios(
            String[] prioridades,
            double[][] datos,
            int cantidad,
            double tarifa) {

        if (!hayRegistros(cantidad)) {
            return;
        }

        double actual = 0;
        double optimizado = 0;

        for (int i = 0; i < cantidad; i++) {

            actual += datos[i][COL_MENSUAL];

            if (prioridades[i].equalsIgnoreCase(
                    "No esencial")) {

                optimizado +=
                        datos[i][COL_MENSUAL] * 0.75;

            } else {

                optimizado +=
                        datos[i][COL_MENSUAL];
            }
        }

        double ahorroEnergia =
                actual - optimizado;

        double porcentaje;

        if (actual == 0) {
            porcentaje = 0;

        } else {
            porcentaje =
                    ahorroEnergia * 100 / actual;
        }

        System.out.println();
        System.out.println(
                "--- Comparación de escenarios ---");

        System.out.printf(
                "Escenario actual: %.2f kWh/mes ($%.2f)%n",
                actual,
                actual * tarifa);

        System.out.printf(
                "Escenario optimizado: %.2f kWh/mes ($%.2f)%n",
                optimizado,
                optimizado * tarifa);

        System.out.printf(
                "Ahorro estimado: %.2f kWh y $%.2f al mes%n",
                ahorroEnergia,
                ahorroEnergia * tarifa);

        System.out.printf(
                "Reducción estimada: %.2f%%%n",
                porcentaje);

        System.out.println(
                "Supuesto: reducción del 25% en equipos no esenciales.");
    }

    // LEY DE WATT Y LEY DE OHM

    public static void calcularLeyWattOhm(
            double voltaje) throws IOException {

        System.out.println();
        System.out.println(
                "--- Ley de Watt y Ley de Ohm ---");

        double potencia = leerDouble(
                "Potencia del dispositivo en watts: ");

        while (potencia <= 0) {

            potencia = leerDouble(
                    "La potencia debe ser mayor que cero: ");
        }

        double intensidad = calcularIntensidad(
                potencia,
                voltaje);

        double resistencia = calcularResistencia(
                voltaje,
                intensidad);

        System.out.printf(
                "I = P / V = %.2f / %.2f = %.2f A%n",
                potencia,
                voltaje,
                intensidad);

        System.out.printf(
                "R = V / I = %.2f / %.2f = %.2f ohmios%n",
                voltaje,
                intensidad,
                resistencia);
    }

    // FUNCIONES DE CÁLCULO

    public static double calcularConsumoDiario(
            double potencia,
            double horas) {

        /*
         * Watts por horas produce Wh.
         * Se divide entre 1000 para convertir de Wh a kWh.
         */
        return potencia * horas / 1000.0;
    }

    public static double calcularConsumoSemanal(
            double diario,
            int dias) {

        return diario * dias;
    }

    public static double calcularConsumoMensual(
            double semanal) {

        return semanal * SEMANAS_POR_MES;
    }

    public static double calcularConsumoAnual(
            double semanal) {

        return semanal * SEMANAS_POR_ANIO;
    }

    public static double sumarColumna(
            double[][] datos,
            int cantidad,
            int columna) {

        double total = 0;

        for (int i = 0; i < cantidad; i++) {
            total += datos[i][columna];
        }

        return total;
    }

    public static double calcularIndiceConsumo(
            double mensual,
            int encendidos,
            int altaPotencia) {

        return mensual
                + encendidos * 2
                + altaPotencia * 5;
    }

    public static String obtenerClasificacion(
            double indice) {

        if (indice < 50) {
            return "BAJO";

        } else if (indice < 120) {
            return "NORMAL";

        } else if (indice < 200) {
            return "ALTO";

        } else {
            return "CRÍTICO";
        }
    }

    public static double calcularIntensidad(
            double potencia,
            double voltaje) {

        return potencia / voltaje;
    }

    public static double calcularResistencia(
            double voltaje,
            double intensidad) {

        return voltaje / intensidad;
    }

    // MÉTODO PRINCIPAL

    public static void main(
            String[] args) throws IOException {

        String[] nombresCatalogo =
                new String[10];

        double[] potenciasCatalogo =
                new double[10];

        String[] nombresSeleccionados =
                new String[MAX_DISPOSITIVOS];

        String[] estados =
                new String[MAX_DISPOSITIVOS];

        String[] prioridades =
                new String[MAX_DISPOSITIVOS];

        double[][] datos =
                new double[MAX_DISPOSITIVOS][7];

        double[][] consumoSemana =
                new double[MAX_DISPOSITIVOS][DIAS_SEMANA];

        /*
         * Una posición para cada tipo de electrodoméstico
         * existente en el catálogo.
         */
        int[] contadorPorTipo =
                new int[nombresCatalogo.length];

        final double tarifa = 0.10;
        final double limiteMensual = 150.0;
        final double voltajeRed = 110.0;

        int cantidad = 0;
        int opcion;

        cargarElectrodomesticos(
                nombresCatalogo,
                potenciasCatalogo);

        do {
            mostrarMenu();

            opcion = leerEnteroEnRango(
                    "Seleccione una opción (1-8): ",
                    1,
                    8);

            switch (opcion) {

                case 1:

                    cantidad =
                            registrarElectrodomesticos(
                                    nombresCatalogo,
                                    potenciasCatalogo,
                                    nombresSeleccionados,
                                    estados,
                                    prioridades,
                                    datos,
                                    consumoSemana,
                                    contadorPorTipo,
                                    cantidad);

                    break;

                case 2:

                    reporteGeneral(
                            datos,
                            cantidad,
                            tarifa);

                    break;

                case 3:

                    detallePorDispositivo(
                            nombresSeleccionados,
                            estados,
                            prioridades,
                            datos,
                            consumoSemana,
                            cantidad,
                            tarifa);

                    break;

                case 4:

                    clasificarYAlertar(
                            estados,
                            prioridades,
                            datos,
                            cantidad,
                            limiteMensual);

                    break;

                case 5:

                    mostrarEstadisticas(
                            nombresSeleccionados,
                            datos,
                            cantidad);

                    break;

                case 6:

                    compararEscenarios(
                            prioridades,
                            datos,
                            cantidad,
                            tarifa);

                    break;

                case 7:

                    calcularLeyWattOhm(
                            voltajeRed);

                    break;

                case 8:

                    mostrarMatrizFinal(
                            nombresSeleccionados,
                            estados,
                            prioridades,
                            datos,
                            consumoSemana,
                            cantidad);

                    System.out.println(
                            "\nGracias por usar SEACER. "
                            + "Cuide su consumo eléctrico.");

                    break;

                default:
                    break;
            }

        } while (opcion != 8);
    }
}