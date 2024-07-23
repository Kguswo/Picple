<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue"

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

import { RouterView, useRouter } from "vue-router";
import { ref, onMounted, onUnmounted,defineProps, onUpdated } from 'vue';

// 비디오 표현을 위한 변수
const videoElement = ref(null); 
let mediaStream = null;

// 화면 표시에 있어 사용되는 변수
let isMirrored = false;
let isvideoOn = ref(true); 
let isMicroOn = ref(true);

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

onMounted(async () => {
  console.log("Select Booth 페이지 호출되었습니다");
  // try {
  //   mediaStream = await navigator.mediaDevices.getUserMedia({ video: true,audio: true });

  //   videoElement.value.srcObject = mediaStream;
  // } catch (error) {
  //   console.error('Error accessing webcam:', error);
  // }
});

onUnmounted(() => {
  // if (mediaStream) {
  //   mediaStream.getTracks().forEach((track) => {
  //     track.stop();
  //   });
  // }
});

// // 거울모드 여부
// const toggleMirror = () => {
//   isMirrored = !isMirrored;
//   videoElement.value.style.transform = isMirrored ? 'scaleX(-1)' : 'scaleX(1)';
// };

// //카메라의 온오프 
// const toggleCamera = () => {
//   isvideoOn.value = !isvideoOn.value;
//   console.log('비디오 온')

//   if (isvideoOn.value) {
//     mediaStream.getVideoTracks().forEach((track) => {
//       track.enabled = true; // 비디오 트랙 활성화
//     });
//     videoElement.value.srcObject = mediaStream;
//   } else {
//     console.log('비디오 오프')

//     mediaStream.getVideoTracks().forEach((track) => {
//       track.enabled = false;  // 비디오 트랙 비활성화
//     });
//     videoElement.value.srcObject = mediaStream; 
//   }
// };

// //마이크의 온오프

// const toggleMicro = () => {
//   isMicroOn.value = !isMicroOn.value;
//   if (isMicroOn.value) {
//     console.log('마이크 온')

//     mediaStream.getAudioTracks().forEach((track) => {
//       track.enabled = true; // 오디오 트랙을 활성화
//     });
//   } else {
//     console.log('마이크 오프')

//     mediaStream.getAudioTracks().forEach((track) => {
//       track.enabled = false; // 오디오  트랙을 비활성화
//     });
//   }
// };
// 사진 찍기

const props = defineProps({
    'bgImage': String
})
const takePhoto = () =>{
  console.log('사진 찍기')
}

// background 변경을 위한 변수
const bgImage = ref('');

const changeImage = (image) => {
  console.log('이미지 변경 클릭')
    bgImage.value = image;
};

const showChild = ref(false);

</script>

<template>
  <WhiteBoardComp class="whiteboard-area-booth">
    <div class="booth-content">
      <div class="close-btn">
        <button class="close" @click="navigateTo('main')">X</button> 
      </div>

      <div class="booth-content-main">
        <BoothBack class="booth-camera-box">
          <div :style="{ backgroundImage: `url(${bgImage})` }" class="photo-zone"></div>

          <div class="create-btn">
            <div class="left-btn">
              <button class="circle-btn" @click="toggleMicro">
                <img :src="isMicroOn ? microOn : microOff" alt="M" />
              </button>
              <button class="circle-btn" @click="toggleCamera">
                <img :src="isvideoOn ? videoOn : videoOff" alt="C" />
              </button>
            </div>

            <button @click="takePhoto" class="take-photo">
              <img src="@/assets/icon/camera.png" alt="" />
            </button>

            <button class="ract-btn" @click="toggleMirror">반전</button>
          </div>
        </BoothBack>

        <BoothBack class="booth-select-box">
            <RouterView :bgImage="bgImage" @update="changeImage"></RouterView>
        </BoothBack>
      </div>
    </div>
  </WhiteBoardComp>
</template>

<style scoped>
.qwer{
  height: 100%;
  width: 100%;
}
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
    flex-wrap: wrap;
    align-items: center;
    .left-btn{
      display: flex;
      margin: 5px;
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
      cursor: pointer; 
    }
    .take-photo{
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
      border: 0.3px solid black;
      background-color: transparent; 
      cursor: pointer; 

      &:hover{
        background-color: rgb(203, 203, 203);
      }
      &:active{
        background-color: rgb(90, 90, 90);
      }
    }
  }
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
