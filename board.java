package jxr;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

/**
 * 
 * @author 
 *
 */
public class board extends Thread implements iboardAction {

	private int[][] m_data = null;
	private int[][] g_data = null;
	private int x = 0, y = 0;
	private int m_cols = 0, m_rows = 0;

	private Color defaultColor;
	private int fullLine = 0;
	private int[][] curData;
	private int[][] lastData;

	public void setBlockData(int[][] data) {
		this.m_data = data;

	}

	/***
	 * 重置方块状态
	 */
	public void reset() {
		
		if (curData == null) {
			curData = lastData = dataManager.getCurBlock();
		}
		curData = lastData;
		dataManager.updateIndex();
		lastData = dataManager.getNewBlock(false);
		globalManager.getPreviewPanel().updatePreviewUI(lastData);
		this.m_data = curData;
		init(true);
	}

	// 初始或游戏状态
	public void init(boolean reset) {
		this.g_data = globalManager.getGlobalData();

		m_cols = this.m_data[0].length;
		m_rows = this.m_data.length;

		if (reset) {
			x = m_cols / 2 + globalManager.global_cols / 2;
			y = 0;
		}

		// System.out.println(" init --- x=" + x + "  " +
		// m_cols + "  " + m_rows + "  g_cols"+g_cols);
	}

	public board() {
		reset();
		defaultColor = new JLabel().getBackground();
	}
	
	// 向左移动
	@Override
	public void moveLeft() {

		if (canMove(x - 1, y)) {
			x--;
			update();
		}
	}
	
	// 向右移动
	@Override
	public void moveRight() {

		if (canMove(x + 1, y)) {
			x++;
			update();
		}
	}
	
	// 向下移动，主要是通过纵坐标的变换来实现
	@Override
	public void moveDown() {

		if (canMove(x, y + 1)) {
			y++;
			update();
		} else {
			// System.out.println(" move stop");
			updateDb(true);
			update();
			emptyLine();
			reset();
		}
	}

	/**
	 * 这个方法主要是实现随着方块的位置的不断改变，然后重绘方块，给人以
	 * 方块在不断移动的效果，这里通过不断更改方块中签的背景颜色来实现
	 */
	private void update() {

		for (int i = 0; i < globalManager.global_cols; i++) {

			for (int j = 0; j < globalManager.global_rows; j++) {
				JLabel jt = globalManager.getBtList().get(j + "_" + i);
				if (null != jt) {
					jt.setOpaque(true);
					jt.setBackground(defaultColor);
					if (globalManager.getGlobalData()[j][i] == 1) {

						jt.setBackground(Color.BLUE);
					}
				}
			}
		}
		updateDb(false);
	}

	/***
	 * 这个方法主要是实现碰撞检测，就是方块堆叠是否达到顶端边界
	 * 如果到达顶端则停止
	 * @param stop
	 */
	private void updateDb(boolean stop) {
		for (int i = 0; i < m_rows; i++) {

			for (int j = 0; j < m_cols; j++) {
				int dx = y + i - m_rows;
				int dy = x + j - m_cols;
				if (stop) {
					if (m_data[i][j] == 1) {
						if (dy < 0 || dx < 0) {
							isstop = true;
							JOptionPane.showMessageDialog(null, " game over !");
							stopGame();
							return;
						}
						globalManager.getGlobalData()[dx][dy] = 1;
					}
				} else {
					JLabel jt = globalManager.getBtList().get(dx + "_" + dy);
					if (jt == null)
						continue;
					if (m_data[i][j] == 1) {
						jt.setOpaque(true);
						jt.setBackground(Color.BLUE);
					}
				}
			}
		}
	}
	
	/**
	 * 也是碰撞检测，主要是在移动方块时，防止砖块出现移出窗体后，
	 * 还能移动。这里是使用了同步代码
	 */
	@Override
	public boolean canMove(int mx, int my) {
		/*
		 * // System.out.println("x=" + mx + "----> y=" + my);
		 * System.out.println(" move  mx=" + mx + "  " + my + "  x=" + x + " y="
		 * + y + "  m_cols=" + m_cols + " m_rows=" + m_rows);
		 */
		synchronized (this) {

			if (mx - m_cols < 0)
				return false;
			if (mx > globalManager.global_cols)
				return false;

			if (my > globalManager.global_rows)
				return false;
			for (int j = 0; j < m_rows; j++) {
				for (int i = 0; i < m_cols; i++) {
					int d_cols = mx + i - m_cols;
					int d_rows = my + j - m_rows;

					if ((d_cols >= 0) && (d_rows >= 0)
							&& (d_cols < globalManager.global_cols)
							&& (d_rows < globalManager.global_rows)) {

						if ((g_data[d_rows][d_cols] == 1)
								&& (1 == m_data[j][i])) {
							return false;
						}

					}
				}
			}
			return true;
		}

	}

	// 判断一行方块是否被堆满
	private void emptyLine() {
		
		boolean hasFullLine = false;
		ArrayList<int[]> fullLineList = new ArrayList<int[]>();
		// 判断一个横排的方块是否堆满
		for (int i = globalManager.global_rows - 1; i >= 0; i--) {
			boolean isFullLine = true;
			for (int j = 0; j < globalManager.global_cols; j++) {
				// 如果为堆满
				if (g_data[i][j] == 0) {
					isFullLine = false;
					break;
				}
			}
			// 如果已经堆满
			if (isFullLine) {
				hasFullLine = true;
				fullLine++;
				// 分数 * 行数
				globalManager.getScore().setText((fullLine * 10) + "");
				// System.out.println("full line");
			} else {
				fullLineList.add(g_data[i].clone());
			}
		}
		// 如果有满行，
		if (hasFullLine) {
			int zero[] = new int[globalManager.global_rows];
			for (int i = 0; i < globalManager.global_rows; i++)
				zero[i] = 0;
			int len = fullLineList.size();
			// System.out.println(len);

			for (int i = 0; i < globalManager.global_rows; i++) {
				if (i < len)
					g_data[globalManager.global_rows - i - 1] = fullLineList
							.get(i);
				else {
					g_data[globalManager.global_rows - i - 1] = zero.clone();
				}
			}
			// System.out.println("update line");
			update();
			// System.out.println("end  update");
		}
	}

	private boolean isstop = false;

	// 运行程序
	public void run() {
		while (!isstop) {
			try {
				// System.out.println(" runig ... x=" + x + "  y=" + y);
				moveDown();
				// 这里是设置方块向下移动的速度
				Thread.sleep(200);
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	/***
	 * 从新开始游戏
	 */

	public void restart() {
		stopGame();
		fullLine = 0;

		board a = new board();
		a.start();
	}

	/***
	 * 停止游戏
	 */
	public void stopGame() {
		isstop = true;
		// 分数清零
		globalManager.getScore().setText("0");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		// 情况所有堆积的方块
		for (int i = 0; i < globalManager.global_rows; i++) {
			for (int j = 0; j < globalManager.global_cols; j++)
				g_data[i][j] = 0;
		}
		update();
	}
}
