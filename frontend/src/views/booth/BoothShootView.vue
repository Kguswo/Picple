<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import ChatModal from '@/components/chat/ChatModal.vue';

import InitializationService from '@/assets/js/showView/InitializationService';
import PhotoService from '@/assets/js/showView/PhotoService';
import WebSocketService from '@/services/WebSocketService';

import { ref, onMounted, onUnmounted, computed, provide } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useBoothStore } from '@/stores/boothStore';
import { useUserStore } from '@/stores/userStore';
import { joinExistingSession } from '@/assets/js/showView/videoConference';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const props = defineProps({
    sessionId: String,
});

const isVideoOn = ref(false);
const isMicroOn = ref(false);
const videoElement = ref(null);
const canvasElement = ref(null);
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

// boothshoot

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

const toggleCamera = () => {
    isVideoOn.value = !isVideoOn.value;
    if (InitializationService.videoElement) {
        InitializationService.videoElement.srcObject.getVideoTracks().forEach((track) => {
            track.enabled = isVideoOn.value;
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

//subscriber 관련 function
const addSubscriber = (subscriber) => {
    subscribers.value.push({
        streamId: subscriber.stream.streamId,
        subscriber: subscriber
    });
};

const removeSubscriber = (streamId) => {
    const index = subscribers.value.findIndex(sub => sub.streamId === streamId);
    if (index > -1) {
        subscribers.value.splice(index, 1);
    }
};

// boothshoot

onMounted(() => {
    joinExistingSession(session, publisher, subscribers, myVideo, sessionId, boothStore, addSubscriber, removeSubscriber);

    WebSocketService.setBoothStore(boothStore);
    WebSocketService.on('background_info', (message) => {
        boothStore.setBgImage(message.backgroundImage);
    });
});

onUnmounted(() => {});

const { remainPicCnt, images } = PhotoService;
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
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
                                <!-- <h3>{{ sub.username }}</h3> -->
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
        <button
            class="chat-icon"
            @click="toggleChat"
        >
            채팅창
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
    transform: scaleX(-1); /* 비디오를 수평으로 반전시킴 */
}
</style>
