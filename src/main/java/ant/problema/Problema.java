package ant.problema;

import org.opencv.core.Mat;
import org.uma.jmetal.problem.Problem;
import py.gapdi.OpenCVUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rainer on 15/10/2015.
 */
public class Problema implements Problem<SolucionEE> {

    public Mat imageMat;
    int fila;
    int columna;
    public List<SolucionEE> poblacionINI = new ArrayList<SolucionEE>();

    public Problema(Mat imagen, int fila, int columna) {
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
    public void evaluate(SolucionEE solucionEE) {
//        evaluar(imageMat, solucionEE).release();
        Gen g = solucionEE.getSolucion();
        g.decode();
        Mat act = OpenCVUtil.newMetodoMO(imageMat, g.getFiltro(), 1,7);
        double[] e = OpenCVUtil.calcularMetrica(imageMat, act);
        act.release();
        g.getFiltro().release();
        solucionEE.setEvaluacion(e);
        solucionEE.setObjective(0, e[0]);
        solucionEE.getSolucion().setVeces(1);


//        System.out.print("V "+(solucionEE.getSolucion().getIndividuo()[0]?"1":"0")+(solucionEE.getSolucion().getIndividuo()[1]?"1":"0")+(solucionEE.getSolucion().getIndividuo()[2]?"1":"0")+
//                "\n");

    }

    public static Mat evaluar(Mat matriz, SolucionEE s) {
        Gen g = s.getSolucion();
        g.decode();

//        OpenCVUtil.imprimir(g.getFiltro());
        Mat m = null;
        double menor = 99999;
        for (int i = 1; i <= 1; i++) {

//            Mat act = OpenCVUtil.newMetodoMO(matriz, g.getFiltro(), i,7);
            Mat act = OpenCVUtil.newMetodo(matriz, g.getFiltro(), i);
            double[] e = OpenCVUtil.calcularMetrica(matriz, act);
            if (e[0] < menor) {
                menor = e[0];
                m = act;
                s.setEvaluacion(e);
                s.setObjective(0, 1 - e[2]);
                s.getSolucion().setVeces(i);
//                System.out.print(""+menor+"\n");
            } else {
                act.release();
            }
        }
        return m;
    }

    static Random randomGenerator = new Random();

    @Override
    public SolucionEE createSolution() {
        SolucionEE s = new SolucionEE();
        boolean[] ind = new boolean[fila * columna * 3];

        for (int i = 0; i < ind.length; i++) {
            ind[i] = randomGenerator.nextBoolean();
        }
        ind[0] = false;
        ind[1] = false;
        ind[2] = false;
        s.setSolucion(new Gen(ind, fila, columna, 3));
        return s;
    }
}
