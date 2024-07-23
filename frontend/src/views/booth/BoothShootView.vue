<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue"

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

import { RouterView, useRouter } from "vue-router";
import { ref, onMounted, onUnmounted,defineProps, onUpdated } from 'vue';

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

// // 비디오 표현을 위한 변수
// const videoElement = ref(null); 
// let mediaStream = null;

// // 화면 표시에 있어 사용되는 변수
// let isMirrored = false;
// let isvideoOn = ref(true); 
// let isMicroOn = ref(true);

// onMounted(async () => {
//   console.log('shootView Mounted!')
//   try {
//     mediaStream = await navigator.mediaDevices.getUserMedia({ video: true,audio: true });

//     videoElement.value.srcObject = mediaStream;
//   } catch (error) {
//     console.error('Error accessing webcam:', error);
//   }
// });

// onUnmounted(() => {
//   console.log('shootView unMounted!')
//   if (mediaStream) {
//     mediaStream.getTracks().forEach((track) => {
//       track.stop();
//     });
//   }
// });

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

const takePhoto = () =>{
  console.log('사진 찍기')
}

// background 변경을 위한 변수
const bgImage = ref('https://via.placeholder.com/400');

const changeImage = (image) => {
  console.log('이미지 변경 클릭')
    bgImage.value = image;
};

// 컴포넌트 변경을 위한 변수 1- 배경선택, 2 - 사진 보기
const showtype = ref(1);

const changeComponent = () =>{
  showtype.value = showtype.value === 1 ? 2 : 1; // showtype 토글
  navigateTo(showtype.value === 1 ? 'background' : 'showphoto');
}

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
          <div class="select-box-top">
            <button class="prev-btn" @click="changeComponent">
              &lt
            </button>
            <div class="box-name">
              <p v-if="showtype === 1">배경선택</p>
              <p v-if="showtype === 2">사진보기</p>
            </div>
            <button class="next-btn" @click="changeComponent">
              &gt
            </button>
          </div>          
          
          <div class="select-text-box">
            <RouterView v-if="showtype === 1" @update="changeImage"></RouterView>
            <RouterView v-else> </RouterView>
          </div>
        </BoothBack>
      </div>
    </div>
  </WhiteBoardComp>
</template>

<style scoped>

.select-text-box{
  display: flex;
  height: 85%;
  width: 90%;
  flex-direction: column;
  align-items: center;
}

.select-box-top{
  height: 15%;
  width: 85%;
  
  display: flex;
  justify-content: space-between;
  align-items: center;
  button{
    background: transparent;
    border: none;
    width: 20%;
    height: 100%;
    font-size: 40px;

    &:hover{
      color: rgb(137, 137, 137);
    }
  }
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
