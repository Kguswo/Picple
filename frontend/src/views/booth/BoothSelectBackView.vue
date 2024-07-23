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
  console.log("Select Booth 페이지 호출되었습니다");
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

// background 변경을 위한 함수 부분

// background 변경을 위한 변수
const bgImage = ref('');

//임시 이미지
const images = [
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
    'https://via.placeholder.com/400',
    'https://via.placeholder.com/400/ff7f7f/333333?text=Image+1',
    'https://via.placeholder.com/400/7f7fff/333333?text=Image+2',
    'https://via.placeholder.com/400/7fff7f/333333?text=Image+3',
  ];

const changeImage = (image) => {
  console.log('이미지 변경 클릭')
    bgImage.value = image;
};

//파일 업로드
const fileUpload = () =>{
  console.log('파일 업로드 클릭')
  // axios를 통한 파일 업로드 필요

}

</script>

<template>
  <WhiteBoardComp class="whiteboard-area-booth">
    <div class="booth-content">
      <div class="close-btn">
        <button class="close" @click="navigateTo('main')">X</button> 
      </div>

      <div class="booth-content-main">
        <BoothBack class="booth-camera-box" >
          <div :style="{ backgroundImage: `url(${bgImage})` }" class="photo-zone" >

          </div>

          <div class="create-btn">
              <button class="circle-btn" @click="toggleMicro">
                  <img :src="isMicroOn ? microOn : microOff"  alt="M">
              </button>
              <button class="circle-btn" @click="toggleCamera">
                  <img :src="isCameraOn ? cameraOn : cameraOff"  alt="C">
              </button>

              <button class="ract-btn" @click="toggleMirror">반전</button>
          </div>

        </BoothBack >
      
        <BoothBack class="booth-select-box">
          <div class="select-text-box">
            <div>
              배경 선택
            </div>
            
          </div>
          <div class="background-box">
            <div class="background-box-scroll">
              <img class="thumbnail" v-for=" (img,idx) in images" 
              :key="idx" 
              :src="img"
              @click="changeImage(img)" 
              alt="Thumbnail">
                
              </img>
            </div>
          </div>
        </BoothBack>
      </div>
    
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
.booth-content-main{
  display: flex;
  flex-wrap: wrap;
  align-content: center;
  justify-content: space-evenly;
  width: 100%;
  height: 95%;

  .photo-zone{
    justify-content: center;
    width: 95%;
    height: 87%;

    border-radius: 20px;
    background-size: cover; 
    background-position: center; 
    transition: background-image 0.5s; 
  }

  .create-btn{
    height: 10%;
    width: 90%;
    display: flex;
    justify-content: space-between;

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
      cursor: pointer; 
    }
  }
}
.select-text-box{
  display: flex;
  height: 15%;
  width: 90%;
  flex-direction: column;
  align-items: center;
  justify-content: space-evenly;
}
.background-box{
  height: 85%;
  width: 90%;
  overflow: hidden;
  
  .background-box-scroll{
    overflow-y: auto;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;

    .thumbnail {
      width: auto; /* 썸네일 이미지 크기 */
      height: 150px; /* 썸네일 이미지 크기 */
      margin: 0 5px; /* 이미지 간격 */
      cursor: pointer; /* 클릭 커서 변경 */
      border: 2px solid transparent; /* 기본 테두리 설정 */
      transition: border 0.3s; /* 테두리 전환 효과 */

      &:hover{
        border: 2px solid red;
      }
    }
    &::-webkit-scrollbar {
      display: none;
    }
  }
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
margin: 5px;
padding: 5px;

}

.ract-btn{
border:none;
border-radius: 10px;
width: 75px;
height: 30px;
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
  .close{
    border:none;
  }
}
</style>
