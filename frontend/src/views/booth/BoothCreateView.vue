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

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;
const OPENVIDU_SERVER_SECRET = import.meta.env.VITE_OPENVIDU_SERVER_SECRET;

const boothStore = useBoothStore();

const OV = ref(null);
const session = ref(null);
const publisher = ref(null);
const subscribers = ref([]);
const isCreating = ref(false);

const router = useRouter();

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

const joinSession = async (sessionId) => {
	try {
		console.log('Joining session with ID:', sessionId);
		await createSession(sessionId);

		OV.value = new OpenVidu();
		session.value = OV.value.initSession();

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

		session.value.on('streamDestroyed', ({ stream }) => {
			console.log('스트림 제거됨:', stream.streamId);
			const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
			if (index >= 0) {
				subscribers.value.splice(index, 1);
			}
		});

		const token = await getToken(sessionId);
		console.log('Token received:', token);
		await session.value.connect(token);

		const devices = await OV.value.getDevices();
		const videoDevices = devices.filter((device) => device.kind === 'videoinput');

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

		publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);
		await session.value.publish(publisher.value);

		router.replace({ path: `/booth/${sessionId}` });
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
	if (isCreating.value) return;
    isCreating.value = true;

	try {
		if (!WebSocketService.isConnected()) {
			await WebSocketService.connect(import.meta.env.VITE_WS);
		}
		const boothId = await WebSocketService.createBooth();
		console.log('Created booth with ID:', boothId);

		const token = await createSessionAndGetToken(boothId);
		console.log('Obtained token:', token);

		boothStore.setSessionInfo({ sessionId: boothId, token, isHost: true });
		boothStore.setBoothCode(boothId);
		boothCode.value = boothId;

		console.log('Session info stored:', { sessionId: boothId, token, isHost: true });
		console.log('Booth code stored:', boothId);

		// 부스 코드를 표시한 후 잠시 대기
		await new Promise(resolve => setTimeout(resolve, 3000));

		router.push({ path: `/booth/${boothId}` });
	} catch (error) {
		console.error('Failed to create booth:', error);
		alert('부스 생성에 실패했습니다. 다시 시도해 주세요.');
	} finally {
        isCreating.value = false;
    }
};

const boothCode = ref('');
const videoElement = ref(null);
let mediaStream = null;
let isMirrored = ref(true);
let isvideoOn = ref(true);
let isMicroOn = ref(true);
const isLoading = ref(true);
const loadingText = ref('화면 준비 중..');

const navigateTo = (path) => {
	router.push({ name: path });
};

onMounted(() => {
	WebSocketService.connect();
	WebSocketService.setBoothStore(boothStore);
	WebSocketService.on('background_info', (message) => {
		boothStore.setBgImage(message.backgroundImage);
	});

	let dotCount = 0;
	const dotInterval = setInterval(() => {
		dotCount = (dotCount + 1) % 4;
		loadingText.value = `화면 준비 중${'.'.repeat(dotCount)}`;
	}, 200);

	const startTime = Date.now();
	let timeoutId = setTimeout(() => {
		if (!mediaStream) {
			isvideoOn.value = false;
			isLoading.value = false;
		}
	}, 5000);

	navigator.mediaDevices
		.getUserMedia({ video: true, audio: true })
		.then((stream) => {
			clearTimeout(timeoutId);
			mediaStream = stream;
			videoElement.value.srcObject = mediaStream;
			videoElement.value.style.transform = 'scaleX(-1)';

			WebSocketService.connect(import.meta.env.VITE_WS);
		})
		.catch((error) => {
			console.error('Error accessing webcam:', error);
			isvideoOn.value = false;
		})
		.finally(() => {
			const elapsedTime = Date.now() - startTime;
			const remainingTime = Math.max(2000 - elapsedTime, 0);

			setTimeout(() => {
				clearInterval(dotInterval);
				isLoading.value = false;
			}, remainingTime);
		});
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
		<div class="booth-content">
			<div v-if="boothCode" class="booth-code">
				부스 코드: {{ boothCode }}
			</div>
			<div class="booth-top-div">
				<div>영상 테스트</div>
				<div class="close-btn">
					<button
						class="close"
						@click="navigateTo('main')"
					>
						나가기
					</button>
				</div>
			</div>

			<div class="video-container">
				<div
					v-show="isLoading"
					class="loading-overlay"
				>
					<div class="video-ready">
						<img
							src="@/assets/icon/모래시계.gif"
							alt="Loading..."
						/>
						<div class="video-ready-text">{{ loadingText }}</div>
					</div>
				</div>
				<video
					ref="videoElement"
					v-show="!isLoading && isvideoOn"
					autoplay
				></video>
				<div
					v-show="!isvideoOn && !isLoading"
					class="video-off"
				>
					카메라가 꺼져있습니다!
				</div>
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
							@click="handleCreateBooth"
						>
							{{ isCreating ? '생성 중...' : '생성' }}
						</button>
						<button
							class="ract-btn"
							@click="navigateTo('main')"
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
}

.circle-btn img {
	width: 24px;
	height: 24px;
}

.circle-btn:hover {
	transform: scale(1.1);
	background-color: #5ec9cd  ;
}

.ract-btn {
	border: none;
	border-radius: 20px;
	width: 100px;
	height: 40px;
	margin: 5px;
	padding: 10px;
	background-color: #E6A4A4 ;
	color: white;
	font-size: 16px;

	text-align: center;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	transition: background-color 0.3s ease, transform 0.2s ease;
}

.ract-btn:hover {
	background-color: #f58080;
	transform: scale(1.05);
}

.ract-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
}

.left-btn {
	display: flex;
}

.loading-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
    background-color: rgba(236, 226, 198, 0.9);
	display: flex;
	justify-content: center;
	align-items: center;
	z-index: 9999;
}

.video-ready {
	position: relative;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
}

.video-ready img {
	width: 12vw; /* 이미지 크기를 키움 */
	height: 11vw;
	max-width: 250px;
	max-height: 230px;
}

.video-ready-text {
	position: absolute;
	bottom: 5px; /* 텍스트가 이미지 위로 올라오게 설정 */
	text-align: center;
	font-size: 18px;
	color: white;
	width: 100%;
}

video {
	transform: scaleX(-1);
	object-fit: cover;
	width: 100%;
	height: 100%;
	border-radius: 20px;
}

.video-off {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: black;
	opacity: 0.7; /* 약간의 투명도 추가 (선택 사항) */
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 18px;
	color: #e4f38e;
	font-weight: bold;
	border-radius: 20px;
}

.video-container {
	position: relative;
	width: 93%;
	height: 74%;
	border-radius: 20px;
	overflow: hidden;
}

.booth-top-div {
	width: 93%;
	height: 7%;
	display: flex;
	align-items: center;
	font-size: 30px;
	justify-content: space-between;

	.close-btn {
		padding: 5px;
		display: flex;
		justify-content: right;
		.close {
			background-color: transparent;
			border: none;
			font-size: 22px;
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
.booth-code {
  font-size: 24px;
  font-weight: bold;
  margin-top: 20px;
  padding: 10px;
  background-color: #f0f0f0;
  border-radius: 5px;
}
</style>
