<script setup>
import BoothBack from "@/components/booth/boothBackComp.vue"
import { ref, onMounted, onUnmounted } from 'vue';
import { useRouter } from "vue-router";

const videoElement = ref(null);
let mediaStream = null;
let isMirrored = false;
let isCameraOn = ref(true);

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

onMounted(async () => {
  console.log("호출되었습니다");
  try {
    mediaStream = await navigator.mediaDevices.getUserMedia({ video: true });
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

const toggleMirror = () => {
  isMirrored = !isMirrored;
  videoElement.value.style.transform = isMirrored ? 'scaleX(-1)' : 'scaleX(1)';
};

const toggleCamera = () => {
  isCameraOn.value = !isCameraOn.value;
  if (isCameraOn.value) {
    videoElement.value.srcObject = mediaStream;
  } else {
    videoElement.value.srcObject = null;
  }
};
</script>

<template>
    <div class="close-btn">
        <button class="close" @click="navigateTo('main')">closeBtn</button> 
    </div>
    <BoothBack class="booth-create" >
        <div class="create-content">
            <div class="mycam-box">
                <video ref="videoElement" autoplay></video>
                <div v-if="!isCameraOn">카메라를 켜주세요!</div>
            </div>


            <div class="create-btn">
                <div class="left-btn">
                    <button class="circle-btn">M</button>
                    <button class="circle-btn" @click="toggleCamera">C</button>
                    <button class="ract-btn" @click="toggleMirror">반전</button>
                </div>
                <div class="right-btn">
                    <button class="ract-btn">생성</button>
                    <button class="ract-btn" @click="navigateTo('main')">취소</button>
                </div>
            </div>
        </div>
    </BoothBack>
    
</template>

<style scoped>
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
    margin: 5px;
    padding: 5px;
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