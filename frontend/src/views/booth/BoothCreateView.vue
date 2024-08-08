<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';

import { useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted } from 'vue';

import WebSocketService from '@/services/WebSocketService';

import axios from 'axios';
import { OpenVidu } from 'openvidu-browser';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

import { useBoothStore } from '@/stores/boothStore';

const boothStore = useBoothStore();

// OpenVidu
// OpenVidu 객체와 세션을 저장할 ref 생성
const OV = ref(null);
const session = ref(null);

// 로컬 스트림(publisher)과 원격 참가자 스트림(subscribers)을 저장할 ref 생성
const publisher = ref(null);
const subscribers = ref([]);

// 고정된 세션 ID 설정
const FIXED_SESSION_ID = 'myFixedSessionId';

// 세션 참가 함수
const joinSession = async () => {
    try {
        // OpenVidu 객체 생성
        OV.value = new OpenVidu();

        // 새 세션 초기화
        session.value = OV.value.initSession();

        // 새 참가자가 들어왔을 때의 이벤트 핸들러
        session.value.on('streamCreated', ({ stream }) => {
            // 새 스트림을 구독하고 subscribers 배열에 추가
            console.log('새로운 스트림 생성됨:', stream.streamId);
            const subscriber = session.value.subscribe(stream);
            subscribers.value.push(subscriber);
        });

        // 참가자가 나갔을 때의 이벤트 핸들러
        session.value.on('streamDestroyed', ({ stream }) => {
            // 나간 참가자의 스트림을 subscribers 배열에서 제거
            console.log('스트림 제거됨:', stream.streamId);
            const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
            }
        });

        // 세션 연결을 위한 토큰 얻기
        const token = await getToken();

        // 세션에 연결
        await session.value.connect(token);

        // 사용 가능한 비디오 장치 가져오기
        const devices = await OV.value.getDevices();
        const videoDevices = devices.filter((device) => device.kind === 'videoinput');

        // Publisher 옵션 설정
        const publisherOptions = {
            audioSource: undefined, // 기본 오디오 소스 사용
            videoSource: videoDevices.length > 0 ? videoDevices[0].deviceId : undefined, // 첫 번째 비디오 장치 사용
            publishAudio: true, // 오디오 발행
            publishVideo: true, // 비디오 발행
            resolution: '640x480', // 해상도 설정
            frameRate: 30, // 프레임 레이트 설정
            insertMode: 'APPEND', // 비디오 삽입 모드
            mirror: true, // 미러링 비활성화
        };

        // 로컬 웹캠 스트림 생성
        publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);

        // 로컬 스트림을 세션에 게시
        await session.value.publish(publisher.value);

        // 세션 연결이 완료되면 화면 페이지로 이동
        router.push({ name: 'videoDisplay', params: { sessionId: FIXED_SESSION_ID } });
    } catch (error) {
        console.error('세션 참가 중 오류 발생:', error);
        if (error.name === 'DEVICE_ACCESS_DENIED') {
            alert('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
        } else {
            alert(`오류 발생: ${error.message}`);
        }
    }
};

// 고정된 세션 ID를 사용하여 토큰 얻기
const getToken = async () => {
    try {
        // 먼저 고정된 세션 ID로 토큰을 얻으려고 시도
        const response = await axios.post(
            `https://localhost:4443/openvidu/api/sessions/${FIXED_SESSION_ID}/connection`,
            {},
            {
                headers: {
                    Authorization: 'Basic ' + btoa('OPENVIDUAPP:MY_SECRET'),
                    'Content-Type': 'application/json',
                },
            },
        );
        return response.data.token;
    } catch (error) {
        if (error.response && error.response.status === 404) {
            // 세션이 없으면 새로 생성
            await createSession(FIXED_SESSION_ID);
            // 세션 생성 후 다시 토큰 얻기 시도
            return getToken();
        }
        throw error;
    }
};

// 고정된 세션 ID로 새 세션 생성
const createSession = async (sessionId) => {
    await axios.post(
        'https://localhost:4443/openvidu/api/sessions',
        { customSessionId: sessionId },
        {
            headers: {
                Authorization: 'Basic ' + btoa('OPENVIDUAPP:MY_SECRET'),
                'Content-Type': 'application/json',
            },
        },
    );
};

// OpenVidu

const boothCode = ref('');

// 비디오 표현을 위한 변수
const videoElement = ref(null);
let mediaStream = null;

// 화면 표시에 있어 사용되는 변수
let isMirrored = ref(true); // 초기값을 true로 변경
let isvideoOn = ref(true);
let isMicroOn = ref(true);

const isLoading = ref(true);

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

onMounted(() => {
    WebSocketService.connect();
    WebSocketService.setBoothStore(boothStore);
    WebSocketService.on('background_info', (message) => {
        boothStore.setBgImage(message.backgroundImage);
    });
});

onMounted(async () => {
    console.log('Create Booth 페이지 호출되었습니다');
    const startTime = Date.now();

    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            video: true,
            audio: true,
        });
        videoElement.value.srcObject = mediaStream;
        videoElement.value.style.transform = 'scaleX(-1)';

        await WebSocketService.connect('ws://localhost:8080/ws');
    } catch (error) {
        console.error('Error accessing webcam:', error);
        console.error('Failed to connect to WebSocket:', error);
    }

    const elapsedTime = Date.now() - startTime;
    const remainingTime = Math.max(1000 - elapsedTime, 0);

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

    // 세션이 존재하면 연결 해제
    if (session.value) {
        session.value.disconnect();
    }
});

// 거울모드 여부
const toggleMirror = () => {
    isMirrored.value = !isMirrored.value;
    videoElement.value.style.transform = isMirrored.value ? 'scaleX(-1)' : 'scaleX(1)';
};

//카메라의 온오프
const toggleCamera = () => {
    isvideoOn.value = !isvideoOn.value;
    console.log('비디오 온');

    if (isvideoOn.value) {
        mediaStream.getVideoTracks().forEach((track) => {
            track.enabled = true; // 비디오 트랙 활성화
        });
        videoElement.value.srcObject = mediaStream;
    } else {
        console.log('비디오 오프');

        mediaStream.getVideoTracks().forEach((track) => {
            track.enabled = false; // 비디오 트랙 비활성화
        });
        videoElement.value.srcObject = mediaStream;
    }
};

//마이크의 온오프
const toggleMicro = () => {
    isMicroOn.value = !isMicroOn.value;
    if (isMicroOn.value) {
        console.log('마이크 온');

        mediaStream.getAudioTracks().forEach((track) => {
            track.enabled = true; // 오디오 트랙을 활성화
        });
    } else {
        console.log('마이크 오프');

        mediaStream.getAudioTracks().forEach((track) => {
            track.enabled = false; // 오디오  트랙을 비활성화
        });
    }
};

const handleCreateBooth = async () => {
    try {
        if (!WebSocketService.isConnected()) {
            await WebSocketService.connect('ws://localhost:8080/ws');
        }
        const boothId = await WebSocketService.createBooth();
        console.log('Created booth with ID:', boothId);
        joinSession();
    } catch (error) {
        console.error('Failed to create booth:', error);
    }
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
                    closeBtn
                </button>
            </div>
            <BoothBack class="booth-create">
                <div class="create-content">
                    <div class="mycam-box">
                        <!-- v-if로 하면 카메라가 나오지 않아 v-show로 미리 렌더링 -->
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
                                @click="handleCreateBooth"
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
    /* display */
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

.loading-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: #8551ff;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 9999;
    filter: hue-rotate(320deg) saturate(10%) brightness(90%) contrast(80%);
}

.loading-overlay img {
    width: 10vw; /* 뷰포트 너비의 10% */
    height: 9vw; /* 뷰포트 너비의 9% */
    max-width: 200px; /* 최대 크기 제한 */
    max-height: 180px; /* 최대 크기 제한 */
}
</style>
