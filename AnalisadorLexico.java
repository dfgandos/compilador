import java.io.IOException;

public class AnalisadorLexico {
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
                    if(letraID2(caracterAnalisado)|| digito(caracterAnalisado)){
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
                    } else if (letra(caracterAnalisado)) {
                        estado = Estados.IDENTIFICADOR;
                    } else if (digito(caracterAnalisado)) {
                        estado = Estados.IDENTIFICADOR;
                    } else if (digito(caracterAnalisado)) {
                        estado = Estados.DIGITO;
                    } else if (lexema.contains("TRUE") || lexema.contains("FALSE")) {
                        devolve = true;
                        token.setTipoToken(AlfabetoEnum.VALOR);
                        token.setLexema(lexema);
                        token.setTipoConstante(TipoEnum.BOOLEAN);
                        estado = Estados.FIM;
                    } else if (fimIdentificador(caracterAnalisado)) {
                        // o caracter lido deve ser devolvido
                        devolve = true;
                        token.setTipoToken(AlfabetoEnum.IDENTIFICADOR);
                        if (lexema.length() > 32) {
                        throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                        }
                        estado = Estados.FIM;
                    } else {
                        throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                    }
                
                    break;

                case HEXADECIMAL2:
                    if(letra(caracterAnalisado) ||digito(caracterAnalisado) ){
                        estado = Estados.IDENTIFICADOR;
                    } else {
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
                        estado = Estados.CARACTER3;
                    } else {
                        throw new ErroLexemaNaoIdentificado(TPCompiladores.getLinhaPrograma(), lexema);
                    }
                    break;

                case CARACTER3:
                    devolve = true;
                    token.setTipoToken(AlfabetoEnum.VALOR);
                    token.setLexema(lexema);
                    token.setTipoConstante(TipoEnum.CHAR);
                    estado = Estados.FIM;
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