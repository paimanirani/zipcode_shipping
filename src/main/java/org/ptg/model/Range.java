package org.ptg.model;

import java.util.Objects;

public class Range {
   private int lower;
   private int upper;

   /**
    * It makes sure the values are set to the right variable. (lower must be less than or equal to upper)
    *
    * @param lower lower bound
    * @param upper upper bound
    * @return A instance of *Range* class with lower and upper bound values.
    */
   public Range setRange(final int lower, final int upper) {
      int value = Integer.compare(lower, upper);
      if (value < 0) {
         this.lower = lower;
         this.upper = upper;
      }
      else if (value > 0) {
         this.lower = upper;
         this.upper = lower;
      }
      else {
         this.lower = lower;
         this.upper = lower;
      }
      return this;
   }

   public int getLower() {
      return lower;
   }

   public int getUpper() {
      return upper;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Range range = (Range) o;
      return lower == range.lower &&
                upper == range.upper;
   }

   @Override
   public int hashCode() {

      return Objects.hash(lower, upper);
   }

   @Override
   public String toString() {
      return "Range{" +
                "lower=" + lower +
                ", upper=" + upper +
                "}";
   }
}
