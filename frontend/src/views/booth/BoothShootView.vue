<script setup>
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import { usePhotoStore } from '@/stores/photoStore';
import { RouterView, useRouter } from 'vue-router';
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import html2canvas from 'html2canvas';
import Swal from 'sweetalert2';

import videoOn from '@/assets/icon/video_on.png';
import videoOff from '@/assets/icon/video_off.png';
import microOn from '@/assets/icon/micro_on.png';
import microOff from '@/assets/icon/micro_off.png';

const router = useRouter();
const photoStore = usePhotoStore();

const navigateTo = (name) => {
	router.push({ name });
};

const videoElement = ref(null);
let mediaStream = null;

let isMirrored = false;
let isvideoOn = ref(true);
let isMicroOn = ref(true);
const remainPicCnt = ref(10);

const images = ref([]);

onMounted(async () => {
	try {
		mediaStream = await navigator.mediaDevices.getUserMedia({
			video: true,
			audio: true,
		});

		videoElement.value.srcObject = mediaStream;
	} catch (error) {}
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

const bgImage = ref('https://via.placeholder.com/400');

const changeImage = (image) => {
	bgImage.value = image;
};

const captureArea = ref(null);
const countdown = ref(0);
var cameraaudio = new Audio('/src/assets/audio/shutter.mp3');

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
		}
	}, 1000);
};

const takePhoto = async () => {
	const img = new Image();
	img.crossOrigin = 'Anonymous';
	img.src = bgImage.value;

	countdown.value = 3;

	img.onload = async () => {
		await nextTick();

		Swal.fire({
			title: `<h1 style='color:white;'>포즈!</h1>`,
			html: `<p style='color:white; font-size:50px;'>${countdown.value}</p>`,
			showConfirmButton: false,
			background: 'rgba(0, 0, 0, 0.3)',
			backdrop: false,
			didOpen: () => {
				startCountdown();
			},
		});
	};

	img.onerror = async (error) => {
		await Swal.fire({
			title: '@배경 오류 발생@',
			text: '해당 사진은 배경으로 사용할 수 없습니다!',
			icon: 'warning',
		});
	};
};

const capturePhoto = async () => {
	await nextTick();
	cameraaudio.play();
	html2canvas(captureArea.value, { useCORS: true, allowTaint: false })
		.then(async (canvas) => {
			const imageData = canvas.toDataURL('image/png');
			images.value.push({ src: imageData, visible: true });
			remainPicCnt.value = 10 - images.value.length;
			if (images.value.length === 10) {
				const { value: result } = await Swal.fire({
					title: '사진 촬영 종료',
					text: '10장을 모두 촬영하여 프레임 선택창으로 이동합니다!',
					icon: 'success',
				});
				if (result) {
					exitphoto();
				}
			}
		})
		.catch((error) => {});
};

const exitphoto = async () => {
	const { value: result } = await Swal.fire({
		title: '촬영 끝내기',
		text: '촬영을 종료하고 저장을 위해 나가시겠습니까?',
		icon: 'warning',
		showCancelButton: true,
		confirmButtonText: '확인',
		cancelButtonText: '취소',
	});

	if (result) {
		photoStore.setPhotoList(images.value);
		router.push('/selectTemp');
	} else {
		Swal.fire('취소', '촬영을 계속합니다!', 'error');
	}
};

const showtype = ref(1);

const changeComponent = () => {
	showtype.value = showtype.value === 1 ? 2 : 1;
	navigateTo(showtype.value === 1 ? 'background' : 'showphoto');
};
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="booth-top-div">
				<div>남은 사진 수: {{ remainPicCnt }}/10</div>
				<div class="close-btn">
					<button
						class="close"
						@click="navigateTo('main')"
					>
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

						<button
							@click="takePhoto"
							class="take-photo"
						>
							<img
								src="@/assets/icon/camera.png"
								alt=""
							/>
						</button>
						<div class="right-btn">
							<button
								class="ract-btn"
								@click="exitphoto"
							>
								템플릿 선택
							</button>
						</div>
					</div>
				</BoothBack>

				<BoothBack class="booth-select-box">
					<div class="select-box-top">
						<button
							class="prev-btn"
							@click="changeComponent"
						>
							&lt;
						</button>
						<div class="box-name">
							<p v-if="showtype === 1">배경선택</p>
							<p v-if="showtype === 2">사진보기</p>
						</div>
						<button
							class="next-btn"
							@click="changeComponent"
						>
							&gt;
						</button>
					</div>

					<div class="select-text-box">
						<RouterView
							v-if="showtype === 1"
							@update="changeImage"
						></RouterView>
						<RouterView
							v-else
							:images="images"
						>
						</RouterView>
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
