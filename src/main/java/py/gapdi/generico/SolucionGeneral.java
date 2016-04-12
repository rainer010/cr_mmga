package py.gapdi.generico;

import org.uma.jmetal.solution.Solution;
import py.gapdi.chainrule.GenCR;

/**
 * Created by rainer on 17/10/2015.
 */
public class SolucionGeneral implements Solution<GenGenerico> {

    private GenGenerico solucion;
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
    public GenGenerico getVariableValue(int i) {
        return solucion;
    }


    @Override
    public void setVariableValue(int i, GenGenerico genGenerico) {
        solucion = genGenerico;
    }

    @Override
    public String getVariableValueString(int i) {
        return "";
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
    public Solution<GenGenerico> copy() {
        System.out.print("ALGO");
        return null;
    }

    @Override
    public void setAttribute(Object o, Object o1) {

    }

    @Override
    public Object getAttribute(Object o) {
        return 1.0;
    }


    protected SolucionGeneral clone() {
        SolucionGeneral s = new SolucionGeneral();
        s.setObjective(0, this.fitness);
        s.setSolucion(this.solucion.clone());
        return s;
    }


    public GenGenerico getSolucion() {
        return solucion;
    }

    public void setSolucion(GenGenerico solucion) {
        this.solucion = solucion;
    }

    public double[] getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(double[] evaluacion) {
        this.evaluacion = evaluacion;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }
}