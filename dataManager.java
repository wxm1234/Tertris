package jxr;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * 这个类主要是为了实现绘制特定形状的方块
 * @author song
 *
 */
public class dataManager {

	static ArrayList<ArrayList<int[][]>> itemList;
	// 这里使用了初始化块
	static {

		ArrayList<int[][]> arrayList = new ArrayList<int[][]>();
		itemList = new ArrayList<ArrayList<int[][]>>();
		int[][] a = { { 1, 1 }, { 1, 1 } };
		arrayList.add(a);
		arrayList.add(a);
		arrayList.add(a);
		arrayList.add(a);
		itemList.add(arrayList);
		
		arrayList = new ArrayList<int[][]>();
		
		// 方块类型1
		int[][] b0 = { { 0, 1, 0 }, { 1, 1, 1 } };
		int[][] b1 = { { 1, 0 }, { 1, 1 }, { 1, 0 } };
		int[][] b2 = { { 1, 1, 1 }, { 0, 1, 0 } };
		int[][] b3 = { { 0, 1 }, { 1, 1 }, { 0, 1 } };
		arrayList.add(b0);
		arrayList.add(b1);
		arrayList.add(b2);
		arrayList.add(b3);
		itemList.add(arrayList);
		
		// 方块类型2
		int[][] c0 = { { 1, 1, 1 } };
		int[][] c1 = { { 1 }, { 1 }, { 1 } };
		arrayList = new ArrayList<int[][]>();
		arrayList.add(c0);
		arrayList.add(c1);
		arrayList.add(c0);
		arrayList.add(c1);
		itemList.add(arrayList);
		
		// 方块类型3
		arrayList = new ArrayList<int[][]>();
		int[][] d1 = { { 1, 0 }, { 1, 0 }, { 1, 1 } };
		int[][] d2 = { { 0, 0, 1 }, { 1, 1, 1 } };
		int[][] d3 = { { 1, 1 }, { 0, 1 }, { 0, 1 } };
		int[][] d4 = { { 1, 1, 1 }, { 1, 0, 0 } };
		arrayList.add(d1);
		arrayList.add(d2);
		arrayList.add(d3);
		arrayList.add(d4);
		itemList.add(arrayList);
		
		arrayList = new ArrayList<int[][]>();
		int[][] e0 = { { 0, 1, 1 }, { 1, 1, 0 } };
		int[][] e1 = { { 1, 0 }, { 1, 1 }, { 0, 1 } };

		arrayList.add(e0);
		arrayList.add(e1);
		arrayList.add(e0);
		arrayList.add(e1);
		itemList.add(arrayList);
	}

	/***
	 * 方块变形的方法
	 */
	public static void reTransformation() {
		boardIndex++;
		if (boardIndex >= 4) {
			boardIndex = 0;
		}
	}

	private static int curIndex, boardIndex, lastIndex;

	public int getCurIndex() {
		return curIndex;
	}

	/***
	 * 获取前砖块
	 * 
	 * @return
	 */
	public static int[][] getCurBlock() {
		return itemList.get(curIndex).get(boardIndex);
	}
	
	/**
	 * 获取一个新方块
	 * @return
	 */
	public static int[][] getNewBlock() {
		return getNewBlock(true);
	}

	/***
	 * 更新方块的位置
	 */
	public static void updateIndex() {
		curIndex = lastIndex;
	}

	// 为了随机获取一个类型的方块
	private static Random random = new Random();
	
	/**
	 * 创建一个新方块
	 * @param isNew 如果传入一个true值表示需要创建一个新方块
	 * @return
	 */
	public static int[][] getNewBlock(boolean isNew) {
		
		// 这里是随机的产生一个方块
		int n = itemList.size();
		random.setSeed(new Date().getTime());
		int index = random.nextInt(n - 1);

		if (isNew) {
			curIndex = index;
		} else {
			lastIndex = index;
		}
		boardIndex = random.nextInt(3);
		return itemList.get(index).get(boardIndex);
	}
}
