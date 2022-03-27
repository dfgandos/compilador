/*
   Ciência da Computação - PUC Minas
   Professor: Alexei Machado
   Disciplina: Compiladores
   Alunos: Darlan Francisco, Larissa Leite e Ygor Melo
*/

import java.util.*;
import java.io.*;

public class TPCompiladores {

   public static PushbackReader leitura;
   public static int linhaPrograma = 1;

   public static void main(String[] args) {
      Token tokenLido = new Token();

      // LEITURA DO TECLADO
      leitura = new PushbackReader(new InputStreamReader(System.in));
      TabelaSimbolos tabela = new TabelaSimbolos();
      // AnalisadorLexico analisadorLexico = new AnalisadorLexico();

      try {
         tokenLido = AnalisadorLexico.obterProximoToken();
         System.out.println("Lido: " + tokenLido.getTipoToken());
         System.out.println(linhaPrograma + " linhas compiladas.");
         leitura.close();

      } catch (Exception e) {
         String message = e.getMessage();
         System.out.println(message);
      }
   }

   static public int incrementarLinha() {
      return linhaPrograma++;
   }

   static public int getLinhaPrograma() {
      return linhaPrograma;
   }
}

// ANALISAR SINTATICO
class AnalizadorSintatico extends AnalisadorLexico {
   /*
    * GRAMATICA DEFINIDA ATÉ COMANDO DE TESTE, NECESSÁRIO COMPLEMENTAR O RESTANTE
    * PG -> {DEC | CMD} EOF
    * DEC -> DEC_V | DEC_C
    * DEC_V -> (INTEGER | REAL | STRING | BOOLEAN | CHAR ) ID [ = [-] VALOR ] {, ID
    * [ = [-] VALOR ] } ;
    * DEC_C -> CONST ID = [ [-] VALOR];
    * CMD -> CMD_A | CMD_R | CMD_T | ; | CMD_L | CMD_E
    * CMD_A -> ID ['[' EXP']'] = EXP;
    * CMD_R -> WHILE EXP (CMD | 'BEGIN' {CMD} 'END');
    * CMD_T -> IF EXP (CMD | 'BEGIN' {CMD} 'END') [else (CMD | 'BEGIN' {CMD}
    * 'END')];
    */

   public static Token tokenLido;
   public static String tipoEsperado;

   // ANALISADOR RECEBE O TOKEN
   public AnalizadorSintatico() {
      tokenLido = new Token();
   }

   public void inicializador() throws ErroPersonalizado, IOException {
      tokenLido = AnalisadorLexico.obterProximoToken();
      PG();
   }

   private boolean verificaCMD() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.IDENTIFICADOR || tokenLido.getTipoToken() == AlfabetoEnum.WHILE
            || tokenLido.getTipoToken() == AlfabetoEnum.IF ||
            tokenLido.getTipoToken() == AlfabetoEnum.READLN || tokenLido.getTipoToken() == AlfabetoEnum.WRITE
            || tokenLido.getTipoToken() == AlfabetoEnum.WRITELN ||
            tokenLido.getTipoToken() == AlfabetoEnum.PONTO_VIRGULA || tokenLido.getTipoToken() == AlfabetoEnum.BEGIN
            || tokenLido.getTipoToken() == AlfabetoEnum.END || tokenLido.getTipoToken() == AlfabetoEnum.ELSE);
   }

   private boolean verificaDEC() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.INTEGER || tokenLido.getTipoToken() == AlfabetoEnum.REAL
            || tokenLido.getTipoToken() == AlfabetoEnum.CHAR ||
            tokenLido.getTipoToken() == AlfabetoEnum.STRING || tokenLido.getTipoToken() == AlfabetoEnum.BOOLEAN);
   }

   private boolean verificaCONST() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.CONST);
   }

   private boolean verificaVAR() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.INTEGER || tokenLido.getTipoToken() == AlfabetoEnum.REAL
            || tokenLido.getTipoToken() == AlfabetoEnum.CHAR ||
            tokenLido.getTipoToken() == AlfabetoEnum.STRING || tokenLido.getTipoToken() == AlfabetoEnum.BOOLEAN);
   }

   // PG -> {DEC | CMD} EOF
   public void PG() throws ErroPersonalizado, IOException {
      while (verificaDEC() || verificaCMD()) {
         if (verificaDEC()) {
            DEC();
         } else {
            // CMD();
         }
      }
      // casatoken(AlfabetoEnum.EOF);
   }

   // DEC -> DEC_V | DEC_C
   public void DEC() throws ErroPersonalizado, IOException {
      if (verificaCONST()) {
         // DEC_C();
      } else if (verificaVAR()) {
         DEC_V();
      }
   }

   // DEC_V -> (INTEGER | REAL | STRING | BOOLEAN | CHAR ) ID [ = [-] VALOR ] {, ID
   // [ = [-] VALOR ] } ;
   public void DEC_V() throws ErroPersonalizado, IOException {

      if (tokenLido.getTipoToken() == AlfabetoEnum.INTEGER) {
         tipoEsperado = "int";
         // CASATOKEN(INT);
      } else if (tokenLido.getTipoToken() == AlfabetoEnum.REAL) {
         tipoEsperado = "real";
         // CASATOKEN(REAL);
      } else if (tokenLido.getTipoToken() == AlfabetoEnum.STRING) {
         tipoEsperado = "string";
         // CASATOKEN(STRING);
      } else if (tokenLido.getTipoToken() == AlfabetoEnum.BOOLEAN) {
         tipoEsperado = "boolean";
         // CASATOKEN(BOOLEAN);
      } else {
         tipoEsperado = "char";
         // CASATOKEN(CHAR);
      }
   }

}

/*
 * CLASSES DE ERROS
 */
class ErroPersonalizado extends Exception {
   public ErroPersonalizado(String mensagem) {
      super(mensagem);
   }
}

class ErroFimDeArquivoNaoEsperado extends ErroPersonalizado {
   public ErroFimDeArquivoNaoEsperado(int linhaPrograma) {
      super(linhaPrograma + "\nfim de arquivo nao esperado.");
   }
}

class ErroCaractereInvalido extends ErroPersonalizado {
   public ErroCaractereInvalido(int linhaPrograma) {
      super(linhaPrograma + "\ncaractere invalido.");
   }
}

class ErroLexemaNaoIdentificado extends ErroPersonalizado {
   public ErroLexemaNaoIdentificado(int linhaPrograma, String lexema) {
      super(linhaPrograma + "\nlexema nao identificado [" + lexema + "].");
   }
}

class ErroTokenNaoEsperado extends ErroPersonalizado {
   public ErroTokenNaoEsperado(int linhaPrograma, String lexema) {
      super(linhaPrograma + "\ntoken nao esperado [" + lexema + "].");
   }
}

/*
 * ENUM REPRESENTA O ALFABETO DA LINGUAGEM COM A SUA NUMERAÇÃO DE TOKENS
 */
enum AlfabetoEnum {

   CONST("const"), INTEGER("integer"), CHAR("char"),
   WHILE("while"), IF("if"), REAL("real"), ELSE("else"),
   AND("and"), OR("or"), NOT("not"), IGUAL("="), IGUAL_IGUAL("=="),
   ABRE_PARENTESES("("), FECHA_PARENTESES(")"), MENOR("<"),
   MAIOR(">"), DIFERENTE("!="), MAIOR_IGUAL(">="),
   MENOR_IGUAL("<="), VIRGULA(","),
   MAIS("+"), MENOS("-"), MULTIPLICACAO("*"),
   DIVISAO("/"), PONTO_VIRGULA(";"), BEGIN("begin"),
   END("end"), READLN("readln"), BARRA_BARRA("//"), STRING("string"),
   WRITE("write"), WRITELN("writeln"), PORCETAGEM("%"),
   ABRE_COLCHETE("["), FECHA_COLCHETE("]"),
   TRUE("TRUE"), FALSE("FALSE"), BOOLEAN("boolean"),
   IDENTIFICADOR("id"), EOF("eof");

   private String enumAlfabeto;

   AlfabetoEnum(String enumAlfabeto) {
      this.enumAlfabeto = enumAlfabeto;
   }

   public String getEnumAlfabeto() {
      return this.enumAlfabeto;
   }

}

/*
 * CLASSE PARA CRIAÇÃO DOS SIMBOLOS
 */
class Simbolo {

   private AlfabetoEnum token;
   private int tamanho;

   Simbolo(AlfabetoEnum token) {
      this.token = token;
   }

   public AlfabetoEnum getToken() {
      return token;
   }

   public void setTamanho(int tamanho) {
      this.tamanho = tamanho;
   }

   public int getTamanho() {
      return this.tamanho;
   }

}

/*
 * CLASSE TABELA DE SIMBOLOS
 */
class TabelaSimbolos {

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
      adicionar(AlfabetoEnum.PORCETAGEM, AlfabetoEnum.PORCETAGEM.getEnumAlfabeto());
      adicionar(AlfabetoEnum.ABRE_COLCHETE, AlfabetoEnum.ABRE_COLCHETE.getEnumAlfabeto());
      adicionar(AlfabetoEnum.FECHA_COLCHETE, AlfabetoEnum.FECHA_COLCHETE.getEnumAlfabeto());
      adicionar(AlfabetoEnum.TRUE, AlfabetoEnum.TRUE.getEnumAlfabeto());
      adicionar(AlfabetoEnum.FALSE, AlfabetoEnum.FALSE.getEnumAlfabeto());
      adicionar(AlfabetoEnum.BOOLEAN, AlfabetoEnum.BOOLEAN.getEnumAlfabeto());
      adicionar(AlfabetoEnum.IDENTIFICADOR, AlfabetoEnum.IDENTIFICADOR.getEnumAlfabeto());
      adicionar(AlfabetoEnum.EOF, AlfabetoEnum.EOF.getEnumAlfabeto());
   }

   /*
    * REALIZA A PESQUISA DO SIMBOLO DENTRO DA TABELA
    */
   public static Simbolo buscar(String lexema) {
      return tabelaDeSimbolos.get(lexema);
   }

   /*
    * REALIZA A INSERÇÃO DO SIMBOLO NA TABELA
    */
   public static Simbolo adicionar(AlfabetoEnum token, String lexema) {
      Simbolo simbolo = new Simbolo(token);
      tabelaDeSimbolos.put(lexema, simbolo);
      return simbolo;
   }

}

/*
 * ENUM PARA OS TIPOS
 */
enum TipoEnum {
   INTEGER, REAL, CHAR, STRING, BOOLEAN, NULL;
}

/*
 * CLASSE DE TOKENS
 */
class Token {

   private AlfabetoEnum tipoToken;
   private String lexema;
   private Simbolo simbolo;
   private TipoEnum tipoConstante;

   public Token() {
      this.tipoConstante = null;
      return;
   }

   public void limpaLexema() {
      this.lexema = "";
   }

   public void setTipoToken(AlfabetoEnum tipoToken) {
      this.tipoToken = tipoToken;
   }

   public void setTipoConstante(TipoEnum tipoConstante) {
      this.tipoConstante = tipoConstante;
   }

   public TipoEnum getTipoConstante() {
      return this.tipoConstante;
   }

   public void setLexema(String lexema) {
      this.lexema = lexema;
   }

   public AlfabetoEnum getTipoToken() {
      return this.tipoToken;
   }

   public void setSimbolo(Simbolo simbolo) {
      this.simbolo = simbolo;
   }

   public Simbolo getSimbolo() {
      return this.simbolo;
   }

   public String getLexema() {
      return this.lexema;
   }

}

/*
 * ESTADOS POSSIVEIS NO ANALISADOR LEXICO
 */
enum Estados {
   INICIO, FIM, MENOR, MAIOR, IDENTIFICADOR, INICIO_COMENTARIO, FIM_COMENTARIO, COMENTARIO,
   PRIMEIRO_DECIMAL, DECIMAL, DIGITO, HEXADECIMAL1, DIVISAO,
   HEXADECIMAL2, CARACTER1, CARACTER2, STRING, IGUAL, EXCLAMACAO;
}

/*
 * CLASSE DO ANALISADOR LEXICO
 */
class AnalisadorLexico {
   public static Token obterProximoToken() throws ErroPersonalizado, IOException {

      int byteLido;
      char caracterAnalisado;
      Estados estado = Estados.INICIO;
      Token token = new Token();
      boolean devolve;
      String lexema = "";

      while (estado != Estados.FIM) {
         devolve = false;
         byteLido = TPCompiladores.leitura.read();

         if (byteLido == -1 && estado != Estados.INICIO) {
            throw new ErroFimDeArquivoNaoEsperado(TPCompiladores.getLinhaPrograma());
         }

         caracterAnalisado = (char) byteLido;

         if (!charValido(caracterAnalisado)) {
            throw new ErroFimDeArquivoNaoEsperado(TPCompiladores.getLinhaPrograma());
         }

         /*
          * IMPLEMENTAÇÃO DO AUTOMATO
          */
         switch (estado) {
            case INICIO:
               lexema = "";
               switch (caracterAnalisado) {

                  /*
                   * UNICO CARACTER PARA SER TOKEN
                   */

                  case '(':
                     token.setTipoToken(AlfabetoEnum.ABRE_PARENTESES);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case ')':
                     token.setTipoToken(AlfabetoEnum.FECHA_PARENTESES);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case ',':
                     token.setTipoToken(AlfabetoEnum.VIRGULA);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case '+':
                     token.setTipoToken(AlfabetoEnum.MAIS);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case '-':
                     token.setTipoToken(AlfabetoEnum.MENOS);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case '*':
                     token.setTipoToken(AlfabetoEnum.MULTIPLICACAO);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case ';':
                     token.setTipoToken(AlfabetoEnum.PONTO_VIRGULA);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case '%':
                     token.setTipoToken(AlfabetoEnum.PORCETAGEM);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case '[':
                     token.setTipoToken(AlfabetoEnum.ABRE_COLCHETE);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  case ']':
                     token.setTipoToken(AlfabetoEnum.FECHA_COLCHETE);
                     token.setLexema(lexema);
                     estado = Estados.FIM;
                     break;

                  /*
                   * DOIS OU MAIS CARACTERES PARA SEREM TOKENS
                   */

                  case '=':
                     estado = Estados.IGUAL;
                     break;

                  case '<':
                     estado = Estados.MENOR;
                     break;

                  case '>':
                     estado = Estados.MAIOR;
                     break;

                  case '!':
                     estado = Estados.EXCLAMACAO;
                     break;

                  case '/':
                     estado = Estados.DIVISAO;
                     break;

                  case '{':
                     estado = Estados.INICIO_COMENTARIO;
                     break;

                  case '.':
                     estado = Estados.PRIMEIRO_DECIMAL;
                     break;

                  case '_':
                     estado = Estados.IDENTIFICADOR;
                     break;

                  case '\'':
                     estado = Estados.CARACTER1;
                     break;

                  case '"':
                     estado = Estados.STRING;
                     break;

                  case (char) -1:
                     token.setTipoToken(AlfabetoEnum.EOF);
                     estado = Estados.FIM;
                     break;

                  case ' ':
                     break;

                  case '\r':
                     break;

                  case '\n':
                     break;

                  case '\t':
                     break;

                  default:
                     if (inicioIdentificador(caracterAnalisado)) {
                        estado = Estados.IDENTIFICADOR;
                     } else if (caracterAnalisado >= '0' && caracterAnalisado <= '9') {
                        estado = Estados.DIGITO;
                     } else {
                        throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(),
                              lexema + caracterAnalisado);
                     }
                     break;
               }
               break;

            /* IGUAL_IGUAL */
            case IGUAL:
               switch (caracterAnalisado) {
                  case '=':
                     token.setTipoToken(AlfabetoEnum.IGUAL_IGUAL);
                     token.setLexema(lexema);
                     break;
                  default:
                     token.setTipoToken(AlfabetoEnum.IGUAL);
                     token.setLexema(lexema);
                     devolve = true;
                     break;
               }
               estado = Estados.FIM;
               break;

            /* // */
            case DIVISAO:
               switch (caracterAnalisado) {
                  case '/':
                     token.setTipoToken(AlfabetoEnum.BARRA_BARRA);
                     token.setLexema(lexema);
                     break;
                  default:
                     token.setTipoToken(AlfabetoEnum.DIVISAO);
                     token.setLexema(lexema);
                     devolve = true;
                     break;
               }
               estado = Estados.FIM;
               break;

            /* <= */
            case MENOR:
               switch (caracterAnalisado) {
                  case '=':
                     token.setTipoToken(AlfabetoEnum.MENOR_IGUAL);
                     token.setLexema(lexema);
                     break;
                  default:
                     token.setTipoToken(AlfabetoEnum.MAIOR);
                     token.setLexema(lexema);
                     devolve = true;
                     break;
               }
               estado = Estados.FIM;
               break;

            /* >= */
            case MAIOR:
               switch (caracterAnalisado) {
                  case '=':
                     token.setTipoToken(AlfabetoEnum.MAIOR_IGUAL);
                     token.setLexema(lexema);
                     break;
                  default:
                     token.setTipoToken(AlfabetoEnum.MAIOR);
                     token.setLexema(lexema);
                     devolve = true;
                     break;
               }
               estado = Estados.FIM;
               break;

            /* != */
            case EXCLAMACAO:
               if (caracterAnalisado == '=') {
                  token.setTipoToken(AlfabetoEnum.DIFERENTE);
                  token.setLexema(lexema);
                  estado = Estados.FIM;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case INICIO_COMENTARIO:
               switch (caracterAnalisado) {
                  case '*':
                     estado = Estados.COMENTARIO;
                     break;
               }
               break;

            case COMENTARIO:
               if (caracterAnalisado == '*')
                  estado = Estados.FIM_COMENTARIO;
               break;

            case FIM_COMENTARIO:
               switch (caracterAnalisado) {
                  case '}':
                     estado = Estados.INICIO;
                     break;
                  case '*':
                     /* CONTINUAÇÃO DA LEITURA */
                     break;
                  default:
                     estado = Estados.COMENTARIO;
                     break;
               }
               break;

            case DIGITO:
               if (caracterAnalisado == '.') {
                  estado = Estados.PRIMEIRO_DECIMAL;
               } else if (hexadecimal(caracterAnalisado)) {
                  estado = Estados.HEXADECIMAL1;
               } else if (!digito(caracterAnalisado)) {
                  devolve = true;
                  // token.setTipoToken(AlfabetoEnum.VALOR);
                  token.setLexema(lexema);
                  token.setTipoConstante(TipoEnum.INTEGER);

                  try {
                     Integer.parseInt(lexema);
                  } catch (NumberFormatException e) {
                     throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                  }
                  estado = Estados.FIM;
               } else {
                  estado = Estados.DIGITO;
               }
               break;

            case HEXADECIMAL1:
               if (hexadecimalH(caracterAnalisado)) {
                  estado = Estados.HEXADECIMAL2;
               } else if (digito(caracterAnalisado)) {
                  estado = Estados.DIGITO;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case HEXADECIMAL2:
               token.setLexema(lexema);
               token.setTipoConstante(TipoEnum.CHAR);
               estado = Estados.FIM;
               break;

            case IDENTIFICADOR:
               if (fimIdentificador(caracterAnalisado)) {
                  // o caracter lido deve ser devolvido
                  devolve = true;
                  token.setTipoToken(AlfabetoEnum.IDENTIFICADOR);
                  if (lexema.length() > 32) {
                     throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                  }
                  estado = Estados.FIM;
               }
               break;

            case STRING:
               if (stringValida(caracterAnalisado)) {
                  estado = Estados.STRING;
               } else if (caracterAnalisado == '"') {
                  // token.setTipoToken(AlfabetoEnum.VALOR);
                  token.setLexema(lexema);
                  token.setTipoConstante(TipoEnum.STRING);
                  // verificação tamanho
                  if (lexema.length() - 2 > 255) {
                     throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                  }
                  estado = Estados.FIM;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case CARACTER1:
               if (charImprimivel(caracterAnalisado)) {
                  estado = Estados.CARACTER2;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case CARACTER2:
               if (caracterAnalisado == '\'') {
                  // token.setTipoToken(AlfabetoEnum.VALOR);
                  token.setLexema(lexema);
                  token.setTipoConstante(TipoEnum.CHAR);
                  estado = Estados.FIM;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case PRIMEIRO_DECIMAL:
               if (digito(caracterAnalisado)) {
                  estado = Estados.DECIMAL;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;

            case DECIMAL:
               if (digito(caracterAnalisado)) {
                  estado = Estados.DECIMAL;
               } else {
                  devolve = true;
                  // token.setTipoToken(AlfabetoEnum.VALOR);
                  token.setTipoConstante(TipoEnum.REAL);
                  token.setLexema(lexema);
                  if (lexema.length() - 1 > 6) {
                     throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                  }
                  estado = Estados.FIM;
               }
               break;

            default:
               break;
         }

         if (devolve) {
            TPCompiladores.leitura.unread(byteLido);
         } else {
            lexema += caracterAnalisado;
            if (caracterAnalisado == '\n') {
               TPCompiladores.incrementarLinha();
            }
         }
      }

      token.setLexema(lexema);

      if (token.getTipoToken() == AlfabetoEnum.IDENTIFICADOR) {
         Simbolo simbolo = TabelaSimbolos.buscar(lexema);

         if (simbolo == null)
            simbolo = TabelaSimbolos.adicionar(AlfabetoEnum.IDENTIFICADOR, lexema);
         token.setSimbolo(simbolo);
         token.setTipoToken(simbolo.getToken());
      }

      return token;
   }

   private static boolean hexadecimal(char c) {
      return (digito(c) || (c >= 'A' && c <= 'F'));
   }

   private static boolean hexadecimalH(char c) {
      return (c == 'h');
   }

   /*
    * private static boolean charImprimivel(char c) {
    * return (c == '\t' || (c >= ' ' && c <= '"') || (c >= 'A' && c <= ']') || c ==
    * '/' || c == '_'
    * || (c >= 'a' && c <= '}') || (c >= '%' && c <= '?'));
    * }
    */

   // validar menor e igual
   private static boolean charImprimivel(char c) {
      return (c == '\r' || (c >= ' ' && c <= '"') || c == '_' || (c >= '+' && c <= '/') || (c >= ':' && c <= ';')
            || (c >= '%' && c <= ')')
            || (c >= '>' && c <= ']') || letra(c) || digito(c) || c == '{' || (c == '<' && c == '='));
   }

   private static boolean charValido(char c) {
      return charImprimivel(c) || c == '\n' || c == '\r' || c == (char) -1;
   }

   private static boolean letra(char c) {
      return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
   }

   private static boolean digito(char c) {
      return (c >= '0' && c <= '9');
   }

   private static boolean fimIdentificador(char c) {
      return !(letra(c) || digito(c) || (c == '.') || (c == '_'));
   }

   private static boolean inicioIdentificador(char c) {
      return (letra(c) || c == '_');
   }

   /*
    * private static boolean stringValida(char c) {
    * return (c == '\t' || (c >= ' ' && c < '"') || (c >= 'A' && c <= ']') || c ==
    * '/' || c == '_'
    * || (c >= 'a' && c <= '}') || (c >= '(' && c <= '?') || (c == '%') || (c ==
    * '&') || (c == '\''));
    * }
    */

   private static boolean stringValida(char c) {
      return (c == '\r' || (c >= ' ' && c < '"') || c == '_' || (c >= '+' && c <= '/') || (c >= ':' && c <= ';')
            || (c >= '%' && c <= ')')
            || (c >= '>' && c <= ']') || letra(c) || digito(c) || c == '{' || (c == '<' || c == '='));
   }

}
