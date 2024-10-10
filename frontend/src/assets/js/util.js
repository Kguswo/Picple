export const debounce = (timer, func, delay) => {
	return () => {
		if (timer.value) clearTimeout(timer.value);
		timer.value = setTimeout(() => func(), delay);
	};
};

export const throttle = (lastCall, func, delay) => {
	return () => {
		const now = new Date().getTime();
		if (now - lastCall.value < delay) {
			return;
		}
		lastCall.value = now;
		func();
	};
};
