export const formatDate = (year, month, day) => {
	return `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
};
