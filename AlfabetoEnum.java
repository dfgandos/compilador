/*
 * ENUM REPRESENTA O ALFABETO DA LINGUAGEM COM A SUA NUMERACAO DE TOKENS
 */
public enum AlfabetoEnum {

    CONST("const"), INTEGER("integer"), CHAR("char"),
    WHILE("while"), IF("if"), REAL("real"), ELSE("else"),
    AND("and"), OR("or"), NOT("not"), IGUAL("="), IGUAL_IGUAL("=="),
    ABRE_PARENTESES("("), FECHA_PARENTESES(")"), MENOR("<"),
    MAIOR(">"), DIFERENTE("!="), MAIOR_IGUAL(">="),
    MENOR_IGUAL("<="), VIRGULA(","),
    MAIS("+"), MENOS("-"), MULTIPLICACAO("*"),
    DIVISAO("/"), PONTO_VIRGULA(";"), BEGIN("begin"),
    END("end"), READLN("readln"), BARRA_BARRA("//"), STRING("string"),
    WRITE("write"), WRITELN("writeln"), PORCENTAGEM("%"),
    ABRE_COLCHETE("["), FECHA_COLCHETE("]"),
    TRUE("TRUE"), FALSE("FALSE"), BOOLEAN("boolean"),
    IDENTIFICADOR("id"), EOF("eof"), VALOR("valor");

    private String enumAlfabeto;

    AlfabetoEnum(String enumAlfabeto) {
        this.enumAlfabeto = enumAlfabeto;
    }

    public String getEnumAlfabeto() {
        return this.enumAlfabeto;
    }

}