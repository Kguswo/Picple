<script setup>
import { ref, defineProps, defineEmits, onMounted, inject, watch } from 'vue';

const boothActions = inject('boothActions');

const props = defineProps({
	boothId: String,
	images: Array,
});

const emit = defineEmits(['update']);

const images = ref(props.images || []);

watch(
	() => props.images,
	(newImages) => {
		images.value = newImages || [];
	},
);

onMounted(() => {
	console.log('BoothShowPhoto 호출됨');
});

const showModal = ref(false);
const imgUrl = ref('');

const showImage = (img) => {
	showModal.value = true;
	imgUrl.value = img;
};

const closeModal = () => {
	showModal.value = false;
	imgUrl.value = null;
};
</script>

<template>
	<div class="select-text-box"></div>
	<div class="background-box">
		<div class="background-box-scroll">
			<img
				class="thumbnail"
				v-for="(img, idx) in images"
				:key="idx"
				:src="img"
				@click="showImage(img)"
				alt="myPhoto"
			/>
		</div>
	</div>

	<div
		class="modal"
		v-if="showModal"
	>
		<div class="modal-content">
			<div class="close-box">
				<span
					class="close"
					@click="closeModal"
					>&times;</span
				>
			</div>
			<div class="modal-img">
				<img
					:src="imgUrl"
					alt=""
				/>
			</div>
		</div>
	</div>
</template>

<style scoped>
.select-text-box {
	display: flex;
	height: 10%;
	width: 90%;
	flex-direction: column;
	align-items: center;
	justify-content: space-evenly;

	.select-btn-type {
		display: flex;
	}
}
.background-box {
	height: 85%;
	width: 90%;
	overflow: hidden;

	.background-box-scroll {
		overflow-y: auto;
		height: 100%;
		display: flex;
		flex-direction: column;
		align-items: center;

		.thumbnail {
			width: auto;
			height: 150px;
			margin: 0 5px;
			cursor: pointer;
			border: 2px solid transparent;
			transition: border 0.3s; /* 테두리 전환 효과 */

			&:hover {
				border: 2px solid red;
			}
		}
		&::-webkit-scrollbar {
			display: none;
		}
	}
}

.modal {
	display: block;
	position: fixed;
	z-index: 1;
	left: 0;
	top: 15%;
	width: 100%;
	height: 100%;
	overflow: auto;
	background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
	background-color: #fefefe;
	margin: 10vh auto;
	padding: 20px;
	border: 1px solid #888;
	width: 40%;
	max-width: 60%;
	height: 60%;
	max-height: 80vh;
	overflow-y: auto;
	border-radius: 10px;
}
.close-box {
	padding-top: 0px;
	padding-bottom: 0px;
	height: 8%;
}
.close {
	color: #aaa;
	float: right;
	font-size: 28px;
	line-height: 28px;
	font-weight: bold;
	height: 28px;
}

.close:hover,
.close:focus {
	color: black;
	text-decoration: none;
	cursor: pointer;
}
.modal-img {
	height: 90%;
	display: flex;
	align-items: center;
	text-align: center;
	justify-content: center;
	flex-wrap: wrap;
	img {
		height: 90%;
		width: 95%;
	}

	.modal-text {
		height: 10%;
		width: 90%;
		display: flex;
		justify-content: space-between;
		text-align: center;
	}
}
</style>
