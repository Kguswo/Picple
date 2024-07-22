<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/boothBackComp.vue"

import cameraOn from '@/assets/icon/camera_on.png';
import cameraOff from '@/assets/icon/camera_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

import { RouterView, RouterLink, useRoute,useRouter } from "vue-router";
import { ref, onMounted, onUnmounted } from 'vue';

// 비디오 표현을 위한 변수
const videoElement = ref(null); 
let mediaStream = null;

// 화면 표시에 있어 사용되는 변수
let isMirrored = false;
let isCameraOn = ref(true); 
let isMicroOn = ref(true);

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

onMounted(async () => {
  console.log("Create Booth 페이지 호출되었습니다");
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({ video: true,audio: true });
    videoElement.value.srcObject = mediaStream;
  } catch (error) {
    console.error('Error accessing webcam:', error);
  }
});

onUnmounted(() => {
  if (mediaStream) {
    mediaStream.getTracks().forEach((track) => {
      track.stop();
    });
  }
});

// 거울모드 여부
const toggleMirror = () => {
  isMirrored = !isMirrored;
  videoElement.value.style.transform = isMirrored ? 'scaleX(-1)' : 'scaleX(1)';
};

//카메라의 온오프 
const toggleCamera = () => {
  isCameraOn.value = !isCameraOn.value;
  console.log('비디오 온')

  if (isCameraOn.value) {
    mediaStream.getVideoTracks().forEach((track) => {
      track.enabled = true; // 비디오 트랙 활성화
    });
    videoElement.value.srcObject = mediaStream;
  } else {
    console.log('비디오 오프')

    mediaStream.getVideoTracks().forEach((track) => {
      track.enabled = false;  // 비디오 트랙 비활성화
    });
    videoElement.value.srcObject = mediaStream; 
  }
};

//마이크의 온오프
const toggleMicro = () => {
  isMicroOn.value = !isMicroOn.value;
  if (isMicroOn.value) {
    console.log('마이크 온')

    mediaStream.getAudioTracks().forEach((track) => {
      track.enabled = true; // 오디오 트랙을 활성화
    });
  } else {
    console.log('마이크 오프')

    mediaStream.getAudioTracks().forEach((track) => {
      track.enabled = false; // 오디오  트랙을 비활성화
    });
  }
};

</script>

<template>
  <WhiteBoardComp class="whiteboard-area-booth">
    <div class="booth-content">
      <div class="close-btn">
        <button class="close" @click="navigateTo('main')">closeBtn</button> 
    </div>
    <BoothBack class="booth-create" >
        <div class="create-content">
            <div class="mycam-box">
              <!-- v-if로 하면 카메라가 나오지 않아 v-show로 미리 렌더링 -->
                <div v-show="isCameraOn">
                  <video ref="videoElement" autoplay></video>
                </div> 
                <div v-if="!isCameraOn">카메라가 꺼져있습니다!</div>
            </div>


            <div class="create-btn">
                <div class="left-btn">
                    <button class="circle-btn" @click="toggleMicro">
                      <img :src="isMicroOn ? microOn : microOff"  alt="M">
                    </button>
                    <button class="circle-btn" @click="toggleCamera">
                      <img :src="isCameraOn ? cameraOn : cameraOff"  alt="C">
                    </button>

                    <button class="ract-btn" @click="toggleMirror">반전</button>
                </div>
                <div class="right-btn">
                    <button class="ract-btn">생성</button>
                    <button class="ract-btn" @click="navigateTo('main')">취소</button>
                </div>
            </div>
        </div>
    </BoothBack>
    
    </div>
  </WhiteBoardComp>
</template>

<style scoped>
.booth-content{

    /* display */
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;

    width: 100%;
    height: 100%;
}

.create-content{

display: flex;
flex-direction: column;
justify-content: space-evenly;
align-items: center;

height: 100%;
width: 100%;
}
.mycam-box{
margin-top: 15px ;
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
.create-btn{
height: 10%;
width: 90%;
display: flex;
justify-content: space-between;
}

.circle-btn{
border:none;
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

.ract-btn{
border:none;
border-radius: 20px;
width: 75px;
height: 50px;
margin: 5px;
padding: 5px;

&:hover{
    background-color: rgb(136, 136, 136)
}


}
.close-btn{
width: 90%;
display: flex;
justify-content: right;
}
</style>
