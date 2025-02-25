package org.firstinspires.ftc.teamcode.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Borrowed from: <a href="https://github.com/Wolfson-Robotics/ColorSampleDetection">
 *     https://github.com/Wolfson-Robotics/ColorSampleDetection</a>
 * <br><br>
 *
 * Object designed to encapsulate a bound created by two integers, a lower bound and an upper bound.
 * This is mainly used to describe the tops and bottoms in columns for grouped points, such as
 * a color sample. In this way, the color sample's points and the shape theretofore can be analyzed
 * through its edges.
 *
 * @author Elijah Sarte
 */
public class IntegerBounds {

    private int lowerBound;
    private int upperBound;

    /**
     * Creates a bound between two specified numbers.
     *
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     */
    public IntegerBounds(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    /**
     * Creates a bound between the lowest and highest values in the given <code>List</code>.
     *
     * @param vals the array to create the bounds from
     */
    public IntegerBounds(List<Integer> vals) {
        Collections.sort(vals);
        this.lowerBound = vals.get(0);
        this.upperBound = vals.get(vals.size() - 1);
    }

    /**
     * Checks whether a given number is in between the constructed bound of this instance. Note that this method is <strong>not</strong> inclusive.
     * @param val the given integer
     * @return if the number is inside the bound <strong>not</strong> inclusive
     */
    public boolean inBetween(double val) {
        return val > lowerBound && val < upperBound;
    }

    /**
     * Checks whether a given number is in between the constructed bound of this instance. Note that this method <strong>is</strong> inclusive, hence the name.
     * @param val the given integer
     * @return if the number is inside the bound inclusive
     */
    public boolean inBetweenClosed(double val) {
        return val >= lowerBound && val <= upperBound;
    }

    /**
     * Returns the number of integers that fall between these bounds.
     */
    public int getLength() {
        return this.upperBound - this.lowerBound;
    }

    /**
     * Returns the numbers in between and including the bounds.
     */
    public List<Integer> getSequence() {
        List<Integer> assembledSeq = new ArrayList<>();
        IntStream.rangeClosed(this.lowerBound, this.upperBound).forEach(assembledSeq::add);
        return assembledSeq;
    }


    public int getLowerBound() {
        return this.lowerBound;
    }
    public int getUpperBound() {
        return this.upperBound;
    }

    public void setLowerBound(int lowerBound) {
        if (upperBound < lowerBound) {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
        } else {
            this.lowerBound = lowerBound;
        }
    }

    public void setUpperBound(int upperBound) {
        if (lowerBound > upperBound) {
            this.lowerBound = upperBound;
            this.upperBound = lowerBound;
        } else {
            this.upperBound = upperBound;
        }
    }

    public void setBounds(int lowerBound, int upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }


}