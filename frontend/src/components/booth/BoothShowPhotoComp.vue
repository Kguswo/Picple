<script setup>
import { ref, defineProps, defineEmits, onMounted, inject, watch } from 'vue';
import { usePhotoStore } from '@/stores/photoStore';
import { useRoute } from 'vue-router';

const photoStore = usePhotoStore();
const route = useRoute();
const boothActions = inject('boothActions');

const props = defineProps({
    boothId: String,
});

const emit = defineEmits(['update']);

const sessionId = route.params.sessionId;
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
    <div class="select-text-box"></div>
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
        </div>
    </div>

    <div
        class="modal"
        v-if="showModal"
    >
        <div class="modal-content">
            <div class="close-box">
                <span
                    class="close"
                    @click="closeModal"
                    >&times;</span
                >
            </div>
            <div class="modal-img">
                <img
                    :src="imgUrl"
                    alt=""
                />
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 기존 스타일 그대로 유지 */
.select-text-box {
    display: flex;
    height: 10%;
    width: 90%;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;

    .select-btn-type {
        display: flex;
    }
}
.background-box {
    height: 85%;
    width: 90%;
    overflow: hidden;

    .background-box-scroll {
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

            &:hover {
                border: 2px solid red;
            }
        }
        &::-webkit-scrollbar {
            display: none;
        }
    }
}

.modal {
    display: block;
    position: fixed;
    z-index: 1;
    left: 0;
    top: 15%;
    width: 100%;
    height: 100%;
    overflow: auto;
    background-color: rgba(0, 0, 0, 0.4);
}

.modal-content {
    background-color: #fefefe;
    margin: 10vh auto;
    padding: 20px;
    border: 1px solid #888;
    width: 40%;
    max-width: 60%;
    height: 60%;
    max-height: 80vh;
    overflow-y: auto;
    border-radius: 10px;
}
.close-box {
    padding-top: 0px;
    padding-bottom: 0px;
    height: 8%;
}
.close {
    color: #aaa;
    float: right;
    font-size: 28px;
    line-height: 28px;
    font-weight: bold;
    height: 28px;
}

.close:hover,
.close:focus {
    color: black;
    text-decoration: none;
    cursor: pointer;
}
.modal-img {
    height: 90%;
    display: flex;
    align-items: center;
    text-align: center;
    justify-content: center;
    flex-wrap: wrap;
    img {
        height: 90%;
        width: 95%;
    }

    .modal-text {
        height: 10%;
        width: 90%;
        display: flex;
        justify-content: space-between;
        text-align: center;
    }
}
</style>
