package org.ptg.service;

import org.apache.log4j.Logger;
import org.ptg.model.Range;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Initiator {
   private static final Logger logger = Logger.getLogger(Initiator.class);

   private static List<Range> rangesList = new LinkedList<>();

   //for pattern matching
   private static final String regex = "^\\[\\s*(\\d{1,5})\\s*[,]\\s*(\\d{1,5})\\s*]$";
   private static final Pattern rangePattern = Pattern.compile(regex);

   public static void main(String[] args) {
      try {
         getRanges();
         logger.info("Successfully fetched and Validated !!!");
         System.out.println(rangesList.toString());

         possibleFusion();
         logger.info("Successfully Executed !!!");
         System.out.println(rangesList.toString());

      } catch (IOException e) {
         logger.warn("Unexpected error while reading file", e);
      }
   }

   /**
    * Get ranges from the *txt* file
    *
    * @throws IOException
    */
   private static void getRanges() throws IOException {
      List<String> list = new ArrayList<>();
      //Get file from resources folder
      ClassLoader classLoader = Initiator.class.getClassLoader();
      try (BufferedReader br = new BufferedReader(new FileReader(classLoader.getResource(
         "zip_code_ranges.txt").getFile()))) {
         String line;
         while ((line = br.readLine()) != null) {
            list = Arrays.asList(line.split(" "));
         }
         setRanges(list);
      }
   }

   /**
    * Validate and set Ranges
    *
    * @param list
    */
   private static void setRanges(List<String> list) {
      for (String range : list) {
         Matcher matcher = rangePattern.matcher(range);
         if (matcher.matches()) {
            rangesList.add(new Range().setRange(Integer.valueOf(matcher.group(1)),
                                                Integer.valueOf(matcher.group(2))));
         }
         else {
            logger.warn("Unexpected error: Invalid zipcode input");
            throw new IllegalArgumentException("Invalid zipcode input: " + range);
         }
      }
   }

   /**
    * First the order is sorted based on their lower bounds, and then checked if it's overlapping
    * for fusion to take place.
    * After the fusion is completed, the ith is saved as [1, 7] and the jth node gets removed.
    * Then, the value of *j* is decremented.
    */
   private static void possibleFusion() {
      Comparator<Range> order = (Range r1, Range r2) -> (int) (r1.getLower() - r2.getLower());
      Collections.sort(rangesList, order);
      for (int i = 0; i < rangesList.size() - 1; i++) {
         for (int j = i + 1; j < rangesList.size(); j++) {
            if (isOverlapping(rangesList.get(i), rangesList.get(j))) {
               fusion(rangesList.get(i), rangesList.get(j));
               rangesList.remove(j);
               j--;
            }
         }
      }
   }

   /**
    * Checking the former's upper bound along with the latter's lower bound. If greater, it overlaps.
    * Ex: [1, 5] [2, 7] -> True (overlaps)
    *
    * @param r1
    * @param r2
    * @return A boolean value whether it overlaps or not.
    */
   private static boolean isOverlapping(Range r1, Range r2) {
      if (r1.getUpper() > r2.getLower()) {
         return true;
      }
      return false;
   }

   /**
    * fusion of former's lower bound with latter's upper bound.
    * Ex: [1, 5] [2, 7] -> [1, 7]
    *
    * @param r1
    * @param r2
    */
   private static void fusion(Range r1, Range r2) {
      r1.setRange(r1.getLower(), r2.getUpper());
   }

}
