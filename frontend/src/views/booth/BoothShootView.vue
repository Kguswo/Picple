<script setup>
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue";

import { usePhotoStore } from "@/stores/photoStore";
import { RouterView, useRouter } from "vue-router";
import { ref, onMounted, onUnmounted, nextTick, computed, watch } from "vue";
import { useDraggable } from "@vueuse/core";

import Swal from "sweetalert2";
import * as selfieSegmentation from "@mediapipe/selfie_segmentation";

import videoOn from "@/assets/icon/video_on.png";
import videoOff from "@/assets/icon/video_off.png";
import microOn from "@/assets/icon/micro_on.png";
import microOff from "@/assets/icon/micro_off.png";

const router = useRouter();
const photoStore = usePhotoStore();

const navigateTo = (path) => {
    router.push({ name: path });
};

const videoElement = ref(null);
const canvasElement = ref(null);
let mediaStream = null;
let selfieSegmentationInstance = null;

let isMirrored = ref(false);
let isvideoOn = ref(true);
let isMicroOn = ref(true);
const remainPicCnt = ref(10);

const images = ref([]);

const videoWidth = ref(213);
const videoHeight = ref(160);

const videoContainerRef = ref(null);
const videoPosition = ref({ x: 0, y: 0 });
const videoScale = ref(1);
const videoRotation = ref(0);
const startPosition = ref({ x: 0, y: 0 });
const isProcessing = ref(true);
const isDragging = ref(false);
const isFocused = ref(false);

const videoStyle = computed(() => ({
    position: "absolute",
    left: "50%",
    top: "50%",
    transform: `translate(-50%, -50%) translate(${videoPosition.value.x}px, ${videoPosition.value.y}px) scale(${videoScale.value}) rotate(${videoRotation.value}deg)`,
    cursor: isDragging.value ? "grabbing" : "grab",
    border: isFocused.value ? "2px dashed gray" : "2px dashed lightgray",
    width: `${videoWidth.value}px`,
    height: `${videoHeight.value}px`,
}));

const centerIndicatorStyle = computed(() => ({
    position: "absolute",
    top: "50%",
    left: "50%",
    width: "10px",
    height: "1px",
    backgroundColor: isFocused.value ? "gray" : "lightgray",
    transform: "translate(-50%, -50%)",
    "&::after": {
        content: '""',
        position: "absolute",
        top: "50%",
        left: "50%",
        width: "1px",
        height: "10px",
        backgroundColor: isFocused.value ? "gray" : "lightgray",
        transform: "translate(-50%, -50%)",
    },
}));

const updateVideoStyle = () => {
    if (videoContainerRef.value) {
        Object.assign(videoContainerRef.value.style, {
            transform: `translate(-50%, -50%) translate(${videoPosition.value.x}px, ${videoPosition.value.y}px) scale(${videoScale.value}) rotate(${videoRotation.value}deg)`,
        });
    }
};

const startDrag = (event) => {
    isDragging.value = true;
    startPosition.value = {
        x: event.clientX - videoPosition.value.x,
        y: event.clientY - videoPosition.value.y,
    };
};

const onDrag = (event) => {
    if (!isDragging.value) return;

    const dx = event.clientX - startPosition.value.x;
    const dy = event.clientY - startPosition.value.y;

    const photoZone = document.querySelector(".photo-zone");
    const photoZoneRect = photoZone.getBoundingClientRect();
    const videoContainerRect = videoContainerRef.value.getBoundingClientRect();

    // 드래그된 위치가 photoZone 내에 있는지 확인
    const newPosX = Math.max(
        Math.min(dx, photoZoneRect.width / 2 - videoContainerRect.width / 2),
        -photoZoneRect.width / 2 + videoContainerRect.width / 2
    );
    const newPosY = Math.max(
        Math.min(dy, photoZoneRect.height / 2 - videoContainerRect.height / 2),
        -photoZoneRect.height / 2 + videoContainerRect.height / 2
    );

    videoPosition.value = { x: newPosX, y: newPosY };
};

const stopDrag = () => {
    isDragging.value = false;
};

// useDraggable 훅 사용
const { isDragging: isDraggableActive } = useDraggable(videoContainerRef, {
    initialValue: videoPosition,
    onStart: startDrag,
    onMove: onDrag,
    onEnd: stopDrag,
});

// loadSelfieSegmentation 함수 정의
const loadSelfieSegmentation = async () => {
    console.log("Loading Selfie Segmentation model");
    selfieSegmentationInstance = new selfieSegmentation.SelfieSegmentation({
        locateFile: (file) => {
            return `https://cdn.jsdelivr.net/npm/@mediapipe/selfie_segmentation/${file}`;
        },
    });
    selfieSegmentationInstance.setOptions({
        modelSelection: 1,
        selfieMode: true,
    });
    selfieSegmentationInstance.onResults(onResults);
    console.log("Selfie Segmentation model loaded successfully");
};

const onResults = (results) => {
    if (!results || !results.segmentationMask || !results.image) return;

    const canvasCtx = canvasElement.value.getContext("2d");
    canvasCtx.save();
    canvasCtx.clearRect(
        0,
        0,
        canvasElement.value.width,
        canvasElement.value.height
    );

    // ImageBitmap을 ImageData로 변환
    const tempCanvas = document.createElement("canvas");
    tempCanvas.width = results.segmentationMask.width;
    tempCanvas.height = results.segmentationMask.height;
    const tempCtx = tempCanvas.getContext("2d");
    tempCtx.drawImage(results.segmentationMask, 0, 0);
    const imageData = tempCtx.getImageData(
        0,
        0,
        tempCanvas.width,
        tempCanvas.height
    );

    // 임계값 설정
    const threshold = 128; // 0 ~ 255 사이의 값으로 설정

    for (let i = 0; i < imageData.data.length; i += 4) {
        if (imageData.data[i] < threshold) {
            imageData.data[i + 3] = 0; // 투명하게 설정
        }
    }

    // 세그멘테이션 마스크 적용
    const maskCanvas = document.createElement("canvas");
    maskCanvas.width = imageData.width;
    maskCanvas.height = imageData.height;
    const maskCtx = maskCanvas.getContext("2d");
    maskCtx.putImageData(imageData, 0, 0);

    // 원본 이미지 그리기
    canvasCtx.drawImage(
        results.image,
        0,
        0,
        canvasElement.value.width,
        canvasElement.value.height
    );

    // 마스크 적용
    canvasCtx.globalCompositeOperation = "destination-in";
    canvasCtx.drawImage(
        maskCanvas,
        0,
        0,
        canvasElement.value.width,
        canvasElement.value.height
    );

    canvasCtx.globalCompositeOperation = "source-over";
    canvasCtx.restore();
};

const processFrame = async () => {
    if (
        !isProcessing.value ||
        !videoElement.value ||
        !selfieSegmentationInstance
    )
        return;

    try {
        await selfieSegmentationInstance.send({ image: videoElement.value });
    } catch (error) {
        console.error("Error processing frame:", error);
    }
    requestAnimationFrame(processFrame);
};

onMounted(async () => {
    console.log("shootView Mounted!");
    try {
        mediaStream = await navigator.mediaDevices.getUserMedia({
            video: { width: 640, height: 480 },
            audio: true,
        });

        if (videoElement.value) {
            videoElement.value.srcObject = mediaStream;
            videoElement.value.onloadedmetadata = async () => {
                console.log("Video metadata loaded");
                videoWidth.value = videoElement.value.videoWidth / 3;
                videoHeight.value = videoElement.value.videoHeight / 3;
                await loadSelfieSegmentation();
                videoElement.value.play();
                requestAnimationFrame(processFrame);
            };
        } else {
            console.error("Video element not found");
        }
    } catch (error) {
        console.error("Error accessing webcam:", error);
    }

    updateVideoStyle();
    watch([videoPosition, videoScale, videoRotation], updateVideoStyle);
});

onUnmounted(() => {
    console.log("shootView unMounted!");
    isProcessing.value = false;
    if (mediaStream) {
        mediaStream.getTracks().forEach((track) => {
            track.stop();
        });
    }
});

const toggleMirror = () => {
    isMirrored.value = !isMirrored.value;
    videoElement.value.style.transform = isMirrored.value
        ? "scaleX(-1)"
        : "scaleX(1)";
    canvasElement.value.style.transform = isMirrored.value
        ? "scaleX(-1)"
        : "scaleX(1)";
    console.log("영상 반전 상태:", isMirrored.value);
};

const toggleCamera = () => {
    isvideoOn.value = !isvideoOn.value;
    console.log("비디오 온/오프:", isvideoOn.value);

    mediaStream.getVideoTracks().forEach((track) => {
        track.enabled = isvideoOn.value;
    });
    videoElement.value.srcObject = mediaStream;
};

const toggleMicro = () => {
    isMicroOn.value = !isMicroOn.value;
    console.log("마이크 온/오프:", isMicroOn.value);

    mediaStream.getAudioTracks().forEach((track) => {
        track.enabled = isMicroOn.value;
    });
};

const bgImage = ref("https://via.placeholder.com/400");

const changeImage = (image) => {
    console.log("이미지 변경 클릭", image);
    bgImage.value = image;
};

const captureArea = ref(null);
const countdown = ref(0);
var cameraaudio = new Audio("/src/assets/audio/shutter.mp3");

const startCountdown = () => {
    const countdownInterval = setInterval(() => {
        countdown.value--;
        if (countdown.value <= 0) {
            clearInterval(countdownInterval);
            Swal.close();
            capturePhoto();
        } else {
            Swal.update({
                html: `<p style='color:white; font-size:50px;'>${countdown.value}</h3>`,
            });

            // 카운트다운이 1이 되면 (즉, 1초 전에) 비디오를 일시 정지
            if (countdown.value === 1) {
                if (videoElement.value) {
                    videoElement.value.pause();
                }
            }
        }
    }, 1000);
};

const takePhoto = async () => {
    console.log("사진 찍기 시작");
    const img = new Image();
    img.crossOrigin = "Anonymous";
    img.src = bgImage.value;

    countdown.value = 3;

    img.onload = async () => {
        await nextTick();

        Swal.fire({
            title: `<h1 style='color:white;'>포즈!</h1>`,
            html: `<p style='color:white; font-size:50px;'>${countdown.value}</p>`,
            showConfirmButton: false,
            background: "rgba(0, 0, 0, 0.3)",
            backdrop: false,
            didOpen: () => {
                startCountdown();
            },
        });
    };

    img.onerror = async (error) => {
        console.error("배경 로딩 에러 발생: ", error);
        await Swal.fire({
            title: "배경 오류 발생",
            text: "해당 사진은 배경으로 사용할 수 없습니다!",
            icon: "warning",
        });
    };
};

const capturePhoto = async () => {
    console.log("사진 캡처 시작");
    await nextTick();

    cameraaudio.play();

    const videoContainer = videoContainerRef.value;
    const canvas = canvasElement.value;
    const captureAreaElement = captureArea.value;

    // 임시 캔버스 생성
    const tempCanvas = document.createElement("canvas");
    const tempCtx = tempCanvas.getContext("2d");
    tempCanvas.width = captureAreaElement.clientWidth;
    tempCanvas.height = captureAreaElement.clientHeight;

    try {
        // 배경 이미지 그리기
        const bgImg = new Image();
        bgImg.crossOrigin = "anonymous";
        bgImg.src = bgImage.value;
        await new Promise((resolve, reject) => {
            bgImg.onload = resolve;
            bgImg.onerror = reject;
        });
        tempCtx.drawImage(bgImg, 0, 0, tempCanvas.width, tempCanvas.height);

        // 비디오 컨테이너의 위치와 크기 계산
        const containerRect = videoContainer.getBoundingClientRect();
        const captureAreaRect = captureAreaElement.getBoundingClientRect();
        const scale = videoScale.value;
        const rotation = videoRotation.value;

        // 비디오 그리기
        tempCtx.save();
        tempCtx.translate(
            containerRect.left - captureAreaRect.left + containerRect.width / 2,
            containerRect.top - captureAreaRect.top + containerRect.height / 2
        );
        tempCtx.rotate((rotation * Math.PI) / 180);
        tempCtx.scale(scale, scale);
        tempCtx.translate(-canvas.width / 2, -canvas.height / 2);
        tempCtx.drawImage(canvas, 0, 0);
        tempCtx.restore();

        // 이미지 데이터 저장
        const imageData = tempCanvas.toDataURL("image/png");
        images.value.push({ src: imageData, visible: true });
        remainPicCnt.value = 10 - images.value.length;

        if (images.value.length === 10) {
            const { value: result } = await Swal.fire({
                title: "사진 촬영 종료",
                text: "10장을 모두 촬영하여 프레임 선택창으로 이동합니다!",
                icon: "success",
            });
            if (result) {
                exitphoto();
            }
        }
    } catch (error) {
        console.error("이미지 캡쳐 에러 발생: ", error);
    } finally {
        // 비디오 재생 재개
        if (videoElement.value) {
            videoElement.value.play();
        }
    }
};

const exitphoto = async () => {
    console.log("촬영종료");
    console.log("저장할 이미지 리스트:", images.value);

    const { value: result } = await Swal.fire({
        title: "촬영 끝내기",
        text: "촬영을 종료하고 저장을 위해 나가시겠습니까?",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "확인",
        cancelButtonText: "취소",
    });

    if (result) {
        photoStore.setPhotoList(images.value);
        console.log(
            "Pinia store에 저장된 이미지 리스트:",
            photoStore.photoList
        );
        router.push("/selectTemp");
    } else {
        Swal.fire("취소", "촬영을 계속합니다!", "error");
    }
};

const showtype = ref(1);

const changeComponent = () => {
    showtype.value = showtype.value === 1 ? 2 : 1;
    navigateTo(showtype.value === 1 ? "background" : "showphoto");
    console.log(
        "컴포넌트 변경:",
        showtype.value === 1 ? "배경 선택" : "사진 보기"
    );
};

let isRotating = ref(false);
let startAngle = ref(0);
let initialRotation = ref(0);

const startRotate = (event) => {
    isRotating.value = true;
    startAngle.value = Math.atan2(
        event.clientY -
            videoContainerRef.value.getBoundingClientRect().top -
            videoHeight.value / 2,
        event.clientX -
            videoContainerRef.value.getBoundingClientRect().left -
            videoWidth.value / 2
    );
    initialRotation.value = videoRotation.value;
    document.addEventListener("mousemove", onRotate);
    document.addEventListener("mouseup", stopRotate);
};

const onRotate = (event) => {
    if (!isRotating.value) return;

    const currentAngle = Math.atan2(
        event.clientY -
            videoContainerRef.value.getBoundingClientRect().top -
            videoHeight.value / 2,
        event.clientX -
            videoContainerRef.value.getBoundingClientRect().left -
            videoWidth.value / 2
    );
    const deltaAngle = (currentAngle - startAngle.value) * (180 / Math.PI);
    videoRotation.value = initialRotation.value + deltaAngle;
};

const stopRotate = () => {
    isRotating.value = false;
    document.removeEventListener("mousemove", onRotate);
    document.removeEventListener("mouseup", stopRotate);
};

let isScaling = ref(false);
let startDistance = ref(0);
let initialScale = ref(1);

const startScale = (event) => {
    isScaling.value = true;
    startDistance.value = Math.hypot(
        event.clientX - videoContainerRef.value.getBoundingClientRect().left,
        event.clientY - videoContainerRef.value.getBoundingClientRect().top
    );
    initialScale.value = videoScale.value;
    document.addEventListener("mousemove", onScale);
    document.addEventListener("mouseup", stopScale);
};

const onScale = (event) => {
    if (!isScaling.value) return;

    const currentDistance = Math.hypot(
        event.clientX - videoContainerRef.value.getBoundingClientRect().left,
        event.clientY - videoContainerRef.value.getBoundingClientRect().top
    );
    const scaleFactor = currentDistance / startDistance.value;
    videoScale.value = initialScale.value * scaleFactor;

    // Scale constraints
    const captureAreaElement = captureArea.value;
    const maxScale = captureAreaElement.clientWidth / (videoWidth.value * 2);
    const minScale = captureAreaElement.clientWidth / (videoWidth.value * 7);
    videoScale.value = Math.max(minScale, Math.min(videoScale.value, maxScale));
};

const stopScale = () => {
    isScaling.value = false;
    document.removeEventListener("mousemove", onScale);
    document.removeEventListener("mouseup", stopScale);
};

const handleFocus = () => {
    isFocused.value = true;
};

const handleBlur = () => {
    isFocused.value = false;
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
                        @focus="handleFocus"
                        @blur="handleBlur"
                        tabindex="0"
                    >
                        <div
                            class="video-container"
                            ref="videoContainerRef"
                            :style="videoStyle"
                            @mousedown="startDrag"
                            @mousemove="onDrag"
                            @mouseup="stopDrag"
                            @mouseleave="stopDrag"
                        >
                            <canvas
                                ref="canvasElement"
                                :width="videoWidth"
                                :height="videoHeight"
                            ></canvas>
                            <video
                                ref="videoElement"
                                autoplay
                                :width="videoWidth"
                                :height="videoHeight"
                                style="display: none"
                            ></video>
                            <div :style="centerIndicatorStyle"></div>
                            <div
                                class="rotate-handle"
                                @mousedown="startRotate"
                            ></div>
                            <div
                                class="scale-handle"
                                @mousedown="startScale"
                            ></div>
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
                            &lt;
                        </button>
                        <div class="box-name">
                            <p v-if="showtype === 1">배경선택</p>
                            <p v-if="showtype === 2">사진보기</p>
                        </div>
                        <button class="next-btn" @click="changeComponent">
                            &gt;
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
@import url("@/assets/css/shootView.css");

.rotate-handle {
    position: absolute;
    top: 0;
    left: 0;
    width: 20px;
    height: 20px;
    background-color: gray;
    cursor: grab;
    z-index: 10;
}

.rotate-handle:active {
    cursor: grabbing;
}

.scale-handle {
    position: absolute;
    bottom: 0;
    right: 0;
    width: 20px;
    height: 20px;
    background-color: gray;
    cursor: nwse-resize;
    z-index: 10;
}

.scale-handle:active {
    cursor: grabbing;
}
</style>
