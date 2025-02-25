package org.firstinspires.ftc.teamcode.oldcodebase.auto.colorsample;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class ColorFilterPipeline extends OpenCvPipeline {

    private double[] hsvMin;
    private double[] hsvMax;
    private int minWidth;
    private int minHeight;

    // Map that associates found columns containing color with the rows that also contain the color.
    // If used correctly, you can create a box containing the object you would like to find
    private Mat lastMat = null;
    private Mat lastMatMain = null;
    private final TreeMap<Integer, List<Integer>> colorLocs = new TreeMap<>();


    public static double normalize(double value, double min, double max) {
        return (value - min) / (max - min);
    }
    public static double denormalize(double normalized, double min, double max) {
        return (normalized * (max - min) + min);
    }

    public static double convertScale(double value, double oldMin, double oldMax, double newMin, double newMax) {
        return denormalize(normalize(value, oldMin, oldMax), newMin, newMax);
    }
    public static double[] convertHSVScaleOpenCV(double[] hsv) {
        return new double[] {
                convertScale(hsv[0], 0, 360, 0, 179),
                convertScale(hsv[1], 0, 100, 0, 255),
                convertScale(hsv[2], 0, 100, 0, 255)};
    }

    public ColorFilterPipeline(double[] rgbMin, double[] rgbMax) {
        this.hsvMin = convertHSVScaleOpenCV(rgbMin);
        this.hsvMax = convertHSVScaleOpenCV(rgbMax);
//        this.hsvMin = rgbMin;
//        this.hsvMax = rgbMax;
    }

    public Mat processFrame(Mat input) {

//        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2RGB);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_RGB2HSV);

        Imgproc.blur(input, input, new Size(5, 5));
        this.lastMat = input.clone();
        Core.inRange(input, new Scalar(hsvMin[0], hsvMin[1], hsvMin[2], 255), new Scalar(hsvMax[0], hsvMax[1], hsvMax[2], 255), input);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_CLOSE, kernel);

        int cols = input.cols(), rows = input.rows();
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (input.get(row, col)[0] == 255d) {
                    List<Integer> loc = colorLocs.getOrDefault(col, new ArrayList<>());
                    loc.add(row);
                    colorLocs.put(col, loc);
                }
            }
        }

        return input;
    }

    public Mat getLastMat() {
        return this.lastMat;
    }
    public TreeMap<Integer, List<Integer>> getColorLocs() {
        return this.colorLocs;
    }

}
