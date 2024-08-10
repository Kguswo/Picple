<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { useRoute } from 'vue-router';
import { OpenVidu } from 'openvidu-browser';
import axios from 'axios';
import { useBoothStore } from '@/stores/boothStore';
import { useUserStore } from '@/stores/userStore';

import { SelfieSegmentation } from '@mediapipe/selfie_segmentation';
import { Camera } from '@mediapipe/camera_utils';
import ChatModal from '@/components/chat/ChatModal.vue';

import VideoBackgroundRemoval from '@/assets/js/showView/VideoBackgroundRemoval';

// Booth 스토어 사용
const boothStore = useBoothStore();

// props 정의
const props = defineProps({
	sessionId: String,
});

// OpenVidu 서버 설정
const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

// OpenVidu 객체와 세션을 저장할 ref 생성
const OV = new OpenVidu();
OV.enableProdMode(false); // 개발 모드 활성화
OV.setAdvancedConfiguration({
	logLevel: 'DEBUG',
	noStreamPlayingEventExceptionTimeout: 8000, // streamPlaying 이벤트 타임아웃 시간 연장 (8초)
});

const session = ref(null);

// 로컬 스트림(publisher)과 원격 참가자 스트림(subscribers)을 저장할 ref 생성
const publisher = ref(null);
const subscribers = ref([]);

// 로컬 비디오 요소에 대한 ref 생성
const myVideo = ref(null);

// 구독자 비디오 엘리먼트를 저장할 ref 배열 생성
const subscriberVideos = ref([]);

// 구독자 캔버스 엘리먼트를 저장할 ref 배열 생성
const subscriberCanvases = ref([]);

// 라우터에서 세션 ID를 가져옴
const route = useRoute();
const sessionId = route.params.sessionId;

// Selfie Segmentation 객체 생성
let selfieSegmentation;

// 세션 참가 함수
const joinExistingSession = async () => {
	try {
		// route.params나 props에서 sessionId를 가져옵니다
		const sessionId = props.sessionId || route.params.sessionId;
		console.log('받은 세션 ID:', sessionId);

		// store에서 세션 정보를 가져옵니다
		const sessionInfo = boothStore.getSessionInfo();
		console.log('스토어에서 가져온 세션 정보:', sessionInfo);

		if (!sessionInfo || !sessionInfo.sessionId || !sessionInfo.token) {
			throw new Error('세션 정보가 없습니다.');
		}

		const { token } = sessionInfo;

		console.log('OpenVidu 객체 생성 중');
		OV.value = new OpenVidu();

		console.log('세션 초기화 중');
		session.value = OV.value.initSession();

        // 이벤트 핸들러 설정
        session.value.on('streamCreated', async ({ stream }) => {
            console.log('새 스트림 생성됨:', stream.streamId);
            const subscriber = await session.value.subscribe(stream);
            console.log('구독 완료');
            console.log('반환된 subscriber:', subscriber);

            // 구독자 및 스트림을 저장
            subscribers.value.push({ subscriber });
            console.log('subscribers에 추가 완료. 현재 길이:', subscribers.value.length);

            // 새 참가자의 비디오에 배경 제거 적용
            nextTick(async () => {
                const video = document.getElementById(`video-${subscriber.stream.streamId}`);
                const canvas = document.getElementById(`canvas-${subscriber.stream.streamId}`);
                if (video && canvas) {
                    console.log('새 참가자에게 배경 제거 적용 중');
                    await initializeBackgroundRemoval(video, canvas);
                } else {
                    console.error('새 참가자의 비디오 또는 캔버스 요소를 찾을 수 없습니다');
                }
            });
        });

        // 참가자가 나갔을 때의 이벤트 핸들러
        session.value.on('streamDestroyed', ({ stream }) => {
            console.log('스트림 제거됨:', stream.streamId);
            const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
            if (index >= 0) {
                subscribers.value.splice(index, 1);
                subscriberVideos.value.splice(index, 1);
                subscriberCanvases.value.splice(index, 1);
            }
        });

		console.log('세션에 연결 중');
		try {
			await session.value.connect(token);
			console.log('세션에 연결 성공');
		} catch (connectionError) {
			console.error('세션 연결 중 오류 발생:', connectionError);
			throw connectionError;
		}

		console.log('비디오 장치 가져오기 중');
		let videoDevices;
		try {
			const devices = await OV.value.getDevices();
			videoDevices = devices.filter((device) => device.kind === 'videoinput');
			console.log('비디오 장치:', videoDevices);
		} catch (deviceError) {
			console.error('비디오 장치 가져오기 중 오류 발생:', deviceError);
			throw deviceError;
		}

		// 퍼블리셔 옵션 설정
		const publisherOptions = {
			audioSource: undefined, // 기본 오디오 소스 사용
			videoSource: undefined, // 기본 비디오 소스 사용
			publishAudio: true,
			publishVideo: true,
			resolution: '640x480', // 낮은 해상도로 시작
			frameRate: 60,
			insertMode: 'APPEND',
			mirror: true,
		};

		console.log('로컬 웹캠 스트림 생성 중');
		try {
			publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);
			console.log('로컬 웹캠 스트림 생성 성공');
		} catch (error) {
			console.error('로컬 웹캠 스트림 생성 중 오류 발생:', error);
			// 비디오 없이 오디오만으로 시도
			publisherOptions.publishVideo = false;
			try {
				publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);
				console.log('오디오 전용 스트림 생성 성공');
			} catch (audioError) {
				console.error('오디오 전용 스트림 생성 중 오류 발생:', audioError);
				// 여기서 사용자에게 장치 접근 권한을 확인하라는 메시지를 표시할 수 있습니다.
			}
		}

		console.log('로컬 스트림 세션에 게시 중');
		try {
			await session.value.publish(publisher.value);
			console.log('로컬 스트림 세션에 게시 성공');
		} catch (publishError) {
			console.error('로컬 스트림 게시 중 오류 발생:', publishError);
			throw publishError;
		}

		console.log('로컬 비디오 스트림 설정 중');
		if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
			myVideo.value.srcObject = publisher.value.stream.getMediaStream();
			console.log('로컬 비디오 스트림 설정 성공');
		} else {
			console.error('로컬 비디오 스트림 설정 실패:', {
				myVideo: myVideo.value,
				publisherStream: publisher.value.stream,
				mediaStream: publisher.value.stream ? publisher.value.stream.getMediaStream() : null,
			});
		}

		console.log('Selfie Segmentation 적용 중');
		applySegmentation(publisher);
		console.log('Selfie Segmentation 적용 성공');
	} catch (error) {
		console.error('세션 참가 중 오류 발생:', error);
		if (error.name === 'DEVICE_ACCESS_DENIED') {
			alert('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
		} else {
			alert(`오류 발생: ${error.message}`);
		}
	}
};

// Selfie Segmentation 적용 함수
const applySegmentation = (streamRef) => {
	console.log('applySegmentation 호출됨');
	console.log('streamRef:', streamRef);

	const actualStreamRef = streamRef.value || streamRef;

	if (!actualStreamRef || !actualStreamRef.stream) {
		console.error('actualStreamRef 또는 actualStreamRef.stream이 정의되지 않았습니다.', actualStreamRef);
		return;
	}

	if (typeof actualStreamRef.stream.getMediaStream !== 'function') {
		console.error('actualStreamRef.stream.getMediaStream 함수가 정의되지 않았습니다.', actualStreamRef.stream);
		return;
	}

	const mediaStream = actualStreamRef.stream.getMediaStream();
	console.log('mediaStream:', mediaStream);

	if (!mediaStream) {
		console.error('MediaStream이 정의되지 않았습니다.', actualStreamRef);
		return;
	}

	console.log('Selfie Segmentation 설정 중');
	const videoElement = document.createElement('video');
	console.log('videoElement 생성됨:', videoElement);

	videoElement.srcObject = mediaStream;
	console.log('videoElement.srcObject 설정됨:', videoElement.srcObject);

	selfieSegmentation = new SelfieSegmentation({
		locateFile: (file) => `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`,
	});

	selfieSegmentation.setOptions({
		modelSelection: 1, // 0: 전체 인체, 1: 인체 전면
	});

	console.log('Selfie Segmentation 옵션 설정됨');

	// Selfie Segmentation 결과 처리 함수
	const onResults = (results) => {
		const canvasElement = document.createElement('canvas');
		const canvasCtx = canvasElement.getContext('2d');

		canvasElement.width = results.image.width;
		canvasElement.height = results.image.height;

		canvasCtx.clearRect(0, 0, canvasElement.width, canvasElement.height);
		canvasCtx.drawImage(results.segmentationMask, 0, 0, canvasElement.width, canvasElement.height);

		canvasCtx.globalCompositeOperation = 'source-in';
		canvasCtx.drawImage(results.image, 0, 0, canvasElement.width, canvasElement.height);

		const videoStream = canvasElement.captureStream(30);
		const videoTrack = videoStream.getVideoTracks()[0];
		const originalStream = actualStreamRef.stream.getMediaStream();

		if (originalStream.getVideoTracks().length > 0) {
			originalStream.removeTrack(originalStream.getVideoTracks()[0]);
		}

		originalStream.addTrack(videoTrack);
	};

	selfieSegmentation.onResults(onResults);
	console.log('Selfie Segmentation onResults 설정됨');

	// 카메라 설정
	const camera = new Camera(videoElement, {
		onFrame: async () => {
			await selfieSegmentation.send({ image: videoElement });
		},
		width: 640,
		height: 480,
	});

	console.log('카메라 설정됨');

	camera.start();
	console.log('카메라 시작됨');
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

const isChatOpen = ref(false);

const userStore = useUserStore();
const username = ref(userStore.userNickname);

const toggleChat = () => {
	isChatOpen.value = !isChatOpen.value;
};

// 컴포넌트가 마운트될 때 실행될 로직
onMounted(() => {
	joinExistingSession();
});

// 컴포넌트가 언마운트될 때 실행될 로직
onUnmounted(() => {
	// 세션이 존재하면 연결 해제
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
</style>
