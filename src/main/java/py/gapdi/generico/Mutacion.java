package py.gapdi.generico;

import org.uma.jmetal.operator.MutationOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rainer on 17/10/2015.
 */
public class Mutacion implements MutationOperator<SolucionGeneral> {
    Random randomGenerator = new Random();
    List<Integer> indices;

    public Mutacion(int tamanho){
        indices = new ArrayList<Integer>();
        for (int i = 0; i < tamanho; i++) {
            indices.add(Integer.valueOf(i));
        }
    }

    @Override
    public SolucionGeneral execute(SolucionGeneral solucionEE) {
        int poblacion = solucionEE.getSolucion().getIndividuo().length;
        int prob = (int) (poblacion * 0.025);
        List<Integer> eliminados=new ArrayList<>();
        for (int i = 0; i < prob; i++) {
            int idxCal=randomGenerator.nextInt(indices.size());
            int indice = indices.get(idxCal);
            eliminados.add(indices.get(idxCal));
            indices.remove(idxCal);
            solucionEE.getSolucion().getIndividuo()[indice] = !solucionEE.getSolucion().getIndividuo()[indice];
        }
        indices.addAll(eliminados);
        return solucionEE;
    }
}