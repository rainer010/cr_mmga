package py.gapdi.sinniveles;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import py.gapdi.generico.GenGenerico;

/**
 * Created by rainer on 11/02/2016.
 */
public class GenSN extends GenGenerico {
    private Mat filtro;
    private int filas;
    private int columnas;

    public GenSN(boolean[] individuo, int bitsValores, int filas, int columnas) {
        super(individuo, bitsValores);
        this.filas = filas / 2 + 1;
        this.columnas = columnas / 2 + 1;
    }

    @Override
    protected void reset() {
        if(filtro!=null){
            filtro.release();
        }

//
        int dimf=filas * 2 - 1;
        int dimc=columnas * 2 - 1;
        filtro = Mat.zeros(dimf, dimc, CvType.CV_8UC1);
    }

    @Override
    public GenGenerico clone() {
        return new GenSN(this.getIndividuo().clone(), getBitsXValor(), filas * 2 - 1,columnas * 2 - 1);
    }

    @Override
    public void asignarValorEnPosicion(int valor, long posicion) {

        int c = filtro.cols() / 2 + 1;
        int fil = (int) (posicion / c);
        int col = (int) (posicion % c);
        filtro.put(fil, col, valor);
        filtro.put(filtro.rows() - 1 - fil, col, valor);
        filtro.put(fil, filtro.cols() - 1 - col, valor);
        filtro.put(filtro.rows() - 1 - fil, filtro.cols() - 1 - col, valor);
    }

    public Mat getFiltro() {
        return filtro;
    }

    @Override
    public void decode() {
        super.decode();
        filtro.put(filas-1,columnas-1,1);
    }
}
