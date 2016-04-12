package ant.problema;

import org.uma.jmetal.solution.Solution;

/**
 * Created by rainer on 17/10/2015.
 */
public class SolucionEE implements Solution<Gen>, Comparable<SolucionEE> {

    private Gen solucion;
    private double[] evaluacion;
    private double fitness;


    @Override
    public void setObjective(int i, double v) {
        this.fitness = v;
    }

    @Override
    public double getObjective(int i) {
        return fitness;
    }

    @Override
    public Gen getVariableValue(int i) {
        return solucion;
    }

    @Override
    public void setVariableValue(int i, Gen gen) {
        this.solucion = gen;
    }

    @Override
    public String getVariableValueString(int i) {
        String s = "";
        solucion.decode();

        s = s + "  ## ";
        for (int fila = 0; fila < solucion.getFiltro().rows(); fila++) {
            for (int col = 0; col < solucion.getFiltro().cols(); col++) {
                s = s + " " + solucion.getFiltro().get(fila, col)[0];
            }
            s = s + ";";
        }
        return s;
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
    public Solution<Gen> copy() {

        System.out.print("COPY \n");
        return this.clone();

    }

    @Override
    public void setAttribute(Object o, Object o1) {

    }

    @Override
    public Object getAttribute(Object o) {
        return 1.0;
    }


    protected SolucionEE clone() {
        SolucionEE s = new SolucionEE();
        s.setObjective(0, this.fitness);
        try {
            s.setSolucion(new Gen(this.solucion.getIndividuo(), solucion.getFilas(), solucion.getColumnas(), solucion.getBitsXValor()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static int ids = 0;

    @Override
    public int compareTo(SolucionEE o) {
        double l = this.getObjective(0) - o.getObjective(0);
        if (l > 0) {
            return 1;
        } else if (l < 0) {
            return -1;
        }

        return 0;
    }

    public double[] getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(double[] evaluacion) {
        this.evaluacion = evaluacion;
    }

    public Gen getSolucion() {
        return solucion;
    }

    public void setSolucion(Gen solucion) {
        this.solucion = solucion;
    }
}