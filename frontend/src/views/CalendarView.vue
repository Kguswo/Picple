<script setup>
import Page from '@/components/common/PageComp.vue';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import ListModal from '@/components/calendar/ListModalComp.vue';
import { onMounted, ref } from 'vue';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';
import { calendarMonthlyCountApi } from '@/api/calendarApi';
import { formatDate } from '@/assets/js/date';

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

	try {
		const data = await calendarMonthlyCountApi(year, month, endDate);
		if (!data.isSuccess) {
			await Swal.fire({ icon: 'error', title: '캘린더 조회에 실패하였습니다.', width: 600 });
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
	} catch (error) {}
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
@import '@/assets/css/calendar.css';

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
