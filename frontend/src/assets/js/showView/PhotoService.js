import { useBoothStore } from '@/stores/boothStore';
import { usePhotoStore } from '@/stores/photoStore';
import { ref } from 'vue';
import Swal from 'sweetalert2';

class PhotoService {
    constructor() {
        this.images = ref([]); // 촬영된 이미지 배열
        this.remainPicCnt = ref(10); // 남은 사진 수
        this.countdown = ref(0); // 카운트다운 값
        this.cameraaudio = new Audio('/src/assets/audio/shutter.mp3'); // 카메라 셔터 소리
        this.boothStore = useBoothStore();
        this.photoStore = usePhotoStore();
    }

    // 사진 촬영 시작
    async takePhoto(sessionId) {
        console.log('사진 찍기 시작');
        const img = new Image();
        img.crossOrigin = 'Anonymous';
        img.src = this.boothStore.bgImage;

        this.countdown.value = 3;

        img.onload = async () => {
            await this.startCountdown(sessionId);
        };

        img.onerror = async (error) => {
            console.error('배경 로딩 에러 발생: ', error);
            await Swal.fire({
                title: '배경 오류 발생',
                text: '배경 없이 촬영을 진행합니다.',
                icon: 'warning',
            });
            await this.startCountdown(sessionId);
        };
    }

    // 카운트다운 시작
    startCountdown(sessionId) {
        console.log('카운트다운 시작');
        return new Promise((resolve) => {
            const countdownInterval = setInterval(() => {
                this.countdown.value--;
                if (this.countdown.value <= 0) {
                    clearInterval(countdownInterval);
                    Swal.close();
                    this.capturePhoto(sessionId);
                    resolve();
                } else {
                    Swal.update({
                        html: `<p style='color:white; font-size:50px;'>${this.countdown.value}</h3>`,
                    });

                    if (this.countdown.value === 1) {
                        const video = document.querySelector('video');
                        if (video) {
                            video.pause();
                        }
                    }
                }
            }, 1000);

            Swal.fire({
                title: `<h1 style='color:white;'>포즈!</h1>`,
                html: `<p style='color:white; font-size:50px;'>${this.countdown.value}</p>`,
                showConfirmButton: false,
                background: 'rgba(0, 0, 0, 0.3)',
                backdrop: false,
            });
        });
    }

    // 사진 캡처
    async capturePhoto(sessionId) {
        console.log('사진 캡처 시작');
        this.cameraaudio.play();

        const videoElement = document.querySelector('video');

        if (!videoElement) {
            console.error('비디오 엘리먼트를 찾을 수 없습니다.');
            return;
        }

        const captureAreaElement = document.querySelector('.photo-zone');

        if (!captureAreaElement) {
            console.error('captureAreaElement를 찾을 수 없습니다.');
            return;
        }

        const tempCanvas = document.createElement('canvas');
        const tempCtx = tempCanvas.getContext('2d');
        tempCanvas.width = captureAreaElement.clientWidth;
        tempCanvas.height = captureAreaElement.clientHeight;

        try {
            // 배경 이미지 로드 및 그리기
            const bgImg = new Image();
            bgImg.crossOrigin = 'anonymous';
            bgImg.src = this.boothStore.bgImage;
            await new Promise((resolve, reject) => {
                bgImg.onload = resolve;
                bgImg.onerror = reject;
            });
            tempCtx.drawImage(bgImg, 0, 0, tempCanvas.width, tempCanvas.height);

            // 비디오 요소의 크기와 위치
            const videoRect = videoElement.getBoundingClientRect();
            const captureRect = captureAreaElement.getBoundingClientRect();

            // Transform을 고려하여 비디오 요소를 캔버스에 그리기
            tempCtx.save(); // 이전 상태 저장

            // 비디오 요소의 중앙을 기준으로 변환
            tempCtx.translate(
                videoRect.left - captureRect.left + videoRect.width / 2,
                videoRect.top - captureRect.top + videoRect.height / 2,
            );

            // 비디오 요소의 transform 스타일 적용 (반전된 상태 포함)
            const computedStyle = window.getComputedStyle(videoElement);
            const transform = computedStyle.transform;
            if (transform !== 'none') {
                const matrix = transform
                    .match(/matrix\((.+)\)/)[1]
                    .split(',')
                    .map(parseFloat);
                tempCtx.transform(...matrix);
            }

            // 비디오 요소를 캔버스에 그리기
            tempCtx.drawImage(
                videoElement,
                -videoRect.width / 2, // 중앙 기준으로 그리기
                -videoRect.height / 2, // 중앙 기준으로 그리기
                videoRect.width,
                videoRect.height,
            );

            tempCtx.restore(); // 변환 상태 복원

            // 캡쳐된 이미지 저장
            const imageData = tempCanvas.toDataURL('image/png');
            this.photoStore.addPhoto(sessionId, { src: imageData, visible: true });

            // 남은 사진 수 갱신
            this.remainPicCnt.value = 10 - this.photoStore.getPhotosBySession(sessionId).length;

            if (this.photoStore.getPhotosBySession(sessionId).length === 10) {
                const { value: result } = await Swal.fire({
                    title: '사진 촬영 종료',
                    text: '10장을 모두 촬영하여 프레임 선택창으로 이동합니다!',
                    icon: 'success',
                });
                if (result) {
                    await this.exitphoto(sessionId);
                }
            }
        } catch (error) {
            console.error('이미지 캡쳐 에러 발생: ', error);
        } finally {
            videoElement.play(); // 비디오 재생 재개
        }
    }

    // 촬영 종료
    async exitphoto(sessionId) {
        console.log('exitphoto 함수 호출');
        console.log('촬영종료');
        console.log('저장할 이미지 리스트:', this.photoStore.getPhotosBySession(sessionId));

        const { value: result } = await Swal.fire({
            title: '촬영 끝내기',
            text: '촬영을 종료하고 저장을 위해 나가시겠습니까?',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: '확인',
            cancelButtonText: '취소',
        });

        console.log('Swal result:', result);

        if (result) {
            // 추가적으로 필요한 작업이 있다면 여기에 작성
            return true; // 라우터 네비게이션을 위해 true 반환
        } else {
            Swal.fire('취소', '촬영을 계속합니다!', 'error');
            return false;
        }
    }
}

export default new PhotoService();
