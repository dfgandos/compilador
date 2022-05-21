/*
   Professor: Alexei Machado
   Disciplina: Compiladores
   Alunos: Darlan Francisco, Larissa Leite e Ygor Melo
*/

import java.util.*;
import java.io.*;

public class TPCompiladores {

    public static String assembly = "";
    /*
     * VALOR PARA HEXA
     */
    public static long proximoEnderecoLivre = 0x10000;
    public static PushbackReader leitura;
    public static int linhaPrograma = 1;
    public static long proximoTemporarioLivre = 0;
    public static int incrementoRotulo = 0;

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
            BufferedWriter gravarArq = new BufferedWriter(new FileWriter("saida.asm"));

            gravarArq.write(assembly);

            gravarArq.close();

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