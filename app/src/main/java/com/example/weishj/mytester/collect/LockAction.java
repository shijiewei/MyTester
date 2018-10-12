package com.example.weishj.mytester.collect;

import com.mob.tools.utils.FileLocker;

public interface LockAction {
	
	public boolean run(FileLocker lock);
	
}
