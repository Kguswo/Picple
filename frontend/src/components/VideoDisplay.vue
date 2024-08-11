<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { useBoothStore } from '@/stores/boothStore';
import { useUserStore, useUserNickname } from '@/stores/userStore';
import { joinExistingSession } from '@/assets/js/showView/videoConference';

const props = defineProps({
    sessionId: String,
});

const isChatOpen = ref(false);
const session = ref(null);
const publisher = ref(null);
const subscribers = ref([]);
const myVideo = ref(null);

const boothStore = useBoothStore();
const userStore = useUserStore();
const username = useUserNickname();

const route = useRoute();
const sessionId = route.params.sessionId || props.sessionId;

const toggleChat = () => {
    isChatOpen.value = !isChatOpen.value;
};

onMounted(() => {
    joinExistingSession(session, publisher, subscribers, myVideo, sessionId, boothStore);
});

onUnmounted(() => {
    if (session.value) {
        session.value.disconnect();
    }
});
</script>

<template>
    <div>
        <h1>화상 회의 화면</h1>
        <div class="video-container">
            <!-- 로컬 비디오 스트림 -->
            <div
                v-if="publisher"
                class="stream-container"
            >
                <h3>내 비디오</h3>
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
                v-for="(sub, index) in subscribers"
                :key="sub.subscriber.stream.streamId"
                class="stream-container"
            >
                <h3>참가자 비디오 {{ index + 1 }}</h3>
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
    </div>
</template>

<style scoped>
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
    border: 1px solid #ccc;
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
