import java.text.NumberFormat;
import java.util.ArrayList;
import java.math.BigInteger;

/* 
   This is a program to check the numbers up to a desired N for idoneality.
   That is, whether or not they are one of Euler's idoneal numbers.
   So far, it has checked up to 100 billion.
   
   Made by William Easton, December 2021 - January 2022
*/
public class IdonealSearch {

    public static final long N = 100000000000L;  // the number to check up to
    public static final double ln2 = Math.log(2);

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        long numIdoneals = 0;
        // check 1 to 418 without coverage, first interval starts at 419
        for (long n = 1; n < 419; n++) {
            if (checkN(n)) {
                System.out.println(n);
                numIdoneals++;
            }
        }
        // the only values not covered with h's of 4, 5, 6, and 7, and thus an interval of 420
        long[] openings = new long[] {1, 19, 23, 26, 29, 31, 38, 43, 58, 59, 61, 71, 73, 79, 86, 89, 94, 101, 103, 106, 113, 121, 131, 134, 143, 149, 163, 166, 169, 173, 178, 191, 194, 199, 206, 211, 218, 226, 229, 233, 239, 241, 253, 254, 269, 271, 274, 278, 281, 283, 289, 299, 311, 313, 323, 331, 334, 338, 341, 346, 353, 358, 359, 373, 374, 379, 383, 386, 394, 401, 409, 418};
        for (long b = 419; b < N + 420; b += 420) {
            for (long opening : openings) {
                long n = b + opening;
                if (checkN(n)) {
                    System.out.println(n);
                    numIdoneals++;
                }
            }
        }
        System.out.println();
        System.out.println(numIdoneals + " idoneals found up to " + NumberFormat.getInstance().format(N) +
                           " in " + (System.currentTimeMillis() - start) / 1000 + " seconds.");
    }
    
    // Checks if n is idoneal using n + k^2 method
    public static boolean checkN(long n) {
        long maxK = (long)Math.sqrt(n * 3);
        boolean even = n % 2 == 0;
        for (long k = 1; k <= maxK; k += even ? 2 : 1)
            if (gcd(k, n) == 1)
                if (!meetsParams(n + k * k))
                    return false;
        return true;
    }

    // Checks if a certain value is prime, twice a prime, a prime squared, or a power of 2
    public static boolean meetsParams(long test) {
        return isPrime(test) ||
               isPrime(test / 2.0) ||
               isPrime(Math.sqrt(test)) ||
               isInt(Math.log(test) / ln2);
    }

    // Checks if n is prime via deterministic Miller test
    public static boolean isPrime(long n) {
        if (n == 2) return true;
        if (n % 2 == 0 || n == 1) return false;
        // n = d*2^r + 1
        long r = Long.numberOfTrailingZeros(n-1);
        long d = (n - 1) / (long)Math.pow(2, r);
        // Sufficient a's for n < 2,152,302,898,747
        long[] aValues = new long[] {2, 3, 5, 7, 11};
        long maxA = (long)Math.min(n-2, Math.floor(2*Math.pow(Math.log(n), 2)));
        WitnessLoop:
        for (long a : aValues) {
            if (a > maxA) break;
            long x = modPow(a, d, n);
            if (x == 1 || x == n - 1)
                continue WitnessLoop;
            for (long i = 0; i < r - 1; i++) {
                x = modPow(x, 2, n);
                if (x == n - 1)
                    continue WitnessLoop;
            }
            return false;
        }
        return true;
    }

    // Calculates base^power % mod for especially large numbers that would overflow longs
    public static long modPow(long base, long power, long mod) {
        return new BigInteger(base + "").modPow(new BigInteger(power + ""), new BigInteger(mod + "")).longValue();
    }

    // Returns the greatest common denominator of two numbers
    public static long gcd(long a, long b) {
        long max = 1;
        for (long i : getFactors(a))
            if (i > max && b % i == 0)
                max = i;
        return max;
    }

    // Returns all the factors except 1 of the input
    public static ArrayList<Long> getFactors(long n) {
        ArrayList<Long> factors = new ArrayList<>();
        for (double i = 2; i <= Math.sqrt(n); i++) {
            double div = n / i;
            if (isInt(div)) {
                factors.add((long)i);
                factors.add((long)div);
            }
        }
        factors.add(n);
        return factors;
    }

    // Checks if n is an integer and prime
    public static boolean isPrime(double n) {
        return isInt(n) && isPrime((long)n);
    }

    // Checks if a number is an integer
    public static boolean isInt(double in) {
        return in == (int)in;
    }
}
