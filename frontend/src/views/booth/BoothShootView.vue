<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import ChatModal from '@/components/chat/ChatModal.vue';

import InitializationService from '@/assets/js/showView/InitializationService';
import PhotoService from '@/assets/js/showView/PhotoService';
import WebSocketService from '@/services/WebSocketService';

import { ref, onMounted, computed, provide } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBoothStore } from '@/stores/boothStore';
import { useUserStore } from '@/stores/userStore';
import { joinExistingSession } from '@/assets/js/showView/videoConference';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const isVideoOn = ref(false);
const isMicroOn = ref(false);
const isMirrored = ref(false);
const videoElement = ref(null);
const canvasElement = ref(null);
const isChatOpen = ref(false);
const publisher = ref(null);
const subscribers = ref([]);
const myVideo = ref(null);

const route = useRoute();
const router = useRouter();

const boothStore = useBoothStore();
const boothCode = computed(() => boothStore.getBoothCode());

const userStore = useUserStore();

const username = userStore.userNickname;

const sessionId = boothStore.getSessionInfo().sessionId;

const isHost = computed(() => {
  const result = boothStore.isHost;
  console.log('Current isHost value:', result);
  return result;
});

const toggleChat = () => {
    isChatOpen.value = !isChatOpen.value;
};

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
    images: () => PhotoService.images,
};

provide('boothActions', boothActions);

const changeImage = async (image) => {
    await boothActions.changeImage(image);
};

const takePhoto = async () => {
  console.log('Attempting to take photo. isHost:', isHost.value);
  if (!isHost.value) {
    console.warn('Only the host can take photos');
    return;
  }
  console.log('takePhoto 함수 호출');
  await PhotoService.takePhoto(sessionId);
};

const exitphoto = async () => {
	console.log('exitphoto 호출 시도');
	const shouldExit = await PhotoService.exitphoto();
	console.log('exitphoto 결과:', shouldExit);
	if (shouldExit) {
		console.log('라우터 이동 시작');
		router.push({ name: 'selectTemp' });
	} else {
		console.log('라우터 이동 취소');
	}
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
	if (myVideo.value) {
		myVideo.value.style.transform = transform;
	}
};

const toggleCamera = () => {
	isVideoOn.value = !isVideoOn.value;
	if (publisher.value) {
		publisher.value.publishVideo(isVideoOn.value);
		console.log(`Camera state: ${isVideoOn.value}`);
		updateVideoDisplay();
	} else {
		console.error('Publisher is not defined.');
	}
};

const toggleMicro = () => {
	isMicroOn.value = !isMicroOn.value;
	if (publisher.value) {
		publisher.value.publishAudio(isMicroOn.value);
		console.log(`Microphone state: ${isMicroOn.value}`);
	} else {
		console.error('Publisher is not defined.');
	}
};

// 비디오 엘리먼트를 보여주거나 숨기는 함수
const updateVideoDisplay = () => {
	if (myVideo.value) {
		myVideo.value.style.display = isVideoOn.value ? 'block' : 'none';
	}
};

const { remainPicCnt, images } = PhotoService;

onMounted(() => {
	console.log('BoothShootView mounted. Checking isHost:', isHost.value);

	joinExistingSession(publisher, subscribers, myVideo, boothStore).then(() => {
		if (publisher.value) {
			isVideoOn.value = publisher.value.stream.videoActive;
			isMicroOn.value = publisher.value.stream.audioActive;
			updateVideoDisplay();
		}
	});

	WebSocketService.on('booth_created', (message) => {
		boothCode.value = message.boothId.slice(0, 10);
		boothStore.setBoothCode(boothCode.value);
	});

	WebSocketService.on('joined_booth', (message) => {
		if (message.boothId) {
		boothCode.value = message.boothId.slice(0, 10);
		boothStore.setBoothCode(boothCode.value);
		}
	});

    WebSocketService.setBoothStore(boothStore);
    WebSocketService.on('background_info', (message) => {
        boothStore.setBgImage(message.backgroundImage);
    });
});
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-shoot-booth">
		<div class="booth-content">
			<div class="booth-top-div">
				<div v-if="boothCode" class="booth-code">부스 코드: {{ boothCode }}</div>
				<div class="remaining-pics">남은 사진 수: {{ remainPicCnt }}/10</div>
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
                        <div class="video-container">
                            <!-- 로컬 비디오 스트림 -->
                            <div
                                v-if="publisher"
                                class="stream-container"
                            >
                                <h3>Me</h3>
                                <video
                                    ref="myVideo"
                                    autoplay
                                    muted
                                    playsinline
                                    class="mirrored-video"
                                ></video>
                            </div>

							<!-- 원격 참가자 비디오 스트림 -->
							<div
								v-for="sub in subscribers"
								:key="sub.subscriber.stream.streamId"
								class="stream-container"
							>
								<video
									:id="`video-${sub.subscriber.stream.streamId}`"
									:width="320"
									:height="240"
									autoplay
									playsinline
									style="display: none"
									:srcObject="sub.subscriber.stream.getMediaStream()"
								></video>
								<canvas
									:id="`canvas-${sub.subscriber.stream.streamId}`"
									:width="320"
									:height="240"
									class="mirrored-video"
								></canvas>
							</div>
						</div>
					</div>
					<BoothBack class="booth-shoot">
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
										:src="isVideoOn ? videoOn : videoOff"
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
								v-if="isHost"  
								@click="takePhoto"
								class="take-photo"
							>
								<img
									src="@/assets/icon/camera.png"
									alt="Take Photo"
								/>
							</button>
							<div class="right-btn">
								<button
									class="ract-btn"
									@click="exitphoto()"
								>
									템플릿 선택
								</button>
							</div>
						</div>
					</BoothBack>
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
		<button class="chat-icon" @click="toggleChat">
			<svg xmlns="http://www.w3.org/2000/svg" height="24" viewBox="0 0 24 24" width="24">
      <path d="M0 0h24v24H0z" fill="none"/>
      <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z" fill="white"/>
    </svg>
		</button>

		<ChatModal
			v-show="isChatOpen"
			:username="username"
			:session="session"
			@close="toggleChat"
		/>
	</WhiteBoardComp>
</template>

<style scoped>
@import url('@/assets/css/shootView.css');

.video-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
}

.stream-container {
    width: 320px;
}

video {
    width: 100%;
    height: auto;
    border: 1px dashed #ccc;
    border-radius: 8px;
}

.chat-icon {
  position: fixed;
  bottom: 30px;
  right: 30px;
  background-color: #62bc65;
  border: none;
  border-radius: 50%;
  width: 70px;
  height: 70px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s, box-shadow 0.3s, background-color 0.3s;
}

.chat-icon:hover {
  background-color: #41af47;
  transform: scale(1.1);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
}

.chat-icon svg {
  width: 32px;
  height: 32px;
}

canvas {
    width: 100%;
    height: auto;
    border: 1px solid #ccc;
    border-radius: 8px;
}

.mirrored-video {
	transform: scaleX(-1);
}

.booth-top-div {
	width: 93%;
	height: 7%;
	display: flex;
	align-items: center;
	font-size: 26px;
	justify-content: space-between;

	.booth-top-div-content {
		display: flex;
		justify-content: space-between;
		width: 100%;
	}

	.remaining-pics {
		margin-left: 20px;
	}

	.booth-code {
		margin-right: 20px;
	}

	.close-btn {
		padding: 5px;
		display: flex;
		justify-content: right;
		.close {
			background-color: transparent;
			border: none;
		}

		.close {
            background-color: #E6A4A4;
            color: white;
            border: none;
            border-radius: 20px;
            padding: 10px 20px;
            font-size: 18px;
            cursor: pointer;
            transition: background-color 0.3s ease, transform 0.2s ease;
        }

		.close:hover {
            background-color: #f58080;
            transform: scale(1.05);
        }
	}
}

.close {
	font-size: 22px;
}

.booth-code {
  font-size: 26px;
  margin-right: 20px;
}
</style>
