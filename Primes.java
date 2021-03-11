//ben mygatt (c) 2020

import java.util.stream.IntStream;
import java.util.ArrayList;


public class Primes {

  /*
  * method for generating an infinite stream of primes; prints 1e6 primes in a few seconds
  */
  public static IntStream stream() {
    ArrayList<Integer> p = new ArrayList<Integer>();
    p.add(2);
    return IntStream.iterate(2,i -> i + 1).filter(n -> isPrime(n));
  }

  /*
  * tests whether n is prime by comparing its divisibility by all numbers less than sqrt(n)
  */
  public static boolean isPrime(int n) {
    if (n==2) {return true;}
    if (n%2 == 0) {return false;}
    for (int i = 3; i <= Math.sqrt(n); i += 2) {
      if (n%i ==0) {return false;}
    }
    return true;
  }

  public static void main(String args[]) {
    stream().forEach(s -> System.out.println(s));
  }
}
