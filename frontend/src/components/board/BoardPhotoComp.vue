<script setup>
import { ref } from 'vue';

const props = defineProps({
    photo: Object,
})

const showModal = ref(false);
const currentItem = ref(null);

const picZoom = () => {
    showModal.value = true;
    currentItem.value = props.photo;
};

const closeModal = () => {
    showModal.value = false;
    currentItem.value = null;
};

const toggleLike = () => {
    if (props.photo.liked) {
        --props.photo.hit;
    } else {
        ++props.photo.hit;
    }
    props.photo.liked = !props.photo.liked
}
</script>

<template>
    <div class="photo-card">
        <div class="photo" @click="picZoom">
        </div>
        <div class="content">
            <div class="like">
                <svg v-if="photo.liked" xmlns="@/assets/icon/hear-fill.svg" class="heart" width="20" height="20"
                    fill="red" viewBox="0 0 16 16" @click="toggleLike">
                    <path fill-rule="evenodd"
                        d="M8 1.314C12.438-3.248 23.534 4.735 8 15-7.534 4.736 3.562-3.248 8 1.314" />
                </svg>
                <svg v-else xmlns="@/assets/icon/hear.svg" width="20" height="20" fill="red" class="heart"
                    viewBox="0 0 16 16" @click="toggleLike">
                    <path
                        d="m8 2.748-.717-.737C5.6.281 2.514.878 1.4 3.053c-.523 1.023-.641 2.5.314 4.385.92 1.815 2.834 3.989 6.286 6.357 3.452-2.368 5.365-4.542 6.286-6.357.955-1.886.838-3.362.314-4.385C13.486.878 10.4.28 8.717 2.01zM8 15C-7.333 4.868 3.279-3.04 7.824 1.143q.09.083.176.171a3 3 0 0 1 .176-.17C12.72-3.042 23.333 4.867 8 15" />
                </svg>
                <span class="like-cnt">{{ photo.hit }}</span>
            </div>

        </div>
    </div>

    <div class="modal" v-if="showModal">
        <div class="modal-content">
            <div class="close-box">
                <span class="close" @click="closeModal">&times;</span>
            </div>
            <div class="modal-img">
                <img src="@/assets/img/tempImg.png" alt="">
                <div class="modal-text">
                    <span class="modal-date">촬영일: {{ currentItem.createdAt }}</span>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
@import "@/assets/css/board.css";
</style>