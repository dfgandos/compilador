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

   public static void main(String[] args) {

      // LEITURA DO TECLADO
      leitura = new PushbackReader(new InputStreamReader(System.in));
      TabelaSimbolos tabela = new TabelaSimbolos();
      try {

         // IMPLEMENTAR

      } catch (Exception e) {
         String message = e.getMessage();
         System.out.println(message);
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
 * 
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
   CLASSE TABELA DE SIMBOLOS
*/
class TabelaSimbolos {

   private static Hashtable<String, Simbolo> tabelaDeSimbolos;

   TabelaSimbolos() {

      tabelaDeSimbolos = new Hashtable<String, Simbolo>();

      // Larissa terminar
      adicionar(AlfabetoEnum.CONST, AlfabetoEnum.CONST.getEnumAlfabeto());

   }

   /*
      REALIZA A PESQUISA DO SIMBOLO DENTRO DA TABELA
   */
   public static Simbolo buscar(String lexema){
      return tabelaDeSimbolos.get(lexema);
   }

   /*
      REALIZA A INSERÇÃO DO SIMBOLO NA TABELA
   */
   public static Simbolo adicionar(AlfabetoEnum token, String lexema){
      Simbolo simbolo = new Simbolo(token);
      tabelaDeSimbolos.put(lexema, simbolo);
      return simbolo;
   }

}

/*
   ENUM PARA OS TIPOS
*/
enum TipoEnum {
   INTEGER, REAL, CHAR, STRING, BOOLEAN, NULL;
}

/*
   CLASSE DE TOKENS
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