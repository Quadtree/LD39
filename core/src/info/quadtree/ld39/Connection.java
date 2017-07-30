package info.quadtree.ld39;

public class Connection implements Comparable<Connection> {
	public final int priority;
	public final double retained;
	public final Building sink;
	public final Building source;

	public Connection(double retained, Building source, Building sink, int priority) {
		super();
		this.retained = retained;
		this.source = source;
		this.sink = sink;
		this.priority = priority;
	}

	@Override
	public int compareTo(Connection o) {

		if (o.priority != this.priority)
			return this.priority - o.priority;

		return (int) (o.retained * 10000 - this.retained * 10000);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Connection)
			return (int) (this.retained * 10000) == (int) (((Connection) (obj)).retained * 10000);
		return super.equals(obj);
	}

	public void execute() {
		double transfer = ((sink.getMaxPower() - sink.power) / retained);
		transfer = Math.min(transfer, source.power);

		if (transfer > 0.1) {
			source.power -= transfer;
			sink.power += transfer * retained;

			LD39.s.gs.lastFramePowerWasted += (1 - retained) * transfer;

			// System.out.println(this + " " + transfer);
		}
	}

	@Override
	public String toString() {
		return "Connection [retained=" + retained + ", sink=" + sink + ", source=" + source + "]";
	}
}
