import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IdonealSearch {

    public static int A = -1;
    public static int B = -1;
    public static final int N = 10000000;
    public static final Integer[][] factorsList = new Integer[0][];//new Integer[N * 4][];
    private static final long[] squares = new long[(int) Math.sqrt(N)];

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        for (int i = 0; i < squares.length; i++)
            squares[i] = i*i;

        int numIdoneals = 0;
        for (int n = 1; n < N; n++) {
            int maxK = (int)Math.sqrt(n * 3);
            boolean idoneal = true;
            boolean even = n % 2 == 0;
            for (int k = 1; k <= maxK; k += even ? 2 : 1) {
                //int gcd = gcd1(n, k, 0);
                //while (gcd == -1)
                //    gcd = gcd1(A, B, 0);
                if (gcd2(n, k) == 1) {
                    if (!meetsParams(n + k * k)) {
                        idoneal = false;
                        break;
                    }
                }
            }
            if (idoneal) {
                System.out.println(n);
                numIdoneals++;
            }
        }
        System.out.println("\nNum of idoneals found: " + numIdoneals);
        //System.out.println(new Date());
        System.out.println((System.currentTimeMillis() - start) / 1000 + " seconds passed for n < " + N);
    }

    public static int[] getFactorsArray(int n) {
        int sqrt = (int)Math.sqrt(n);
        int[] factors = new int[sqrt*2];
        int numFactors = 0;
        for (double i = 2; i <= sqrt; i++) {
            double div = n / i;
            if (isInt(div)) {
                factors[numFactors] = (int)i;
                factors[numFactors+1] = (int)div;
                numFactors+=2;
            }
        }
        factors[numFactors] = n;
        return Arrays.copyOf(factors, numFactors+1);
    }

    public static ArrayList<Integer> getFactors(int n) {
        ArrayList<Integer> factors = new ArrayList<>();
        for (double i = 2; i <= Math.sqrt(n); i++) {
            double div = n / i;
            if (isInt(div)) {
                factors.add((int)i);
                factors.add((int)div);
            }
        }
        factors.add(n);
        return factors;
    }

    public static boolean meetsParams(int test) {
        return (isPrime(test) ||
                isPrime(test / 2.0) ||
                isInt(Math.log(test) / Math.log(2)) ||
                isPrime(Math.sqrt(test)));
    }

    public static boolean isInt(double in) {
        return in == (int)in;
    }

    public static boolean isPrime(double n) {
        return isInt(n) && getFactors((int)n).size() == 1;
    }

    public static int gcd2(int a, int b) {
        ArrayList<Integer> coFactors = new ArrayList<>();
        for (int i : getFactors(a))
            for (int j : getFactors(b))
                if (i == j)
                    coFactors.add(i);
        if (coFactors.size() == 0)
            return 1;
        return Collections.max(coFactors);
    }

    public static int intSqrt(double d) {
        for (int i = 0; i < squares.length; i++)
            if (squares[i] > d)
                return i-1;
        return squares.length;
    }

    public static Integer[] getFactorsCache(int n) {
        if (factorsList[n] == null) {
            ArrayList<Integer> factors = new ArrayList<>();
            for (double i = 2; i <= Math.sqrt(n); i++) {
                double div = n / i;
                if (isInt(div)) {
                    factors.add((int) i);
                    factors.add((int) div);
                }
            }
            factors.add(n);
            factorsList[n] = factors.toArray(new Integer[0]);
        }
        return factorsList[n];
    }

    public static void isIdonealABC(int n) {
        for(int a = 1; a <= n; a++)
            for(int b = a + 1; b <= n; b++)
                for(int c = b + 1; c <= n; c++)
                    if (a * b + b * c + c * a == n)
                        System.out.println(a + " " + b + " " + c);
    }

    public static boolean isPrime1(double n) {
        if (!isInt(n)) return false;

        // Corner case
        if (n <= 1)
            return false;

        // Check from 2 to n-1
        for (int i = 2; i < Math.sqrt(n); i++)
            if (n % i == 0)
                return false;

        return true;
    }

    public static int gcd(int a, int b) {
        if (a == b) return a;
        for (double i = Math.floor(Math.max(a, b) / 2.0); i > 0; i--) {
            double aDiv = a / i;
            double bDiv = b / i;
            if (isInt(aDiv) && isInt(bDiv)) {
                return (int) i;
            }
        }
        return 1;
    }

    public static int gcd1(int a, int b, int num) {
        num++;
        if (num >= 1000) {
            A = a;
            B = b;
            return -1;
        }
        // Everything divides 0
        if (a == 0 || b == 0)
            return 0;

        // base case
        if (a == b)
            return a;

        // a is greater
        if (a > b)
            return gcd1(a-b, b, num);

        return gcd1(a, b-a, num);
    }
}
