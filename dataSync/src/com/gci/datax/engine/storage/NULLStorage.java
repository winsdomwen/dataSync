package com.gci.datax.engine.storage;

import com.gci.datax.common.plugin.Line;

public class NULLStorage extends Storage {
	
	private boolean pushClosed = false;
	
	public boolean init(int lineCapacity, int byteCapacity){
        return !(lineCapacity <= 0 || byteCapacity <= 0);
    }
	@Override
	public boolean push(Line line) {
		if (isPushClosed()) return false;
		getStat().incLineRx(1);
		getStat().incByteRx(line.length());
        return true;
	}
	
	@Override
	public boolean fakePush(int lineLength) {
		getStat().incLineRx(1);
		getStat().incByteRx(lineLength);
		return false;
	}
	@Override
	public Line pull() {
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public boolean isPushClosed(){
		return pushClosed;
	}
	
	@Override
	public void setPushClosed(boolean close){
		pushClosed = close;
	}
	@Override
	public int size() {
		return 0;
	}
	@Override
	public boolean empty() {
		return (size() <= 0);
	}
	@Override
	public int getLineLimit() {
		return 0;
	}
	@Override
	public boolean push(Line[] lines, int size) {
		if (isPushClosed()) return false;
		getStat().incLineRx(lines.length);
        for (Line line : lines)
            getStat().incByteRx(line.length());
		return true;
	}
	@Override
	public int pull(Line[] lines) {
		try {
			Thread.sleep(Integer.MAX_VALUE);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 0;
	}
	@Override
	public String info() {
		return null;
	}
	
}
