package org.firstinspires.ftc.teamcode.oldcodebase.auto.colorsample;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class ColorSampleDetectionPipeline {


    private static int size = -1;

    public static double processFrame(TreeMap<Integer, List<Integer>> colorLocs) throws Exception {

        double degrees = 0;
        boolean trapezoidProd = false;
        boolean lSeg = false;
        boolean rSeg = false;

        colorLocs = colorLocs.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> {
                    List<Integer> thisThing = entry.getValue();
                    Collections.sort(thisThing);
                    List<List<Integer>> consecutiveGroups = new ArrayList<>();
                    List<Integer> currGroup = new ArrayList<>();
                    final int[] lastThing = {0};
                    thisThing.forEach(thing -> {
                        lastThing[0] = thing;
                        if (Math.abs(thing - lastThing[0]) <= 3) {
                            currGroup.add(thing);
                        } else {
                            consecutiveGroups.add(currGroup);
                            currGroup.clear();
                        }
                    });
                    consecutiveGroups.add(currGroup);
                    return consecutiveGroups.stream().max(Comparator.comparingInt(List::size)).orElseThrow(() -> new RuntimeException("Nooo"));
                },
                (g, u) -> g,
                TreeMap::new
        ));


        TreeMap<Integer, List<Integer>> sepSidesA = new TreeMap<>();
        List<Integer> cols = new ArrayList<>(colorLocs.keySet());


        int[] colIndex = {-1};
        colorLocs.forEach((col, rows) -> {
            colIndex[0]++;
            Collections.sort(rows);
            List<Integer> newRows = rows;
            if (!(colIndex[0] == 0 || colIndex[0] == cols.size() - 1)) {
//                sepSidesA.put(col, rows);
                rows = Arrays.asList(rows.get(0), rows.get(rows.size() - 1));
                return;
            }
            if (newRows.size() < 2) return;
            sepSidesA.put(col, newRows);
        });

        List<TreeMap<Integer, List<Integer>>> sepSidesC = new ArrayList<>(sepSidesA.entrySet().stream()
                .collect(ArrayList::new,
                        (list, entry) -> {
                            if (list.isEmpty() || entry.getKey() - list.get(list.size() - 1).lastKey() > 10)
                                list.add(new TreeMap<>());
                            list.get(list.size() - 1).put(entry.getKey(), entry.getValue());
                        },
                        (list1, list2) -> list1.addAll(list2))
        );
        TreeMap<Integer, List<Integer>> sepSides = sepSidesC.stream().max(Comparator.comparingInt(map -> map.size())).orElseThrow(() -> new RuntimeException("Exception here"));

        // todo: make it only detect actual rectangle
//        TreeMap<Integer, List<Integer>> sepSides = sepSidesD;
//        TreeMap<Integer, List<Integer>> sepSides = new TreeMap<>(sepSidesA.headMap(1300).tailMap(800));
        // 15441
//        TreeMap<Integer, List<Integer>> sepSides = new TreeMap<>(sepSidesA.headMap(1000));
//        TreeMap<Integer, List<Integer>> sepSides = new TreeMap<>(sepSidesA);

        // for other width
//        int combinationFactor = 25;
//        int combinationFactor = (int) (0.04448398576512455516014234875445 * (sepSides.lastKey() - sepSides.firstKey()));
        // 25 (combinationFactor / 562 (largest apparent color sample width observed where combinationFactor works)
        // old: 25 / 562
        // new: 35 / 562
//        int combinationFactor = (int) (0.04545454545454545454545454545455 * (sepSides.lastKey() - sepSides.firstKey()));
        int combinationFactor = (int) (0.06227758007117437722419928825623 * (sepSides.lastKey() - sepSides.firstKey()));
//        int sepSidesHeight = sepSides.entrySet().stream().map(e -> Collections.max(e.getValue())).max(Integer::compare).orElseThrow(() -> new RuntimeException("Exception here"));
//        int hookHeightRatio =


        // rudimentary interpolation
        // todo: it shouldnt be missing pts anyway should just be a 1 in 1k backup mechanism
        List<Integer> lastFullSide = null;
        int start1 = sepSides.firstKey(), start2 = sepSides.lastKey();
        for (int start = sepSides.firstKey() + 1; start <= sepSides.lastKey() - 1; start++) {
            List<Integer> hereSide = sepSides.getOrDefault(start, new ArrayList<>(Arrays.asList(0, 0)));
            if (hereSide.size() > 2) {
                throw new IllegalStateException("Erm what the sigma");
            }
//            if (Math.abs(start - start1) < combinationFactor && Math.abs(start - start2) < combinationFactor && Math.abs(start - start2) < combinationFactor) {
//                continue;
//            }
            if (Math.abs(hereSide.get(0) - hereSide.get(1)) > 150) {
                lastFullSide = hereSide;
                continue;
            }
            if (lastFullSide == null) continue;
            if (Math.abs(Collections.max(hereSide) - Collections.max(lastFullSide)) >= 90) {
                listPut(sepSides, start, Collections.max(lastFullSide));
                List<Integer> hereStart = sepSides.get(start);
                if (hereStart.size() > 2) {
                    hereStart = Arrays.asList(Collections.min(hereStart), Collections.max(hereStart));
                    sepSides.put(start, hereStart);
                }
            } else if (Math.abs(Collections.min(hereSide) - Collections.min(lastFullSide)) >= 90) {
                listPut(sepSides, start, Collections.min(lastFullSide));
                List<Integer> hereStart = sepSides.get(start);
                if (hereStart.size() > 2) {
                    hereStart = Arrays.asList(Collections.min(hereStart), Collections.max(hereStart));
                    sepSides.put(start, hereStart);
                }
            }
        }


        List<Integer> sideCols = new ArrayList<>(sepSides.keySet());


        TreeMap<Integer, List<Integer>> anchorPts = new TreeMap<>();

        TreeMap<Integer, List<Integer>> firstTen = new TreeMap<>(sepSides.headMap(sideCols.get(combinationFactor)));
        TreeMap<Integer, List<Integer>> lastTen = new TreeMap<>(sepSides.tailMap(sideCols.get(sideCols.size() - combinationFactor)));
        int lowestLeft = Collections.min(firstTen.entrySet().stream().min(Comparator.comparingInt(entry -> entry.getValue().get(0))).orElse(new AbstractMap.SimpleEntry<>(0, Arrays.asList(0))).getValue());
        int highestLeft = Collections.max(firstTen.entrySet().stream().max(Comparator.comparingInt(entry -> entry.getValue().stream().max(Integer::compare).orElseThrow(() -> new RuntimeException("Exception here")))).orElse(new AbstractMap.SimpleEntry<>(0, Arrays.asList(0))).getValue());
        sideCols.stream().limit(combinationFactor).forEach(sepSides::remove);
        sepSides.put(sideCols.get(combinationFactor), Arrays.asList(lowestLeft, highestLeft));

        int lowestRight = Collections.min(lastTen.entrySet().stream().min(Comparator.comparingInt(entry -> entry.getValue().get(0))).orElse(new AbstractMap.SimpleEntry<>(0, Arrays.asList(0))).getValue());
        int highestRight = Collections.max(lastTen.entrySet().stream().max(Comparator.comparingInt(entry -> Optional.ofNullable(entry.getValue().get(1)).orElse(entry.getValue().get(0)))).orElse(new AbstractMap.SimpleEntry<>(0, Arrays.asList(0))).getValue());
        sideCols.subList(sideCols.size() - combinationFactor, sideCols.size()).stream().forEach(sepSides::remove);
        sepSides.put(sideCols.get(sideCols.size() - combinationFactor), Arrays.asList(lowestRight, highestRight));


        final int totalLen = 2;
        int[] foundXes = new int[totalLen];
        for (int sideIndex = 0; sideIndex < totalLen; sideIndex++) {

            final int sideIndex2 = sideIndex;
            TreeMap<Integer, Integer> thisSepSide = sepSides.entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().get(sideIndex2),
                    (a, b) -> a,
                    TreeMap::new
            ));
            Map.Entry<Integer, Integer> middlePoint = new AbstractMap.SimpleEntry<>(new ArrayList<>(thisSepSide.keySet()).get(thisSepSide.keySet().size() / 2), thisSepSide.get(new ArrayList<>(thisSepSide.keySet()).get(thisSepSide.keySet().size() / 2)));

            int xm = middlePoint.getKey(), ym = middlePoint.getValue(),
                    x0 = thisSepSide.firstEntry().getKey(), y0 = thisSepSide.firstEntry().getValue(),
                    xf = thisSepSide.lastEntry().getKey(), yf = thisSepSide.lastEntry().getValue();
            System.out.println("xm: " + xm);

//            NavigableMap<Integer, Integer> lPts = thisSepSide.subMap(x0, true, xm, false);
//            NavigableMap<Integer, Integer> rPts = thisSepSide.subMap(xm, false, xf, true);

            Map.Entry<String, Map<String, Integer>> l = bestFit(new TreeMap<>(thisSepSide.headMap(xm)), sideIndex2 == 0 ? 1 : 0);
            Map.Entry<String, Map<String, Integer>> r = bestFit(new TreeMap<>(thisSepSide.tailMap(xm)), sideIndex2 == 0 ? 1 : 0);

            System.out.println("l: " + l);
            System.out.println("r: " + r);


            Map<String, Integer> lFitData = l.getValue();
            Map<String, Integer> rFitData = r.getValue();
            double lC = Double.NaN;
            double rC = Double.NaN;

            switch (l.getKey()) {
                case "trapezoid": {
                    trapezoidProd = true;
                    int leftHookX2 = lFitData.get("leftHookX2"),
                            rightHookX2 = lFitData.get("rightHookX2"),
                            leftHookX = lFitData.get("leftHookX"),
                            rightHookX = lFitData.get("rightHookX");
                    listPut(anchorPts, leftHookX2, thisSepSide.get(leftHookX2));
                    listPut(anchorPts, rightHookX2, thisSepSide.get(rightHookX2));

                    // lx is bare left, lx2 is trapezoid left top same with r
                    double c0n = (double) (thisSepSide.get(leftHookX2) - thisSepSide.get(leftHookX)) / (double) (leftHookX2 - leftHookX);
//                    double acc0n =
                    firstTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, leftHookX, -1, c0n, 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }
                case "triangle": {
                    int extremumKey = lFitData.get("extremumKey"),
                            extremumVal = lFitData.get("extremumVal");
                    listPut(anchorPts, extremumKey, extremumVal);

                    double c0n = (double) (thisSepSide.get(extremumKey) - y0) / (double) (extremumKey - x0);
                    double acc0n = getInterpolationAccuracy(thisSepSide, x0, true, extremumKey, false);
//                    double acc0n =
                    firstTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, extremumKey, -1, c0n, acc0n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }
                case "seg": {
                    lSeg = true;
                    int hookX = lFitData.get("hookX");
                    listPut(anchorPts, hookX, thisSepSide.get(hookX));

                    double c0n = (double) (thisSepSide.get(hookX) - y0) / (double) (hookX - x0);
                    double acc0n = getInterpolationAccuracy(thisSepSide, x0, true, hookX, false);
//                    double acc0n =
                    firstTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, hookX, -1, c0n, acc0n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));

                    break;
                }
                case "line": {
                    lC = (double) (thisSepSide.get(xm) - y0) / (double) (xm - x0);
                    double acc0n = getInterpolationAccuracy(thisSepSide, x0, true, xm, false);
                    firstTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, xm, -1, lC, acc0n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }

            }


            switch (r.getKey()) {
                case "trapezoid": {
                    trapezoidProd = true;
                    int leftHookX2 = rFitData.get("leftHookX2"),
                            rightHookX2 = rFitData.get("rightHookX2"),
                            leftHookX = rFitData.get("leftHookX"),
                            rightHookX = rFitData.get("rightHookX");
                    listPut(anchorPts, leftHookX2, thisSepSide.get(leftHookX2));
                    listPut(anchorPts, rightHookX2, thisSepSide.get(rightHookX2));

                    // lx is bare left, lx2 is trapezoid left top same with r
                    double c1n = (double) (thisSepSide.get(rightHookX) - thisSepSide.get(rightHookX2)) / (double) (rightHookX - rightHookX2);
//                    double acc0n =
                    lastTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, rightHookX2, 1, c1n, 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }
                case "triangle": {
                    int extremumKey = rFitData.get("extremumKey"),
                            extremumVal = rFitData.get("extremumVal");
                    listPut(anchorPts, extremumKey, extremumVal);

                    double c1n = (double) (yf - thisSepSide.get(extremumKey)) / (double) (xf - extremumKey);
                    double acc1n = getInterpolationAccuracy(thisSepSide, extremumKey, true, xf, false);
//                    double acc0n =
                    lastTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, extremumKey, 1, c1n, acc1n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }
                case "seg": {
                    rSeg = true;
                    int hookX = rFitData.get("hookX");
                    listPut(anchorPts, hookX, thisSepSide.get(hookX));

                    double c1n = (double) (yf - thisSepSide.get(hookX)) / (double) (xf - hookX);
                    double acc1n = getInterpolationAccuracy(thisSepSide, hookX, true, xf, false);
//                    double acc0n =
                    lastTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, hookX, 1, c1n, acc1n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));

                    break;
                }
                case "line": {
                    rC = (double) (yf - thisSepSide.get(xm)) / (double) (xf - xm);
                    double acc1n = getInterpolationAccuracy(thisSepSide, xm, true, xf, false);
                    lastTen.forEach((col, rows) -> thisSepSide.put(col, rows.get(sideIndex2)));
                    condApply(getMaxInterpolPt(thisSepSide, xm, 1, rC, acc1n + 2),
                            (x) -> x != 0,
                            (x) -> listPut(anchorPts, x, thisSepSide.get(x)));
                    break;
                }

            }
            if ((Math.abs(lC) > 0.1 || Math.abs(rC) > 0.1)
                    && (lC < 0 && rC >= 0) || (rC < 0 && lC >= 0)) {
                listPut(anchorPts, xm, ym);
            }

            String a = "better get used to those bars kid";



        }































































        // YESSSSSSSSSSSSSSSSSSSSSSSSSS
        Map<Integer, List<Integer>> oldAnchorPts = new TreeMap<>(anchorPts);







        if (lSeg && rSeg) trapezoidProd = true;
        int size = (int) anchorPts.values().stream().flatMap(List::stream).count();
        ColorSampleDetectionPipeline.size = size;
        System.out.println("size: " + size);
/*
        if (size >= 6) {

            List<Integer> anchorKeys = returnSort(new ArrayList<>(anchorPts.keySet()));
            List<Map.Entry<Integer, List<Integer>>> anchorEntries = new ArrayList<>(anchorPts.entrySet());
            Map.Entry<Integer, List<Integer>> bmPt = Arrays.asList(anchorKeys.get(size / 2 - 1), anchorKeys.get(size / 2)).stream()
                    .map(k -> anchorEntries.get(k))
                    .min(Comparator.comparingInt(e -> e.getValue().stream().min(Comparator.naturalOrder()).orElseThrow(() -> new RuntimeException("Exception here"))))
                    .orElseThrow(() -> new RuntimeException("Exception here"));


        }*/

        // todo:
        // meh ill rewrite the logic later
        // it is as follows:
        // everything stays the same except for two special cases:
        // if it is 90 degrees but oriented to the right and/or left then do the respective point grabbing
        // where x is bottom middle and y is lt and rt respectively
        // if it has a trapezoidal top then it's just 90 degrees...
        // otherwise everything else stays the same (fix it later though)
        // note: for determining the corner point within the rectangle compare the interpolation acc
        // of the top and bottom. whichever one has a higher accuracy wins and their *rate* is used instead (anchor point
        // from which rate is exteneded does not change.)
        // NOTE
        // TODO: debug_0_IMG_20241115_165646_HDR.jpg does NOT produce the interpolated corner. figure out why later.
        try {
            if (size >= 6 && !trapezoidProd) {


                Map.Entry<Integer, List<Integer>> bPt = anchorPts.entrySet().stream().max((e1, e2) -> {
                    int maxVal1 = e1.getValue().stream().max(Integer::compare).orElse(Integer.MAX_VALUE);
                    int maxVal2 = e2.getValue().stream().max(Integer::compare).orElse(Integer.MAX_VALUE);
                    return Integer.compare(maxVal1, maxVal2);
                }).orElseThrow(() -> {
                    throw new IllegalStateException("Amchor pts is not defined?");
                });

//            int middleIndex = anchorPts.size() / 2;
//            int middleKey = anchorPts.keySet().stream().skip(middleIndex).findFirst().orElseThrow(() -> new RuntimeException("Exception here"));
                int middleKey = bPt.getKey();

                int distRight = Math.abs(anchorPts.lastKey() - middleKey);
                int distLeft = Math.abs(anchorPts.firstKey() - middleKey);
                int minDistFromMid = Math.min(distRight, distLeft);
                int proximThreshold = (int) Math.round(0.45 * minDistFromMid);
                TreeMap<Integer, List<Integer>> anchorPtsProcessed = anchorPts.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (existing, newEntry) -> {
                                    existing.addAll(newEntry);
                                    return existing;
                                },
                                TreeMap::new
                        ))
                        .entrySet().stream()
                        .reduce(new TreeMap<>(), (acc, entry) -> {
                            Integer lastKey = acc.isEmpty() ? null : acc.lastKey();
                            if (lastKey != null && entry.getKey() - lastKey <= proximThreshold) {
                                acc.get(lastKey).addAll(entry.getValue());
                            } else {
                                acc.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                            }
                            return acc;
                        }, (acc1, acc2) -> acc1);


                Map.Entry<Integer, List<Integer>> tPt = anchorPtsProcessed.entrySet().stream().min((e1, e2) -> {
                    int maxVal1 = e1.getValue().stream().min(Integer::compare).orElse(Integer.MIN_VALUE);
                    int maxVal2 = e2.getValue().stream().min(Integer::compare).orElse(Integer.MIN_VALUE);
                    return Integer.compare(maxVal1, maxVal2);
                }).orElseThrow(() -> new IllegalStateException("Anchor pts is not defined?"));
            /*Map.Entry<Integer, List<Integer>> bPt = anchorPtsProcessed.entrySet().stream().max((e1, e2) -> {
                int maxVal1 = e1.getValue().stream().max(Integer::compare).orElse(Integer.MAX_VALUE);
                int maxVal2 = e2.getValue().stream().max(Integer::compare).orElse(Integer.MAX_VALUE);
                return Integer.compare(maxVal1, maxVal2);
            }).orElseThrow(() -> { throw new IllegalStateException("Amchor pts is not defined?"); });*/


                // todo: make it so that middleKey (in this case) is dependent on top being less than or greater than bottom point
                Map.Entry<Integer, List<Integer>> rtPt = anchorPtsProcessed.tailMap(middleKey, false).entrySet().stream()
                        .min(Comparator.comparingInt(entry -> entry.getValue().stream().mapToInt(Integer::intValue).min().orElseThrow(() -> new RuntimeException("Exception here")))).orElseThrow(() -> new RuntimeException("Exception here"));
                Map.Entry<Integer, List<Integer>> rbPt = anchorPtsProcessed.tailMap(middleKey, false).entrySet().stream()
                        .max(Comparator.comparingInt(entry -> entry.getValue().stream().mapToInt(Integer::intValue).max().orElseThrow(() -> new RuntimeException("Exception here")))).orElseThrow(() -> new RuntimeException("Exception here"));
                Map.Entry<Integer, List<Integer>> ltPt = anchorPtsProcessed.headMap(middleKey, false).entrySet().stream()
                        .min(Comparator.comparingInt(entry -> entry.getValue().stream().mapToInt(Integer::intValue).min().orElseThrow(() -> new RuntimeException("Exception here")))).orElseThrow(() -> new RuntimeException("Exception here"));
                Map.Entry<Integer, List<Integer>> lbPt = anchorPtsProcessed.headMap(middleKey, false).entrySet().stream()
                        .max(Comparator.comparingInt(entry -> entry.getValue().stream().mapToInt(Integer::intValue).max().orElseThrow(() -> new RuntimeException("Exception here")))).orElseThrow(() -> new RuntimeException("Exception here"));
                int rtKey = rtPt.getKey();
                int rtVal = Collections.min(rtPt.getValue());
                int rbKey = rbPt.getKey();
                int rbVal = Collections.max(rbPt.getValue());
                int ltKey = ltPt.getKey();
                int ltVal = Collections.min(ltPt.getValue());
                int lbKey = lbPt.getKey();
                int lbVal = Collections.max(ltPt.getValue());

                int tY = Collections.min(tPt.getValue());
                int bY = Collections.max(bPt.getValue());

//            int estimatedCornerY = (int) Math.floor(((((double) (rtVal-tY))/((double) (rtKey-tPt.getKey()))) * (bPt.getKey() - ltKey)) + ltVal);
//            int estimatedCornerY = (int) Math.round(((((double) (bY-lbVal))/((double) (bPt.getKey()-lbKey))) * (bPt.getKey()-lbKey)) + ltVal);
                // quick ref:
                //int distRight = Math.abs(anchorPts.lastKey() - middleKey);
                //int distLeft = Math.abs(anchorPts.firstKey() - middleKey);
                int estimatedCornerY = distLeft < distRight ?
                        (int) Math.round(((((double) (bY - lbVal)) / ((double) (bPt.getKey() - lbKey))) * (bPt.getKey() - lbKey)) + ltVal)
                        : (int) Math.round(((((double) (bY - rbVal)) / ((double) (bPt.getKey() - rbKey))) * (bPt.getKey() - rbKey)) + rtVal);
                List<Integer> hereRows = anchorPtsProcessed.getOrDefault(bPt.getKey(), new ArrayList<>());
                hereRows.add(estimatedCornerY);
                anchorPtsProcessed.put(bPt.getKey(), hereRows);
                List<Integer> finalHereRows = anchorPts.getOrDefault(bPt.getKey(), new ArrayList<>());
                finalHereRows.add(estimatedCornerY);
                anchorPts.put(bPt.getKey(), finalHereRows);


                List<Integer> xesAgain = new ArrayList<>(anchorPtsProcessed.keySet());
                Collections.sort(xesAgain);

                int minAnchorX = xesAgain.get(0);
                int maxAnchorX = xesAgain.get(xesAgain.size() - 1);
                List<Integer> minEdge = anchorPtsProcessed.get(minAnchorX);
                List<Integer> maxEdge = anchorPtsProcessed.get(maxAnchorX);
                Collections.sort(minEdge);
                Collections.sort(maxEdge);

                int minXmaxY = minEdge.get(minEdge.size() - 1);
                int maxXmaxY = maxEdge.get(maxEdge.size() - 1);

                List<Integer> midXes = xesAgain;
                midXes.remove(0);
                midXes.remove(midXes.size() - 1);

                final int[] highestX = {0};
                final int[] highestY = {0};
                int highestXRelativeToY = 0;
                midXes.forEach(midX -> {
                    List<Integer> currsArr = anchorPtsProcessed.get(midX);
                    Collections.sort(currsArr);
                    int currs = currsArr.get(currsArr.size() - 1);
                    if (currs > highestY[0]) {
                        highestY[0] = currs;
                        highestX[0] = midX;
                    }
                });
                double tanRatio = distLeft > distRight ? ((double) (minXmaxY - highestY[0]) / (double) (highestX[0] - minAnchorX))
                        : ((double) (maxXmaxY - highestY[0]) / (double) (highestX[0] - maxAnchorX));
                degrees = Math.abs(Math.atan(tanRatio) * (180 / Math.PI));
/*
            int geoMidpointX = (int) ((double) (minAnchorX + maxAnchorX) / 2);
            int diffmidAnchorRaw = highestX[0] - geoMidpointX;
            int degCoef = (diffmidAnchorRaw) < 0 ? -1 : 1;
            int diffMidAnchor = Math.abs(diffmidAnchorRaw);
            double degRatio = ((double) diffMidAnchor / geoMidpointX) * 90;
            double finalDRat = degCoef < 0 ? degRatio : degRatio + 90;
            degrees = finalDRat;*/

            } else if (size == 4) {
                List<Integer> colsAgain = new ArrayList<>(anchorPts.keySet());
                Collections.sort(colsAgain);

                List<Integer> leftRows = anchorPts.get(colsAgain.get(0));
                List<Integer> rightRows = anchorPts.get(colsAgain.get(colsAgain.size() - 1));

                int leftAnchorPt = leftRows.get(0);
                int rightAnchorPt = rightRows.get(0);
                int widthLength = Math.abs(rightAnchorPt - leftAnchorPt) / (colsAgain.size() - 1);
                int heightLength = Math.abs(leftRows.get(leftRows.size() - 1) - leftAnchorPt);

                if (widthLength > heightLength) {
                    degrees = 0;
                } else {
                    degrees = 90;
                }

            } else if (size == 6) {

                System.out.println("to be determined...");
                degrees = Integer.MAX_VALUE;
            }
        } catch (Exception e) {
            System.out.println("fuck off forever");
        }

        return degrees;

    }

















    public static <K, V> Map.Entry<K, V> getMiddleEntry(TreeMap<K, V> treeMap) {
        return variableOper(new ArrayList<>(treeMap.entrySet()),
                (x) -> x.get(x.size() / 2));
    }



    private static double getInterpolationAccuracy(NavigableMap<Integer, Integer> pts, int x1, boolean inc1, int x2, boolean inc2, double c) {
        int y1 = pts.get(x1);
        NavigableMap<Integer, Integer> ptsInterval = x1 < x2 ? pts.subMap(x1, inc1, x2, inc2) : pts.subMap(x2, inc2, x1, inc1);
        TreeMap<Integer, Integer> ptsInterpolated = ptsInterval.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> (int) ((c * (entry.getKey() - x1)) + y1),
                (a, b) -> a,
                TreeMap::new
        ));
        List<Integer> interpolatedCols = new ArrayList<>(ptsInterpolated.keySet());
        List<Integer> ptDevs = IntStream.rangeClosed(0, ptsInterpolated.size() - 1)
                .map(i -> interpolatedCols.get(i))
                .map(i -> condReturn(Math.abs(ptsInterval.get(i) - ptsInterpolated.get(i)), (x) -> x <= 2, 0)).boxed().collect(Collectors.toCollection(ArrayList::new));
        return (double) (ptDevs.stream().mapToInt(Integer::intValue).sum()) / (double) (ptDevs.size());
    }
    private static double getInterpolationAccuracy(NavigableMap<Integer, Integer> pts, int x1, boolean inc1, int x2, boolean inc2) {
        int y1 = pts.get(x1), y2 = pts.get(x2);
        double c = (y2 - y1)/(double) (x2-x1);
        return getInterpolationAccuracy(pts, x1, inc1, x2, inc2, c);
    }
    private static double getInterpolationAccuracy(NavigableMap<Integer, Integer> pts, int x1, int x2, double c) {
        return getInterpolationAccuracy(pts, x1, false, x2, false, c);
    }
    private static double getInterpolationAccuracy(NavigableMap<Integer, Integer> pts, int x1, int x2) {
        return getInterpolationAccuracy(pts, x1, false, x2, false);
    }

    // returns x coord
    // todo: make direction dynamically calculated with pts endpoint or something like that
    // foundchange is the rate at which it interpolates
    // desiredacc is the rate that it should be modeled against
    private static int getMaxInterpolPt(NavigableMap<Integer, Integer> givenPts, int anchorX, int direction, double foundChange, double desiredAcc) {
        int lastX = 0, furthestX = direction < 0 ? givenPts.firstKey() : givenPts.lastKey();
        NavigableMap<Integer, Integer> pts =  direction < 0 ? givenPts.subMap(furthestX, true, anchorX, true).descendingMap() : givenPts.subMap(anchorX, true, furthestX, true);
        List<Integer> ptIndices = pts.keySet().stream().skip(1).collect(Collectors.toList());
        for (int xcF : ptIndices) {
            if (Math.round(Math.abs(getInterpolationAccuracy(givenPts, xcF, true, anchorX, false, foundChange)) - desiredAcc) > 2) {
                furthestX = lastX == 0 ? xcF : lastX;
                break;
            }
            lastX = xcF;
        }
        return furthestX;
    }



    public static Map.Entry<String, Map<String, Integer>> bestFit(TreeMap<Integer, Integer> givenPts, int top) {
        // first check: trapezoid
        // second check: triangle
        // third check: line segment check (hyp on both sides failure ofc)
        // fourth check: straight line
        // fifth: none
        TreeMap<Double, String> queryAccuracies = new TreeMap<>();
        Map<String, Map<String, Integer>> fitData = new HashMap<>();


        Map.Entry<Integer, Integer> middlePoint = new AbstractMap.SimpleEntry<>(new ArrayList<>(givenPts.keySet()).get(givenPts.keySet().size() / 2), givenPts.get(new ArrayList<>(givenPts.keySet()).get(givenPts.keySet().size() / 2)));

        int xm = middlePoint.getKey(), ym = middlePoint.getValue(),
                x0 = givenPts.firstEntry().getKey(), y0 = givenPts.firstEntry().getValue(),
                xf = givenPts.lastEntry().getKey(), yf = givenPts.lastEntry().getValue();

        int hookY = top == 1 ? y0 < yf ? y0 : yf : y0 > yf ? y0 : yf;
        int maxY = top == 1 ? y0 > yf ? y0 : yf : y0 < yf ? yf : y0;

        NavigableMap<Integer, Integer> lPts = givenPts.subMap(x0, true, xm, false);
        NavigableMap<Integer, Integer> rPts = givenPts.subMap(xm, false, xf, true);

        try {
            int leftHookX = lPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - hookY) < 2).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            int rightHookX = rPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - hookY) < 2).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            int leftHookX2 = lPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - ym) < 2).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            int rightHookX2 = rPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - ym) < 2).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            System.out.println("trapezoid leftHookX - " + leftHookX + ", rightHookX - " + rightHookX);
            System.out.println("trapezoid leftHookX2 - " + leftHookX2 + ", rightHookX2 - " + rightHookX2);

            // todo: maybe abs h1
            int b1 = rightHookX2 - leftHookX2, b2 = rightHookX - leftHookX, h1 = hookY - ym;
            double estimatedTrapezoidalArea = ((double) (b1 + b2) / 2) * h1;
            int actualArea = givenPts.entrySet().stream().filter(e -> !(e.getKey() < leftHookX || e.getKey() > rightHookX))
                    .mapToInt(e -> Math.abs(e.getValue() - hookY)).sum();
            System.out.println("estimated trapezoidal area: " + estimatedTrapezoidalArea);
            System.out.println("actual area: " + actualArea);

            if (Math.abs(actualArea - estimatedTrapezoidalArea) < (int) Math.abs(actualArea - (0.9*actualArea))
                    && estimatedTrapezoidalArea > 0
                    && Math.abs((double) (-h1) / (double) (ym - maxY)) > 0.05
                    && (estimatedTrapezoidalArea / (b1 >= b2 ? b1 : b2)) > 5) {
                System.out.println("using trapezoidal area");
                queryAccuracies.put(Math.abs(estimatedTrapezoidalArea - actualArea) / actualArea, "trapezoid");
                
                HashMap<String, Integer> trapezoidFitData = new HashMap<>();
                trapezoidFitData.put("leftHookX", leftHookX);
                trapezoidFitData.put("rightHookX", rightHookX);
                trapezoidFitData.put("leftHookX2", leftHookX2);
                trapezoidFitData.put("rightHookX2", rightHookX2);
                
                fitData.put("trapezoid", trapezoidFitData);
//                return "trapezoid";

            } else {
                throw new RuntimeException("Not using trapezoidal area");
            }

        } catch (Exception e) {
            System.out.println("skipping trapezoidal area");
        }


        try {
            int leftHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - hookY) <= 5).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            int rightHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - hookY) <= 5).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
            System.out.println("triangle leftHookX - " + leftHookX + ", rightHookX - " + rightHookX);
            Map<Integer, Integer> triangleInterval = givenPts.subMap(leftHookX, rightHookX);
//            Map.Entry<Integer, Integer> extremumPt = variableOper(
            TreeMap<Integer, Integer> extremumPts = variableOper(
                    condTransform(triangleInterval.entrySet().stream(),
                            (x) -> top == 1,
                            (s) -> s.min(Comparator.comparingInt(e -> e.getValue())).orElseThrow(() -> new RuntimeException("Exception here")),
                            (s) -> s.max(Comparator.comparingInt(e -> e.getValue())).orElseThrow(() -> new RuntimeException("Exception here"))
                    ),
                    (x) -> toMap(
                            triangleInterval.entrySet().stream().filter(e -> e.getValue().equals(x.getValue())),
                            TreeMap::new
                    ));
            Map.Entry<Integer, Integer> extremumPt = Math.abs(y0 - yf) < 5 ? getMiddleEntry(extremumPts) : (y0 < yf ? extremumPts.lastEntry() : extremumPts.firstEntry());
            System.out.println("triangle extremum at (" + extremumPt.getKey() + ", " + extremumPt.getValue() + ")");
            double estimatedTriangleArea = (double) ((rightHookX - leftHookX) * Math.abs((extremumPt.getValue() - hookY))) / (double) 2;
            int actualArea = givenPts.entrySet().stream().filter(e -> !(e.getKey() < leftHookX || e.getKey() > rightHookX))
                    .mapToInt(e -> Math.abs(e.getValue() - hookY)).sum();
            System.out.println("estimated triangle area: " + estimatedTriangleArea);
            System.out.println("actual area: " + actualArea);

            if (estimatedTriangleArea > 0 && actualArea > 0) {
                queryAccuracies.put(Math.abs(actualArea - estimatedTriangleArea) / actualArea, "triangle");
            }
            
            HashMap<String, Integer> triangleFitData = new HashMap<>();
            triangleFitData.put("extremumKey", extremumPt.getKey());
            triangleFitData.put("extremumVal", extremumPt.getValue());
            fitData.put("triangle", triangleFitData);
            
            if (Math.abs(actualArea - estimatedTriangleArea) < Math.abs(actualArea - (0.85*actualArea))
                    && estimatedTriangleArea > 0) {
//                System.out.println("using triangle area");
//                return "triangle";

            } else {
                throw new RuntimeException("Not using triangle area");
            }
        } catch (Exception e) {
            System.out.println("skipping triangle area");
//                throw new RuntimeException("Oops what the actual skibidi?\n\n\n\n" + e.getMessage());
        }

        try {

            int tolConstant = 5;
            int hookX = -1;
            int hypEndpointX = -1;
            int hypEndpointX2 = -1;
            boolean hyp1L = true;
            int opHookX = -1;
            if (top == 1) {
                if (y0 > yf) {
                    hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < tolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    hypEndpointX = x0;
                    hypEndpointX2 = xf;
                } else {
                    hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < tolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    hypEndpointX = xf;
                    hypEndpointX2 = x0;
                    hyp1L = false;
                }
            } else {
                if (y0 > yf) {
                    hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < tolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    hypEndpointX = x0;
                    hypEndpointX2 = xf;
                } else {
                    hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < tolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    hypEndpointX = xf;
                    hypEndpointX2 = x0;
                    hyp1L = false;
                }
            }

            if (top == 1) {
                if (y0 > yf) {
                    opHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < tolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                } else {
                    opHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < tolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                }
            } else {
                if (y0 > yf) {
                    opHookX = givenPts.descendingMap().entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < tolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                } else {
                    opHookX = givenPts.descendingMap().entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < tolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                }
            }

            final int reducedTolConstant = 2;
            if (Math.abs(opHookX - hookX) > 2) {
                throw new RuntimeException("Not a seg");
            }

            if (Math.abs(hookX - hypEndpointX) < 5 || Math.abs(hookX - hypEndpointX2) < 5) {
                if (top == 1) {
                    if (y0 > yf) {
                        hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < reducedTolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                        hypEndpointX = x0;
                        hypEndpointX2 = xf;
                    } else {
                        hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < reducedTolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                        hypEndpointX = xf;
                        hypEndpointX2 = x0;
                        hyp1L = false;
                    }
                } else {
                    if (y0 > yf) {
                        hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < reducedTolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                        hypEndpointX = x0;
                        hypEndpointX2 = xf;
                    } else {
                        hookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < reducedTolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                        hypEndpointX = xf;
                        hypEndpointX2 = x0;
                        hyp1L = false;
                    }
                }

                if (top == 1) {
                    if (y0 > yf) {
                        opHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < reducedTolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    } else {
                        opHookX = givenPts.entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < reducedTolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    }
                } else {
                    if (y0 > yf) {
                        opHookX = givenPts.descendingMap().entrySet().stream().filter(e -> Math.abs(e.getValue() - y0) < reducedTolConstant).findFirst().orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    } else {
                        opHookX = givenPts.descendingMap().entrySet().stream().filter(e -> Math.abs(e.getValue() - yf) < reducedTolConstant).reduce((a, b) -> b).orElseThrow(() -> new RuntimeException("Exception here")).getKey();
                    }
                }
            }

            double cL = top == 1 ? y0 < yf ? 0 : (double) (givenPts.get(hookX) -  y0) / (double) (hookX - x0) : y0 < yf ? (double) (givenPts.get(hookX) -  y0) / (double) (hookX - x0) : 0;
            double cR = top == 1 ? y0 < yf ? (double) (yf - givenPts.get(hookX)) / (double) (xf - hookX) : 0 : y0 < yf ? 0 : (double) (yf - givenPts.get(hookX)) / (double) (xf - hookX);
            double gAcc0 = getInterpolationAccuracy(givenPts, hypEndpointX, hookX, hyp1L ? cL : cR);
            double gAcc1 = getInterpolationAccuracy(givenPts, hookX, hypEndpointX2, hyp1L ? cR : cL);
            double gAcc2 = getInterpolationAccuracy(givenPts, hypEndpointX, opHookX, hyp1L ? cL : cR);
            double gAcc3 = getInterpolationAccuracy(givenPts, opHookX, hypEndpointX2, hyp1L ? cR : cL);
            if (Math.abs(Math.abs(gAcc0 - gAcc2) - Math.abs(gAcc1 - gAcc3)) == 0) {
                if (cR == 0d) {
                    queryAccuracies.put(Math.min(gAcc1, gAcc3), "seg");
                } else if (cL == 0d) {
                    queryAccuracies.put(Math.min(gAcc0, gAcc2), "seg");
                } else {
                    throw new Exception("What is even happening???");
                }
            } else {
            }
//            queryAccuracies.put(Math.abs(Math.abs(gAcc0 - gAcc2) - Math.abs(gAcc1 - gAcc3)), "seg");
            HashMap<String, Integer> segFitData = new HashMap<>();
            segFitData.put("hookX", hookX);
            fitData.put("seg", segFitData);

            // NOTE FOR ELIJAH (12/13/2024 9:39:28 PM):
            // remember the seg and its geometrical behavior? yeah, well, do these following steps:
            // one: calculate the actual hypotenuse first on the respective side with distance formula
            // two: then draw the imaginary base and height and calculate that hypotenuse then check similarity for safety
            // third: attempt to draw it on the other side. if there is no exception thrown, then stop. otherwise,
            // when both checks are met (i dont see how the first one would be met as of yet), then it can be
            // concluded that it is a seg
            /*
            double estimatedHypot = Math.hypot(hypEndpointX - hookX, givenPts.get(hypEndpointX) - givenPts.get(hookX));
            interpolatePts(condTransform(givenPts, hookX < hypEndpointX, (gPts) -> (TreeMap<Integer, Integer>) gPts.descendingMap()), hookX, givenPts.get(hookX));*/

        } catch (Exception e) {
            System.out.println("skipping seg");
        }

        Map<String, Integer> shapePrios = new HashMap<>();
        shapePrios.put("line", 3);
        shapePrios.put("triangle", 2);
        shapePrios.put("seg", 1);
        shapePrios.put("trapezoid", 0);

        
        double lineAcc = getInterpolationAccuracy(givenPts, x0, true, xf, true);
        queryAccuracies.put(lineAcc, "line");
        Map<String, Double> queryLabels = queryAccuracies.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        String bestFit = queryAccuracies.firstEntry().getValue();
        double bestAcc = queryAccuracies.firstEntry().getKey();
        final double threshold = 1.25;
        AbstractMap.SimpleEntry<String, Map<String, Integer>> newBestFit = new AbstractMap.SimpleEntry<>("line", fitData.get("line"));
        if (!bestFit.equals("line")) {
            if (queryLabels.containsKey("triangle") && Math.abs(bestAcc - queryLabels.get("triangle")) < threshold) {
                newBestFit = new AbstractMap.SimpleEntry<>("triangle", fitData.get("triangle"));
            }
            else if (Math.abs(bestAcc - lineAcc) < threshold) {
                newBestFit = new AbstractMap.SimpleEntry<>("line", fitData.get("line"));
            }
            else if (queryLabels.containsKey("seg") && Math.abs(bestAcc - queryLabels.get("seg")) < threshold) {
                newBestFit = new AbstractMap.SimpleEntry<>("seg", fitData.get("seg"));
            }
            else if (queryLabels.containsKey("trapezoid") && Math.abs(bestAcc - queryLabels.get("trapezoid")) < threshold) {
                newBestFit = new AbstractMap.SimpleEntry<>("trapezoid", fitData.get("trapezoid"));
            }
        }
        if (newBestFit == null || queryLabels.get(newBestFit.getKey()) > 5) {
            return new AbstractMap.SimpleEntry<>("none", null);
        }

        return new AbstractMap.SimpleEntry<>(queryAccuracies.firstEntry().getValue(), fitData.get(queryAccuracies.firstEntry().getValue()));
    }








    private static <T> T condReturn(T t, Function<T, Boolean> cond, T otherVal) {
        return cond.apply(t) ? otherVal : t;
    }
    private static <T> T condReturn(T t, Function<T, Boolean> cond, T condVal, T otherVal) {
        return cond.apply(t) ? condVal : otherVal;
    }


    private static <T, V> V condTransform(T t, Function<T, Boolean> cond, Function<T, V> condVal, Function<T, V> otherVal) {
        return cond.apply(t) ? condVal.apply(t) : otherVal.apply(t);
    }
    private static <T> T condTransform(T t, Function<T, Boolean> cond, Function<T, T> otherApply) {
        return cond.apply(t) ? otherApply.apply(t) : t;
    }
    private static <T> T condTransform(T t, boolean cond, Function<T, T> otherApply) {
        return cond ? otherApply.apply(t) : t;
    }

    private static <T> void condApply(T t, Function<T, Boolean> cond, Consumer<T> fn) {
        if (cond.apply(t)) fn.accept(t);
    }
    private static <T> void condApply(T t, boolean cond, Consumer<T> fn) {
        condApply(t, (a) -> cond, fn);
    }

    private static <T, V> V variableOper(T t, Function<T, V> fn) {
        return fn.apply(t);
    }

    
    private static <K, V> void listPut(Map<K, List<V>> map, K key, V value, boolean unique) {
        List<V> hereList = new ArrayList<>(map.getOrDefault(key, new ArrayList<>()));
        if (unique && hereList.contains(value)) {
            return;
        }
        hereList.add(value);
        map.put(key, hereList);
    }
    private static <K, V> void listPut(Map<K, List<V>> map, K key, V value) {
        listPut(map, key, value, true);
    }
    private static <K, V> void listPut(Map<K, List<V>> map, K key, List<V> value, boolean unique) {
        value.forEach(val -> listPut(map, key, val, unique));
    }
    private static <K, V> void listPut(Map<K, List<V>> map, K key, List<V> value) {
        listPut(map, key, value, true);
    }


    public static <K, V, T extends Map<K, V>> T toMap(Stream<Map.Entry<K, V>> stream, BinaryOperator<V> mergeFunction, Supplier<T> mapFactory) {
        return stream.collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                mergeFunction,
                mapFactory
        ));
    }

    public static <K, V, T extends Map<K, V>> T toMap(Stream<Map.Entry<K, V>> stream, Supplier<T> mapFactory) {
        return toMap(stream, (a, b) -> a, mapFactory);
    }



    public static int getSize() {
        return size;
    }




}
