<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue";

import Karina from "@/assets/img/template/카리나.jpg";

const route = useRoute();
const selectedImage = decodeURIComponent(route.query.selectedImage);

const draggedImage = ref(null);
const droppedImage = ref(null);

console.log(`BoothInsertView 로드됨: 이미지: ${selectedImage}`);

const onDragStart = (event, image) => {
    console.log(`이미지 드래그 시작: ${image}`);
    draggedImage.value = image;
    event.dataTransfer.effectAllowed = "move";
};

const onDrop = (event) => {
    event.preventDefault();
    if (draggedImage.value) {
        console.log(`이미지 드롭됨: ${draggedImage.value}`);
        droppedImage.value = draggedImage.value;
    }
};

const onDragOver = (event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
};
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="booth-content-main">
                <BoothBack
                    class="booth-camera-box"
                    @dragover="onDragOver"
                    @drop="onDrop"
                >
                    <div class="selected-image-area">
                        <img
                            :src="selectedImage"
                            alt="선택된 템플릿 이미지"
                            class="template-image"
                        />
                        <div v-if="droppedImage" class="dropped-image-wrapper">
                            <img
                                :src="droppedImage"
                                alt="드롭된 이미지"
                                class="dropped-image"
                            />
                        </div>
                    </div>
                </BoothBack>
                <BoothBack class="booth-select-box">
                    <div class="photo-list">
                        <img
                            src="@/assets/img/template/카리나.jpg"
                            alt="카리나"
                            draggable="true"
                            @dragstart="(event) => onDragStart(event, Karina)"
                            class="draggable-image"
                        />
                    </div>
                </BoothBack>
            </div>
        </div>
    </WhiteBoardComp>
</template>

<style scoped>
.booth-content {
    display: flex;
    flex-direction: column;
    justify-content: space-evenly;
    align-items: center;
    width: 100%;
    height: 100%;
}

.booth-content-main {
    display: flex;
    flex-wrap: wrap;
    align-content: center;
    justify-content: space-evenly;
    width: 100%;
    height: 95%;
}

.selected-image-area {
    position: relative;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100%;
}

.selected-image-area img.template-image {
    max-width: 100%;
    height: auto;
}

.dropped-image-wrapper {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 50%; /* 고정된 비율을 유지하기 위해 */
    height: auto;
}

.dropped-image-wrapper img.dropped-image {
    width: 100%;
    height: auto;
}

.booth-select-box {
    display: flex;
    justify-content: center;
    align-items: center;
}

.photo-list {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-wrap: wrap;
    width: 100%;
}

.draggable-image {
    margin: 10px;
    width: 100px;
    height: auto;
    cursor: grab;
}
</style>
