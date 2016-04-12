package py.gapdi.chainrule;

import org.opencv.core.Mat;
import org.uma.jmetal.problem.Problem;
import py.gapdi.OpenCVUtil;
import py.gapdi.generico.SolucionGeneral;
import py.gapdi.helper.HOpencv;

import java.util.List;
import java.util.Random;

/**
 * Created by rainer on 15/10/2015.
 */
public class ProblemaCR implements Problem<SolucionGeneral> {

    public Mat imageMat;
    private int numeroElementos;
    private List<Mat> base;

    public ProblemaCR(Mat imagen, int tamanhoMaximoDimension, List<Mat> base) {
        imageMat = imagen;
        numeroElementos = (tamanhoMaximoDimension - 3) / 2 + 1;
        System.out.print("Nro elementos " + numeroElementos + "\n");
        this.base = base;
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
        GenCR g = (GenCR) solucionEE.getSolucion();
        g.decode();
        Mat act = HOpencv.newMetodo_Binario_OCV(imageMat, g.getFiltro());
        double[] e = OpenCVUtil.calcularMetrica(imageMat, act);
        act.release();
        solucionEE.setEvaluacion(e);
        solucionEE.setObjective(0, e[0]);
    }


    static Random randomGenerator = new Random();
    private int generado0 = 1;

    @Override
    public SolucionGeneral createSolution() {
        SolucionGeneral s = new SolucionGeneral();
        boolean[] ind = new boolean[numeroElementos * 8];
//        for (int i = 0; i < ind.length; i++) {
//            ind[i] = randomGenerator.nextBoolean();
//        }
//

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
        s.setSolucion(new GenCR(ind, 8, base));
        return s;
    }

    public int getNumeroElementos() {
        return numeroElementos;
    }
}