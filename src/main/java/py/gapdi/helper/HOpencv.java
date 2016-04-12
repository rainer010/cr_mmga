package py.gapdi.helper;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainer on 11/02/2016.
 */
public class HOpencv {

    enum Operacion {
        DILATACION, EROCION
    }

    public static Mat convertToElements(List<Mat> strels) {
        if (strels.size() == 1) {
            return strels.get(0).clone();
        }
        List<Mat> list = new ArrayList<Mat>();
        list.addAll(strels);

        int tama = 2 * list.size() + 1;
        Mat s = Mat.zeros(tama, tama, CvType.CV_8UC1);
        int center = list.size() + 1;
        s.put(center, center, 1);

        for (Mat strel : list) {
            Mat n = new Mat();
            Imgproc.dilate(s, n, strel);
            s.release();
            s = n;
        }
        return s;
    }


    public static Mat newMetodo_Binario_OCV(Mat image, Mat strel) {

        Mat topHat = topHat(image, strel);
        Mat result = new Mat();
        org.opencv.core.Core.add(image, topHat, result);
        topHat.release();

        Mat botHat = bottonHat(image, strel);
        Mat r = new Mat();
        org.opencv.core.Core.subtract(result, botHat, r);
        botHat.release();
        result.release();
        return r;
    }

    public static Mat newMetodo_Binario_OCV(Mat image, List<Mat> strel) {
//        System.out.print(""+strel.size()+"\n");
        return newMetodo_Binario_OCV(image, convertToElements(strel));
    }

    public static Mat topHat(Mat image, Mat srtel) {
        Mat clon = new Mat();
        Imgproc.morphologyEx(image, clon, Imgproc.MORPH_TOPHAT, srtel);
        return clon;
    }

    public static Mat bottonHat(Mat image, Mat srtel) {
        Mat clon = new Mat();
        Imgproc.morphologyEx(image, clon, Imgproc.MORPH_CLOSE, srtel);
        Mat r = new Mat();
        org.opencv.core.Core.subtract(clon, image, r);
        clon.release();
        return r;
    }


    public static Mat operacion_morfologica(Mat src, Mat se, int max, Operacion mop) {

        int m1H_SE = se.width() / 2;
        int m2H_SE = se.width() - m1H_SE;

        int m1V_SE = se.height() / 2;
        int m2V_SE = se.height() - m1V_SE;

        Mat result = src.clone();
        for (int i = 0; i < src.rows(); i++) {
            for (int j = 0; j < src.cols(); j++) {

                int pi_m1V_SRC = i - m1H_SE;
                if (pi_m1V_SRC < 0) {
                    pi_m1V_SRC = 0;
                }

                int pf_m2V_SRC = i + m2H_SE;
                if (pf_m2V_SRC > src.cols()) {
                    pf_m2V_SRC = src.cols() - 1;
                }

                int pi_m1H_SRC = j - m1V_SE;
                if (pi_m1H_SRC < 0) {
                    pi_m1H_SRC = 0;
                }

                int pf_m2H_SRC = j + m2V_SE;
                if (pf_m2H_SRC > src.rows()) {
                    pf_m2H_SRC = src.rows();
                }


                int pi_m1H_SE = m1H_SE - (j - pi_m1H_SRC);
                int pf_m2H_SE = m1H_SE + (pf_m2H_SRC - j);

                int pi_m1V_SE = m1V_SE - (i - pi_m1V_SRC);
                int pf_m2V_SE = m1V_SE + (pf_m2V_SRC - i);


                Mat im_sub = src.submat(pi_m1V_SRC, pf_m2V_SRC, pi_m1H_SRC, pf_m2H_SRC);
                Mat se_sub = se.submat(pi_m1V_SE, pf_m2V_SE, pi_m1H_SE, pf_m2H_SE);


                Mat sum = new Mat();
                if (max <= 1) {
                    Core.multiply(im_sub, se_sub, sum);
                } else {
//                    System.out.print(se_sub.dims()+"\n");
                    Mat cerar = new Mat(se_sub.size(), CvType.CV_8UC1);
                    //el maximo valor queda igual a 1
                    Core.subtract(se_sub, new Scalar(max - 1), cerar);
                    //invertimos la matriz
                    Mat aux_ones = Mat.ones(se_sub.size(), cerar.depth());
                    Core.subtract(aux_ones, cerar, cerar);
                    Core.add(im_sub, se_sub, sum);
                    //para cerar posiciones que no importan se(i,j)=max
                    Core.multiply(sum, cerar, sum);
                    cerar.release();
                    aux_ones.release();
                }

                Core.MinMaxLocResult r = Core.minMaxLoc(sum);

                if (mop == Operacion.DILATACION) {
                    result.put(i, j, im_sub.get((int) r.maxLoc.y, (int) r.maxLoc.x));
                } else if (mop == Operacion.EROCION) {
                    result.put(i, j, im_sub.get((int) r.minLoc.y, (int) r.minLoc.x));
                }

                im_sub.release();
                se_sub.release();

                sum.release();
//                System.out.print(" "+j+" ");
            }
//            System.out.print("_"+i+"\n");

        }
        System.gc();
        return result;
    }


    public static Mat open_close(Mat src, Mat se, int op, int max) {
        Mat result;
        if (op == 0) {
            Mat r = operacion_morfologica(src, se, max, Operacion.EROCION);
            result = operacion_morfologica(r, se, max, Operacion.DILATACION);
            r.release();
            return result;

        } else if (op == 1) {
            Mat r = operacion_morfologica(src, se, max, Operacion.DILATACION);
            result = operacion_morfologica(r, se, max, Operacion.EROCION);
            r.release();
            return result;
        } else {
            return null;
        }
    }

    public static Mat TH_BH(Mat src, Mat se, int op, int max) {
        Mat retrn = src.clone();
        if (op == 0) {
            Mat r = open_close(src, se, 0, max);
            Core.subtract(src, r, retrn);
            r.release();
            return retrn;

        } else if (op == 1) {
            Mat r = open_close(src, se, 1, max);
            Core.subtract(r, src, retrn);
            r.release();
            return retrn;
        } else {
            return null;
        }
    }

    public static Mat newMetodoMO(Mat image, Mat strel, int max) {
        Mat topHat = TH_BH(image, strel, 0, max);
        Mat result = new Mat();
        org.opencv.core.Core.add(image, topHat, result);
        topHat.release();

        Mat botHat = TH_BH(image, strel, 1, max);
        org.opencv.core.Core.subtract(result, botHat, result);
        botHat.release();
        return result;
    }


}
