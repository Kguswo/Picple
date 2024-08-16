<script setup>
import { ref, defineProps, defineEmits, onMounted, inject, watch } from 'vue';
import { usePhotoStore } from '@/stores/photoStore';
import { useBoothStore } from '@/stores/boothStore';
import { useRoute } from 'vue-router';

const boothStore = useBoothStore();
const photoStore = usePhotoStore();
const route = useRoute();
const boothActions = inject('boothActions');

const props = defineProps({
	boothId: String,
});

const emit = defineEmits(['update']);

const sessionId = boothStore.getSessionInfo().sessionId;
const images = ref(photoStore.getPhotosBySession(sessionId) || []);

watch(
	() => photoStore.getPhotosBySession(sessionId),
	(newImages) => {
		images.value = newImages || [];
	},
	{ immediate: true },
);

onMounted(() => {
	console.log('BoothShowPhoto 호출됨');
});

const showModal = ref(false);
const imgUrl = ref('');

const showImage = (img) => {
	showModal.value = true;
	imgUrl.value = img.src; // `src`로 수정
};

const closeModal = () => {
	showModal.value = false;
	imgUrl.value = '';
};
</script>

<template>
    <div class="background-box">
        <div class="background-box-scroll">
            <img
                class="thumbnail"
                v-for="(img, idx) in images"
                :key="idx"
                :src="img.src"
                @click="showImage(img)"
                alt="myPhoto"
            />
            <!-- images가 비어 있을 때 표시되는 문구 -->
            <p
                v-if="!images || images.length === 0"
                class="no-images-text"
            >
                사진 촬영을 해보세요!
            </p>
        </div>
    </div>

    <div
        class="modal-backdrop"
        v-if="showModal"
        @click="closeModal"
    >
        <div
            class="modal-content"
            @click.stop
        >
            <div class="modal-img">
                <img
                    :src="imgUrl"
                    alt="Photo"
                />
            </div>
            <div class="modal-footer">
                <button
                    class="action-button"
                    @click="closeModal"
                >
                    닫기
                </button>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 배경 박스 */
.background-box {
    height: 95%;
    width: 100%;
    overflow: hidden;
    border-radius: 15px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

/* 배경 박스 스크롤 */
.background-box-scroll {
    overflow-y: auto;
    overflow-x: hidden;
    height: 95%;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 10px;
    width: 100%;
}

.thumbnail {
    width: 100%;
    height: auto;
    object-fit: contain;
    cursor: pointer;
    border: 2px solid #e0e0e0;
    border-radius: 12px;
    transition: border 0.3s, transform 0.3s;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.thumbnail:hover {
    border: 2px solid white;
    transform: scale(1.05);
}

/* 어두운 배경 */
.modal-backdrop {
    display: flex;
    align-items: center;
    justify-content: center;
    position: fixed;
    z-index: 1000;
    left: 0;
    top: 0;
    width: 100vw;
    height: 100vh;
    background-color: rgba(0, 0, 0, 0.6);
    overflow: hidden;
}

.modal-content {
    background: white;
    border-radius: 15px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
    padding: 20px;
    position: relative;
    max-width: 550px;
    width: 80%;
    max-height: 80vh;
    overflow-y: auto;
    text-align: center;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    transform: translateY(-50px); /* 시작 위치를 약간 위로 설정 */
    opacity: 0; /* 시작 시 투명하게 */
    animation: slideInModal 0.5s ease-out forwards; /* 모달 애니메이션 */
}

@keyframes slideInModal {
    0% {
        transform: translateY(-50px);
        opacity: 0;
    }
    100% {
        transform: translateY(0);
        opacity: 1;
    }
}

.modal-img {
    flex-grow: 1;
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 10px 5px 10px 5px;
}

.modal-img img {
    max-width: 100%;
    max-height: 50vh;
    border-radius: 10px;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
}

.modal-footer {
    margin-top: 15px;
}

.action-button {
    background-color: #76c7c0;
    border: none;
    border-radius: 25px;
    color: white;
    padding: 10px 40px;
    font-size: 16px;
    cursor: pointer;
    box-shadow: 0 4px 10px rgba(118, 199, 192, 0.4);
    transition: background-color 0.3s ease, transform 0.3s ease;
    width: 100%;
}

.action-button:hover {
    background-color: #5da39a;
    transform: translateY(-2px);
}

.no-images-text {
    text-align: center;
    font-size: 18px;
    color: #000000bd;
    margin-right: 20px;
    margin-top: 10px;
    width: 100%;
}
</style>
