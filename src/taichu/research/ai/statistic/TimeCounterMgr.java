package taichu.research.ai.statistic;

import java.util.HashMap;

public class TimeCounterMgr {

	private static TimeCounterMgr instance;

	private static final HashMap<String, TimeCounter> timeCounterMap = new HashMap<String, TimeCounter>();

	public static TimeCounterMgr I() {
		if (instance != null) {
			return instance;
		} else {
			instance = new TimeCounterMgr();
			return instance;
		}
	}

	/**
	 * StartTimeCount必须和EndTimeCounter成对使用，不成对使用的时候，遵循下面规律。
	 * EndXXX找不到StartXXX将被废弃； EndXXX前有多个StartXXX的，仅最后一个StartXXX有效，其余被废弃。
	 * 
	 * @param flag
	 *            用于时间统计的归类标记，相同的flag被统计在一起！
	 * @throws TimeCounterException
	 */
	public static void StartTimeCounter(String flag)
			throws TimeCounterException {
		// 用flag对应到内部hash数据，没有就插入新的node
		if (timeCounterMap.containsKey(flag)) {
			// 存在计数器
			TimeCounter tc = timeCounterMap.get(flag);
			if (tc.NeedEnd()) {
				throw new TimeCounterException(
						TimeCounterException.WAIT_FOR_END);
			} else {
				tc.PushStartTime(System.currentTimeMillis());
			}
		} else {
			// 新建计数器
			TimeCounter tc = new TimeCounter();
			tc.PushStartTime(System.currentTimeMillis());
			timeCounterMap.put(flag, tc);
		}
		// 将当前时间设置到flag对应的node

	}

	/**
	 * StartTimeCount必须和EndTimeCounter成对使用，不成对使用的时候，遵循下面规律。
	 * EndXXX找不到StartXXX将被废弃； EndXXX前有多个StartXXX的，仅最后一个StartXXX有效，其余被废弃。
	 * 
	 * @param flag
	 *            用于时间统计的归类标记，相同的flag被统计在一起！
	 * @throws TimeCounterException
	 */
	public static void EndTimeCounter(String flag) throws TimeCounterException {
		// 用flag对应到内部hash数据，没有就插入新的node
		if (timeCounterMap.containsKey(flag)) {
			// 存在计数器
			TimeCounter tc = timeCounterMap.get(flag);
			tc.PushEndTime(System.currentTimeMillis());
		} else {
			throw new TimeCounterException(TimeCounterException.WAIT_FOR_START);

		}
		// 将当前时间设置到flag对应的node

	}

	/**
	 * @param flag
	 *            ： 通过flag找到对应timecounter并返回
	 * @return
	 */
	public static TimeCounter GetTimeCounterByClone(String flag) {
		return (TimeCounter) timeCounterMap.get(flag).clone();

	}

}
