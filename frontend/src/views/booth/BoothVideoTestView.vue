<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { useRouter, useRoute } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';
import { useBoothStore } from '@/stores/boothStore';
import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const router = useRouter();
const route = useRoute();
const boothStore = useBoothStore();

const sessionId = route.params.boothId;
console.log('sessionId:', sessionId);

const token = boothStore.sessionInfo.token;
console.log('token:', token);

const videoElement = ref(null);
let mediaStream = null;
let isMirrored = ref(true);
let isvideoOn = ref(true);
let isMicroOn = ref(true);
const isLoading = ref(true);

const join = async () => {
    try {
        boothStore.setSessionInfo({ sessionId, token });
        router.push({ path: `/booth/${sessionId}` });
    } catch (error) {
        console.error('Failed to join booth:', error);
        alert('부스 참여에 실패했습니다.');
    }
};

const cancel = () => {
    router.push({ name: 'main' });
};

onMounted(async () => {
    console.log('Booth Video Test 페이지 호출되었습니다');
    const startTime = Date.now();

    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            video: true,
            audio: true,
        });
        videoElement.value.srcObject = mediaStream;
        videoElement.value.style.transform = 'scaleX(-1)';
    } catch (error) {
        console.error('Error accessing webcam:', error);
    }

    const elapsedTime = Date.now() - startTime;
    const remainingTime = Math.max(2000 - elapsedTime, 0);

    setTimeout(() => {
        isLoading.value = false;
    }, remainingTime);
});

onUnmounted(() => {
    if (mediaStream) {
        mediaStream.getTracks().forEach((track) => {
            track.stop();
        });
    }
});

const toggleMirror = () => {
    isMirrored.value = !isMirrored.value;
    videoElement.value.style.transform = isMirrored.value ? 'scaleX(-1)' : 'scaleX(1)';
};

const toggleCamera = () => {
    isvideoOn.value = !isvideoOn.value;
    mediaStream.getVideoTracks().forEach((track) => {
        track.enabled = isvideoOn.value;
    });
    videoElement.value.srcObject = mediaStream;
};

const toggleMicro = () => {
    isMicroOn.value = !isMicroOn.value;
    mediaStream.getAudioTracks().forEach((track) => {
        track.enabled = isMicroOn.value;
    });
};
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
            <div class="close-btn">
                <button
						class="close"
						@click="navigateTo('main')"
					>
						나가기
					</button>
            </div>

            <div
                class="video-container"
                v-show="isvideoOn"
            >
                <video
                    ref="videoElement"
                    autoplay
                ></video>
            </div>
            <div
                v-if="!isvideoOn"
                class="video-off"
            >
                카메라가 꺼져있습니다!
            </div>

            <BoothBack class="booth-create">
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
                            @click="join"
                        >
                            확인
                        </button>
                        <button
                            class="ract-btn"
                            @click="cancel"
                        >
                            취소
                        </button>
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
    background-color: #f4f0d1f7;
    border-radius: 20px;
}

.create-btn {
    width: 98%;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.booth-camera-box {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100%;
    overflow: hidden;
}

.circle-btn {
    border: none;
    border-radius: 50%;
    width: 50px;
    height: 50px;
    padding: 10px;
    display: flex;
    justify-content: center;
    align-items: center;
    margin: 0 10px;
    background-color: #7bd9dc;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: transform 0.2s ease, background-color 0.2s ease;
    cursor: pointer;
}

.circle-btn img {
    width: 24px;
    height: 24px;
}

.circle-btn:hover {
    transform: scale(1.1);
    background-color: #44c7cc;
}

.ract-btn {
    border: none;
    border-radius: 20px;
    width: 100px;
    height: 40px;
    margin: 5px;
    padding: 10px;
    background-color: #f7a1a1;
    color: white;
    font-size: 16px;
    font-weight: bold;
    text-align: center;
    cursor: pointer;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
    transition: background-color 0.3s ease, transform 0.2s ease;
}

.ract-btn:hover {
    background-color: #f58080;
    transform: scale(1.05);
}

.left-btn {
    display: flex;
}

.close-btn {
    width: 90%;
    display: flex;
    justify-content: flex-end;
    align-items: center;
}

.close-icon {
    width: 30px;
    height: 30px;
    cursor: pointer;
    transition: transform 0.2s ease;
}

.close-icon:hover {
    transform: scale(1.1);
}

.loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgb(133, 81, 255);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    filter: hue-rotate(110deg) saturate(40%) brightness(250%) contrast(80%);
}

.loading-overlay img {
    width: 10vw;
    height: 9vw;
    max-width: 200px;
    max-height: 180px;
}

video {
    transform: scaleX(-1);
    object-fit: cover;
    width: 100%;
    height: 100%;
    border-radius: 20px;
}

.video-off {
    width: 90%;
    height: 75%;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 18px;
    color: #ff6666;
    font-weight: bold;
}

.video-container {
    width: 90%;
    height: 75%;
}

.booth-content {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    height: 100%;
    background-color: #f4f0d1f7;
    border-radius: 20px;
}

.close-btn {
    width: 93%;
    padding: 5px;
    display: flex;
    justify-content: flex-end;
    align-items: center;
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
</style>
