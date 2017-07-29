package info.quadtree.ld39;

public class TilePos {
	public static TilePos create(int x, int y) {
		return new TilePos(x, y);
	}

	public final int x, y;

	public TilePos(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TilePos) {
			return ((TilePos) obj).x == x && ((TilePos) obj).y == y;
		}

		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "TilePos [x=" + x + ", y=" + y + "]";
	}

}
