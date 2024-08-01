<script setup>
import Page from '@/components/common/PageComp.vue';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import ListModal from '@/components/calendar/ListModalComp.vue';
import { ref } from 'vue';
import { allPhotos } from '@/composables/calendarModal';
import { format } from 'date-fns';
import { ko } from 'date-fns/locale';

const attributes = ref([]);

const updateAttributes = () => {
	const datesWithPhotos = {};
	allPhotos.forEach((photo) => {
		if (!datesWithPhotos[photo.date]) {
			datesWithPhotos[photo.date] = { count: 3, dot: getRandomColor() };
		} else {
			datesWithPhotos[photo.date].count += 1;
		}
	});

	attributes.value = Object.keys(datesWithPhotos).map((date) => {
		const attribute = {
			dates: new Date(date),
			dot: datesWithPhotos[date].dot,
			popover: {
				label: `${datesWithPhotos[date].count - 2}개의 사진`,
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

const showModal = ref(false);
const selectedDate = ref('');

const openModal = (date) => {
	console.log('Date clicked:', date);
	if (typeof date === 'string') {
		date = new Date(date);
	} else if (date instanceof Object && date.hasOwnProperty('date')) {
		date = new Date(date.date);
	}

	const year = date.getFullYear();
	const month = date.getMonth() + 1;
	const day = date.getDate();

	selectedDate.value = `${year}-${String(month).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
	showModal.value = true;
};

const closeModal = () => {
	showModal.value = false;
};

const formatDate = (date) => {
	return format(date, 'M월 d일, EEEE', { locale: ko });
};

updateAttributes(); // 초기 로드 시 attributes 설정
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
									{{ formatDate(day.date) }}
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
			:visible="showModal"
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
