<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue";

import videoOn from "@/assets/icon/video_on.png";
import videoOff from "@/assets/icon/video_off.png";
import microOn from "@/assets/icon/micro_on.png";
import microOff from "@/assets/icon/micro_off.png";

import Swal from "sweetalert2";

import { RouterView, useRouter } from "vue-router";
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import html2canvas from "html2canvas";

//화면 전환을 위한 router
const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

// 비디오 표현을 위한 변수
const videoElement = ref(null);
let mediaStream = null;

// 화면 표시에 있어 사용되는 변수
let isMirrored = false; // 거울모드 여부
let isvideoOn = ref(true); // 비디오 ON/OFF 여부
let isMicroOn = ref(true); // 마이크 ON/OFF 여부
const remainPicCnt = ref(10);

onMounted(async () => {
    console.log("shootView Mounted!");
    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            video: true,
            audio: true,
        }); // mediaStream을 열어 영상과 목소리 넣기

        videoElement.value.srcObject = mediaStream;
    } catch (error) {
        console.error("Error accessing webcam:", error);
    }
});

onUnmounted(() => {
    console.log("shootView unMounted!");
    if (mediaStream) {
        mediaStream.getTracks().forEach((track) => {
            track.stop();
        });
    }
});

// 거울모드 여부
const toggleMirror = () => {
    isMirrored = !isMirrored;
    videoElement.value.style.transform = isMirrored
        ? "scaleX(-1)"
        : "scaleX(1)";
};

//카메라의 온오프
const toggleCamera = () => {
    isvideoOn.value = !isvideoOn.value;
    console.log("비디오 온");

    if (isvideoOn.value) {
        mediaStream.getVideoTracks().forEach((track) => {
            track.enabled = true; // 비디오 트랙 활성화
        });
        videoElement.value.srcObject = mediaStream;
    } else {
        console.log("비디오 오프");

        mediaStream.getVideoTracks().forEach((track) => {
            track.enabled = false; // 비디오 트랙 비활성화
        });
        videoElement.value.srcObject = mediaStream;
    }
};

//마이크의 온오프
const toggleMicro = () => {
    isMicroOn.value = !isMicroOn.value;
    if (isMicroOn.value) {
        console.log("마이크 온");

        mediaStream.getAudioTracks().forEach((track) => {
            track.enabled = true; // 오디오 트랙을 활성화
        });
    } else {
        console.log("마이크 오프");

        mediaStream.getAudioTracks().forEach((track) => {
            track.enabled = false; // 오디오  트랙을 비활성화
        });
    }
};

// background 변경을 위한 변수
const bgImage = ref("https://via.placeholder.com/400");

const changeImage = (image) => {
    console.log("이미지 변경 클릭", image);
    bgImage.value = image;
};

//촬영 버튼
const captureArea = ref(null); // 사진 저장 범위 ref를 통해 되어있음
const images = ref([]); // 찍힌 사진 저장 배열
const countdown = ref(0);
var cameraaudio = new Audio("/src/assets/audio/shutter.mp3"); // 카메라 찰칵 오디오 파일 js는 직접경로 필요로 @불가

// 카운트다운 진행 함수
const startCountdown = () => {
    const countdownInterval = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
            clearInterval(countdownInterval);
            Swal.close(); // 모달 닫기
            capturePhoto(); // 카운트다운이 끝나면 사진 촬영
        } else {
            Swal.update({
                html: `<p style='color:white; font-size:50px;'>${countdown.value}</h3>`, // 시간이 2초 이하일 때 나타내는 것
            });
        }
    }, 1000);
};

// 사진 촬영 함수
const takePhoto = async () => {
    console.log("사진 찍기");
    // 이미지가 로드되었는지 확인
    const img = new Image();
    img.crossOrigin = "Anonymous";
    img.src = bgImage.value;

    countdown.value = 3;

    img.onload = async () => {
        // DOM 업데이트가 완료된 후에 실행
        await nextTick();

        Swal.fire({
            title: `<h1 style='color:white;'>포즈!</h1>`,
            html: `<p style='color:white; font-size:50px;'>${countdown.value}</p>`, //text 부분
            showConfirmButton: false, // 기본 버튼을 없애는 것
            background: "rgba(0, 0, 0, 0.3)", // 모달 배경을 투명하게 설정
            backdrop: false, // 모달 배경 투명도 조정
            didOpen: () => {
                startCountdown();
            },
        });
    };

    img.onerror = async (error) => {
        console.error("배경 로딩 에러 발생: ", error);
        await Swal.fire({
            title: "@배경 오류 발생@", // Alert 제목
            text: "해당 사진은 배경으로 사용할 수 없습니다!",
            icon: "warning",
        });
    };
};

// 사진 캡처 함수
const capturePhoto = async () => {
    console.log("사진 캡처 시작");
    await nextTick();
    cameraaudio.play();
    html2canvas(captureArea.value, { useCORS: true, allowTaint: false })
        .then(async (canvas) => {
            const imageData = canvas.toDataURL("image/png");
            images.value.push(imageData);
            remainPicCnt.value = 10 - images.value.length;
            if (images.value.length === 10) {
                const { value: result } = await Swal.fire({
                    title: "사진 촬영 종료", // Alert 제목
                    text: "10장을 모두 촬영하여 프레임 선택창으로 이동합니다!",
                    icon: "success",
                });
                if (result) {
                    router.push("/");
                }
            }
        })
        .catch((error) => {
            console.error("이미지 캡쳐 에러 발생: ", error);
        });
};

// 촬영 종료
const exitphoto = async () => {
    console.log("촬영종료");

    const { value: result } = await Swal.fire({
        title: "촬영 끝내기",
        text: "촬영을 종료하고 저장을 위해 나가시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "확인",
        cancelButtonText: "취소",
    });

    if (result) {
        // 확인 버튼 클릭 시 실행되는 코드
        Swal.fire("저장", "사진 선택 화면으로 이동합니다!", "success");
        router.push("/"); // 사진 선택 화면으로 이동하도록 변경 필요
    } else {
        // 취소 버튼 클릭 시 실행되는 코드
        Swal.fire("취소", "촬영을 계속합니다!", "error");
    }
};

// 컴포넌트 변경을 위한 변수 1- 배경선택, 2 - 사진 보기
const showtype = ref(1);

const changeComponent = () => {
    showtype.value = showtype.value === 1 ? 2 : 1; // showtype 토글
    navigateTo(showtype.value === 1 ? "background" : "showphoto");
};
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="booth-top-div">
                <div>남은 사진 수: {{ remainPicCnt }}/10</div>
                <div class="close-btn">
                    <button class="close" @click="navigateTo('main')">
                        나가기
                    </button>
                </div>
            </div>

            <div class="booth-content-main">
                <BoothBack class="booth-camera-box">
                    <div
                        ref="captureArea"
                        :style="{ backgroundImage: `url(${bgImage})` }"
                        class="photo-zone"
                    >
                        <div v-show="isvideoOn">
                            <video
                                ref="videoElement"
                                autoplay
                                style="width: 30%; height: fit-content"
                            ></video>
                        </div>
                    </div>

                    <div class="create-btn">
                        <div class="left-btn">
                            <button class="circle-btn" @click="toggleMicro">
                                <img
                                    :src="isMicroOn ? microOn : microOff"
                                    alt="M"
                                />
                            </button>
                            <button class="circle-btn" @click="toggleCamera">
                                <img
                                    :src="isvideoOn ? videoOn : videoOff"
                                    alt="C"
                                />
                            </button>
                            <button class="ract-btn" @click="toggleMirror">
                                반전
                            </button>
                        </div>

                        <button @click="takePhoto" class="take-photo">
                            <img src="@/assets/icon/camera.png" alt="" />
                        </button>
                        <div class="right-btn">
                            <button class="ract-btn" @click="exitphoto">
                                템플릿 선택
                            </button>
                        </div>
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
                        <RouterView
                            v-if="showtype === 1"
                            @update="changeImage"
                        ></RouterView>
                        <RouterView v-else :images="images"> </RouterView>
                    </div>
                </BoothBack>
            </div>
        </div>
    </WhiteBoardComp>
</template>

<style scoped>
.select-text-box {
    display: flex;
    height: 85%;
    width: 90%;
    flex-direction: column;
    align-items: center;
}

.select-box-top {
    height: 15%;
    width: 85%;

    display: flex;
    justify-content: space-between;
    align-items: center;
    button {
        background: transparent;
        border: none;
        width: 20%;
        height: 100%;
        font-size: 40px;

        &:hover {
            color: rgb(137, 137, 137);
        }
    }
}
.booth-content {
    /* display */
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;

    width: 100%;
    height: 100%;
}
.booth-top-div {
    width: 95%;
    height: 8%;
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
        }
    }
}

.booth-content-main {
    display: flex;
    flex-wrap: wrap;
    align-content: center;
    justify-content: space-evenly;
    width: 100%;
    height: 95%;

    .photo-zone {
        justify-content: center;
        width: 95%;
        height: 87%;

        border-radius: 20px;
        background-size: cover;
        background-position: center;
        background-size: cover;
        transition: background-image 0.5s;
    }

    .create-btn {
        height: 10%;
        width: 90%;
        display: flex;
        flex-wrap: wrap;
        justify-content: space-between;
        align-content: flex-end;
        align-items: center;
        .left-btn {
            display: flex;
            margin: 5px;
            align-items: center;
            width: 40%;
        }
        .right-btn {
            display: flex;
            margin: 5px;
            flex-direction: row-reverse;
            align-items: center;
            width: 40%;
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
            cursor: pointer;
        }
        .take-photo {
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
            border: 0.3px solid black;
            background-color: transparent;
            cursor: pointer;

            &:hover {
                background-color: rgb(203, 203, 203);
            }
            &:active {
                background-color: rgb(90, 90, 90);
            }
        }
    }
}

.ract-btn {
    border: none;
    border-radius: 10px;
    width: 100px;
    height: 36px;
    margin: 5px;
    padding: 5px;

    &:hover {
        background-color: rgb(136, 136, 136);
    }
}
.timer-modal {
    box-shadow: none;
    border: none;
    color: rgb(255, 255, 255);
}
</style>
