import java.util.Hashtable;

public class TabelaSimbolos {

    private static Hashtable<String, Simbolo> tabelaDeSimbolos;

    TabelaSimbolos() {

        tabelaDeSimbolos = new Hashtable<String, Simbolo>();

        adicionar(AlfabetoEnum.CONST, AlfabetoEnum.CONST.getEnumAlfabeto());
        adicionar(AlfabetoEnum.INTEGER, AlfabetoEnum.INTEGER.getEnumAlfabeto());
        adicionar(AlfabetoEnum.CHAR, AlfabetoEnum.CHAR.getEnumAlfabeto());
        adicionar(AlfabetoEnum.WHILE, AlfabetoEnum.WHILE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.IF, AlfabetoEnum.IF.getEnumAlfabeto());
        adicionar(AlfabetoEnum.REAL, AlfabetoEnum.REAL.getEnumAlfabeto());
        adicionar(AlfabetoEnum.ELSE, AlfabetoEnum.ELSE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.AND, AlfabetoEnum.AND.getEnumAlfabeto());
        adicionar(AlfabetoEnum.OR, AlfabetoEnum.OR.getEnumAlfabeto());
        adicionar(AlfabetoEnum.NOT, AlfabetoEnum.NOT.getEnumAlfabeto());
        adicionar(AlfabetoEnum.IGUAL, AlfabetoEnum.IGUAL.getEnumAlfabeto());
        adicionar(AlfabetoEnum.IGUAL_IGUAL, AlfabetoEnum.IGUAL_IGUAL.getEnumAlfabeto());
        adicionar(AlfabetoEnum.ABRE_PARENTESES, AlfabetoEnum.ABRE_PARENTESES.getEnumAlfabeto());
        adicionar(AlfabetoEnum.FECHA_PARENTESES, AlfabetoEnum.FECHA_PARENTESES.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MENOR, AlfabetoEnum.MENOR.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MAIOR, AlfabetoEnum.MAIOR.getEnumAlfabeto());
        adicionar(AlfabetoEnum.DIFERENTE, AlfabetoEnum.DIFERENTE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MAIOR_IGUAL, AlfabetoEnum.MAIOR_IGUAL.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MENOR_IGUAL, AlfabetoEnum.MENOR_IGUAL.getEnumAlfabeto());
        adicionar(AlfabetoEnum.VIRGULA, AlfabetoEnum.VIRGULA.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MAIS, AlfabetoEnum.MAIS.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MENOS, AlfabetoEnum.MENOS.getEnumAlfabeto());
        adicionar(AlfabetoEnum.MULTIPLICACAO, AlfabetoEnum.MULTIPLICACAO.getEnumAlfabeto());
        adicionar(AlfabetoEnum.DIVISAO, AlfabetoEnum.DIVISAO.getEnumAlfabeto());
        adicionar(AlfabetoEnum.PONTO_VIRGULA, AlfabetoEnum.PONTO_VIRGULA.getEnumAlfabeto());
        adicionar(AlfabetoEnum.BEGIN, AlfabetoEnum.BEGIN.getEnumAlfabeto());
        adicionar(AlfabetoEnum.END, AlfabetoEnum.END.getEnumAlfabeto());
        adicionar(AlfabetoEnum.READLN, AlfabetoEnum.READLN.getEnumAlfabeto());
        adicionar(AlfabetoEnum.BARRA_BARRA, AlfabetoEnum.BARRA_BARRA.getEnumAlfabeto());
        adicionar(AlfabetoEnum.STRING, AlfabetoEnum.STRING.getEnumAlfabeto());
        adicionar(AlfabetoEnum.WRITE, AlfabetoEnum.WRITE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.WRITELN, AlfabetoEnum.WRITELN.getEnumAlfabeto());
        adicionar(AlfabetoEnum.PORCENTAGEM, AlfabetoEnum.PORCENTAGEM.getEnumAlfabeto());
        adicionar(AlfabetoEnum.ABRE_COLCHETE, AlfabetoEnum.ABRE_COLCHETE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.FECHA_COLCHETE, AlfabetoEnum.FECHA_COLCHETE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.TRUE, AlfabetoEnum.TRUE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.FALSE, AlfabetoEnum.FALSE.getEnumAlfabeto());
        adicionar(AlfabetoEnum.BOOLEAN, AlfabetoEnum.BOOLEAN.getEnumAlfabeto());
        adicionar(AlfabetoEnum.IDENTIFICADOR, AlfabetoEnum.IDENTIFICADOR.getEnumAlfabeto());
        adicionar(AlfabetoEnum.EOF, AlfabetoEnum.EOF.getEnumAlfabeto());
    }

    /*
     *  REALIZA A PESQUISA DO SIMBOLO DENTRO DA TABELA
     */
    public static Simbolo buscar(String lexema){
        return tabelaDeSimbolos.get(lexema);
    }

    /*
     *  REALIZA A INSERCAO DO SIMBOLO NA TABELA
     */
    public static Simbolo adicionar(AlfabetoEnum token, String lexema){
        Simbolo simbolo = new Simbolo(token);
        tabelaDeSimbolos.put(lexema, simbolo);
        return simbolo;
    }

}