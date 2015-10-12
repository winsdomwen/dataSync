package com.gci.datax.engine.storage;

import com.gci.datax.common.plugin.Line;

public class RouteStorage extends Storage {

	@Override
	public String info() {
		return null;
	}

	@Override
	public boolean push(Line line) {
		return false;
	}

	@Override
	public boolean push(Line[] lines, int size) {
		return false;
	}

	@Override
	public boolean fakePush(int lineLength) {
		return false;
	}

	@Override
	public Line pull() {
		return null;
	}

	@Override
	public int pull(Line[] lines) {
		return 0;
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public boolean empty() {
		return false;
	}

	@Override
	public int getLineLimit() {
		return 0;
	}

}
