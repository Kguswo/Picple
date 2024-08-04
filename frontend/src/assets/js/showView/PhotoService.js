import { ref } from "vue";
import { useBoothStore } from "@/stores/boothStore";
import { usePhotoStore } from "@/stores/photoStore";
import Swal from "sweetalert2";

class PhotoService {
  constructor() {
    this.images = ref([]);
    this.remainPicCnt = ref(10);
    this.countdown = ref(0);
    this.cameraaudio = new Audio("/src/assets/audio/shutter.mp3");
    this.boothStore = useBoothStore();
    this.photoStore = usePhotoStore();
  }

  async takePhoto() {
    console.log("사진 찍기 시작");
    const img = new Image();
    img.crossOrigin = "Anonymous";
    img.src = this.boothStore.bgImage;

    this.countdown.value = 3;

    img.onload = async () => {
      await this.startCountdown();
    };

    img.onerror = async (error) => {
      console.error("배경 로딩 에러 발생: ", error);
      await Swal.fire({
        title: "배경 오류 발생",
        text: "배경 없이 촬영을 진행합니다.",
        icon: "warning",
      });
      await this.startCountdown();
    };
  }

  startCountdown() {
    console.log("카운트다운 시작");
    return new Promise((resolve) => {
      const countdownInterval = setInterval(() => {
        this.countdown.value--;
        if (this.countdown.value <= 0) {
          clearInterval(countdownInterval);
          Swal.close();
          this.capturePhoto();
          resolve();
        } else {
          Swal.update({
            html: `<p style='color:white; font-size:50px;'>${this.countdown.value}</h3>`,
          });

          if (this.countdown.value === 1) {
            if (document.querySelector("video")) {
              document.querySelector("video").pause();
            }
          }
        }
      }, 1000);

      Swal.fire({
        title: `<h1 style='color:white;'>포즈!</h1>`,
        html: `<p style='color:white; font-size:50px;'>${this.countdown.value}</p>`,
        showConfirmButton: false,
        background: "rgba(0, 0, 0, 0.3)",
        backdrop: false,
      });
    });
  }

  async capturePhoto() {
    console.log("사진 캡처 시작");
    this.cameraaudio.play();

    const videoContainer = document.querySelector(".video-container");
    const canvas = document.querySelector("canvas");
    const captureAreaElement = document.querySelector(".photo-zone");

    const tempCanvas = document.createElement("canvas");
    const tempCtx = tempCanvas.getContext("2d");
    tempCanvas.width = captureAreaElement.clientWidth;
    tempCanvas.height = captureAreaElement.clientHeight;

    try {
      const bgImg = new Image();
      bgImg.crossOrigin = "anonymous";
      bgImg.src = this.boothStore.bgImage;
      await new Promise((resolve, reject) => {
        bgImg.onload = resolve;
        bgImg.onerror = reject;
      });
      tempCtx.drawImage(bgImg, 0, 0, tempCanvas.width, tempCanvas.height);

      const containerRect = videoContainer.getBoundingClientRect();
      const captureAreaRect = captureAreaElement.getBoundingClientRect();
      const scale = videoContainer.style.transform.match(/scale\((.*?)\)/)[1];
      const rotation = videoContainer.style.transform.match(/rotate\((.*?)deg\)/)[1];

      tempCtx.save();
      tempCtx.translate(
        containerRect.left - captureAreaRect.left + containerRect.width / 2,
        containerRect.top - captureAreaRect.top + containerRect.height / 2
      );
      tempCtx.rotate((rotation * Math.PI) / 180);
      tempCtx.scale(scale, scale);

      // 반전 효과 적용
      const mirrorFactor = document.querySelector("video").style.transform.includes("scaleX(-1)") ? -1 : 1;
      tempCtx.scale(mirrorFactor, 1);
      tempCtx.translate((-canvas.width / 2) * mirrorFactor, -canvas.height / 2);

      tempCtx.drawImage(canvas, 0, 0);
      tempCtx.restore();

      const imageData = tempCanvas.toDataURL("image/png");
      this.boothStore.addImage({ src: imageData, visible: true });

      this.remainPicCnt.value = 10 - this.images.value.length;

      if (this.images.value.length === 10) {
        const { value: result } = await Swal.fire({
          title: "사진 촬영 종료",
          text: "10장을 모두 촬영하여 프레임 선택창으로 이동합니다!",
          icon: "success",
        });
        if (result) {
          await this.exitphoto();
        }
      }
    } catch (error) {
      console.error("이미지 캡쳐 에러 발생: ", error);
    } finally {
      if (document.querySelector("video")) {
        document.querySelector("video").play();
      }
    }
  }

  async exitphoto() {
    console.log("exitphoto 함수 호출");
    console.log("촬영종료");
    console.log("저장할 이미지 리스트:", this.images.value);

    const { value: result } = await Swal.fire({
      title: "촬영 끝내기",
      text: "촬영을 종료하고 저장을 위해 나가시겠습니까?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "확인",
      cancelButtonText: "취소",
    });

    console.log("Swal result:", result);

    if (result) {
      this.photoStore.setPhotoList(this.images.value);
      console.log("Pinia store에 저장된 이미지 리스트:", this.photoStore.photoList);
      return true; // 라우터 네비게이션을 위해 true 반환
    } else {
      Swal.fire("취소", "촬영을 계속합니다!", "error");
      return false;
    }
  }
}

export default new PhotoService();
