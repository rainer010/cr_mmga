package py.gapdi;

import py.gapdi.run.AppChainRules;
import py.gapdi.run.AppSinNiveles;

import java.io.IOException;

/**
 * Created by rainer on 15/10/2015.
 */
public class App {
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
        System.loadLibrary(pathLib);
//        AppSinNiveles.main(args);
        AppChainRules.main(args);


    }
}
