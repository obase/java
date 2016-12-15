package test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class CreateRndInteger {

	/**
	 * 王海平
	 */
	public static void main(String[] args) {

		System.out.println(2L << 31 - 1);
		// TODO 自动生成的方法存根
		try {
			System.out.println("随机产生一个长整数!!");
			System.out.println("请输入一个1到63中间的任意一个整数：");
			BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
			int num1 = Integer.parseInt(br1.readLine());
			long RndInteger;
			if (num1 >= 0 || num1 < 64) {
				for (int i = 0;; i++) {
					RndInteger = CreateRndInteger(num1);
					if (isPrime1(RndInteger) == 1) {
						break;
					}

				}
				System.out.println("随机产生" + num1 + "bit的质数是：" + RndInteger);

			} else {
				System.out.print("输入的数不再正确的区间");
			}
		} catch (Exception e) {
			System.out.print("输入的不是正确的数据");
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