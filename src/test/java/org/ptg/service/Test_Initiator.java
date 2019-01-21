package org.ptg.service;

import org.junit.Assert;
import org.junit.Test;
import org.ptg.model.Range;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Test_Initiator {

   List<Range> actual = new LinkedList<>();
   private String ranges1 = "[94133,94133] [94200,94299] [94600,94699]";
   private String ranges2 = "[94200,94299] [94133,94133] [94600,94699]";
   private String ranges3 = "[94133,94133] [94226,94399] [94200,94299]";
   private String ranges4 = "[94133,94133] [94300,94399] [94200,94299]";

   private String ranges_reverse = "[94133,94133] [94299,94200] [94699,94600]";


   //for pattern matching
   private static final String regex = "^\\[\\s*(\\d{1,5})\\s*[,]\\s*(\\d{1,5})\\s*]$";
   private static final Pattern rangePattern = Pattern.compile(regex);

   @Test
   public void testGetRanges() throws Exception {
      List<String> list = new LinkedList<>();
      //Get file from resources folder
      String file = Test_Initiator.class.getClassLoader().getResource("zip_code_ranges.txt").getFile();
      BufferedReader br = new BufferedReader(new FileReader(file));
      String line = br.readLine();
      Assert.assertEquals(ranges2, line);

   }

   @Test(expected = IllegalArgumentException.class)
   public void testSetRangesNegativeValueError() throws IllegalArgumentException {
      String range_error1 = "[-94133,94133]";
      Matcher matcher = rangePattern.matcher(range_error1);
      if (matcher.matches()) {
         new Range().setRange(Integer.valueOf(matcher.group(1)),
                              Integer.valueOf(matcher.group(2)));
      }
      else {
         throw new IllegalArgumentException("Invalid zipcode input: " + range_error1);
      }
   }

   @Test
   public void testSetRangesReOrder() {
      List<Range> expected = new LinkedList<>();
      expected.add(new Range().setRange(94133, 94133));
      expected.add(new Range().setRange(94200, 94299));
      expected.add(new Range().setRange(94600, 94699));
      Assert.assertEquals(expected, getRangeInstances(ranges_reverse));

   }

   @Test
   public void testOrderedRanges() {
      List<Range> expected = new LinkedList<>();
      expected.add(new Range().setRange(94133, 94133));
      expected.add(new Range().setRange(94200, 94299));
      expected.add(new Range().setRange(94600, 94699));

      actual = getRangeInstances(ranges1);
      possibleFusion();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testUnOrderedRanges() {
      List<Range> expected = new LinkedList<>();
      expected.add(new Range().setRange(94133, 94133));
      expected.add(new Range().setRange(94200, 94299));
      expected.add(new Range().setRange(94600, 94699));

      actual = getRangeInstances(ranges2);
      possibleFusion();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testOverlappedRanges() {
      List<Range> expected = new LinkedList<>();
      expected.add(new Range().setRange(94133, 94133));
      expected.add(new Range().setRange(94200, 94399));

      actual = getRangeInstances(ranges3);
      possibleFusion();
      Assert.assertEquals(expected, actual);
   }

   @Test
   public void testMergeWorthyRanges() {
      List<Range> expected = new LinkedList<>();
      expected.add(new Range().setRange(94133, 94133));
      expected.add(new Range().setRange(94200, 94399));

      actual = getRangeInstances(ranges4);
      possibleFusion();
      Assert.assertEquals(expected, actual);
   }

   public List<Range> getRangeInstances(String value) {
      List<Range> ranges = new LinkedList<>();
      for (String range : Arrays.asList(value.split(" "))) {
         Matcher matcher = rangePattern.matcher(range);
         if (matcher.matches()) {
            ranges.add(new Range().setRange(Integer.valueOf(matcher.group(1)),
                                            Integer.valueOf(matcher.group(2))));
         }
      }
      return ranges;
   }

   private void possibleFusion() {
      Comparator<Range> order = (Range r1, Range r2) -> (int) (r1.getLower() - r2.getLower());
      Collections.sort(actual, order);
      for (int i = 0; i < actual.size() - 1; i++) {
         for (int j = i + 1; j < actual.size(); j++) {
            if (checkFusion(actual.get(i), actual.get(j))) {
               fusion(actual.get(i), actual.get(j));
               actual.remove(j);
               j--;
            }
         }
      }
   }

   private static boolean checkFusion(Range r1, Range r2) {
      if (r1.getUpper() > r2.getLower() || r1.getUpper() == (r2.getLower() - 1)) {
         return true;
      }
      else {
         return false;
      }
   }

   private static void fusion(Range r1, Range r2) {
      r1.setRange(r1.getLower(), r2.getUpper());
   }
}

