package py.gapdi.run;


import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.uma.jmetal.algorithm.singleobjective.geneticalgorithm.GenerationalGeneticAlgorithm;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import py.gapdi.OpenCVUtil;
import py.gapdi.generico.Cruzamiento;
import py.gapdi.generico.Mutacion;
import py.gapdi.generico.SolucionGeneral;
import py.gapdi.helper.HOpencv;
import py.gapdi.sinniveles.GenSN;
import py.gapdi.sinniveles.ProblemaSN;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

/**
 * Created by rainer on 15/10/2015.
 */
public class AppSinNiveles {
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
        System.out.print("COMENZAR EJECUCION SIN NIVELES\n");
        System.out.print("PARAMETROS:\n");
        System.out.print("Imagenes de entradas:" + pathDb + "\n");
        System.out.print("Directorio de salida:" + pathOut + "\n");


        for (int i = 1; i <=1; i++) {
            excecuteInDir(pathDb, pathOut + "/sin_niveles/" + i, 800 * 100, 100, 21, 21);
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
            System.out.print(file.getName()+"\n");
            Mat imagen = Imgcodecs.imread(source + file.getName(), Imgcodecs.IMREAD_GRAYSCALE);
//                        try {
//                            System.out.print(file.getName() + "\n");
            AppSinNiveles.run(imagen, out,
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

        ProblemaSN pro = new ProblemaSN(imp, fila,columna);
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
                new Cruzamiento(), new Mutacion((pro.getColumna()/2+1)*(pro.getFila()/2+1)-1),
                new org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection<SolucionGeneral>(), new SequentialSolutionListEvaluator()) {
            int iter=0;
            @Override
            public void updateProgress() {
                super.updateProgress();
                String rS=""+ this.getResult().getObjective(0)+"\t"+this.getResult().getEvaluacion()[1]+
                        "\t"+this.getResult().getEvaluacion()[2]+ "\n";
                rS=rS.replace(".",",");
                pw.print(rS);
            }

        };

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
                .execute();

        pw.close();
        algorithm.getResult().getSolucion().decode();
        GenSN g= (GenSN) algorithm.getResult().getSolucion();
        Imgcodecs.imwrite(out + "/" + filename, HOpencv.newMetodo_Binario_OCV(imp, g.getFiltro()));
        g.getFiltro().convertTo(g.getFiltro(), 0, 255.0);
        Imgcodecs.imwrite(out + "/" + filename+"F.png",g.getFiltro());
        imp.release();

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
