package provided.entity;

import java.math.BigDecimal;
import java.util.Objects;


public abstract class EntityBase {
  
   //This converts from a decimal amount to a fractional amount
  protected String toFraction(BigDecimal value) {
    String result = "";
    Double amount = Objects.isNull(value) ? null : value.doubleValue();

    if (Objects.nonNull(amount) && amount > 0.0) {
      int wholePart = Double.valueOf(Math.floor(amount)).intValue();
      double fractionalPart = amount - wholePart;
      Factor twoFactor = findFactor(fractionalPart, 16, 2);
      Factor threeFactor = findFactor(fractionalPart, 15, 5);

      
       //Pick the factor to use. This just picks the factor with the lowest value.
      Factor factor =
          twoFactor.factor < threeFactor.factor ? twoFactor : threeFactor;

      /*
       * Only use the whole part if it's greater than zero. Otherwise this would
       * generate values like "0 1/2" instead of "1/2".
       */
      if (wholePart > 0) {
        result += Integer.valueOf(wholePart).toString();
      }

      /* If there is a fractional part, finish the result. */
      if (factor.num != 0) {
        /*
         * If the result is not empty, add a space. So: "2" becomes "2 ". This
         * allows the fraction to be spaced properly.
         */
        if (!result.isEmpty()) {
          result += " ";
        }

        /* Now, add the fractional part onto the result. */
        result += factor;
      }

      result += " ";
    }

    return result;
  }

  
   // Find the closest match given the factor and divisor.
  private Factor findFactor(double fractionalPart, int factor, int divisor) {
    
    int num = Double.valueOf(Math.round(fractionalPart * factor)).intValue();

    
    while (num != 0 && num % divisor == 0 && factor % divisor == 0) {
      num /= divisor;
      factor /= divisor;
    }

    return new Factor(num, factor);
  }

  
  private static class Factor {
    int num;
    int factor;

    Factor(int num, int factor) {
      this.num = num;
      this.factor = factor;
    }

    @Override
    public String toString() {
      return num + "/" + factor;
    }
  }
}
