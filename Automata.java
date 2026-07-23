import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Automata {
    private String lineaOriginal;
    private boolean esValida;
    private List<Estado> estados;
    private List<Transicion> transiciones;
    private List<Estado> caminoEstados;
    private List<Transicion> caminoTransiciones;
    private String tipoDFA; // "Numero", "Si", "SiNo", "Desconocido"

    public Automata(String linea) {
        this.lineaOriginal = linea.trim();
        this.estados = new ArrayList<>();
        this.transiciones = new ArrayList<>();
        this.caminoEstados = new ArrayList<>();
        this.caminoTransiciones = new ArrayList<>();
        
        analizar();
    }

    private void analizar() {
        List<String> tokens = tokenizar(lineaOriginal);
        
        if (tokens.isEmpty()) {
            // Línea vacía: DFA simple aceptado
            Estado q0 = new Estado("q0", true, true, false, 200, 100);
            estados.add(q0);
            caminoEstados.add(q0);
            esValida = true;
            tipoDFA = "Vacia";
            return;
        }

        String primerToken = tokens.get(0);
        String primerLower = primerToken.toLowerCase();
        if (primerLower.equals("numero")) {
            tipoDFA = "Numero";
            construirDFANumero();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("cadena")) {
            tipoDFA = "Cadena";
            construirDFACadena();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("si")) {
            tipoDFA = "Si";
            construirDFASi();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("sino")) {
            tipoDFA = "SiNo";
            construirDFASiNo();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("mientras")) {
            tipoDFA = "Mientras";
            construirDFAMientras();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("para")) {
            tipoDFA = "Para";
            construirDFAPara();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("logico")) {
            tipoDFA = "Logico";
            construirDFALogico();
            ejecutarDFA(tokens);
        } else if (primerLower.equals("imprimir") || primerLower.equals("mostrar")) {
            tipoDFA = "Imprimir";
            construirDFAImprimir();
            ejecutarDFA(tokens);
        } else if (clasificarToken(primerToken).equals("id")) {
            tipoDFA = "Asignacion";
            construirDFAAsignacion();
            ejecutarDFA(tokens);
        } else {
            tipoDFA = "Desconocido";
            construirDFADesconocido(primerToken);
            ejecutarDFA(tokens);
        }
    }

    private List<String> tokenizar(String linea) {
        List<String> tokens = new ArrayList<>();
        // Expresión regular para separar palabras clave, operadores relacionales, números, variables, paréntesis, asignación y signos de puntuación
        Pattern pattern = Pattern.compile("Numero|Cadena|SiNo|Si|Mientras|Para|Hasta|Hacer|Logico|Imprimir|Mostrar|verdadero|vardadero|falso|y|o|\"[^\"]*\"|[a-zA-Z_][a-zA-Z0-9_]*|-?\\d+(\\.\\d+)?|==|!=|<=|>=|<|>|=|\\+|\\-|\\*|/|\\(|\\)|:|\\S", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(linea);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

    private String clasificarToken(String token) {
        String valor = token.trim();
        String lower = valor.toLowerCase();

        if (lower.equals("numero")) return "Numero";
        if (lower.equals("cadena")) return "Cadena";
        if (lower.equals("si")) return "Si";
        if (lower.equals("sino")) return "SiNo";
        if (lower.equals("mientras")) return "Mientras";
        if (lower.equals("para")) return "Para";
        if (lower.equals("hasta")) return "Hasta";
        if (lower.equals("hacer")) return "Hacer";
        if (lower.equals("logico")) return "Logico";
        if (lower.equals("imprimir") || lower.equals("mostrar")) return "imprimir";
        if (lower.equals("verdadero") || lower.equals("vardadero")) return "BooleanLiteral";
        if (lower.equals("falso")) return "BooleanLiteral";
        if (lower.equals("y") || lower.equals("o")) return lower;
        if (valor.equals("=")) return "=";
        if (valor.equals("(")) return "(";
        if (valor.equals(")")) return ")";
        if (valor.equals(":")) return ":";
        if (valor.equals("+") || valor.equals("-") || valor.equals("*") || valor.equals("/")) return "operador";
        if (valor.equals("<") || valor.equals(">") || valor.equals("<=") || valor.equals(">=")) return "relacion";
        if (valor.startsWith("\"") && valor.endsWith("\"")) return "CadenaLiteral";
        if (valor.matches("-?\\d+(\\.\\d+)?")) return "NumeroLiteral";
        if (valor.matches("[a-zA-Z_][a-zA-Z0-9_]*")) return "id";
        return "desconocido";
    }

    // ----------------------------------------------------
    // CONSTRUCCIÓN DE LOS AUTÓMATAS (ESTADOS Y TRANSICIONES)
    // ----------------------------------------------------

    private void construirDFAPara() {
        Estado p0 = new Estado("p0", true, false, false, 60, 120);
        Estado p1 = new Estado("p1", false, false, false, 150, 120);
        Estado p2 = new Estado("p2", false, false, false, 240, 120);
        Estado p3 = new Estado("p3", false, false, false, 330, 120);
        Estado p4 = new Estado("p4", false, false, false, 420, 120);
        Estado p5 = new Estado("p5", false, false, false, 510, 120);
        Estado p6 = new Estado("p6", false, true, false, 600, 120); // Aceptación
        Estado pe = new Estado("pe", false, false, true, 330, 220); // Error

        estados.add(p0);
        estados.add(p1);
        estados.add(p2);
        estados.add(p3);
        estados.add(p4);
        estados.add(p5);
        estados.add(p6);
        estados.add(pe);

        transiciones.add(new Transicion(p0, p1, "Para"));
        transiciones.add(new Transicion(p1, p2, "NumeroLiteral"));
        transiciones.add(new Transicion(p1, p2, "id"));
        transiciones.add(new Transicion(p2, p3, "Hasta"));
        transiciones.add(new Transicion(p3, p4, "NumeroLiteral"));
        transiciones.add(new Transicion(p3, p4, "id"));
        transiciones.add(new Transicion(p4, p5, "Hacer"));
        transiciones.add(new Transicion(p5, p6, ":"));
        transiciones.add(new Transicion(p5, p6, ""));
    }

    private void construirDFALogico() {
        Estado l0 = new Estado("l0", true, false, false, 80, 120);
        Estado l1 = new Estado("l1", false, false, false, 180, 120);
        Estado l2 = new Estado("l2", false, false, false, 280, 120);
        Estado l3 = new Estado("l3", false, false, false, 380, 120);
        Estado l4 = new Estado("l4", false, true, false, 480, 120); // Aceptación
        Estado le = new Estado("le", false, false, true, 280, 220); // Error

        estados.add(l0);
        estados.add(l1);
        estados.add(l2);
        estados.add(l3);
        estados.add(l4);
        estados.add(le);

        transiciones.add(new Transicion(l0, l1, "Logico"));
        transiciones.add(new Transicion(l1, l2, "id"));
        transiciones.add(new Transicion(l2, l3, "="));
        transiciones.add(new Transicion(l3, l4, "BooleanLiteral"));
    }

    private void construirDFANumero() {
        // q0 (inicio) -> q1 (Numero) -> q2 (id) -> q3 (=) -> q4 (valor/id)
        Estado q0 = new Estado("q0", true, false, false, 50, 100);
        Estado q1 = new Estado("q1", false, false, false, 170, 100);
        Estado q2 = new Estado("q2", false, false, false, 290, 100);
        Estado q3 = new Estado("q3", false, false, false, 410, 100);
        Estado q4 = new Estado("q4", false, true, false, 530, 100); // Aceptación
        Estado qe = new Estado("qe", false, false, true, 290, 220);  // Error

        estados.add(q0);
        estados.add(q1);
        estados.add(q2);
        estados.add(q3);
        estados.add(q4);
        estados.add(qe);

        transiciones.add(new Transicion(q0, q1, "Numero"));
        transiciones.add(new Transicion(q1, q2, "id"));
        transiciones.add(new Transicion(q2, q3, "="));
        transiciones.add(new Transicion(q3, q4, "NumeroLiteral"));
        transiciones.add(new Transicion(q3, q4, "id"));
        transiciones.add(new Transicion(q4, q3, "operador")); // Permite operaciones: b + c + 1
    }

    private void construirDFACadena() {
        // c0 (inicio) -> c1 (cadena) -> c2 (id) -> c3 (=) -> c4 (CadenaLiteral)
        Estado c0 = new Estado("c0", true, false, false, 50, 100);
        Estado c1 = new Estado("c1", false, false, false, 170, 100);
        Estado c2 = new Estado("c2", false, false, false, 290, 100);
        Estado c3 = new Estado("c3", false, false, false, 410, 100);
        Estado c4 = new Estado("c4", false, true, false, 530, 100); // Estado de Aceptación
        Estado ce = new Estado("ce", false, false, true, 290, 220); // Estado de Error

        estados.add(c0);
        estados.add(c1);
        estados.add(c2);
        estados.add(c3);
        estados.add(c4);
        estados.add(ce);

        transiciones.add(new Transicion(c0, c1, "Cadena"));
        transiciones.add(new Transicion(c1, c2, "id"));
        transiciones.add(new Transicion(c2, c3, "="));
        transiciones.add(new Transicion(c3, c4, "CadenaLiteral"));
        transiciones.add(new Transicion(c3, c4, "id")); // También permite asignar otra variable
        transiciones.add(new Transicion(c4, c3, "operador")); // Permite concatenación o sumas de cadenas
    }

    private void construirDFAImprimir() {
        Estado i0 = new Estado("i0", true, false, false, 50, 120);
        Estado i1 = new Estado("i1", false, false, false, 150, 120);

        // Rama con paréntesis
        Estado i2_par = new Estado("i2_p", false, false, false, 250, 60);
        Estado i3_par = new Estado("i3_p", false, false, false, 370, 60);
        Estado i4_par = new Estado("i4_p", false, true, false, 490, 120); // Aceptación con ()

        // Rama sin paréntesis
        Estado i2_nopar = new Estado("i2", false, true, false, 370, 180); // Aceptación sin ()

        Estado ie = new Estado("ie", false, false, true, 270, 240); // Error

        estados.add(i0);
        estados.add(i1);
        estados.add(i2_par);
        estados.add(i3_par);
        estados.add(i4_par);
        estados.add(i2_nopar);
        estados.add(ie);

        // Transición inicial
        transiciones.add(new Transicion(i0, i1, "imprimir"));

        // Rama con paréntesis: imprimir ( expr )
        transiciones.add(new Transicion(i1, i2_par, "("));
        transiciones.add(new Transicion(i2_par, i3_par, "id"));
        transiciones.add(new Transicion(i2_par, i3_par, "NumeroLiteral"));
        transiciones.add(new Transicion(i2_par, i3_par, "CadenaLiteral"));
        transiciones.add(new Transicion(i2_par, i3_par, "BooleanLiteral"));
        transiciones.add(new Transicion(i2_par, i4_par, ")")); // imprimir() vacío
        transiciones.add(new Transicion(i3_par, i2_par, "operador"));
        transiciones.add(new Transicion(i3_par, i4_par, ")"));

        // Rama sin paréntesis: imprimir expr
        transiciones.add(new Transicion(i1, i2_nopar, "id"));
        transiciones.add(new Transicion(i1, i2_nopar, "NumeroLiteral"));
        transiciones.add(new Transicion(i1, i2_nopar, "CadenaLiteral"));
        transiciones.add(new Transicion(i1, i2_nopar, "BooleanLiteral"));
        transiciones.add(new Transicion(i2_nopar, i1, "operador"));
    }

    private void construirDFAAsignacion() {
        // r0 (inicio) -> r1 (id) -> r2 (=) -> r3 (valor/id/literal)
        Estado r0 = new Estado("r0", true, false, false, 50, 100);
        Estado r1 = new Estado("r1", false, false, false, 170, 100);
        Estado r2 = new Estado("r2", false, false, false, 290, 100);
        Estado r3 = new Estado("r3", false, true, false, 450, 100); // Aceptación
        Estado re = new Estado("re", false, false, true, 290, 220);  // Error

        estados.add(r0);
        estados.add(r1);
        estados.add(r2);
        estados.add(r3);
        estados.add(re);

        transiciones.add(new Transicion(r0, r1, "id"));
        transiciones.add(new Transicion(r1, r2, "="));
        transiciones.add(new Transicion(r2, r3, "NumeroLiteral"));
        transiciones.add(new Transicion(r2, r3, "CadenaLiteral"));
        transiciones.add(new Transicion(r2, r3, "BooleanLiteral"));
        transiciones.add(new Transicion(r2, r3, "id"));
        transiciones.add(new Transicion(r3, r2, "operador")); // Permite a = b + 1
    }
    private void construirDFAMientras() {
        // m0 (inicio) -> m1 (mientras) -> m2 (() -> m3 (izq) -> m4 (relacion/igual/diferente) -> m5 (der) -> m6 ())
        Estado m0 = new Estado("m0", true, false, false, 50, 100);
        Estado m1 = new Estado("m1", false, false, false, 130, 100);
        Estado m2 = new Estado("m2", false, false, false, 210, 100);
        Estado m3 = new Estado("m3", false, false, false, 290, 100);
        Estado m4 = new Estado("m4", false, false, false, 370, 100);
        Estado m5 = new Estado("m5", false, false, false, 450, 100);
        Estado m6 = new Estado("m6", false, true, false, 530, 100); // Estado de Aceptación
        Estado me = new Estado("me", false, false, true, 290, 200);  // Estado de Error

        estados.add(m0);
        estados.add(m1);
        estados.add(m2);
        estados.add(m3);
        estados.add(m4);
        estados.add(m5);
        estados.add(m6);
        estados.add(me);

        // Inicio y apertura de paréntesis
        transiciones.add(new Transicion(m0, m1, "mientras"));
        transiciones.add(new Transicion(m1, m2, "("));

        // Lado izquierdo de la condición (m2 -> m3)
        transiciones.add(new Transicion(m2, m3, "id"));
        transiciones.add(new Transicion(m2, m3, "NumeroLiteral"));
        transiciones.add(new Transicion(m2, m3, "CadenaLiteral"));

        // Operadores relacionales y de igualdad/desigualdad (m3 -> m4)
        transiciones.add(new Transicion(m3, m4, "relacion"));
        transiciones.add(new Transicion(m3, m4, "es igual a"));
        transiciones.add(new Transicion(m3, m4, "es diferente a"));

        // Lado derecho de la condición (m4 -> m5)
        transiciones.add(new Transicion(m4, m5, "id"));
        transiciones.add(new Transicion(m4, m5, "NumeroLiteral"));
        transiciones.add(new Transicion(m4, m5, "CadenaLiteral"));

        // Cierre de paréntesis
        transiciones.add(new Transicion(m5, m6, ")"));
    }
    private void construirDFASi() {
        // s0 (inicio) -> s1 (Si) -> s2_par (abrió paréntesis) o s2_nopar (id/número)
        Estado s0 = new Estado("s0", true, false, false, 50, 120);
        Estado s1 = new Estado("s1", false, false, false, 130, 120);
        
        // Rama con paréntesis
        Estado s2_par = new Estado("s2_p", false, false, false, 210, 60);
        Estado s3_par = new Estado("s3_p", false, false, false, 290, 60);
        Estado s4_par = new Estado("s4_p", false, false, false, 370, 60);
        Estado s5_par = new Estado("s5_p", false, false, false, 450, 60);
        Estado s6_par = new Estado("s6_p", false, true, false, 530, 120); // Aceptación
        
        // Rama sin paréntesis
        Estado s2_nopar = new Estado("s2", false, false, false, 210, 180);
        Estado s3_nopar = new Estado("s3", false, false, false, 330, 180);
        Estado s4_nopar = new Estado("s4", false, true, false, 450, 180); // Aceptación

        Estado se = new Estado("se", false, false, true, 290, 280); // Error

        estados.add(s0);
        estados.add(s1);
        estados.add(s2_par);
        estados.add(s3_par);
        estados.add(s4_par);
        estados.add(s5_par);
        estados.add(s6_par);
        estados.add(s2_nopar);
        estados.add(s3_nopar);
        estados.add(s4_nopar);
        estados.add(se);

        // Transiciones
        transiciones.add(new Transicion(s0, s1, "Si"));
        
        // Rama con paréntesis
        transiciones.add(new Transicion(s1, s2_par, "("));
        transiciones.add(new Transicion(s2_par, s3_par, "id"));
        transiciones.add(new Transicion(s2_par, s3_par, "NumeroLiteral"));
        transiciones.add(new Transicion(s3_par, s4_par, "relacion"));
        transiciones.add(new Transicion(s4_par, s5_par, "id"));
        transiciones.add(new Transicion(s4_par, s5_par, "NumeroLiteral"));
        transiciones.add(new Transicion(s5_par, s6_par, ")"));

        // Rama sin paréntesis
        transiciones.add(new Transicion(s1, s2_nopar, "id"));
        transiciones.add(new Transicion(s1, s2_nopar, "NumeroLiteral"));
        transiciones.add(new Transicion(s2_nopar, s3_nopar, "relacion"));
        transiciones.add(new Transicion(s3_nopar, s4_nopar, "id"));
        transiciones.add(new Transicion(s3_nopar, s4_nopar, "NumeroLiteral"));
    }

    private void construirDFASiNo() {
        Estado e0 = new Estado("e0", true, false, false, 80, 100);
        Estado e1 = new Estado("e1", false, true, false, 280, 100); // Aceptación
        Estado ee = new Estado("ee", false, false, true, 180, 200);  // Error

        estados.add(e0);
        estados.add(e1);
        estados.add(ee);

        transiciones.add(new Transicion(e0, e1, "SiNo"));
    }

    private void construirDFADesconocido(String primerToken) {
        Estado d0 = new Estado("d0", true, false, false, 100, 100);
        Estado de = new Estado("de", false, false, true, 300, 100); // Error directo

        estados.add(d0);
        estados.add(de);

        // Transición directa al error
        transiciones.add(new Transicion(d0, de, primerToken));
    }

    // ----------------------------------------------------
    // EJECUCIÓN DEL AUTÓMATA PASO A PASO
    // ----------------------------------------------------

    private void ejecutarDFA(List<String> tokens) {
        Estado estadoActual = buscarInicial();
        if (estadoActual == null) return;

        caminoEstados.add(estadoActual);
        boolean errorDetectado = false;

        for (String token : tokens) {
            String tokenClasificado = clasificarToken(token);
            
            if (errorDetectado) {
                // Si ya estamos en error, permanecemos en el estado de error (pozo)
                Estado estadoError = buscarError();
                if (estadoError != null) {
                    caminoEstados.add(estadoError);
                }
                continue;
            }

            // Buscar transición válida desde el estado actual
            Transicion transicionValida = buscarTransicion(estadoActual, tokenClasificado);
            if (transicionValida != null) {
                caminoTransiciones.add(transicionValida);
                estadoActual = transicionValida.getDestino();
                caminoEstados.add(estadoActual);
            } else {
                // Transición al estado de error
                errorDetectado = true;
                Estado estadoError = buscarError();
                if (estadoError != null) {
                    // Agregamos una transición implícita de error
                    Transicion transicionError = new Transicion(estadoActual, estadoError, token + " (Error)");
                    caminoTransiciones.add(transicionError);
                    estadoActual = estadoError;
                    caminoEstados.add(estadoActual);
                }
            }
        }

        // Al terminar los tokens, es válida si llegamos a un estado de aceptación y no hubo error
        esValida = estadoActual.isEsAceptacion() && !errorDetectado;
    }

    // Métodos auxiliares de búsqueda

    private Estado buscarInicial() {
        for (Estado e : estados) {
            if (e.isEsInicial()) return e;
        }
        return null;
    }

    private Estado buscarError() {
        for (Estado e : estados) {
            if (e.isEsError()) return e;
        }
        return null;
    }

    private Transicion buscarTransicion(Estado origen, String etiquetaToken) {
        for (Transicion t : transiciones) {
            if (t.getOrigen().equals(origen) && t.getEtiqueta().equals(etiquetaToken)) {
                return t;
            }
        }
        return null;
    }

    // ----------------------------------------------------
    // GETTERS
    // ----------------------------------------------------

    public String getLineaOriginal() {
        return lineaOriginal;
    }

    public boolean isEsValida() {
        return esValida;
    }

    public List<Estado> getEstados() {
        return estados;
    }

    public List<Transicion> getTransiciones() {
        return transiciones;
    }

    public List<Estado> getCaminoEstados() {
        return caminoEstados;
    }

    public List<Transicion> getCaminoTransiciones() {
        return caminoTransiciones;
    }

    public String getTipoDFA() {
        return tipoDFA;
    }
}
