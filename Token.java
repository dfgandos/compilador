public class Token {

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