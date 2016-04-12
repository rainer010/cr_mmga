package ant.problema;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import py.gapdi.OpenCVUtil;

/**
 * Created by rainer on 17/10/2015.
 * <p/>
 * Representa un gen de un individuo
 * <p/>
 * Contiene el elemento estructurante y la cantidad de iteraciones que se debe correr el algoritmo
 */
public class Gen {

    private boolean[] individuo;
    private Mat filtro;
    private int veces=0;

    private int filas;
    private int columnas;
    private int bitsXValor;

    public Gen(boolean[] individuo, int filas, int columnas, int bitsValores){
        this.individuo = individuo;
        this.filas = filas;
        this.columnas = columnas;
        this.bitsXValor = bitsValores;
    }

    public void decode() {
        filtro = Mat.zeros(filas * 2 - 1, columnas * 2 - 1, CvType.CV_8UC1);
//        filtro = Mat.zeros(filas , columnas , CvType.CV_8UC1);
        String s = "";
        int esElemento = 0;
        int elementoActual = 0;
        for (int i = 0; i < individuo.length; i++) {
                s = s + (individuo[i] ? "1" : "0");
                esElemento++;
                if (esElemento == bitsXValor) {
                    asiganarValor(filtro, s, elementoActual);
                    elementoActual++;
                    esElemento = 0;
                    s = "";
            }
        }
//        System.out.print("F "+filtro+" "+individuo.length+"\n");
//        filtro=OpenCVUtil.reducirMat(filtro);
    }

    public static int stringBinaryToNumber(String numeroBinario) {
//        System.out.print(numeroBinario+" \n");
        int n = 0;
        for (int j = 0; j < numeroBinario.length(); j++) {
            n *= 2;
            n += numeroBinario.charAt(j) == '0' ? 0 : 1;
        }
        return n;
    }

    private void asiganarValor(Mat mat, String valor, long posicion) {

//        if(posicion==0){
//            System.out.print("VAL "+valor+"\n");
//        }

        int columnas = mat.cols() / 2 + 1;
        int filas = mat.rows() / 2 + 1;

        int fila = (int) (posicion / columnas);
        int col = (int) (posicion % columnas);

        int valorR = stringBinaryToNumber(valor);
//        if(valorR==7){
//            valorR= 0;
//        }

        mat.put(fila, col, valorR);
        mat.put(mat.rows() - 1 - fila, col, valorR);
        mat.put(fila, mat.cols() - 1 - col, valorR);
        mat.put(mat.rows() - 1 - fila, mat.cols() - 1 - col, valorR);
    }

    public boolean[] getIndividuo() {
        return individuo;
    }

    public void setIndividuo(boolean[] individuo) {
        this.individuo = individuo;
    }

    public Mat getFiltro() {
        return filtro;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }

    public int getBitsXValor() {
        return bitsXValor;
    }

    public int getVeces() {
        return veces;
    }

    public void setVeces(int veces) {
        this.veces = veces;
    }
}