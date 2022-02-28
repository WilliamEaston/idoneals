import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Arrays;

/*
   This is a program to check the numbers up to a desired N for idoneality.
   That is, whether they are one of Euler's idoneal numbers.
   So far, it has checked up to 35 trillion.

   Made by William Easton, December 2021 - February 2022
*/
public class IdonealSearch {

    private static final long N = 10000000000000L;  // the number to check up to

    private static final double sqrtMaxLong = Math.sqrt(Long.MAX_VALUE);
    private static final int[] primes = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23};
    private static final int[] witnesses = getWitnesses();

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
            if (areCoprime(k, n) && !meetsParams(n + k * k))
                return false;
        return true;
    }

    // Checks if a certain value is prime, twice a prime, a prime squared, or a power of 2
    public static boolean meetsParams(long test) {
        return isPrime(test) ||
               isPrime(test / 2.0) ||
               isPrime(Math.sqrt(test)) ||
               Long.bitCount(test) == 1;
    }

    // Checks if n is prime via deterministic Miller test
    public static boolean isPrime(long n) {
        if (n == 1) return true;
        for (int p : primes) {  // check through some low primes
            if (n % p == 0) {
                return n == p;
            }
        }

        // write n as d*2^r + 1
        int r = Long.numberOfTrailingZeros(n - 1);
        long d = (n - 1) >> r;

        WitnessLoop:
        for (int witness : witnesses) {
            if (witness > n - 2)
                break;
            long x = modPow(witness, d, n);   // check if b^d = 1 mod n
            if (x == 1 || x == n - 1)
                continue;
            for (int i = 1; i < r; i++) {
                x = x < sqrtMaxLong
                        ? (x * x) % n
                        : squarePow(x, n);
                if (x == n - 1)             // check if b^(d*2^i) = -1 mod n
                    continue WitnessLoop;
            }
            return false;
        }
        return true;
    }

    // Calculates base^power % mod for numbers that won't overflow longs
    public static long modPow(long base, long power, long mod) {
        long result = base;
        for (long l = Long.highestOneBit(power) >> 1; l != 0; l >>= 1) {
            result = (result * result) % mod;
            if ((power & l) != 0)
                result *= base;
        }
        return result % mod;
    }

    // Calculates base^2 % mod for especially large numbers that would overflow longs
    public static long squarePow(long base, long mod) {
        return BigInteger.valueOf(base).pow(2).mod(BigInteger.valueOf(mod)).longValue();
    }

    // Returns whether the two inputs are coprime
    public static boolean areCoprime(long a, long b) {
        long t;
        while (b != 0) {
            t = b;
            b = a % b;
            a = t;
        }
        return a == 1;
    }

    // Returns the list of sufficient witnesses for all possible primes considering N
    public static int[] getWitnesses() {
        long maxPrime = 4 * N;
        long[] upperBounds = new long[] {2047, 1373653L, 25326001L, 3215031751L, 2152302898747L, 3474749660383L, 341550071728321L, 341550071728321L, 3825123056546413051L};
        for (int i = 0; i < primes.length; i++)
            if (maxPrime < upperBounds[i])
                return Arrays.copyOf(primes, i + 1);
        return primes;
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
