package py.gapdi.run;


import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import py.gapdi.OpenCVUtil;
import py.gapdi.chainrule.GenCR;
import py.gapdi.chainrule.ProblemaCR;
import py.gapdi.generico.Cruzamiento;
import py.gapdi.generico.Mutacion;
import py.gapdi.generico.SolucionGeneral;
import py.gapdi.helper.HOpencv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by rainer on 15/10/2015.
 */
public class AppChainRules {
    private static final Object countLock = new Object();
    private static int count = 0;

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.print("Error en la cantidad de parametros \n");
            System.out.print(
                    "<path lib openCV> <path dir image> <path dir output> <opcional max iter><opcional tam poblac>\n");
        }
        String pathLib = args[0];
        String pathDb = args[1];
        String pathOut = args[2];
//        System.loadLibrary(pathLib);
        System.out.print("COMENZAR EJECUCION\n");
        System.out.print("PARAMETROS:\n");
        System.out.print("Imagenes de entradas:" + pathDb + "\n");
        System.out.print("Directorio de salida:" + pathOut + "\n");


        for (int i = 2; i <= 2; i++) {
            excecuteInDir(pathDb, pathOut + "/chain_rules/" + i, 800 * 100, 100, 21, 21);
        }

    }

    static final Syncronizador sync = new Syncronizador();
    static int hilos = 0;

    private static void excecuteInDir(final String source, final String out,
                                      final int numeroMaxIteraciones, final int poblacionTamanho,
                                      final int fila, final int columna) throws IOException {

        System.out.print("Nro de iteraciones:" + numeroMaxIteraciones + "\n");
        System.out.print("Tamano de la poblacion:" + poblacionTamanho + "\n");

        final File[] files = new File(source).listFiles();
        int idice = files.length - 1;

        while (idice >= 0) {
//            synchronized (sync) {
//                hilos = sync.valor().intValue();
//            }
//
//            if (hilos < 1) {
            final File file = files[idice];
            idice--;
//                final Thread thread = new Thread("" + idice + " I") {
//                    public void run() {
            Mat imagen = Imgcodecs.imread(source + file.getName(), Imgcodecs.IMREAD_GRAYSCALE);
//                        try {
//                            System.out.print(file.getName() + "\n");
            AppChainRules.run(imagen, out,
                    numeroMaxIteraciones, poblacionTamanho, fila, columna, file.getName());
//                            synchronized (sync) {
//                                sync.sub();
//                            }
//                            System.out.print("FIN DE EJECUCION " + Thread.currentThread().getName() + " CONTA" + count + "\n");
//                        } catch (IOException e) {
//                            System.out.print(e);
//                            synchronized (sync) {
//                                sync.sub();
//                            }
//                        }
//                    }
//                };
//                synchronized (sync) {
//                    sync.add();
//                }
//                thread.start();

//            }

        }

    }

    private static void run(Mat imp, final String out,
                            int numeroMaxIteraciones,
                            final int tamanhoPoblacion,
                            int fila, int columna, final String filename) throws IOException {

        ArrayList matrizIDs = new ArrayList<Mat>();
        String[] s = {"0", "1"};
        Perm1(s, "", 8, 2, matrizIDs);

        ProblemaCR pro = new ProblemaCR(imp, fila, matrizIDs);

        File directori = new File(out + "/");
        directori.mkdirs();

        final Calendar tiempo_ini = Calendar.getInstance();
        final long tiempoEnMinutos = 2;

        File salidaF = new File(out + "/" + filename + ".txt");
        if (!salidaF.exists()) {
            salidaF.createNewFile();
        }
        final FileWriter fw = new FileWriter(salidaF, true);
        final PrintWriter pw = new PrintWriter(fw);
        pw.print("Fitness\tSSIM\tCONSTRASTE");
        GenerationalGeneticAlgorithm<SolucionGeneral> algorithm = new GenerationalGeneticAlgorithm<SolucionGeneral>(pro,
                numeroMaxIteraciones, tamanhoPoblacion,
                new Cruzamiento(), new Mutacion(pro.getNumeroElementos() * 8),
                new org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection<SolucionGeneral>(), new SequentialSolutionListEvaluator()) {

            @Override
            public void updateProgress() {
                super.updateProgress();

                String rS = "" + this.getResult().getObjective(0) + "\t" + this.getResult().getEvaluacion()[1] +
                        "\t" + this.getResult().getEvaluacion()[2] + "\n";
                rS = rS.replace(".", ",");
                pw.print(rS);

            }

//            @Override
//            protected boolean isStoppingConditionReached() {
//
//                long diffMinutes = (Calendar.getInstance().getTimeInMillis() - tiempo_ini.getTimeInMillis());
//                long actual = tiempoEnMinutos * (60 * 1000);
//                return diffMinutes >= actual;
//            }
        };

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();
        pw.close();
        algorithm.getResult().getSolucion().decode();
        GenCR g = (GenCR) algorithm.getResult().getSolucion();
        g.decode();
        System.out.print("T: "+g.getFiltro().size()+"\n");
        Imgcodecs.imwrite(out + "/" + filename, HOpencv.newMetodo_Binario_OCV(imp, g.getFiltro()));

        Mat f = HOpencv.convertToElements(g.getFiltro());
        f.convertTo(f, 0, 255.0);
        System.out.print(" "+f.rows()+"x"+f.cols()+"\n");
        Imgcodecs.imwrite(out + "/" + filename + "F.png", f);
        imp.release();
    }

    private static Mat convertTo(String s) {
        Mat m = Mat.zeros(3, 3, CvType.CV_8UC1);
        m.put(0, 0, Integer.valueOf(String.valueOf(s.charAt(0))));
        m.put(0, 1, Integer.valueOf(String.valueOf(s.charAt(1))));
        m.put(0, 2, Integer.valueOf(String.valueOf(s.charAt(2))));
        m.put(1, 0, Integer.valueOf(String.valueOf(s.charAt(3))));
        m.put(1, 1, 1);
        m.put(1, 2, Integer.valueOf(String.valueOf(s.charAt(4))));
        m.put(2, 0, Integer.valueOf(String.valueOf(s.charAt(5))));
        m.put(2, 1, Integer.valueOf(String.valueOf(s.charAt(6))));
        m.put(2, 2, Integer.valueOf(String.valueOf(s.charAt(7))));
        return m;
    }

    private static void Perm1(String[] elem, String act, int n, int r, List<Mat> valores) {
        if (n == 0) {
            valores.add(convertTo(act));
        } else {
            for (int i = 0; i < r; i++) {
                Perm1(elem, act + elem[i], n - 1, r, valores);
            }
        }
    }

    static class Syncronizador {
        Integer cantidadHilos = 0;

        public void add() {
            cantidadHilos++;
        }

        public void sub() {


            System.out.print("Hilo liberado \n");
            cantidadHilos--;
        }

        public Integer valor() {
            return cantidadHilos;
        }
    }
}
