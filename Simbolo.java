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