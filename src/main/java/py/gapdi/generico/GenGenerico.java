package py.gapdi.generico;

public abstract class GenGenerico {

    private boolean[] individuo;
    private int bitsXValor;

    public GenGenerico(boolean[] individuo, int bitsValores) {
        this.individuo = individuo;
        this.bitsXValor = bitsValores;
    }

    public void decode() {
        this.reset();
        String s = "";
        int esElemento = 0;
        int elementoActual = 0;
        for (int i = 0; i < individuo.length; i++) {
            s = s + (individuo[i] ? "1" : "0");
            esElemento++;
            if (esElemento == bitsXValor) {
                asignarValorEnPosicion(stringBinaryToNumber(s), elementoActual);
                elementoActual++;
                esElemento = 0;
                s = "";
            }
        }
    }

    protected abstract void reset();

    public static int stringBinaryToNumber(String numeroBinario) {
        int n = 0;
        for (int j = 0; j < numeroBinario.length(); j++) {
            n *= 2;
            n += numeroBinario.charAt(j) == '0' ? 0 : 1;
        }
        return n;
    }

    public boolean[] getIndividuo() {
        return individuo;
    }

    public void setIndividuo(boolean[] individuo) {
        this.individuo = individuo;
    }

    public int getBitsXValor() {
        return bitsXValor;
    }

    public void setBitsXValor(int bitsXValor) {
        this.bitsXValor = bitsXValor;
    }

    public abstract GenGenerico clone();

    public abstract void asignarValorEnPosicion(int valor, long posicion);
}