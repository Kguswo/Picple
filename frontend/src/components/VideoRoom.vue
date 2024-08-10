<template>
	<div>
		<h1>OpenVidu 화상 회의</h1>
		<button @click="joinSession">세션 참가</button>
	</div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch } from 'vue';
import axios from 'axios';
import { OpenVidu } from 'openvidu-browser';
import ChatModal from '@/components/chat/ChatModal.vue';

const props = defineProps({
	username: {
		type: String,
		required: true,
	},
});

const OPENVIDU_SERVER_URL = import.meta.env.VITE_API_OPENVIDU_SERVER;

// username prop 변경 감지
watch(
	() => props.username,
	(newUsername) => {
		console.log('Username changed:', newUsername);
	},
);

// OpenVidu 객체와 세션을 저장할 ref 생성
const OV = ref(null);
const session = ref(null);

// 로컬 스트림(publisher)과 원격 참가자 스트림(subscribers)을 저장할 ref 생성
const publisher = ref(null);
const subscribers = ref([]);

// 고정된 세션 ID 설정
const FIXED_SESSION_ID = 'myFixedSessionId';

// 채팅 모달 상태
const isChatOpen = ref(false);

// 채팅창 열기/닫기 메서드
const toggleChat = () => {
	isChatOpen.value = !isChatOpen.value;
};

// 세션 참가 함수
const joinSession = async () => {
	try {
		// OpenVidu 객체 생성
		OV.value = new OpenVidu();

		// 새 세션 초기화
		session.value = OV.value.initSession();

		// 새 참가자가 들어왔을 때의 이벤트 핸들러
		session.value.on('streamCreated', ({ stream }) => {
			console.log('새로운 스트림 생성됨:', stream.streamId);
			const subscriber = session.value.subscribe(stream);
			subscribers.value.push(subscriber);
		});

		// 참가자가 나갔을 때의 이벤트 핸들러
		session.value.on('streamDestroyed', ({ stream }) => {
			console.log('스트림 제거됨:', stream.streamId);
			const index = subscribers.value.findIndex((sub) => sub.stream.streamId === stream.streamId);
			if (index >= 0) {
				subscribers.value.splice(index, 1);
			}
		});

		// 세션 연결을 위한 토큰 얻기
		const token = await getToken();

		// 세션에 연결
		await session.value.connect(token, { clientData: props.username });

		// 사용 가능한 비디오 장치 가져오기
		const devices = await OV.value.getDevices();
		const videoDevices = devices.filter((device) => device.kind === 'videoinput');

		// Publisher 옵션 설정
		const publisherOptions = {
			audioSource: undefined,
			videoSource: videoDevices.length > 0 ? videoDevices[0].deviceId : undefined,
			publishAudio: true,
			publishVideo: true,
			resolution: '640x480',
			frameRate: 30,
			insertMode: 'APPEND',
			mirror: false,
		};

		// 로컬 웹캠 스트림 생성
		publisher.value = await OV.value.initPublisherAsync(undefined, publisherOptions);

		// 로컬 스트림을 세션에 게시
		await session.value.publish(publisher.value);

		// 로컬 비디오 스트림 설정
		if (myVideo.value && publisher.value.stream && publisher.value.stream.getMediaStream()) {
			myVideo.value.srcObject = publisher.value.stream.getMediaStream();
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
		const response = await axios.post(
			`${OPENVIDU_SERVER_URL}/openvidu/api/sessions/${FIXED_SESSION_ID}/connection`,
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
			await createSession(FIXED_SESSION_ID);
			return getToken();
		}
		throw error;
	}
};

// 고정된 세션 ID로 새 세션 생성
const createSession = async (sessionId) => {
	await axios.post(
		`${OPENVIDU_SERVER_URL}/openvidu/api/sessions`,
		{ customSessionId: sessionId },
		{
			headers: {
				Authorization: 'Basic ' + btoa('OPENVIDUAPP:MY_SECRET'),
				'Content-Type': 'application/json',
			},
		},
	);
};

// 컴포넌트가 마운트될 때 실행될 로직
onMounted(() => {
	console.log('VideoRoom mounted. Username:', props.username);
});

// 컴포넌트가 언마운트될 때 실행될 로직
onUnmounted(() => {
	if (session.value) {
		session.value.disconnect();
	}
});
</script>

<template>
	<div>
		<h1>OpenVidu 화상 회의</h1>
		<p>현재 사용자: {{ username }}</p>
		<button @click="joinSession">세션 참가</button>

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
				v-for="sub in subscribers"
				:key="sub.stream.streamId"
				class="stream-container"
			>
				<h3>참가자 비디오</h3>
				<video
					:srcObject="sub.stream.getMediaStream()"
					autoplay
					playsinline
				></video>
			</div>
		</div>

		<button
			class="chat-icon"
			@click="toggleChat"
		>
			채팅창
		</button>

		<ChatModal
			v-if="isChatOpen"
			:username="username"
			:session="session"
			@close="toggleChat"
		/>
	</div>
</template>

<style scoped>
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
</style>
