package py.gapdi.helper;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rainer on 11/02/2016.
 */
public class Metricas {
    public static double[] calcularMetrica(Mat src, Mat result) {
        double contraste = contraste(result);
        double ssim = getMSSIM(src, result);
        double[] r = new double[3];
        r[0] = (1 - ssim * contraste);
        r[1] = ssim;
        r[2] = contraste;
        return r;
    }

    public static double contraste(Mat imagen) {
        Mat hist = new Mat();
        float range[] = {0, 256};
        List<Mat> lll = new ArrayList<Mat>();
        lll.add(imagen);
        Imgproc.calcHist(lll, new MatOfInt(0), new Mat(), hist, new MatOfInt(256), new MatOfFloat(range), true);
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

    public static double getMSSIM(Mat i1, Mat i2) {
        double C1 = 6.5025, C2 = 58.5225;
        /***************************** INITS **********************************/
        int d = CvType.CV_32F;

        Mat I1 = i1, I2 = i2;
        i1.convertTo(I1, d);            // cannot calculate on one byte large values
        i2.convertTo(I2, d);
        Mat I2_2 = I2.mul(I2);        // I2^2
        Mat I1_2 = I1.mul(I1);        // I1^2
        Mat I1_I2 = I1.mul(I2);        // I1 * I2

        /*************************** END INITS **********************************/

        Mat mu1 = I1.clone(), mu2 = I2.clone();                   // PRELIMINARY COMPUTING

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

        Mat sigma1_2 = I1_2.clone(), sigma2_2 = I2_2, sigma12 = I1_I2.clone();

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
        Mat t1 = mu1_mu2.clone(), t2 = sigma12.clone(), t3 = t1.clone();

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

        Mat ssim_map = t3.clone();
        Core.divide(t3, t1, ssim_map);
//        divide(t3, t1, ssim_map);        // ssim_map =  t3./t1;
        t3.release();
        t1.release();

        Scalar mssim = Core.mean(ssim_map);   // mssim = average of ssim map
        ssim_map.release();
        return mssim.val[0];
    }
}
