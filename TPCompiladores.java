/*
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
      
      // LEITURA DO TECLADO
      leitura = new PushbackReader(new InputStreamReader(System.in));
      TabelaSimbolos tabela = new TabelaSimbolos();
      AnalisadorLexico analisadorLexico = new AnalisadorLexico();
      AnalisadorSintatico analisadorSintatico = new AnalisadorSintatico();
   
      try {
      
         // TESTE LEXICO
         //Token tokenLido = new Token();
         //tokenLido = AnalisadorLexico.obterProximoToken();
         //System.out.println("Lido: " + tokenLido.getLexema());
         //System.out.println("Tipo: " + tokenLido.getTipoToken());
      
         analisadorSintatico.inicializador();
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

/*
 * ANALISAR SINTATICO
 */
class AnalisadorSintatico extends AnalisadorLexico {

   /*
      PG -> {DEC | CMD} EOF
      DEC -> DEC_V | DEC_C
      DEC_V -> (INTEGER | REAL | STRING | BOOLEAN | CHAR ) ID [ = VALOR ] {, ID [ = VALOR ] } ;
      DEC_C -> CONST ID = [ [-] VALOR];
      CMD -> CMD_A | CMD_R | CMD_T | ; | CMD_L | CMD_E
      CMD_A -> ID ['[' EXP']'] = EXP;
      CMD_R -> WHILE EXP (CMD | 'BEGIN' {CMD} 'END');
      CMD_T -> IF EXP (CMD | 'BEGIN' {CMD} 'END') [else (CMD | 'BEGIN' {CMD} 'END')];
      CMD_L -> READLN '('ID')'';
      CMD_E -> (WRITE | WRITELN) '(' EXP {, EXP } ')';
      EXP -> SEXP [( == | != | < | > | <= | >= ) SEXP]
      SEXP -> [+ | -] T {(+ | - | OR) T}
      T -> F {(* | AND | / | // | %) F}
      F -> NOT F | INTEGER '(' EXP ')' | REAL '(' EXP ')' | '(' EXP ')' | ID ['(' EXP ')'] | CONST
   */

   public static Token tokenLido;

   //ANALISADOR RECEBE O TOKEN
   public AnalisadorSintatico(){
      tokenLido = new Token();
   }

   public void CASATOKEN(AlfabetoEnum tokenEsperado) throws ErroPersonalizado, IOException {
      /* TESTE CASATOKEN */
      //System.out.println("TK E: " + tokenEsperado);
      //System.out.println("TK L: " + tokenLido.getTipoToken());
      if (tokenLido.getTipoToken() == tokenEsperado) {
         if (tokenLido.getTipoToken() != AlfabetoEnum.EOF)
            tokenLido = AnalisadorLexico.obterProximoToken();
      } else if (tokenLido.getTipoToken() == AlfabetoEnum.EOF){
         throw new ErroFimDeArquivoNaoEsperado(TPCompiladores.getLinhaPrograma());
      } else{
         throw new ErroTokenNaoEsperado(TPCompiladores.getLinhaPrograma(), tokenLido.getLexema());
      }
   }

   public void inicializador() throws ErroPersonalizado, IOException{
      tokenLido = AnalisadorLexico.obterProximoToken();
      PG();
   }

   public boolean verificaCMD() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.IDENTIFICADOR || tokenLido.getTipoToken() == AlfabetoEnum.WHILE || tokenLido.getTipoToken() == AlfabetoEnum.IF || 
         tokenLido.getTipoToken() == AlfabetoEnum.READLN || tokenLido.getTipoToken() == AlfabetoEnum.WRITE || tokenLido.getTipoToken() == AlfabetoEnum.WRITELN || 
         tokenLido.getTipoToken() == AlfabetoEnum.BEGIN || tokenLido.getTipoToken() == AlfabetoEnum.END || tokenLido.getTipoToken() == AlfabetoEnum.ELSE);
   }

   public boolean verificaDEC() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.INTEGER  || tokenLido.getTipoToken() == AlfabetoEnum.REAL || tokenLido.getTipoToken() == AlfabetoEnum.CHAR ||
         tokenLido.getTipoToken() == AlfabetoEnum.STRING || tokenLido.getTipoToken() == AlfabetoEnum.BOOLEAN ||tokenLido.getTipoToken() == AlfabetoEnum.CONST );
   }

   public boolean verificacCONST(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.CONST);
   }

   public boolean verificaCMDA(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.IDENTIFICADOR);
   }

   public boolean verificaCMDR(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.WHILE);
   }

   public boolean verificaCMDT(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.IF);
   }

   public boolean verificaCMDL(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.READLN);
   }

   public boolean verificaCMDE(){
      return(tokenLido.getTipoToken() == AlfabetoEnum.WRITE || tokenLido.getTipoToken() == AlfabetoEnum.WRITELN);
   }

   private boolean verificaOPR() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.IGUAL_IGUAL || tokenLido.getTipoToken() == AlfabetoEnum.MENOR
         || tokenLido.getTipoToken() == AlfabetoEnum.MAIOR || tokenLido.getTipoToken() == AlfabetoEnum.MENOR_IGUAL || tokenLido.getTipoToken() == AlfabetoEnum.MAIOR_IGUAL);
   }

   //PG -> {DEC | CMD} EOF
   public void PG() throws ErroPersonalizado, IOException{
      while(verificaDEC() || verificaCMD()){
         if(verificaCMD()){
            CMD();
         }else{
            DEC();
         }
      }
      CASATOKEN(AlfabetoEnum.EOF);
   }

   //DEC -> DEC_V | DEC_C
   public void DEC() throws ErroPersonalizado, IOException{
      if(verificacCONST()){
         DEC_C();
      }else {
         DEC_V();
      }
   }

   //DEC_V -> (INTEGER | REAL | STRING | BOOLEAN | CHAR ) ID [ = [-] VALOR ] {, ID [ = [-] VALOR ] } ;
   //DEC_V -> (INTEGER | REAL | STRING | BOOLEAN | CHAR ) ID (1)(2) [ = [-] VALOR ] {, ID [ = [-] VALOR (3)(4) ] } ;
   public void DEC_V() throws ErroPersonalizado, IOException{

      TipoEnum tipoVariavel;
      Token tokenID;
      boolean negacao = false;

      switch (tokenLido.getTipoToken()) {
         case INTEGER:
            tipoVariavel = TipoEnum.INTEGER;
            CASATOKEN(AlfabetoEnum.INTEGER);
         break;

         case REAL:
            tipoVariavel = TipoEnum.REAL;
            CASATOKEN(AlfabetoEnum.REAL);
         break;

         case CHAR:
            tipoVariavel = TipoEnum.CHAR;
            CASATOKEN(AlfabetoEnum.CHAR);
         break;

         case BOOLEAN:
            tipoVariavel = TipoEnum.BOOLEAN;
            CASATOKEN(AlfabetoEnum.BOOLEAN);
         break;

         default:
            tipoVariavel = TipoEnum.STRING;
            CASATOKEN(AlfabetoEnum.STRING);
         break;
      }

      tokenID = tokenLido;
      CASATOKEN(AlfabetoEnum.IDENTIFICADOR);

      if(tokenID.getSimbolo().getTipoClasse() != null){
         throw new ErroIdentificadorJaDeclarado(TPCompiladores.getLinhaPrograma(), tokenID.getLexema());
      }

      tokenID.getSimbolo().setTipoClasse(ClasseEnum.VARIAVEL);
      tokenID.getSimbolo().setTipoDados(tipoVariavel);

      if(tokenLido.getTipoToken() == AlfabetoEnum.IGUAL){
   
         CASATOKEN(AlfabetoEnum.IGUAL);

         if (tokenLido.getTipoToken() == AlfabetoEnum.MENOS) {
            CASATOKEN(AlfabetoEnum.MENOS);
            negacao = true;
         }

         Token tokenConstante = tokenLido;
         CASATOKEN(AlfabetoEnum.VALOR);

         if(negacao && tokenConstante.getTipoConstante() != TipoEnum.INTEGER && 
            tokenConstante.getTipoConstante() != TipoEnum.REAL){
            throw new ErroTiposIncompativeis(TPCompiladores.getLinhaPrograma());
         }

         tokenID.getSimbolo().setTamanho(tokenConstante.getTamanhoConstante());
         
         if(!(tipoVariavel == TipoEnum.REAL && tokenConstante.getTipoConstante()  == TipoEnum.INTEGER)){
            if(tipoVariavel != tokenConstante.getTipoConstante()){
               throw new ErroTiposIncompativeis(TPCompiladores.getLinhaPrograma());
            }
         }  

         while(tokenLido.getTipoToken() == AlfabetoEnum.VIRGULA) {
            
            negacao = false;
            
            CASATOKEN(AlfabetoEnum.VIRGULA);

            tokenID = tokenLido;
            CASATOKEN(AlfabetoEnum.IDENTIFICADOR);

            if(tokenID.getSimbolo().getTipoClasse()!= null){
               throw new ErroIdentificadorJaDeclarado(TPCompiladores.getLinhaPrograma(), tokenID.getLexema());
            }

            //NOVO ID, ATRIBUICAO DE CLASSE E TIPOS
            tokenID.getSimbolo().setTipoClasse(ClasseEnum.VARIAVEL);
            tokenID.getSimbolo().setTipoDados(tipoVariavel);

            if(tokenLido.getTipoToken() == AlfabetoEnum.IGUAL){
               CASATOKEN(AlfabetoEnum.IGUAL);
         
               if(tokenLido.getTipoToken() == AlfabetoEnum.MENOS){
                  CASATOKEN(AlfabetoEnum.MENOS);
                  negacao = true;
               }

               Token tokenConstante2 = tokenLido;
               CASATOKEN(AlfabetoEnum.VALOR);

               if(negacao && tokenConstante2.getTipoConstante() != TipoEnum.INTEGER &&
                  tokenConstante2.getTipoConstante() != TipoEnum.REAL){
                  throw new ErroTiposIncompativeis(TPCompiladores.getLinhaPrograma());
               }

               if(!(tipoVariavel == TipoEnum.REAL && tokenConstante2.getTipoConstante() == TipoEnum.INTEGER)){
                  if(tipoVariavel != tokenConstante2.getTipoConstante()){
                     throw new ErroTiposIncompativeis(TPCompiladores.getLinhaPrograma());
                  }
               }
               tokenID.getSimbolo().setTamanho(tokenConstante2.getTamanhoConstante());

            }
         }
      }
      CASATOKEN(AlfabetoEnum.PONTO_VIRGULA);
   }

   //DEC_C -> CONST ID = [ [-] VALOR];
   //DEC_C -> CONST ID (5) = [ [-] VALOR];
   public void DEC_C() throws ErroPersonalizado, IOException{
      boolean negacao = false;
      Token constante;
      Token tokenID;

      CASATOKEN(AlfabetoEnum.CONST);

      tokenID = tokenLido;

      CASATOKEN(AlfabetoEnum.IDENTIFICADOR);

      if(tokenID.getSimbolo().getTipoClasse() != null){
         throw new ErroIdentificadorJaDeclarado(TPCompiladores.getLinhaPrograma(), tokenLido.getLexema());
      }

      CASATOKEN(AlfabetoEnum.IGUAL);

      if(tokenLido.getTipoToken() == AlfabetoEnum.MENOS){
         negacao = true;
         CASATOKEN(AlfabetoEnum.MENOS); 
      }

      constante = tokenLido;
      CASATOKEN(AlfabetoEnum.VALOR);

      if(negacao && constante.getTipoConstante() != TipoEnum.INTEGER && constante.getTipoConstante() != TipoEnum.REAL){
         throw new ErroTiposIncompativeis(TPCompiladores.getLinhaPrograma());
      }

      tokenID.getSimbolo().setTipoClasse(ClasseEnum.CONSTANTE);
      tokenID.getSimbolo().setTipoDados(constante.getTipoConstante());
      CASATOKEN(AlfabetoEnum.PONTO_VIRGULA);
   }

   //CMD -> CMD_A | CMD_R | CMD_T | CMD_L | CMD_E | ;
   public void CMD() throws ErroPersonalizado, IOException{
      if(verificaCMDA()){ 
         CMD_A();
      } else if(verificaCMDR()){
         CMD_R();
      }else if(verificaCMDT()){
         CMD_T();
      }else if(verificaCMDL()){
         CMD_L();
      }else if(verificaCMDE()){
         CMD_E();
      } else {
         CASATOKEN(AlfabetoEnum.EOF);
      }
   }

   //CMD_A -> ID ['[' EXP']'] = EXP;
   public void CMD_A() throws ErroPersonalizado, IOException{
      CASATOKEN(AlfabetoEnum.IDENTIFICADOR);
      if(tokenLido.getTipoToken() == AlfabetoEnum.ABRE_COLCHETE){
         CASATOKEN(AlfabetoEnum.ABRE_COLCHETE);
         EXP();
         CASATOKEN(AlfabetoEnum.FECHA_COLCHETE);
      }
      CASATOKEN(AlfabetoEnum.IGUAL);
      EXP();
      CASATOKEN(AlfabetoEnum.PONTO_VIRGULA);
   }

   //CMD_R -> WHILE EXP (CMD | 'BEGIN' {CMD} 'END')
   public void CMD_R() throws ErroPersonalizado, IOException{
      CASATOKEN(AlfabetoEnum.WHILE);
      EXP();
      if(tokenLido.getTipoToken() == AlfabetoEnum.BEGIN){
         CASATOKEN(AlfabetoEnum.BEGIN);
         while(verificaCMD1()){
            CMD(); 
         }
         CASATOKEN(AlfabetoEnum.END);
      }else{
         CMD();
      }
   }

   private boolean verificaCMD1() {
      return (tokenLido.getTipoToken() == AlfabetoEnum.IDENTIFICADOR || tokenLido.getTipoToken() == AlfabetoEnum.WHILE
              || tokenLido.getTipoToken() == AlfabetoEnum.IF || tokenLido.getTipoToken() == AlfabetoEnum.READLN
              || tokenLido.getTipoToken() == AlfabetoEnum.WRITE || tokenLido.getTipoToken() == AlfabetoEnum.WRITELN
              || tokenLido.getTipoToken() == AlfabetoEnum.PONTO_VIRGULA);
   }

   //CMD_T -> IF EXP (CMD | 'BEGIN' {CMD} 'END') [else (CMD | 'BEGIN' {CMD} 'END')]
   public void CMD_T() throws ErroPersonalizado, IOException{
      CASATOKEN(AlfabetoEnum.IF);
      EXP();
      if(tokenLido.getTipoToken() == AlfabetoEnum.BEGIN){
         CASATOKEN(AlfabetoEnum.BEGIN);
         CMD();
         while(tokenLido.getTipoToken() != AlfabetoEnum.END){
            CMD();
         }
         CASATOKEN(AlfabetoEnum.END);
      }else{
         CMD();
      }
   
      if(tokenLido.getTipoToken() == AlfabetoEnum.ELSE){
         CASATOKEN(AlfabetoEnum.ELSE);
         if(tokenLido.getTipoToken() == AlfabetoEnum.BEGIN){
            CASATOKEN(AlfabetoEnum.BEGIN);
            CMD();
            while(tokenLido.getTipoToken() != AlfabetoEnum.END){
               CMD();
            }
            CASATOKEN(AlfabetoEnum.END);
         }else{
            CMD();
         }
      }else{
         CMD();
      }
   }

   //CMD_L -> READLN '('ID')'';
   public void CMD_L() throws ErroPersonalizado, IOException{
      CASATOKEN(AlfabetoEnum.READLN);
      CASATOKEN(AlfabetoEnum.ABRE_PARENTESES);
      CASATOKEN(AlfabetoEnum.IDENTIFICADOR);
      CASATOKEN(AlfabetoEnum.FECHA_PARENTESES);
      CASATOKEN(AlfabetoEnum.PONTO_VIRGULA);
   }

   //CMD_E -> (WRITE | WRITELN) '(' EXP {, EXP } ')';
   public void CMD_E() throws ErroPersonalizado, IOException{
      if(tokenLido.getTipoToken() == AlfabetoEnum.WRITE){
         CASATOKEN(AlfabetoEnum.WRITE);
      } else {
         CASATOKEN(AlfabetoEnum.WRITELN);
      }
      CASATOKEN(AlfabetoEnum.ABRE_PARENTESES);
      EXP();
      if(tokenLido.getTipoToken() == AlfabetoEnum.VIRGULA){
         while(tokenLido.getTipoToken() == AlfabetoEnum.VIRGULA){
            CASATOKEN(AlfabetoEnum.VIRGULA);
            EXP();
         }
      }
      CASATOKEN(AlfabetoEnum.FECHA_PARENTESES);
      CASATOKEN(AlfabetoEnum.PONTO_VIRGULA);
   }  

   //EXP -> SEXP [( == | != | < | > | <= | >= ) SEXP]
   public void EXP() throws ErroPersonalizado, IOException{
      SEXP();
      if(verificaOPR()){
         if(tokenLido.getTipoToken() == AlfabetoEnum.IGUAL_IGUAL){
            CASATOKEN(AlfabetoEnum.IGUAL_IGUAL);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.MENOR){
            CASATOKEN(AlfabetoEnum.MENOR);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.MAIOR){
            CASATOKEN(AlfabetoEnum.MAIOR);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.MENOR_IGUAL){
            CASATOKEN(AlfabetoEnum.MENOR_IGUAL);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.MAIOR_IGUAL){
            CASATOKEN(AlfabetoEnum.MAIOR_IGUAL);
         }
         SEXP();
      }
   }

   //SEXP -> [+ | -] T {(+ | - | OR) T}
   public void SEXP() throws ErroPersonalizado, IOException{
      if(tokenLido.getTipoToken() == AlfabetoEnum.MENOS){
         CASATOKEN(AlfabetoEnum.MENOS);
      }else if(tokenLido.getTipoToken() == AlfabetoEnum.MAIS){
         CASATOKEN(AlfabetoEnum.MAIS);
      }
      T();
   
      while(tokenLido.getTipoToken() == AlfabetoEnum.MENOS || tokenLido.getTipoToken() == AlfabetoEnum.MAIS || tokenLido.getTipoToken() == AlfabetoEnum.OR){
         if(tokenLido.getTipoToken() == AlfabetoEnum.MENOS){
            CASATOKEN(AlfabetoEnum.MENOS);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.MAIS){
            CASATOKEN(AlfabetoEnum.MAIS);
         }else{
            CASATOKEN(AlfabetoEnum.OR);
         }
         T();
      }
   }

   //T -> F {(* | AND | / | // | %) F}
   public void T() throws ErroPersonalizado, IOException{
      F();
      while(tokenLido.getTipoToken() == AlfabetoEnum.MULTIPLICACAO || tokenLido.getTipoToken() == AlfabetoEnum.AND || tokenLido.getTipoToken() == AlfabetoEnum.DIVISAO
      || tokenLido.getTipoToken() == AlfabetoEnum.BARRA_BARRA || tokenLido.getTipoToken() == AlfabetoEnum.PORCENTAGEM){
         if(tokenLido.getTipoToken() == AlfabetoEnum.MULTIPLICACAO){
            CASATOKEN(AlfabetoEnum.MULTIPLICACAO);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.AND){
            CASATOKEN(AlfabetoEnum.AND);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.DIVISAO){
            CASATOKEN(AlfabetoEnum.DIVISAO);
         }else if(tokenLido.getTipoToken() == AlfabetoEnum.BARRA_BARRA){
            CASATOKEN(AlfabetoEnum.BARRA_BARRA);
         }else{
            CASATOKEN(AlfabetoEnum.PORCENTAGEM);
         }
         F();
      }
   }

   //F -> NOT F | INTEGER '(' EXP ')' | REAL '(' EXP ')' | '(' EXP ')' | ID ['(' EXP ')'] | VALOR
   public void F() throws ErroPersonalizado, IOException{
      if(tokenLido.getTipoToken() == AlfabetoEnum.NOT){
         CASATOKEN(AlfabetoEnum.NOT);
         F();
      }else if(tokenLido.getTipoToken() == AlfabetoEnum.INTEGER){
         CASATOKEN(AlfabetoEnum.INTEGER);
         CASATOKEN(AlfabetoEnum.ABRE_PARENTESES);
         EXP();
         CASATOKEN(AlfabetoEnum.FECHA_PARENTESES);
      }else if(tokenLido.getTipoToken() == AlfabetoEnum.REAL){
         CASATOKEN(AlfabetoEnum.REAL);
         CASATOKEN(AlfabetoEnum.ABRE_PARENTESES);
         EXP();
         CASATOKEN(AlfabetoEnum.FECHA_PARENTESES);
      }else if(tokenLido.getTipoToken() == AlfabetoEnum.IDENTIFICADOR){
         CASATOKEN(AlfabetoEnum.IDENTIFICADOR);
         if(tokenLido.getTipoToken() == AlfabetoEnum.ABRE_COLCHETE){
            CASATOKEN(AlfabetoEnum.ABRE_COLCHETE);
            EXP();
            CASATOKEN(AlfabetoEnum.FECHA_COLCHETE);
         }
      }else if(tokenLido.getTipoToken() == AlfabetoEnum.VALOR){
         CASATOKEN(AlfabetoEnum.VALOR);
      } else {
         CASATOKEN(AlfabetoEnum.ABRE_PARENTESES);
         EXP();
         CASATOKEN(AlfabetoEnum.FECHA_PARENTESES);
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

class ErroIdentificadorJaDeclarado extends ErroPersonalizado {
   public ErroIdentificadorJaDeclarado(int linhaPrograma, String lexema) {
      super(linhaPrograma + "\nidentificador ja declarado [" + lexema + "].");
   }
}

class ErroClasseIdentificadorIncompativel extends ErroPersonalizado {
   public ErroClasseIdentificadorIncompativel(int linhaPrograma, String lexema) {
      super(linhaPrograma + "\nclasse de identificador incompativel [" + lexema + "].");
   }
}

class ErroTiposIncompativeis extends ErroPersonalizado {
   public ErroTiposIncompativeis(int linhaPrograma) {
      super(linhaPrograma + "\ntipos incompativeis.");
   }
}

class ErroIdentificadorNaoDeclarado extends ErroPersonalizado {
   public ErroIdentificadorNaoDeclarado(int linhaPrograma, String lexema) {
      super(linhaPrograma + "\nidentificador nao declarado [" + lexema + "].");
   }
}


/*
 * ENUM REPRESENTA O ALFABETO DA LINGUAGEM COM A SUA NUMERACAO DE TOKENS
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

/*
 * CLASSE PARA CRIACAO DOS SIMBOLOS
 */
class Simbolo {

   private AlfabetoEnum token;
   private ClasseEnum tipoClasse;
   private TipoEnum tipoDados;
   private int tamanho;
   private long endereco;

   Simbolo(AlfabetoEnum token) {
      this.token = token;
      this.tipoClasse = null;
      this.tipoDados = TipoEnum.NULL;
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

   public ClasseEnum getTipoClasse() {
      return this.tipoClasse;
   }

   public void setTipoClasse(ClasseEnum tipoClasse) {
      this.tipoClasse = tipoClasse;
   }

   public TipoEnum getTipoDados() {
      return this.tipoDados;
   }

   public void setTipoDados(TipoEnum tipo) {
      this.tipoDados = tipo;
   }

   public void setEndereco(long endereco) {
      this.endereco = endereco;
   }

   public long getEndereco() {
      return this.endereco;
   }

}

/*
 *  CLASSE TABELA DE SIMBOLOS
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

/*
 *  ENUM PARA OS TIPOS
 */
enum TipoEnum {
   INTEGER, REAL, CHAR, STRING, BOOLEAN, NULL;
}

/*
 * ENUM PARA AS CLASSES
 */
enum ClasseEnum {
   VARIAVEL, CONSTANTE;
}

/*
 *  CLASSE PARA A ALTERACAO DE VARIAVIES PELA REFERENCIA
 */
class Referencia<T> {
   public T referencia;

   public Referencia(T referencia) {
       this.referencia = referencia;
   }
}

/*
 *  CLASSE DE TOKENS
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

   public int getTamanhoConstante() {
      switch (tipoConstante) {
      case BOOLEAN:
      case CHAR:
         return 1;
      case INTEGER:
      case REAL:
         return 4;
      case STRING:
         return lexema.length() - 1;
      default:
         return 0;
      }
  }

}

/*
 *  ESTADOS POSSIVEIS NO ANALISADOR LEXICO
 */
enum Estados {
   INICIO, FIM, MENOR, MAIOR, IDENTIFICADOR, INICIO_COMENTARIO, FIM_COMENTARIO, COMENTARIO,
   PRIMEIRO_DECIMAL, DECIMAL, DIGITO, HEXADECIMAL1, DIVISAO,
   HEXADECIMAL2, CARACTER1, CARACTER2, STRING, IGUAL, EXCLAMACAO, HEXA_ID; 
} 

/*
 *   CLASSE DO ANALISADOR LEXICO
 */
class AnalisadorLexico {
   public static Token obterProximoToken() throws ErroPersonalizado, IOException {
      
      int byteLido;
      char caracterAnalisado;
      Estados estado = Estados.INICIO;
      Token token = new Token();
      boolean devolve;
      String lexema = "";
   
      while(estado != Estados.FIM){
         
         devolve = false;
         byteLido = TPCompiladores.leitura.read();

         
         if(byteLido == -1 && estado != Estados.INICIO) {
            throw new ErroFimDeArquivoNaoEsperado(TPCompiladores.getLinhaPrograma());
         }
         
         caracterAnalisado = (char) byteLido;
      
         if(!charValido(caracterAnalisado)) {
            throw new ErroCaractereInvalido(TPCompiladores.getLinhaPrograma());
         }
      
         /*
          *  IMPLEMENTACAO DO AUTOMATO
          */
         switch(estado) {
            case INICIO:
               lexema = "";
               switch(caracterAnalisado){
                  
                  /*
                   *   UNICO CARACTER PARA SER TOKEN
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
                     token.setTipoToken(AlfabetoEnum.PORCENTAGEM);
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
                   *  DOIS OU MAIS CARACTERES PARA SEREM TOKENS
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
                  case '\r':
                  case '\n':
                  case '\t':
                     break;
                  
                  default:
                     if (inicioIdentificador(caracterAnalisado)) {
                        estado = Estados.HEXA_ID;
                     } 
                     else if (caracterAnalisado >= '0' && caracterAnalisado <= '9') {
                        estado = Estados.DIGITO;
                     } else {
                        throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema + caracterAnalisado);
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
               if (caracterAnalisado == '*') {
                  estado = Estados.COMENTARIO;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
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
                  /* CONTINUACAO DA LEITURA */
                     break;
                  default:
                     estado = Estados.COMENTARIO;
                     break;
               }
               break;
            
            case DIGITO:
               if (caracterAnalisado == '.') {
                  estado = Estados.PRIMEIRO_DECIMAL;
               } else if (!digito(caracterAnalisado) && hexadecimal(caracterAnalisado)) {
                  estado = Estados.HEXADECIMAL1; 
               } else if (!digito(caracterAnalisado)) {
                  devolve = true;
                  token.setTipoToken(AlfabetoEnum.VALOR);
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
         
            case HEXA_ID:
               if(letraID2(caracterAnalisado)){
                  estado = Estados.IDENTIFICADOR;
               } else if(hexadecimal(caracterAnalisado)){
                  estado = Estados.HEXADECIMAL1;
               } else if (fimIdentificador(caracterAnalisado)) {
                  // o caracter lido deve ser devolvido
                  devolve = true;
                  token.setTipoToken(AlfabetoEnum.IDENTIFICADOR);
                  if (lexema.length() > 32) {
                     throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                  }
                  estado = Estados.FIM;
               }
               break;
         
            case HEXADECIMAL1:
               if (hexadecimalH(caracterAnalisado)) {
                  estado = Estados.HEXADECIMAL2;
               } else if(letra(caracterAnalisado)) {
                  estado = Estados.IDENTIFICADOR;
               }
               else if(digito(caracterAnalisado)){
                  estado = Estados.DIGITO;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;
            
            case HEXADECIMAL2:
               if(digito(caracterAnalisado) || charValido(caracterAnalisado)){
                  estado = Estados.IDENTIFICADOR;
               } else{
                  devolve = true;
                  token.setTipoToken(AlfabetoEnum.VALOR);
                  token.setLexema(lexema);
                  token.setTipoConstante(TipoEnum.CHAR);
                  estado = Estados.FIM;
               
               }
               break;
         
            case IDENTIFICADOR:
               if(hexadecimal(caracterAnalisado)){
                  estado = Estados.HEXADECIMAL1;
               }
               else if (fimIdentificador(caracterAnalisado)) {
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
                  token.setTipoToken(AlfabetoEnum.VALOR);
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
                  //System.out.println("Lexema: "+ caracterAnalisado);
                  estado = Estados.CARACTER2;
               } else {
                  throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
               }
               break;
         
            case CARACTER2:
               if (caracterAnalisado == '\'') {
                  token.setTipoToken(AlfabetoEnum.VALOR);
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
                  token.setTipoToken(AlfabetoEnum.VALOR);
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

   private static boolean hexadecimalH (char c) {
      return (c == 'h');
   }
   
   private static boolean charImprimivel(char c) {
            
      return (c == '\t' || (c >= ' ' && c <= '"') || (c >= 'A' && c <= ']') || c == '/' || c == '_'
         || (c >= 'a' && c <= '}') || (c >= '%' && c <= '?')) || (c == '@');
   }

   private static boolean charValido(char c) {
      return charImprimivel(c) || c == '\n' || c == '\r' || c == '\t' || c == (char) -1;
   }

   private static boolean letra(char c) {
      return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
   }

   private static boolean letraID2(char c) {
      return ((c >= 'g' && c <= 'z') || (c >= 'G' && c <= 'Z'));
   }

   private static boolean digito(char c) {
      return (c >= '0' && c <= '9');
   }

   private static boolean fimIdentificador(char c) {
      return !(letra(c) || digito(c) || (c == '.') || (c == '_'));
   }

   private static boolean inicioIdentificador(char c) {
      return (letraID(c) || c == '_');
   }

   private static boolean letraID(char c) {
      return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
   }

   private static boolean stringValida(char c) {
      return (c == '\n' || c == '\t' ||c == '\r' || (c >= ' ' && c < '"') || c == '_' ||  (c >= '+' && c <='/') || (c >= ':' && c<=';') || (c >= '%' && c <= ')') 
         || (c >= '>' && c <= ']') || letra(c) || digito(c) || c == '{' || (c == '<' || c == '=') );
   }

}