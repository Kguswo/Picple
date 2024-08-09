<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { useBoothStore } from '@/stores/boothStore';
import { usePhotoStore } from '@/stores/photoStore';
import { RouterView, useRouter, useRoute } from 'vue-router';
import { ref, onMounted, onUnmounted, computed, provide } from 'vue';
import InitializationService from '@/assets/js/showView/InitializationService';
import PhotoService from '@/assets/js/showView/PhotoService';
import WebSocketService from '@/services/WebSocketService';
import WebRTCService from '@/services/WebRTCService';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const route = useRoute();
const router = useRouter();
const photoStore = usePhotoStore();
const boothStore = useBoothStore();

const participants = ref([]);
const peers = ref([]);

const navigateTo = (path) => {
	router.push({ name: path });
};

const bgImage = computed(() => boothStore.bgImage);

const showtype = ref(1);

const changeComponent = () => {
	showtype.value = showtype.value === 1 ? 2 : 1;
	navigateTo(showtype.value === 1 ? 'background' : 'showphoto');
	console.log('컴포넌트 변경:', showtype.value === 1 ? '배경 선택' : '사진 보기');
};

const showControls = ref(false);

const toggleControls = () => {
	showControls.value = !showControls.value;
};

const handleControlClick = (event) => {
	event.stopPropagation();
};

const boothActions = {
	changeImage: async (image) => {
		try {
			await WebSocketService.send({
				type: 'change_background',
				boothId: route.params.boothId,
				backgroundImage: image,
			});
			boothStore.setBgImage(image);
		} catch (error) {
			console.error('Failed to change background:', error);
		}
	},
	takePhoto: () => PhotoService.takePhoto(),
	exitphoto: async () => {
		console.log('exitphoto 호출 시도');
		const shouldExit = await PhotoService.exitphoto();
		console.log('exitphoto 결과:', shouldExit);
		if (shouldExit) {
			console.log('라우터 이동 시작');
			router.push('/selectTemp');
		} else {
			console.log('라우터 이동 취소');
		}
	},
	images: () => PhotoService.images,
};

provide('boothActions', boothActions);

const videoElement = ref(null);
const canvasElement = ref(null);

onMounted(async () => {
	WebSocketService.setBoothStore(boothStore);
	WebSocketService.on('background_info', (message) => {
		boothStore.setBgImage(message.backgroundImage);
	});
	WebSocketService.on('new-peer', handleNewPeer);
	WebSocketService.on('peer-left', handlePeerLeft);
	WebRTCService.onRemoteStream = handleRemoteStream;

	if (videoElement.value && canvasElement.value) {
		InitializationService.setVideoElement(videoElement.value);
		InitializationService.setCanvasElement(canvasElement.value);
		await InitializationService.initialize(router, route, boothStore, photoStore);
	} else {
		console.error('videoElement 또는 canvasElement가 설정되지 않았습니다.');
	}
	if (videoElement.value) {
		videoElement.value.style.transform = 'scaleX(-1)';
	}
	if (canvasElement.value) {
		canvasElement.value.style.transform = 'scaleX(-1)';
	}

	await initializeWebRTC();
});

onUnmounted(() => {
	InitializationService.cleanup();
	WebSocketService.off('new-peer', handleNewPeer);
	WebSocketService.off('peer-left', handlePeerLeft);
	WebRTCService.disconnect();
});

const initializeWebRTC = async () => {
	await WebRTCService.initializeLocalStream();
	if (videoElement.value) {
		videoElement.value.srcObject = WebRTCService.localStream;
	}
};

const handleNewPeer = async (peerId) => {
	const peerConnection = await WebRTCService.createPeerConnection(peerId);
	peers.value.push({ id: peerId, connection: peerConnection });
};

const handlePeerLeft = (peerId) => {
	const index = peers.value.findIndex((peer) => peer.id === peerId);
	if (index !== -1) {
		peers.value.splice(index, 1);
	}
};

const handleRemoteStream = (peerId, stream) => {
	const peer = peers.value.find((p) => p.id === peerId);
	if (peer) {
		peer.stream = stream;
		const videoElement = document.querySelector(`#video-${peerId}`);
		if (videoElement) {
			videoElement.srcObject = stream;
		}
	}
};

const changeImage = async (image) => {
	await boothActions.changeImage(image);
};

const videoWidth = ref(213);
const videoHeight = ref(160);
const videoContainerRef = ref(null);
const videoPosition = ref({ x: 0, y: 0 });
const videoScale = ref(1);
const videoRotation = ref(0);
const isDragging = ref(false);
const startPosition = ref({ x: 0, y: 0 });
const isFocused = ref(false);
const isMirrored = ref(true);
const isvideoOn = ref(true);
const isMicroOn = ref(true);

const handleFocus = () => {
	isFocused.value = true;
};

const handleBlur = () => {
	isFocused.value = false;
};

const takePhoto = async () => {
	console.log('takePhoto 함수 호출');
	await PhotoService.takePhoto();
};

const exitphoto = async () => {
	console.log('exitphoto 호출 시도');
	const shouldExit = await PhotoService.exitphoto();
	console.log('exitphoto 결과:', shouldExit);
	if (shouldExit) {
		console.log('라우터 이동 시작');
		router.push('/selectTemp');
	} else {
		console.log('라우터 이동 취소');
	}
};

const centerIndicatorStyle = computed(() => ({
	position: 'absolute',
	top: '50%',
	left: '50%',
	width: '10px',
	height: '1px',
	backgroundColor: isFocused.value ? 'gray' : 'lightgray',
	transform: 'translate(-50%, -50%)',
}));

const videoStyle = computed(() => ({
	position: 'absolute',
	left: '50%',
	top: '50%',
	transform: `translate(-50%, -50%) translate(${videoPosition.value.x}px, ${videoPosition.value.y}px) scale(${videoScale.value}) rotate(${videoRotation.value}deg)`,
	cursor: isDragging.value ? 'grabbing' : 'grab',
	border: isFocused.value ? '2px dashed gray' : '2px dashed lightgray',
	width: `${videoWidth.value}px`,
	height: `${videoHeight.value}px`,
}));

const startDrag = (event) => {
	isDragging.value = true;
	startPosition.value = {
		x: event.clientX - videoPosition.value.x,
		y: event.clientY - videoPosition.value.y,
	};
};

const onDrag = (event) => {
	if (!isDragging.value) return;
	const dx = event.clientX - startPosition.value.x;
	const dy = event.clientY - startPosition.value.y;
	videoPosition.value = { x: dx, y: dy };
};

const stopDrag = () => {
	isDragging.value = false;
};

const toggleMirror = () => {
	isMirrored.value = !isMirrored.value;
	const transform = isMirrored.value ? 'scaleX(-1)' : 'scaleX(1)';
	if (videoElement.value) {
		videoElement.value.style.transform = transform;
	}
	if (canvasElement.value) {
		canvasElement.value.style.transform = transform;
	}
};

const toggleCamera = () => {
	isvideoOn.value = !isvideoOn.value;
	if (InitializationService.videoElement) {
		InitializationService.videoElement.srcObject.getVideoTracks().forEach((track) => {
			track.enabled = isvideoOn.value;
		});
	}
};

const toggleMicro = () => {
	isMicroOn.value = !isMicroOn.value;
	if (InitializationService.videoElement) {
		InitializationService.videoElement.srcObject.getAudioTracks().forEach((track) => {
			track.enabled = isMicroOn.value;
		});
	}
};

const { isLoading } = InitializationService;
const { remainPicCnt, images } = PhotoService;
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div
			v-if="isLoading"
			class="loading-overlay"
		>
			<img
				src="@/assets/img/common/loading.gif"
				alt="Loading..."
			/>
		</div>
		<div class="booth-content">
			<div class="booth-top-div">
				<div>남은 사진 수: {{ remainPicCnt }}/10</div>
				<div class="close-btn">
					<button
						class="close"
						@click="navigateTo('main')"
					>
						나가기
					</button>
				</div>
			</div>

			<div class="booth-content-main">
				<BoothBack class="booth-camera-box">
					<div
						ref="captureArea"
						:style="{ backgroundImage: `url(${bgImage})` }"
						class="photo-zone"
						@focus="handleFocus"
						@blur="handleBlur"
						tabindex="0"
					>
						<div
							class="video-container"
							ref="videoContainerRef"
							:style="videoStyle"
							@mousedown="startDrag"
							@mousemove="onDrag"
							@mouseup="stopDrag"
							@mouseleave="stopDrag"
							@click="toggleControls"
							tabindex="0"
						>
							<div class="video-item">
								<canvas
									ref="canvasElement"
									:width="videoWidth"
									:height="videoHeight"
								></canvas>
								<video
									ref="videoElement"
									autoplay
									muted
									:width="videoWidth"
									:height="videoHeight"
									style="display: none"
								></video>
								<div :style="centerIndicatorStyle"></div>
								<p>You</p>
							</div>
						</div>
						<div
							v-if="showControls"
							class="controls"
							@click="handleControlClick"
						>
							<button
								class="close-controls"
								@click="toggleControls"
							>
								X
							</button>
							<label>
								Rotate
								<input
									type="range"
									v-model="videoRotation"
									min="0"
									max="360"
								/>
							</label>
							<label>
								Scale
								<input
									type="range"
									v-model="videoScale"
									min="0.5"
									max="2"
									step="0.01"
								/>
							</label>
						</div>
					</div>

					<div class="remote-streams">
						<div
							v-for="peer in peers"
							:key="peer.id"
							class="video-item"
						>
							<video
								:id="`video-${peer.id}`"
								autoplay
								playsinline
							></video>
							<p>{{ peer.id }}</p>
						</div>
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
									alt="Toggle Camera"
								/>
							</button>
							<button
								class="ract-btn"
								@click="toggleMirror"
							>
								반전
							</button>
						</div>

						<button
							@click="takePhoto"
							class="take-photo"
						>
							<img
								src="@/assets/icon/camera.png"
								alt=""
							/>
						</button>
						<div class="right-btn">
							<button
								class="ract-btn"
								@click="exitphoto"
							>
								템플릿 선택
							</button>
						</div>
					</div>
				</BoothBack>

				<BoothBack class="booth-select-box">
					<div class="select-box-top">
						<button
							class="prev-btn"
							@click="changeComponent"
						>
							&lt;
						</button>
						<div class="box-name">
							<p v-if="showtype === 1">배경선택</p>
							<p v-if="showtype === 2">사진보기</p>
						</div>
						<button
							class="next-btn"
							@click="changeComponent"
						>
							&gt;
						</button>
					</div>

					<div class="select-text-box">
						<RouterView
							v-if="showtype === 1"
							@update="changeImage"
						></RouterView>
						<RouterView
							v-else
							:images="images"
						>
						</RouterView>
					</div>
				</BoothBack>
			</div>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
@import url('@/assets/css/shootView.css');
</style>
