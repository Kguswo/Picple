<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';

const videoElement = ref(null);
let mediaStream = null;

let isMirrored = false;
let isvideoOn = ref(true);
let isMicroOn = ref(true);

const router = useRouter();

const navigateTo = (name) => {
	router.push({ name });
};

onMounted(async () => {
	try {
		mediaStream = await navigator.mediaDevices.getUserMedia({ video: true, audio: true });
		videoElement.value.srcObject = mediaStream;
	} catch (error) {}
});

onUnmounted(() => {
	if (mediaStream) {
		mediaStream.getTracks().forEach((track) => {
			track.stop();
		});
	}
});

const toggleMirror = () => {
	isMirrored = !isMirrored;
	videoElement.value.style.transform = isMirrored ? 'scaleX(-1)' : 'scaleX(1)';
};

const toggleCamera = () => {
	isvideoOn.value = !isvideoOn.value;

	if (isvideoOn.value) {
		mediaStream.getVideoTracks().forEach((track) => {
			track.enabled = true;
		});
		videoElement.value.srcObject = mediaStream;
	} else {
		mediaStream.getVideoTracks().forEach((track) => {
			track.enabled = false;
		});
		videoElement.value.srcObject = mediaStream;
	}
};

const toggleMicro = () => {
	isMicroOn.value = !isMicroOn.value;
	if (isMicroOn.value) {
		mediaStream.getAudioTracks().forEach((track) => {
			track.enabled = true;
		});
	} else {
		mediaStream.getAudioTracks().forEach((track) => {
			track.enabled = false;
		});
	}
};
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="close-btn">
				<button
					class="close"
					@click="navigateTo('main')"
				>
					closeBtn
				</button>
			</div>
			<BoothBack class="booth-create">
				<div class="create-content">
					<div class="mycam-box">
						<div v-show="isvideoOn">
							<video
								ref="videoElement"
								autoplay
							></video>
						</div>
						<div v-if="!isvideoOn">카메라가 꺼져있습니다!</div>
					</div>

					<div class="create-btn">
						<div class="left-btn">
							<button
								class="circle-btn"
								@click="toggleMicro"
							>
								<img
									:src="isMicroOn ? microOn : microOff"
									alt="M"
								/>
							</button>
							<button
								class="circle-btn"
								@click="toggleCamera"
							>
								<img
									:src="isvideoOn ? videoOn : videoOff"
									alt="C"
								/>
							</button>

							<button
								class="ract-btn"
								@click="toggleMirror"
							>
								반전
							</button>
						</div>
						<div class="right-btn">
							<button
								class="ract-btn"
								@click="navigateTo('background')"
							>
								생성
							</button>
							<button
								class="ract-btn"
								@click="navigateTo('main')"
							>
								취소
							</button>
						</div>
					</div>
				</div>
			</BoothBack>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
.booth-content {
	display: flex;
	flex-direction: column;
	justify-content: space-evenly;
	align-items: center;

	width: 100%;
	height: 100%;
}

.create-content {
	display: flex;
	flex-direction: column;
	justify-content: space-evenly;
	align-items: center;

	height: 100%;
	width: 100%;
}

.mycam-box {
	margin-top: 15px;
	height: 80%;
	width: 90%;
	min-height: 350px;
	min-width: 700px;
	background-color: rgb(255, 255, 255);

	border-radius: 20px;

	display: flex;
	align-items: center;
	justify-content: center;
	flex-direction: column;
}

.create-btn {
	height: 10%;
	width: 90%;
	display: flex;
	justify-content: space-between;
}

.circle-btn {
	border: none;
	border-radius: 50%;
	width: 50px;
	height: 50px;
	line-height: 50px;
	padding: 5px;
	display: flex;
	justify-content: center;
	align-items: center;
	margin: 0 5px;
	border: none;
	background-color: transparent;
}

.ract-btn {
	border: none;
	border-radius: 20px;
	width: 75px;
	height: 50px;
	margin: 5px;
	padding: 5px;

	&:hover {
		background-color: rgb(136, 136, 136);
	}
}

.left-btn {
	display: flex;
}

.close-btn {
	width: 90%;
	display: flex;
	justify-content: right;
}
</style>
