<script setup>
// 필요한 컴포넌트와 유틸리티 import
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { VideoBackgroundRemoval } from '@/assets/js/showView/mediapipeUtils.js';

// Vue 3의 Composition API 기능들 import
import { ref, reactive, onMounted, onUnmounted, nextTick, toRefs, markRaw, computed } from 'vue';
import { useRoute } from 'vue-router';

// OpenVidu 라이브러리와 axios import
import { OpenVidu } from 'openvidu-browser';
import axios from 'axios';

// OpenVidu 객체와 세션을 저장할 reactive 객체 생성
const state = reactive({
    OV: null, // OpenVidu 인스턴스
    session: null, // 현재 세션
    publisher: null, // 로컬 스트림 발행자
    subscribers: [], // 원격 참가자들의 구독 목록
});

// 로컬 비디오 요소에 대한 ref 생성
const myVideo = ref(null); // 비디오 요소
const myCanvas = ref(null); // 캔버스 요소 (배경 제거 결과 표시용)
const videoStarted = ref(false); // 비디오 스트림 시작 여부

// 라우터에서 세션 ID를 가져옴
const route = useRoute();
const sessionId = route.params.sessionId;

// 성능 설정을 위한 reactive 객체
const performanceSettings = reactive({
    frameRate: 60, // 프레임 레이트 (초당 프레임 수)
    resolution: '320x240', // 해상도
    videoQuality: 'high', // 비디오 품질
});

// 성능 설정에 따른 video constraints 계산
const videoConstraints = computed(() => {
    const [width, height] = performanceSettings.resolution.split('x').map(Number);
    return {
        width: { ideal: width },
        height: { ideal: height },
        frameRate: { ideal: performanceSettings.frameRate },
    };
});

// 성능 설정 변경 함수
const updatePerformanceSettings = (newSettings) => {
    Object.assign(performanceSettings, newSettings);
    if (state.publisher) {
        // 비디오 발행을 잠시 중단했다가 다시 시작하여 새 설정 적용
        state.publisher.publishVideo(false);
        nextTick(() => {
            state.publisher.publishVideo(true);
        });
    }
};

// 배경 제거 초기화 함수
const initializeBackgroundRemoval = async (videoElement, canvasElement) => {
    if (!videoElement || !canvasElement) {
        console.error('비디오 또는 캔버스 요소를 찾을 수 없습니다');
        return;
    }

    // 비디오 스트림이 시작될 때까지 대기
    await new Promise((resolve) => {
        const checkVideo = () => {
            if (videoElement.readyState >= 2 && videoElement.videoWidth > 0 && videoElement.videoHeight > 0) {
                // 캔버스 크기를 비디오 크기에 맞춤
                canvasElement.width = videoElement.videoWidth;
                canvasElement.height = videoElement.videoHeight;
                resolve();
            } else {
                requestAnimationFrame(checkVideo);
            }
        };
        checkVideo();
    });

    try {
        const newBackgroundRemoval = new VideoBackgroundRemoval();
        await newBackgroundRemoval.initialize();
        newBackgroundRemoval.startProcessing(videoElement, canvasElement);
        videoStarted.value = true;
        return newBackgroundRemoval;
    } catch (error) {
        console.error('MediaPipe 초기화 중 오류 발생:', error);
    }
};

// 세션 참가 함수
const joinSession = async () => {
    try {
        console.log('세션 참가 중...');

        // OpenVidu 객체 생성
        state.OV = new OpenVidu();
        console.log('OpenVidu 객체 생성됨');

        // 새 세션 초기화
        state.session = state.OV.initSession();
        console.log('세션 초기화됨');

        // 새 참가자가 들어왔을 때의 이벤트 핸들러
        state.session.on('streamCreated', async ({ stream }) => {
            console.log('새 스트림 생성됨:', stream.streamId);
            const subscriber = state.session.subscribe(stream);
            state.subscribers.push(subscriber);

            // 새 참가자의 비디오에 배경 제거 적용
            nextTick(async () => {
                const video = document.getElementById(`video-${stream.streamId}`);
                const canvas = document.getElementById(`canvas-${stream.streamId}`);
                if (video && canvas) {
                    console.log('새 참가자에게 배경 제거 적용 중');
                    await initializeBackgroundRemoval(video, canvas);
                } else {
                    console.error('새 참가자의 비디오 또는 캔버스 요소를 찾을 수 없습니다');
                }
            });
        });

        // 참가자가 나갔을 때의 이벤트 핸들러
        state.session.on('streamDestroyed', ({ stream }) => {
            console.log('스트림 제거됨:', stream.streamId);
            const index = state.subscribers.findIndex((sub) => sub.stream.streamId === stream.streamId);
            if (index >= 0) {
                state.subscribers.splice(index, 1);
            }
        });

        // 세션 연결을 위한 토큰 얻기
        const token = await getToken();
        console.log('토큰 획득됨');

        // 세션에 연결
        await state.session.connect(token);
        console.log('세션에 연결됨');

        // 사용 가능한 비디오 장치 가져오기
        const devices = await state.OV.getDevices();
        const videoDevices = devices.filter((device) => device.kind === 'videoinput');
        console.log('사용 가능한 비디오 장치:', videoDevices);

        // Publisher 옵션 설정
        const publisherOptions = {
            audioSource: undefined, // 기본 오디오 소스 사용
            videoSource: videoDevices.length > 0 ? videoDevices[0].deviceId : undefined, // 첫 번째 비디오 장치 사용
            publishAudio: true, // 오디오 발행
            publishVideo: true, // 비디오 발행
            videoConstraints: videoConstraints.value, // 해상도와 프레임 레이트 설정
            insertMode: 'APPEND', // 삽입 모드
            mirror: false, // 미러링 비활성화
        };

        // 로컬 웹캠 스트림 생성
        state.publisher = await state.OV.initPublisherAsync(undefined, publisherOptions);
        console.log('로컬 웹캠 스트림 생성됨');

        // 로컬 스트림을 세션에 게시
        await state.session.publish(state.publisher);
        console.log('로컬 스트림이 세션에 게시됨');

        // 퍼블리셔 비디오 스트림 설정 및 배경 제거 적용
        if (myVideo.value && myCanvas.value && state.publisher.stream) {
            console.log('로컬 비디오 스트림 설정 및 배경 제거 적용 중');
            myVideo.value.srcObject = state.publisher.stream.getMediaStream();

            myVideo.value.onloadedmetadata = async () => {
                console.log('로컬 비디오 메타데이터 로드 완료');
                await initializeBackgroundRemoval(myVideo.value, myCanvas.value);
            };
        } else {
            console.error('로컬 비디오 스트림 설정 또는 배경 제거 적용 실패');
        }
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
            `https://localhost:4443/openvidu/api/sessions/${sessionId}/connection`,
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
            await createSession(sessionId);
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

// 성능 모니터링 함수 (옵션)
const monitorPerformance = () => {
    if (state.publisher && state.publisher.stream && state.publisher.stream.getRTCPeerConnection) {
        const pc = state.publisher.stream.getRTCPeerConnection();
        if (pc) {
            pc.getStats(null)
                .then((stats) => {
                    stats.forEach((report) => {
                        if (report.type === 'outbound-rtp' && report.kind === 'video') {
                            console.log('현재 프레임 레이트:', report.framesPerSecond);
                            console.log('현재 비트레이트:', report.bitrateMean);
                        }
                    });
                })
                .catch((error) => {
                    console.error('성능 통계 가져오기 실패:', error);
                });
        }
    }
};

// 컴포넌트가 마운트될 때 실행될 로직
onMounted(() => {
    joinSession();
    setInterval(monitorPerformance, 5000); // 5초마다 성능 체크
});

// 컴포넌트가 언마운트될 때 실행될 로직
onUnmounted(() => {
    // 세션이 존재하면 연결 해제
    if (state.session) {
        state.session.disconnect();
    }
    clearInterval(monitorPerformance);
});

// template에서 사용할 수 있도록 state를 분해
const { publisher, subscribers } = toRefs(state);
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="booth-content-main">
                <BoothBack class="booth-camera-box">
                    <div>
                        <div class="video-container">
                            <!-- 로컬 비디오 스트림 -->
                            <div
                                v-if="publisher && videoStarted"
                                class="stream-container"
                            >
                                <h2>로컬 참가자</h2>
                                <video
                                    ref="myVideo"
                                    :width="320"
                                    :height="240"
                                    autoplay
                                    muted
                                    playsinline
                                    style="display: none"
                                ></video>
                                <canvas
                                    ref="myCanvas"
                                    :width="320"
                                    :height="240"
                                    class="mirrored"
                                ></canvas>
                            </div>

                            <!-- 원격 참가자 비디오 스트림 -->
                            <div
                                v-for="sub in subscribers"
                                :key="sub.stream.streamId"
                                class="stream-container"
                            >
                                <h2>원격 참가자</h2>
                                <video
                                    :id="`video-${sub.stream.streamId}`"
                                    :width="320"
                                    :height="240"
                                    autoplay
                                    playsinline
                                    style="display: none"
                                ></video>
                                <canvas
                                    :id="`canvas-${sub.stream.streamId}`"
                                    :width="320"
                                    :height="240"
                                    class="mirrored"
                                ></canvas>
                            </div>
                        </div>
                    </div>
                </BoothBack>
            </div>
        </div>
    </WhiteBoardComp>
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
    width: 300px;
}

/* 비디오 요소 스타일 */
video {
    width: 100%;
    height: auto;
    border: 1px solid #ccc;
    border-radius: 8px;
}

/* booth content */
.booth-content {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    height: 100%;
}

.booth-content-main {
    display: flex;
    flex-wrap: wrap;
    align-content: center;
    justify-content: space-evenly;
    width: 100%;
    height: 95%;
}

/* 미러링 효과를 위한 클래스 */
.mirrored {
    transform: scaleX(-1);
}
</style>
