<script setup>
import { defineProps, defineEmits, watch, ref } from 'vue';
import useCalendarModal from '@/composables/calendarModal';
import { calendarDailyListApi } from '@/api/calendarApi';

const props = defineProps({
	visible: Boolean,
	selectedDate: String,
});

const emits = defineEmits(['close']);

const dailyList = ref([]);

const getDailyList = async () => {
	try {
		const data = await calendarDailyListApi(props.selectedDate);
		if (!data) {
			return;
		}
		dailyList.value = data.result;
	} catch (error) {}
};

watch(
	() => props.selectedDate,
	() => getDailyList(),
);

const close = () => {
	emits('close');
};

const {
	currentPhotoExpanded,
	prevPhoto,
	nextPhoto,
	getPrevPhoto,
	getCurrentPhoto,
	getNextPhoto,
	toggleCurrentPhoto,
	getPhotoClass,
} = useCalendarModal(dailyList);
</script>

<template>
	<div
		v-if="visible"
		class="modal-overlay"
		@click="close"
	>
		<div
			class="modal-content"
			@click.stop
		>
			<div class="modal-header">
				<button
					class="close-button"
					@click="close"
				>
					<img
						src="@/assets/img/common/close.png"
						alt="Close"
					/>
				</button>
				<div>
					{{ selectedDate }}
				</div>
			</div>
			<div class="modal-body">
				<div
					v-if="dailyList.length > 0"
					class="photo-container"
				>
					<div
						class="photo prev-photo"
						v-if="dailyList.length > 2"
						:class="getPhotoClass(getPrevPhoto())"
					>
						<div
							class="photo-background"
							:class="getPhotoClass(getPrevPhoto())"
						>
							<img
								:src="getPrevPhoto().photoUrl"
								alt="Previous Photo"
							/>
						</div>
					</div>
					<div
						class="photo current-photo"
						:class="[getPhotoClass(getCurrentPhoto()), { expanded: currentPhotoExpanded }]"
						@click="toggleCurrentPhoto"
					>
						<div
							class="photo-background"
							:class="getPhotoClass(getCurrentPhoto())"
						>
							<img
								:src="getCurrentPhoto().photoUrl"
								alt="Current Photo"
							/>
							<button
								class="nav-button share-button"
								:class="getPhotoClass(getCurrentPhoto())"
							>
								<img
									src="@/assets/img/calendar/share.png"
									alt="Share"
								/>
							</button>
						</div>
					</div>
					<div
						class="photo next-photo"
						v-if="dailyList.length > 1"
						:class="getPhotoClass(getNextPhoto())"
					>
						<div
							class="photo-background"
							:class="getPhotoClass(getNextPhoto())"
						>
							<img
								:src="getNextPhoto().photoUrl"
								alt="Next Photo"
							/>
						</div>
					</div>
					<div class="nav-buttons">
						<button
							class="nav-button prev-button"
							:class="{ expanded: currentPhotoExpanded }"
							@click="prevPhoto"
						>
							<img
								src="@/assets/img/calendar/arrow-left.png"
								alt="Previous"
							/>
						</button>
						<button
							class="nav-button next-button"
							:class="{ expanded: currentPhotoExpanded }"
							@click="nextPhoto"
						>
							<img
								src="@/assets/img/calendar/arrow-right.png"
								alt="Next"
							/>
						</button>
					</div>
				</div>
				<div
					v-else
					class="no-dailyList"
				>
					해당 날짜에는 사진이 없습니다.
				</div>
			</div>
		</div>
	</div>
</template>

<style scoped>
@import '@/assets/css/calendarModal.css';

.no-dailyList {
	display: flex;
	justify-content: center;
	align-items: center;
	height: 100%;
	font-size: 1.5rem;
	color: red;
}
</style>
