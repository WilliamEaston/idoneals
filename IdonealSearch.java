import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

/*
   This is a program to check the numbers up to a desired N for idoneality.
   That is, whether they are one of Euler's idoneal numbers.
   So far, it has checked up to 100 billion.

   Made by William Easton, December 2021 - January 2022
*/
public class IdonealSearch {

    public static final long N = 100000000000L;  // the number to check up to

    public static final double ln2 = Math.log(2);
    public static final double sqrtMaxLong = Math.sqrt(Long.MAX_VALUE);
    public static long[] witnesses = getWitnesses();

    // main w/ coverage checks
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
        System.out.println(numIdoneals + " idoneals found up to " + NumberFormat.getInstance().format(N)
                + " in " + (System.currentTimeMillis() - start) / 1000.0 + " seconds, using "
                + Arrays.toString(witnesses) + " for witnesses.");
    }

    // Checks if n is idoneal using n + k^2 method
    public static boolean checkN(long n) {
        long maxK = (long)Math.sqrt(n * 3);
        boolean even = n % 2 == 0;
        for (long k = 1; k <= maxK; k += even ? 2 : 1)
            if (gcd(k, n) == 1 && !meetsParams(n + k * k))
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

        // write n as d*2^r + 1
        long r = Long.numberOfTrailingZeros(n - 1);
        long d = (n - 1) >> r;

        WitnessLoop:
        for (long witness : witnesses) {
            if (witness > n - 2)
                break;
            long x = modPow(witness, d, n);   // check b^d = 1 mod n
            if (x == 1 || x == n - 1)
                continue WitnessLoop;
            for (long i = 1; i < r ; i++) {
                x = x < sqrtMaxLong ? (x * x) % n : modPow(x, 2, n);    // check b^(d*2^i) = -1 mod n
                if (x == n - 1)
                    continue WitnessLoop;
            }
            return false;
        }
        return true;
    }

    // Calculates base^power % mod for especially large numbers that would overflow longs
    public static long modPow(long base, long power, long mod) {
        return BigInteger.valueOf(base).modPow(BigInteger.valueOf(power), BigInteger.valueOf(mod)).longValue();
    }

    // Returns the greatest common denominator of two numbers
    public static long gcd(long a, long b) {
        long max = 1;
        for (long factor : getFactors(a))
            if (factor > max && b % factor == 0)
                max = factor;
        return max;
    }

    // Returns all the factors of the input except 1
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

    // Returns the list of sufficient witnesses for all possible primes considering N
    public static long[] getWitnesses() {
        long[] primes = new long[] {2, 3, 5, 7, 11, 13, 17, 19};
        long maxPrime = 4*N;
        int numNeededPrimes;
             if (maxPrime < 2047L)             numNeededPrimes = 1;
        else if (maxPrime < 1373653L)          numNeededPrimes = 2;
        else if (maxPrime < 25326001L)         numNeededPrimes = 3;
        else if (maxPrime < 3215031751L)       numNeededPrimes = 4;
        else if (maxPrime < 2152302898747L)    numNeededPrimes = 5;
        else if (maxPrime < 3474749660383L)    numNeededPrimes = 6;
        else if (maxPrime < 341550071728321L)  numNeededPrimes = 7;
        else                                   numNeededPrimes = 8;
        return Arrays.copyOf(primes, numNeededPrimes);
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
