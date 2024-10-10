<script setup>
import Page from '@/components/common/PageComp.vue';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import ListModal from '@/components/calendar/ListModalComp.vue';
import { onMounted, ref } from 'vue';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { calendarMonthlyCountApi } from '@/api/calendarApi';
import { formatDate } from '@/assets/js/date';
import { alertResult } from '@/api/baseApi';

const monthlyCount = ref({});
const attributes = ref([]);
const isModalOpen = ref(false);
const selectedDate = ref('');

onMounted(async () => {
	await getMonthlyCount();
	updateAttributes();
});

const getMonthlyCount = async () => {
	const now = new Date();
	const year = now.getFullYear();
	const month = now.getMonth() + 1;
	const endDate = new Date(year, month, 0).getDate();

	const { data } = await calendarMonthlyCountApi(year, month, endDate);
	if (!data.isSuccess) {
		await alertResult(false, '조회에 실패하였습니다.');
		return;
	}
	data.result.forEach((count, index) => {
		if (count) {
			monthlyCount.value[`${year}-${month}-${index + 1}`] = {
				count,
				dot: getRandomColor(),
			};
		}
	});
};

const updateAttributes = () => {
	attributes.value = Object.keys(monthlyCount.value).map((date) => {
		const attribute = {
			dates: new Date(date),
			dot: monthlyCount.value[date].dot,
			popover: {
				label: `${monthlyCount.value[date].count}개의 사진`,
				placement: 'top',
				hideIndicator: true,
			},
		};
		return attribute;
	});
};

const getRandomColor = () => {
	const colors = ['green', 'red', 'blue', 'yellow'];
	return colors[Math.floor(Math.random() * colors.length)];
};

const changeToModalDate = (date) => {
	if (typeof date === 'string') {
		date = new Date(date);
	} else if (date instanceof Object && date.hasOwnProperty('date')) {
		date = new Date(date.date);
	}
	const year = date.getFullYear();
	const month = date.getMonth() + 1;
	const day = date.getDate();
	return formatDate(year, month, day);
};

const openModal = (date) => {
	selectedDate.value = changeToModalDate(date);
	isModalOpen.value = true;
};

const closeModal = () => {
	isModalOpen.value = false;
};

const formatDatePopOver = (date) => {
	return format(date, 'M월 d일, EEEE', { locale: ko });
};
</script>

<template>
	<Page>
		<WhiteBoardComp class="whiteboard-area-calendar">
			<div class="name-area">추억 저장소</div>
			<div class="calendar-area">
				<div class="calendar">
					<v-calendar
						class="my-calendar"
						transparent
						borderless
						expanded
						:attributes="attributes"
						:masks="{ title: 'YYYY MMM' }"
						@dayclick="openModal"
					>
						<template #day-popover="{ day, dayTitle, attributes }">
							<div class="vc-day-popover-container">
								<div class="vc-day-popover-header">
									{{ formatDatePopOver(day.date) }}
								</div>
								<div
									class="vc-day-popover-row"
									v-for="attribute in attributes"
									:key="attribute.key"
								>
									{{ attribute.popover.label }}
								</div>
							</div>
						</template>
					</v-calendar>
				</div>
			</div>
		</WhiteBoardComp>

		<ListModal
			v-if="isModalOpen"
			:selectedDate="selectedDate"
			@close="closeModal"
		/>
	</Page>
</template>

<style scoped>
.name-area {
	width: 100%;
	height: 20%;

	display: flex;
	justify-content: center;
	align-items: center;

	border-bottom: 5px solid rgba(0, 0, 0, 0.9);

	color: black;
	font-size: 50px;
	text-shadow: 5px 5px #0000004d;
}

.calendar-area {
	width: 100%;
	height: 80%;

	display: flex;
	justify-content: center;
	align-items: center;
}

.calendar {
	width: 85%;
	height: 85%;

	background-color: #fffef7;

	border: 5px solid rgba(0, 0, 0, 0.8);
	border-radius: 20px;
}

.form-input,
.button-group button,
.close,
.thumnail img,
.heart {
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.my-calendar .vc-weekday-1) {
	color: #6366f1;
}

:deep(.vc-container .vc-weekday-7) {
	color: #6366f1;
}

:deep(.vc-expanded) {
	width: 100%;
	height: 100%;
	border-radius: 20px;
}

:deep(.vc-pane-container) {
	display: grid;
	align-content: space-between;
	align-items: center;
	height: 100%;
}

:deep(.vc-header) {
	margin-top: 0px;
}

:deep(.vc-pane-header-wrapper) {
	height: 20%;
	z-index: 1;
}

:deep(div.vc-pane-header-wrapper > div) {
	height: 100%;
	display: flex;
	justify-content: space-around;
}

:deep(div.vc-header.is-lg > div > button) {
	background-color: #fffef7;
	font-size: 25px;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(button.vc-arrow.vc-prev.vc-focus),
:deep(button.vc-arrow.vc-next.vc-focus) {
	background-color: #fffef7;
	width: 40px;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(button.vc-arrow.vc-prev.vc-focus > svg),
:deep(button.vc-arrow.vc-next.vc-focus > svg) {
	width: 40px;
	height: 40px;
}

:deep(.vc-pane-layout) {
	height: 100%;
	position: absolute;
	top: 0;
	width: 100%;
	z-index: 0;
}

:deep(div.vc-pane-layout > div) {
	height: 100%;
}

:deep(div.vc-pane-layout > div > div.vc-header.is-lg) {
	height: 20%;
}

:deep(.vc-weeks) {
	height: 80%;
	padding: 0px;
	padding-bottom: 20px;

	display: flex;
	flex-direction: column;
}

:deep(.vc-weekdays) {
	height: 16%;

	display: flex;
	justify-content: space-around;
	align-items: center;
}

:deep(.vc-weekday) {
	font-size: 25px;
	cursor: url('@/assets/img/app/pointer.png') 5 5, auto;
}

:deep(.vc-week) {
	height: 16%;
}

:deep(.vc-day-content) {
	cursor: url('@/assets/img/app/pointer.png') 5 5, auto;
}

:deep(div.vc-day-content.vc-focusable.vc-focus.vc-attr) {
	font-size: 20px;
	width: 60%;
	height: 70%;
	display: flex;
	align-items: center;
	justify-content: center;
	cursor: url('@/assets/img/app/hoverCursorIcon.png') 5 5, pointer !important;
}

:deep(.vc-light) {
	--vc-focus-ring: 0 0 0 7px rgba(193, 193, 193, 0.752);
}

:deep(.vc-popover-content-wrapper .vc-day-popover-header) {
	font-weight: bold;
	margin-bottom: 2px;
	padding: 2px;
}

:deep(.vc-popover-content-wrapper .vc-day-popover-row) {
	font-size: 12px;
	padding: 2px;
}
</style>
