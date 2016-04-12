package py.gapdi.sinniveles;

import org.opencv.core.Mat;
import org.uma.jmetal.problem.Problem;
import py.gapdi.OpenCVUtil;
import py.gapdi.generico.SolucionGeneral;
import py.gapdi.helper.HOpencv;

import java.util.Random;

/**
 * Created by rainer on 15/10/2015.
 */
public class ProblemaSN implements Problem<SolucionGeneral> {

    public Mat imageMat;
    private int fila;
    private int columna;

    public ProblemaSN(Mat imagen, int fila, int columna) {
        imageMat = imagen;
        this.fila = fila;
        this.columna = columna;
    }

    @Override
    public int getNumberOfVariables() {
        return 1;
    }

    @Override
    public int getNumberOfObjectives() {
        return 1;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return "CONTRASTE";
    }

    @Override
    public void evaluate(SolucionGeneral solucionEE) {
        GenSN g = (GenSN) solucionEE.getSolucion();
        g.decode();
        Mat act =null;
        try {
            act = HOpencv.newMetodo_Binario_OCV(imageMat, g.getFiltro());
        }catch (Exception e){
            System.out.print("ERROR "+e+"\n");
        }
        double[] e = OpenCVUtil.calcularMetrica(imageMat, act);
        act.release();
        solucionEE.setEvaluacion(e);
        solucionEE.setObjective(0, e[0]);

    }


    static Random randomGenerator = new Random();

    private int generado0=1;
    @Override
    public SolucionGeneral createSolution() {
        SolucionGeneral s = new SolucionGeneral();
        boolean[] ind = new boolean[((fila/2+1) * (columna/2+1) -1)*1];

//        if(generado0==1) {
//            for (int i = 0; i < ind.length; i++) {
//                ind[i] = true;
//            }
//            generado0++;
//        }else if(generado0==2){
//            for (int i = 0; i < ind.length; i++) {
//                ind[i] = false;
//            }
//            generado0++;
//        }else{
            for (int i = 0; i < ind.length; i++) {
                ind[i] = randomGenerator.nextBoolean();
            }
//        }
        s.setSolucion(new GenSN(ind, 1, fila, columna));
        return s;
    }

    public int getFila() {
        return fila;
    }

    public int getColumna() {
        return columna;
    }
}