package py.gapdi.generico;

import org.uma.jmetal.operator.CrossoverOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by rainer on 17/10/2015.
 */
public class Cruzamiento implements CrossoverOperator<SolucionGeneral> {
    Random randomGenerator = new Random();

    @Override
    public List<SolucionGeneral> execute(List<SolucionGeneral> solucionEEs) {

        SolucionGeneral s1 = solucionEEs.get(0);
        SolucionGeneral s2 = solucionEEs.get(1);

        int punto = randomGenerator.nextInt(s1.getSolucion().getIndividuo().length - 1);
        boolean[] result1 = new boolean[s1.getSolucion().getIndividuo().length];
        boolean[] result2 = new boolean[s1.getSolucion().getIndividuo().length];

        int idx1 = 0;
        int idx2 = 0;

        for (int i = 0; i < result1.length; i++) {
            if (i <= punto) {
                result1[i] = s1.getSolucion().getIndividuo()[i];
                result2[i] = s2.getSolucion().getIndividuo()[i];
            } else {
                result1[i] = s2.getSolucion().getIndividuo()[i];
                result2[i] = s1.getSolucion().getIndividuo()[i];
            }
        }

        SolucionGeneral rsul1 = s1.clone();
        rsul1.getSolucion().setIndividuo(result1);
        SolucionGeneral rsul2 = s2.clone();
        rsul2.getSolucion().setIndividuo(result2);

        List<SolucionGeneral> sols = new ArrayList<>();
        sols.add(rsul1);
        sols.add(rsul2);
        return sols;
    }
}
