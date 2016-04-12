package py.gapdi;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import ant.problema.Gen;
import org.opencv.imgproc.Imgproc;
import py.gapdi.helper.HOpencv;

import java.util.Random;

/**
 * Created by rainer on 03/12/2015.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.print("Error en la cantidad de parametros \n");
            System.out.print(
                    "<path lib openCV> <path dir image> <path dir output> <opcional max iter><opcional tam poblac>\n");
        }
        String pathLib = args[0];
        String pathDb = args[1];
        String pathOut = args[2];

        int numeroMaxIteraciones;
        int poblacionIni;
        if (args.length > 3) {
            numeroMaxIteraciones = Integer.valueOf(args[3]);

            poblacionIni = Integer.valueOf(args[4]);
        } else {
            numeroMaxIteraciones = 400;
            poblacionIni = 100;
        }
        System.loadLibrary(pathLib);

        Random randomGenerator = new Random();
        boolean[] individuo = new boolean[8 + 6 * 6 * 3];
        for (int i = 0; i < individuo.length; i++) {
            individuo[i] = randomGenerator.nextBoolean();
        }

        Gen g = new Gen(individuo, 3, 3, 3);
        g.decode();


        Mat ma = new Mat(5, 5, CvType.CV_8UC1);


        ma.put(0, 0, 7);
        ma.put(0, 1, 7);
        ma.put(0, 2, 2);
        ma.put(0, 3, 1);
        ma.put(0, 4, 3);
        ma.put(1, 0, 3);
        ma.put(1, 1, 3);
        ma.put(1, 2, 2);
        ma.put(1, 3, 0);
        ma.put(1, 4, 1);
        ma.put(2, 0, 5);
        ma.put(2, 1, 3);
        ma.put(2, 2, 1);
        ma.put(2, 3, 1);
        ma.put(2, 4, 5);
        ma.put(3, 0, 2);
        ma.put(3, 1, 4);
        ma.put(3, 2, 3);
        ma.put(3, 3, 4);
        ma.put(3, 4, 0);
        ma.put(4, 0, 2);
        ma.put(4, 1, 1);
        ma.put(4, 2, 7);
        ma.put(4, 3, 7);
        ma.put(4, 4, 7);

        OpenCVUtil.imprimir(ma);

        Mat se = new Mat(3, 3, CvType.CV_8U);
        se.put(0, 0, 0);
        se.put(0, 1, 0);
        se.put(0, 2, 0);
        se.put(1, 0, 0);
        se.put(1, 1, 1);
        se.put(1, 2, 4);
        se.put(2, 0, 0);
        se.put(2, 1, 0);
        se.put(2, 2, 0);

        OpenCVUtil.imprimir(se);



        Mat clon = new Mat();
        Imgproc.morphologyEx(ma,clon,Imgproc.MORPH_DILATE,se);
        OpenCVUtil.imprimir(clon);
if(true){
    return;
}

//        Imgproc.filter2D(ma,clon,-1,se,new Point(-1,-1),0, Core.BORDER_DEFAULT);

//        OpenCVUtil.imprimir(clon);


//        Imgproc.dilate(ma, clon, se);
//        Imgproc.morphologyEx(ma, clon, Imgproc.MORPH_DILATE, se);


        Mat imagen = Imgcodecs.imread("db2/woman.jpg", Imgcodecs.IMREAD_GRAYSCALE);

        OpenCVUtil.imprimir(HOpencv.newMetodoMO(imagen,ma,7));


        long ini=System.currentTimeMillis();

        Mat sterl=Mat.zeros(21,21,CvType.CV_8UC1);
        sterl.put(10,10,1);
        Mat r= HOpencv.newMetodo_Binario_OCV(imagen,sterl);

        double [] m=OpenCVUtil.calcularMetrica(imagen,r);
        System.out.print(" "+m[0]+" "+m[1]+" "+m[2]+"\n");

        System.out.print("T "+(System.currentTimeMillis()-ini)+"\n");
//
//        System.out.print("T "+OpenCVUtil.calcularMetrica(imagen,r)[0]);


//        Size size=imagen.size();
//        size.width=size.width*0.6;
//        size.height=size.height*0.6;
//        Imgproc.resize(imagen.clone(),imagen,size);
//
//        Imgcodecs.imwrite("lenna.png",imagen);
//


//        Mat io=Imgcodecs.imread("result/1_3/woman.jpg_SRC.png", Imgcodecs.IMREAD_GRAYSCALE);
//
//        OpenCVUtil.imprimir(io);
//        Mat re=Imgcodecs.imread("result/1_3/woman.jpg_RES.png", Imgcodecs.IMREAD_GRAYSCALE);

//        System.out.print("ME "+(OpenCVUtil.calcularMetrica(io,re)[2]));


//        OpenCVUtil.imprimir(g.getFiltro());
//        for (int i =0;i<g.getOperaciones().length;i++) {
//            System.out.print(g.getOperaciones()[i] + " ");
//        }


    }
}
