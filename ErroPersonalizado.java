public class ErroPersonalizado extends Exception {
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