<script setup>
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue";

// 각 템플릿의 실제 이미지 import
import image1x2_1 from "@/assets/img/template/240x360_1x2.png";
import image1x2_2 from "@/assets/img/template/240x360_1x2_2.png";
// import image1x3_1 from "@/assets/img/template/240x360_1x3.png";
// import image4x4_1 from "@/assets/img/template/240x360_4x4.png";
// import image1x1_1 from "@/assets/img/template/240x360_1x1.png";
// import image2x2_1 from "@/assets/img/template/240x360_2x2.png";

const router = useRouter();

const selectedTemplate = ref(null);
const selectedImage = ref(null);

const templates = [
    { text: "1X1", key: "1x1" },
    { text: "1X2", key: "1x2" },
    { text: "1X3", key: "1x3" },
    { text: "4X4", key: "4x4" },
    { text: "2X2", key: "2x2" },
];

const templateImages = {
    // "1x1": [image1x1_1],
    "1x2": [image1x2_1, image1x2_2],
    // "1x3": [image1x3_1],
    // "4x4": [image4x4_1],
    // "2x2": [image2x2_1],
};

const selectTemplate = (template) => {
    selectedTemplate.value = template.key;
    selectedImage.value = null; // 템플릿 변경 시 선택한 이미지 초기화
};

const selectImage = (image) => {
    selectedImage.value = image;
};

const imagesToShow = computed(() => {
    return templateImages[selectedTemplate.value] || [];
});

const goToNext = () => {
    if (selectedImage.value) {
        router.push({
            name: "insertImg",
            params: {
                templateKey: selectedTemplate.value,
                selectedImage: selectedImage.value,
            },
        });
    }
};
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="close-btn">
                <button class="close" @click="navigateTo('main')">X</button>
            </div>

            <div class="booth-content-main">
                <BoothBack class="booth-camera-box">
                    <div class="selected-template-area">
                        <div class="selected-template">
                            <div class="template-images">
                                <div
                                    v-for="image in imagesToShow"
                                    :key="image"
                                    class="image-wrapper"
                                    @click="selectImage(image)"
                                >
                                    <img
                                        :src="image"
                                        :class="{
                                            selected: selectedImage === image,
                                        }"
                                        alt="Template Image"
                                    />
                                </div>
                            </div>
                            <div class="box-footer">
                                <button
                                    @click="goToNext"
                                    :disabled="!selectedImage"
                                >
                                    다음
                                </button>
                            </div>
                        </div>
                    </div>
                </BoothBack>

                <BoothBack class="booth-select-box">
                    <div class="select-box">
                        <div class="select-text-box">
                            <div>템플릿 선택</div>
                        </div>
                        <div class="select-temp-box">
                            <div class="temp-area">
                                <div
                                    v-for="template in templates"
                                    :key="template.text"
                                    class="array-area"
                                >
                                    <button
                                        class="array-button"
                                        @click="selectTemplate(template)"
                                    >
                                        {{ template.text }}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </BoothBack>
            </div>
        </div>
    </WhiteBoardComp>
</template>

<style scoped>
@import url("@/assets/css/boothsSelectTemp.css");

.image-wrapper {
    margin: 10px;
    cursor: pointer;
}

.image-wrapper img {
    max-width: 100%;
    height: auto;
    border: 2px solid transparent;
}

.image-wrapper img.selected {
    border-color: blue;
}

.array-button {
    display: block;
    width: 100%;
    padding: 10px;
    margin: 5px 0;
    background-color: #f0f0f0;
    border: 1px solid #ccc;
    cursor: pointer;
    text-align: center;
}

.array-button:hover {
    background-color: #e0e0e0;
}

.box-footer {
    display: flex;
    justify-content: space-between;
    margin-top: 20px;
}

.box-footer button {
    padding: 10px 20px;
}
</style>
