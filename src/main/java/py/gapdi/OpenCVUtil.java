package py.gapdi;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainer on 21/10/2015.
 */
public class OpenCVUtil {
    enum Operacion {
        DILATACION, EROCION
    }

    public static Mat convertToElements(List<Mat> strels) {
        if (strels.size() == 1) {
            return strels.get(0).clone();
        }
        List<Mat> list = new ArrayList<Mat>();
        list.addAll(strels);

        int tama = 3 + 4 * (list.size() - 1);
        Mat s = Mat.zeros(tama, tama, CvType.CV_8UC1);
        int center = tama / 2 + 1 - 1;
        Mat primero = list.get(0);
        list.remove(0);
//fila 1
        s.put(center - 1, center - 1, primero.get(0, 0));
        s.put(center, center - 1, primero.get(1, 0));
        s.put(center + 1, center - 1, primero.get(2, 0));
//fila 2
        s.put(center - 1, center, primero.get(0, 1));
        s.put(center, center, primero.get(1, 1));
        s.put(center + 1, center, primero.get(2, 1));
//fila 3
        s.put(center - 1, center + 1, primero.get(2, 2));
        s.put(center, center + 1, primero.get(2, 2));
        s.put(center + 1, center + 1, primero.get(2, 2));

        for (Mat strel : list) {
            Mat n = new Mat();
            Imgproc.dilate(s, n, strel);
            s.release();
            s = n;
        }
        return s;
    }

    public static Mat newMetodo(Mat image, Mat strel, int k) {
        Mat result = image.clone();

        for (int i = 0; i < k; i++) {
            Mat topHat = topHat(result, strel);
            Mat result2 = new Mat();
            org.opencv.core.Core.add(result, topHat, result2);
            topHat.release();
            Mat botHat = bottonHat(result, strel);
            org.opencv.core.Core.subtract(result2, botHat, result);
            botHat.release();
            result2.release();
        }
        return result;
    }

    public static Mat topHat(Mat image, Mat srtel) {

        Mat clon = new Mat();

        Imgproc.morphologyEx(image, clon, Imgproc.MORPH_TOPHAT, srtel);
        //,new Point(-1,-1),1,Core.BORDER_CONSTANT,new Scalar(7));


        return clon;
    }

    public static Mat bottonHat(Mat image, Mat srtel) {
        Mat clon = new Mat();
        Imgproc.morphologyEx(image, clon, Imgproc.MORPH_CLOSE, srtel);
        //,new Point(-1,-1),1,Core.BORDER_CONSTANT,new Scalar(7));
        org.opencv.core.Core.subtract(clon, image, clon);
        return clon;
    }

    public static double entropy(Mat m) {

        Mat hist = new Mat();
        float range[] = {0, 256};
        List<Mat> lll = new ArrayList<Mat>();
        lll.add(m);
        Imgproc.calcHist(lll, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(range), true);

        double total = m.total();
        double sum = 0.0;
        for (int i = 0; i < hist.rows(); i++) {
            double p = (hist.get(i, 0)[0]) / total;
            Double l = (p * (Math.log(p) / Math.log(2)));
            if (l.isNaN())
                l = 0.0;
            sum = sum + l;
        }
        hist.release();
        return -1 * (sum / 8);
    }


    public static void imprimir(Mat m) {


        System.out.print("" + m.rows() + "x" + m.cols() + "=======================================\n");
        for (int i = 0; i < m.rows(); i++) {
            for (int j = 0; j < m.cols(); j++) {
                System.out.print(" " + (int) m.get(i, j)[0]);
            }
            System.out.print(";\n");
        }
        System.out.print("=======================================\n");
    }

    public static double contraste(Mat imagen) {
        Mat hist = new Mat();
        float range[] = {0, 256};
        List<Mat> lll = new ArrayList<Mat>();
        lll.add(imagen);
        Imgproc.calcHist(lll, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(range), true);

//        OpenCVUtil.imprimir(hist);

        double inten_med = Core.mean(imagen).val[0];
        double total = imagen.total();
        double sum = 0;
        for (int i = 0; i < hist.rows(); i++) {
            double p = (hist.get(i, 0)[0]) / total;
            Double l = Math.pow(i - inten_med, 2) * p;
            if (l.isNaN()) {
                l = 0.0;
                System.out.print("NaN");
            }
            sum = sum + l;
        }
        hist.release();
        return Math.sqrt(sum) / 127.0;
    }

    public static Mat reducirMat(Mat image) {


        if (image.cols() <= 3 || image.rows() <= 3 || Core.countNonZero(image) < 3) {
            return image;
        }
        try {

            Mat anterior;
            int iniC = -1;
            for (int i = 0; i < image.cols(); i++) {
                if (Core.countNonZero(image.col(i)) > 0) {
                    iniC = i;
                    break;
                }
            }
            anterior = image.submat(0, image.rows(), iniC, image.cols());
            anterior.copyTo(image);
            anterior.release();
            int finC = -1;
            for (int i = image.cols() - 1; i >= 0; i--) {
                if (Core.countNonZero(image.col(i)) > 0) {
                    finC = i;
                    break;
                }
            }

            anterior = image.submat(0, image.rows(), 0, finC + 1);
            anterior.copyTo(image);
            anterior.release();
            int iniF = -1;
            for (int i = 0; i < image.rows(); i++) {
                if (Core.countNonZero(image.row(i)) > 0) {
                    iniF = i;
                    break;
                }
            }


            anterior = image.submat(iniF, image.rows(), 0, image.cols());
            anterior.copyTo(image);
            anterior.release();
            int finF = -1;
            for (int i = image.rows() - 1; i > 0; i--) {
                if (Core.countNonZero(image.row(i)) > 0) {
                    finF = i;
                    break;
                }
            }
            anterior = image.submat(0, finF + 1, 0, image.cols());
            anterior.copyTo(image);
            anterior.release();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(" " + e + " ALGO \n");

        }
        return image;
    }

    public static double getPSNR(Mat I1, Mat I2) {
        Mat s1 = I1.clone();
        Core.absdiff(I1, I2, s1);      // |I1 - I2|
        s1.convertTo(s1, CvType.CV_32F);  // cannot make a square on 8 bits
        s1 = s1.mul(s1);           // |I1 - I2|^2
        Scalar s = Core.sumElems(s1);        // sum elements per channel
        double sse = s.val[0] + s.val[1] + s.val[2]; // sum channels
        s1.release();
        if (sse <= 1e-10) // for small values return zero
            return 0;
        else {
            double mse = sse / (double) (I1.channels() * I1.total());
            double psnr = 10.0 * Math.log10((255 * 255) / mse);
            return psnr;
        }
    }

    public static double getMSSIM(Mat i1, Mat i2) {
        double C1 = 6.5025, C2 = 58.5225;
        /***************************** INITS **********************************/
        int d = CvType.CV_32F;

        Mat I1 = i1.clone(), I2 = i2.clone();
        I1.convertTo(I1, d);            // cannot calculate on one byte large values
        I2.convertTo(I2, d);
        Mat I2_2 = I2.mul(I2);        // I2^2
        Mat I1_2 = I1.mul(I1);        // I1^2
        Mat I1_I2 = I1.mul(I2);        // I1 * I2

        /*************************** END INITS **********************************/

        Mat mu1 = new Mat(), mu2 = new Mat();                   // PRELIMINARY COMPUTING

        Imgproc.GaussianBlur(I1, mu1, new Size(11, 11), 1.5);
        I1.release();
        Imgproc.GaussianBlur(I2, mu2, new Size(11, 11), 1.5);
        I2.release();
        //LIBERAR

        Mat mu1_2 = mu1.mul(mu1);
        Mat mu2_2 = mu2.mul(mu2);
        Mat mu1_mu2 = mu1.mul(mu2);
        mu2.release();
        mu1.release();

        Mat sigma1_2 = new Mat(), sigma2_2 = new Mat(), sigma12 = new Mat();

        Imgproc.GaussianBlur(I1_2, sigma1_2, new Size(11, 11), 1.5);
        I1_2.release();

        Core.subtract(sigma1_2, mu1_2, sigma1_2);
//        sigma1_2 -= mu1_2;

        Imgproc.GaussianBlur(I2_2, sigma2_2, new Size(11, 11), 1.5);
        I2_2.release();
        Core.subtract(sigma2_2, mu2_2, sigma2_2);
//        sigma2_2 -= mu2_2;

        Imgproc.GaussianBlur(I1_I2, sigma12, new Size(11, 11), 1.5);
        I1_I2.release();
        Core.subtract(sigma12, mu1_mu2, sigma12);
//        sigma12 -= mu1_mu2;

        ///////////////////////////////// FORMULA ////////////////////////////////
        Mat t1 = new Mat(), t2 = new Mat(), t3 = new Mat();

        Core.multiply(mu1_mu2, new Scalar(2), mu1_mu2);
        Core.add(mu1_mu2, new Scalar(C1), t1);
        mu1_mu2.release();
        //t1 = 2 * mu1_mu2 + C1;

        Core.multiply(sigma12, new Scalar(2), sigma12);
        Core.add(sigma12, new Scalar(C2), t2);
        sigma12.release();
//        t2 = 2 * sigma12 + C2;
        t3 = t1.mul(t2);                 // t3 = ((2*mu1_mu2 + C1).*(2*sigma12 + C2))

        Core.add(mu1_2, mu2_2, t1);
        mu1_2.release();
        mu2_2.release();
        Core.add(t1, new Scalar(C1), t1);
//        t1 = mu1_2 + mu2_2 + C1;

        Core.add(sigma1_2, sigma2_2, t2);
        sigma1_2.release();
        sigma2_2.release();
        Core.add(t2, new Scalar(C2), t2);
//        t2 = sigma1_2 + sigma2_2 + C2;
        //t1 = t1.mul(t2);
        t1 = t1.mul(t2);                 // t1 =((mu1_2 + mu2_2 + C1).*(sigma1_2 + sigma2_2 + C2))
        t2.release();

        Mat ssim_map = new Mat();
        Core.divide(t3, t1, ssim_map);
//        divide(t3, t1, ssim_map);        // ssim_map =  t3./t1;
        t3.release();
        t1.release();

        Scalar mssim = Core.mean(ssim_map);   // mssim = average of ssim map
        ssim_map.release();
        return mssim.val[0];
    }

    public static double[] calcularMetrica(Mat src, Mat result) {
        double contraste = OpenCVUtil.contraste(result);
        double ssim = OpenCVUtil.getMSSIM(src, result);
//        double entropy=entropy(result);
//        double ambe=AMBE(src,result);
//        return 1 - Math.abs((ambe) + (ssim ));

        double[] r = new double[3];
        r[0] = (1 - ssim * contraste);
        r[1] = ssim;
        r[2] = contraste;
        return r;
    }

    /***
     * Cuanto mas se acerque al cero es mejor
     *
     * @param src
     * @param result
     * @return
     */

    public static double AMBE(Mat src, Mat result) {

        return Math.abs(E(src) - E(result)) / 255.0;
    }

    private static double E(Mat src) {
        Mat hist = new Mat();
        float range[] = {0, 256};
        List<Mat> lll = new ArrayList<Mat>();
        lll.add(src);
        Imgproc.calcHist(lll, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(range), true);

        double npxl = src.total();
        double suma = 0.0;
        for (int i = 0; i < hist.total(); i++) {

            suma = suma + (i * (hist.get(i, 0)[0] / npxl));
        }
        hist.release();
        return suma;
    }

    /**
     * @param vector
     * @param filas
     * @param columnas
     * @return
     */
    public static Mat convertToMat(int[] vector, int filas, int columnas) {

        Mat m = new Mat(filas * 2, columnas * 2, CvType.CV_8UC1);
        for (int f = 0; f < columnas; f++) {
            for (int c = 0; c < filas; c++) {
                m.put(f, c, vector[f * (columnas) + c]);
            }
        }
        return m;
    }

    public static double MAE(Mat ideal, Mat senal) {
        Mat diff = ideal.clone();
        Core.absdiff(ideal, senal, diff);
        Scalar scalar = Core.sumElems(diff);
        double d = scalar.val[0] / (ideal.cols() * ideal.rows());
        return d;
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



                Mat cerar = new Mat(se_sub.size(), CvType.CV_8UC1);

                Core.subtract(se_sub, new Scalar(max - 1), cerar);
                Core.subtract(Mat.ones(se_sub.size(), cerar.depth()), cerar, cerar);
                Mat sum = se_sub.clone();
                Core.add(im_sub, se_sub, sum);
                Core.multiply(sum, cerar, sum);
                Core.MinMaxLocResult r = Core.minMaxLoc(sum);

                if (mop == Operacion.DILATACION) {
                    result.put(i, j, im_sub.get((int) r.maxLoc.y, (int) r.maxLoc.x));
                } else if (mop == Operacion.EROCION) {
                    result.put(i, j, im_sub.get((int) r.minLoc.y, (int) r.minLoc.x));
                }

                im_sub.release();
                se_sub.release();
                cerar.release();
                sum.release();
            }
        }
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

    public static Mat newMetodoMO(Mat image, Mat strel, int k, int max) {
        Mat result = image.clone();

        for (int i = 0; i < k; i++) {


            Mat topHat = TH_BH(image, strel, 0, max);
            Mat result2 = new Mat();
            org.opencv.core.Core.add(result, topHat, result2);
            topHat.release();

            Mat botHat = TH_BH(result, strel, 1, max);
            org.opencv.core.Core.subtract(result2, botHat, result);
            botHat.release();
            result2.release();
        }
        return result;
    }


}
