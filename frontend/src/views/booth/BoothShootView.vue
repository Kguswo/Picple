<script setup>
import { ref, onMounted, computed, provide } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBoothStore } from '@/stores/boothStore';
import { useUserStore } from '@/stores/userStore';
import { joinExistingSession } from '@/assets/js/showView/videoConference';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import ChatModal from '@/components/chat/ChatModal.vue';
import InitializationService from '@/assets/js/showView/InitializationService';
import PhotoService from '@/assets/js/showView/PhotoService';
import WebSocketService from '@/services/WebSocketService';
import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const props = defineProps({
    sessionId: String,
});

const isVideoOn = ref(true);
const isMicroOn = ref(true);
const isMirrored = ref(false);
const isChatOpen = ref(false);
const session = ref(null);
const publisher = ref(null);
const subscribers = ref([]);
const myVideo = ref(null);

const boothStore = useBoothStore();
const userStore = useUserStore();

const username = userStore.userNickname;

const route = useRoute();
const router = useRouter();
const sessionId = route.params.sessionId || props.sessionId;

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

const toggleMirror = () => {
    isMirrored.value = !isMirrored.value;
    const transform = isMirrored.value ? 'scaleX(-1)' : 'scaleX(1)';
    if (myVideo.value) {
        myVideo.value.style.transform = transform;
    }
};

const toggleCamera = () => {
    isVideoOn.value = !isVideoOn.value;
    if (publisher.value) {
        publisher.value.publishVideo(isVideoOn.value);
    }
};

const toggleMicro = () => {
    isMicroOn.value = !isMicroOn.value;
    if (publisher.value) {
        publisher.value.publishAudio(isMicroOn.value);
    }
};

onMounted(async () => {
    console.log('컴포넌트 마운트 시도');
    try {
        await joinExistingSession(session, publisher, subscribers, myVideo, sessionId, boothStore);
        console.log('세션 참가 성공');
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
    }

    WebSocketService.setBoothStore(boothStore);
    WebSocketService.on('background_info', (message) => {
        console.log('배경 정보 수신:', message.backgroundImage);
        boothStore.setBgImage(message.backgroundImage);
    });

    // Publisher 비디오에 대한 배경 제거 적용
    if (myVideo.value && publisher.value) {
        console.log('Publisher 비디오에 대한 배경 제거 시도');
        const videoBackgroundRemoval = new VideoBackgroundRemoval();
        const initialized = await videoBackgroundRemoval.initialize(myVideo.value);
        if (initialized) {
            console.log('Publisher 비디오 배경 제거 초기화 성공');
            videoBackgroundRemoval.startProcessing(publisher.value.video, myVideo.value);
        } else {
            console.error('Publisher의 배경 제거 초기화에 실패했습니다.');
        }
    }

    // Subscriber 비디오에 대한 배경 제거 적용
    subscribers.value.forEach(async (sub) => {
        const videoElement = document.getElementById(`video-${sub.subscriber.stream.streamId}`);
        const canvasElement = document.getElementById(`canvas-${sub.subscriber.stream.streamId}`);
        console.log(`Subscriber 비디오 초기화 시도: ${sub.subscriber.stream.streamId}`);
        if (videoElement && canvasElement) {
            const videoBackgroundRemoval = new VideoBackgroundRemoval();
            const initialized = await videoBackgroundRemoval.initialize(canvasElement);
            if (initialized) {
                console.log(`Subscriber 비디오 배경 제거 초기화 성공: ${sub.subscriber.stream.streamId}`);
                videoBackgroundRemoval.startProcessing(videoElement, canvasElement);
            } else {
                console.error(`Subscriber ${sub.subscriber.stream.streamId}의 배경 제거 초기화에 실패했습니다.`);
            }
        } else {
            console.warn(`Subscriber 비디오 또는 캔버스 요소를 찾을 수 없습니다: ${sub.subscriber.stream.streamId}`);
        }
    });
});

const { remainPicCnt, images } = PhotoService;
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="booth-top-div">
                <div>남은 사진 수: {{ remainPicCnt }}/10</div>
                <div class="close-btn">
                    <button class="close" @click="navigateTo('main')">
                        나가기
                    </button>
                </div>
            </div>

            <div class="booth-content-main">
                <BoothBack class="booth-camera-box">
                    <div ref="captureArea" :style="{ backgroundImage: `url(${bgImage})` }" class="photo-zone"
                        @focus="handleFocus" @blur="handleBlur" tabindex="0">
                        <div class="video-container">
                            <div v-if="publisher" class="stream-container">
                                <h3>Me</h3>
                                <canvas ref="myVideo" class="mirrored-video"></canvas>
                            </div>
                            <div v-for="sub in subscribers" :key="sub.subscriber.stream.streamId"
                                class="stream-container">
                                <video :id="`video-${sub.subscriber.stream.streamId}`" :width="320" :height="240"
                                    autoplay playsinline style="display: none"
                                    :srcObject="sub.subscriber.stream.getMediaStream()"></video>
                                <canvas :id="`canvas-${sub.subscriber.stream.streamId}`" :width="320" :height="240"
                                    class="mirrored-video"></canvas>
                            </div>
                        </div>
                    </div>
                    <div class="create-btn">
                        <div class="left-btn">
                            <button class="circle-btn" @click="toggleMicro">
                                <img :src="isMicroOn ? microOn : microOff" alt="M" />
                            </button>
                            <button class="circle-btn" @click="toggleCamera">
                                <img :src="isVideoOn ? videoOn : videoOff" alt="Toggle Camera" />
                            </button>
                        </div>

                        <button @click="takePhoto" class="take-photo">
                            <img src="@/assets/icon/camera.png" alt="" />
                        </button>
                        <div class="right-btn">
                            <button class="ract-btn" @click="exitphoto">
                                템플릿 선택
                            </button>
                        </div>
                    </div>
                </BoothBack>
                <BoothBack class="booth-select-box">
                    <div class="select-box-top">
                        <button class="prev-btn" @click="changeComponent">
                            &lt;
                        </button>
                        <div class="box-name">
                            <p v-if="showtype === 1">배경선택</p>
                            <p v-if="showtype === 2">사진보기</p>
                        </div>
                        <button class="next-btn" @click="changeComponent">
                            &gt;
                        </button>
                    </div>

                    <div class="select-text-box">
                        <RouterView v-if="showtype === 1" @update="changeImage"></RouterView>
                        <RouterView v-else :images="images"></RouterView>
                    </div>
                </BoothBack>
            </div>
        </div>
        <button class="chat-icon" @click="toggleChat">
            채팅창
        </button>
        <ChatModal v-show="isChatOpen" :username="username" :session="session" @close="toggleChat" />
    </WhiteBoardComp>
</template>

<style scoped>
@import url('@/assets/css/shootView.css');

/* 비디오 컨테이너 스타일 */
.video-container {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
}

/* 개별 스트림 컨테이너 스타일 */
.stream-container {
    width: 320px;
}

/* 비디오 요소 스타일 */
video {
    width: 100%;
    height: auto;
    border: 1px dashed #ccc;
    border-radius: 8px;
}

.chat-icon {
    position: fixed;
    bottom: 20px;
    right: 20px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 10px;
    width: 60px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    font-size: 14px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s, box-shadow 0.3s;
}

.chat-icon:hover {
    background-color: #0056b3;
    transform: scale(1.1);
    box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

/* 캔버스 요소 스타일 */
canvas {
    width: 100%;
    height: auto;
    border: 1px solid #ccc;
    border-radius: 8px;
}

.mirrored-video {
    transform: scaleX(-1);
    /* 비디오를 수평으로 반전시킴 */
}
</style>
