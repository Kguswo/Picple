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

// OpenVidu 서버 설정
const OPENVIDU_SERVER_URL = 'https://localhost:4443';
const OPENVIDU_SERVER_SECRET = 'MY_SECRET';

const boothStore = useBoothStore();

// OpenVidu 관련 변수
const OV = ref(null);
const session = ref(null);
const publisher = ref(null);
const subscribers = ref([]);

// 라우터 인스턴스
const router = useRouter();

// 토큰 생성 함수
const createToken = async (sessionId) => {
	const response = await axios.post(
		`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${sessionId}/connection`,
		{},
		{
			headers: {
				Authorization: 'Basic ' + btoa('OPENVIDUAPP:' + OPENVIDU_SERVER_SECRET),
				'Content-Type': 'application/json',
			},
		},
	);
	return response.data.token;
};

// 세션 생성 함수
const createSession = async (sessionId) => {
	const response = await axios.post(
		`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`,
		{ customSessionId: sessionId },
		{
			headers: {
				Authorization: 'Basic ' + btoa('OPENVIDUAPP:' + OPENVIDU_SERVER_SECRET),
				'Content-Type': 'application/json',
			},
		},
	);
	return response.data.id;
};

// OpenVidu 세션 생성 및 토큰 얻기 함수
const createSessionAndGetToken = async (sessionId) => {
	const OV = new OpenVidu();

	try {
		const session = await createSession(sessionId);
		return await createToken(session);
	} catch (error) {
		console.error('Error creating session or getting token:', error);
		throw error;
	}
};

// 세션에 참가하는 함수
const joinSession = async (sessionId) => {
	try {
		console.log('Joining session with ID:', sessionId);
		await createSession(sessionId);

		// OpenVidu 객체 초기화
		OV.value = new OpenVidu();
		session.value = OV.value.initSession();

		// 새 스트림 생성 이벤트 핸들러
		session.value.on('streamCreated', ({ stream }) => {
			console.log('새로운 스트림 생성됨:', stream.streamId);
			const subscriber = session.value.subscribe(stream);
			subscribers.value.push(subscriber);

			subscriber.on('videoElementCreated', (event) => {
				console.log('비디오 엘리먼트 생성됨:', event.element);
				event.element.srcObject = subscriber.stream.getMediaStream();
			});

			subscriber.on('streamPlaying', (event) => {
				console.log('스트림 재생 중:', subscriber.stream.streamId);
			});
		});

		// 스트림 제거 이벤트 핸들러
		session.value.on('streamDestroyed', ({ stream }) => {
			console.log('스트림 제거됨:', stream.streamId);
			const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
			if (index >= 0) {
				subscribers.value.splice(index, 1);
			}
		});

		// 토큰 얻기 및 세션 연결
		const token = await getToken(sessionId);
		console.log('Token received:', token);
		await session.value.connect(token);

		// 비디오 장치 가져오기
		const devices = await OV.value.getDevices();
		const videoDevices = devices.filter((device) => device.kind === 'videoinput');

		// 퍼블리셔 옵션 설정
		const publisherOptions = {
			audioSource: undefined,
			videoSource: videoDevices.length > 0 ? videoDevices[0].deviceId : undefined,
			publishAudio: true,
			publishVideo: true,
			resolution: '640x480',
			frameRate: 30,
			insertMode: 'APPEND',
			mirror: true,
		};

		// 퍼블리셔 초기화 및 세션에 게시
		publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);
		await session.value.publish(publisher.value);

		// videoDisplay 페이지로 이동
		router.push({ name: 'videoDisplay', params: { sessionId: sessionId } });
	} catch (error) {
		console.error('세션 참가 중 오류 발생:', error);
		if (error.name === 'DEVICE_ACCESS_DENIED') {
			alert('카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.');
		} else {
			alert(`오류 발생: ${error.message}`);
		}
	}
};

const handleCreateBooth = async () => {
	try {
		if (!WebSocketService.isConnected()) {
			await WebSocketService.connect('ws://localhost:8080/ws');
		}
		const boothId = await WebSocketService.createBooth();
		console.log('Created booth with ID:', boothId);

		// OpenVidu 세션 생성 및 토큰 얻기
		const token = await createSessionAndGetToken(boothId);
		console.log('Obtained token:', token);

		// 세션 정보를 store에 저장
		boothStore.setSessionInfo({ sessionId: boothId, token });
		console.log('Session info stored:', { sessionId: boothId, token });

		// videoDisplay 페이지로 이동
		router.push({ name: 'videoDisplay', params: { sessionId: boothId } });
	} catch (error) {
		console.error('Failed to create booth:', error);
	}
};

// 기타 변수 초기화
const boothCode = ref('');
const videoElement = ref(null);
let mediaStream = null;
let isMirrored = ref(true);
let isvideoOn = ref(true);
let isMicroOn = ref(true);
const isLoading = ref(true);

// 페이지 이동 함수
const navigateTo = (path) => {
	router.push({ name: path });
};

// 컴포넌트 마운트 시 실행되는 함수
onMounted(() => {
	// WebSocket 연결 및 배경 정보 설정
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
		// 미디어 스트림 가져오기
		mediaStream = await navigator.mediaDevices.getUserMedia({
			video: true,
			audio: true,
		});
		videoElement.value.srcObject = mediaStream;
		videoElement.value.style.transform = 'scaleX(-1)';

		// WebSocket 연결
		await WebSocketService.connect('ws://localhost:8080/ws');
	} catch (error) {
		console.error('Error accessing webcam:', error);
		console.error('Failed to connect to WebSocket:', error);
	}

	// 로딩 화면 표시 시간 계산
	const elapsedTime = Date.now() - startTime;
	const remainingTime = Math.max(1000 - elapsedTime, 0);

	setTimeout(() => {
		isLoading.value = false;
	}, remainingTime);
});

// 컴포넌트 언마운트 시 실행되는 함수
onUnmounted(() => {
	// 미디어 스트림 정리
	if (mediaStream) {
		mediaStream.getTracks().forEach((track) => {
			track.stop();
		});
	}

	// 세션 연결 해제
	if (session.value) {
		session.value.disconnect();
	}
});

// 비디오 미러링 토글 함수
const toggleMirror = () => {
	isMirrored.value = !isMirrored.value;
	videoElement.value.style.transform = isMirrored.value ? 'scaleX(-1)' : 'scaleX(1)';
};

// 카메라 온/오프 토글 함수
const toggleCamera = () => {
	isvideoOn.value = !isvideoOn.value;
	console.log('비디오 ' + (isvideoOn.value ? '온' : '오프'));

	mediaStream.getVideoTracks().forEach((track) => {
		track.enabled = isvideoOn.value;
	});
	videoElement.value.srcObject = mediaStream;
};

// 마이크 온/오프 토글 함수
const toggleMicro = () => {
	isMicroOn.value = !isMicroOn.value;
	console.log('마이크 ' + (isMicroOn.value ? '온' : '오프'));

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
	width: 10vw;
	height: 9vw;
	max-width: 200px;
	max-height: 180px;
}
</style>
