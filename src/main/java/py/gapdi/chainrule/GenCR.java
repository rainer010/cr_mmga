package py.gapdi.chainrule;

import org.opencv.core.Mat;
import py.gapdi.generico.GenGenerico;

import java.util.ArrayList;
import java.util.List;


public class GenCR extends GenGenerico {
    private List<Mat> base;
    private List<Mat> filtro;

    public GenCR(boolean[] individuo, int bitsValores, List<Mat> base) {
        super(individuo, bitsValores);
        this.base = base;
    }

    @Override
    protected void reset() {
        filtro = new ArrayList<>();
    }

    @Override
    public GenGenerico clone() {
        GenCR g = new GenCR(getIndividuo().clone(), getBitsXValor(), base);
        return g;
    }

    @Override
    public void asignarValorEnPosicion(int valor, long posicion) {
        filtro.add(base.get(valor));
    }

    public List<Mat> getFiltro() {
        return filtro;
    }

    public void setFiltro(List<Mat> filtro) {
        this.filtro = filtro;
    }
}