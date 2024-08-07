<script setup>
import axios from "axios";
import { OpenVidu } from "openvidu-browser";
import { ref, onMounted, onUnmounted } from "vue";

// OpenVidu 객체와 세션을 저장할 ref 생성
const OV = ref(null);
const session = ref(null);

// 로컬 스트림(publisher)과 원격 참가자 스트림(subscribers)을 저장할 ref 생성
const publisher = ref(null);
const subscribers = ref([]);

// 로컬 비디오 요소에 대한 ref 생성
const myVideo = ref(null);

// 고정된 세션 ID 설정
// 주의: 실제 환경에서는 이 ID를 동적으로 생성하거나 서버에서 관리하는 것이 좋습니다.
const FIXED_SESSION_ID = "myFixedSessionId";

// 세션 참가 함수
const joinSession = async () => {
  try {
    // OpenVidu 객체 생성
    OV.value = new OpenVidu();

    // 새 세션 초기화
    session.value = OV.value.initSession();

    // 새 참가자가 들어왔을 때의 이벤트 핸들러
    session.value.on("streamCreated", ({ stream }) => {
      // 새 스트림을 구독하고 subscribers 배열에 추가
      console.log("새로운 스트림 생성됨:", stream.streamId);
      const subscriber = session.value.subscribe(stream);
      subscribers.value.push(subscriber);
    });

    // 참가자가 나갔을 때의 이벤트 핸들러
    session.value.on("streamDestroyed", ({ stream }) => {
      // 나간 참가자의 스트림을 subscribers 배열에서 제거
      console.log("스트림 제거됨:", stream.streamId);
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
    const videoDevices = devices.filter((device) => device.kind === "videoinput");

    // Publisher 옵션 설정
    const publisherOptions = {
      audioSource: undefined, // 기본 오디오 소스 사용
      videoSource: videoDevices.length > 0 ? videoDevices[0].deviceId : undefined, // 첫 번째 비디오 장치 사용
      publishAudio: true, // 오디오 발행
      publishVideo: true, // 비디오 발행
      resolution: "640x480", // 해상도 설정
      frameRate: 30, // 프레임 레이트 설정
      insertMode: "APPEND", // 비디오 삽입 모드
      mirror: false, // 미러링 비활성화
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
    console.error("세션 참가 중 오류 발생:", error);
    if (error.name === "DEVICE_ACCESS_DENIED") {
      alert("카메라 또는 마이크 접근이 거부되었습니다. 브라우저 설정에서 권한을 확인해주세요.");
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
          Authorization: "Basic " + btoa("OPENVIDUAPP:MY_SECRET"),
          "Content-Type": "application/json",
        },
      }
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
    "https://localhost:4443/openvidu/api/sessions",
    { customSessionId: sessionId },
    {
      headers: {
        Authorization: "Basic " + btoa("OPENVIDUAPP:MY_SECRET"),
        "Content-Type": "application/json",
      },
    }
  );
};

// 컴포넌트가 마운트될 때 실행될 로직
onMounted(() => {
  // 필요한 초기화 로직
  // 예: 디바이스 권한 요청, 초기 UI 설정 등
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
    <h1>OpenVidu 화상 회의</h1>
    <button @click="joinSession">세션 참가</button>

    <div class="video-container">
      <!-- 로컬 비디오 스트림 -->
      <div v-if="publisher" class="stream-container">
        <h3>내 비디오</h3>
        <video ref="myVideo" autoplay muted playsinline></video>
      </div>

      <!-- 원격 참가자 비디오 스트림 -->
      <div v-for="sub in subscribers" :key="sub.stream.streamId" class="stream-container">
        <h3>참가자 비디오</h3>
        <video :srcObject="sub.stream.getMediaStream()" autoplay playsinline></video>
      </div>
    </div>
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
</style>
