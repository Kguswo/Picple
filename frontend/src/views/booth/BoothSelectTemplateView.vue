<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/boothBackComp.vue";

// 템플릿 이미지 import
import template1x2 from "@/assets/img/template/80x120.png";
import template1x3 from "@/assets/img/template/80x120_1x3.png";
import template4x4 from "@/assets/img/template/80x120_4x4.png";
import template1x1 from "@/assets/img/template/160x120.png";
import template2x2 from "@/assets/img/template/160x120_2x2.png";

// 각 템플릿의 실제 이미지 import
import image1x2_1 from "@/assets/img/template/240x360_1x2.png";
import image1x2_2 from "@/assets/img/template/240x360_1x2_2.png";

const router = useRouter();

const navigateTo = (path) => {
    router.push({ name: path });
};

const selectedTemplate = ref(null);

const templates = [
    { src: template1x2, text: "1X2", key: "1x2" },
    { src: template1x3, text: "1X3", key: "1x3" },
    { src: template4x4, text: "4X4", key: "4x4" },
    { src: template1x1, text: "1X1", key: "1x1" },
    { src: template2x2, text: "2X2", key: "2x2" },
];

const templateImages = {
    "1x2": [
        image1x2_1,
        image1x2_2,
        image1x2_2,
        image1x2_2,
        image1x2_2,
        image1x2_2,
    ],
    "1x3": [image1x2_2, image1x2_2, image1x2_2],
    "4x4": [image1x2_2, image1x2_2, image1x2_2, image1x2_2],
    "1x1": [image1x2_2],
    "2x2": [image1x2_2, image1x2_2],
};

const selectTemplate = (template) => {
    selectedTemplate.value = template.key;
};

const goToNext = () => {
    if (selectedTemplate.value) {
        router.push({
            name: "insertImg",
            params: {
                templateKey: selectedTemplate.value,
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
                            <div
                                v-if="selectedTemplate"
                                class="template-images"
                            >
                                <div
                                    v-for="image in templateImages[
                                        selectedTemplate
                                    ]"
                                    :key="image"
                                    class="image-wrapper"
                                >
                                    <img :src="image" alt="Template Image" />
                                </div>
                            </div>
                            <div class="box-footer">
                                <button>이전</button>
                                <button @click="goToNext">다음</button>
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
                                    @click="selectTemplate(template)"
                                >
                                    <div class="array">
                                        <img :src="template.src" />
                                    </div>
                                    <div class="array-text">
                                        {{ template.text }}
                                    </div>
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
}

.image-wrapper img {
    max-width: 100%;
    height: auto;
}
</style>
