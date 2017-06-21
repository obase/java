package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import com.github.obase.kit.StringKit;
import com.github.obase.kit.StringKit.Split;

public class CreateRndInteger {

	/**
	 * 王海平
	 */
	public static void main(String[] args) {
		String str = "0b0c0";
		System.out.println(StringKit.split2List(str, "0c", false));
		Split s = Split.wrap(str, '0');
		for (String i = null; (i = s.next()) != null;) {
			System.out.println(">>" + i);
		}
	}

	public static long CreateRndInteger(int n) {
		long max = (long) Math.pow(2, n) - 1;
		long min = (long) Math.pow(2, n - 1);
		Random random = new Random();
		long RndInteger = (long) (min + Math.random() * (max - min + 1));
		return RndInteger;

	}

	public static int isPrime1(long num1) {
		int p = 1;
		if (num1 % 2 == 0) {
			p = 0;
		}
		for (int i = 3; i <= Math.sqrt(num1); i = i + 2) {
			if (num1 % i == 0) {
				p = 0;
				break;
			}
		}
		return p;
	}

}