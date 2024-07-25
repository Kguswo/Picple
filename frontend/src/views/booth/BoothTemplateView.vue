<script setup>
import { ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/BoothBackComp.vue";

// 각 템플릿의 실제 이미지 import
import temp_1x1_4x3 from "@/assets/img/template/temp_1x1_4x3.jpg";
import temp_1x2_4x5 from "@/assets/img/template/temp_1x2_4x5.jpg";
import temp_1x3_3x4 from "@/assets/img/template/temp_1x3_3x4.png";
import temp_2x2_4x3 from "@/assets/img/template/temp_2x2_4x3.jpg";

const router = useRouter();

const selectedTemplate = ref("all");
const selectedImage = ref(null);

const templates = [
    { text: "전체", key: "all" },
    { text: "1장", key: "1" },
    { text: "2장", key: "2" },
    { text: "3장", key: "3" },
    { text: "4장", key: "4" },
];

const templateImages = {
    1: [temp_1x1_4x3],
    2: [temp_1x2_4x5],
    3: [temp_1x3_3x4],
    4: [temp_2x2_4x3],
};

const selectTemplate = (template) => {
    console.log(`템플릿 선택됨: ${template.key}`);
    selectedTemplate.value = template.key;
    selectedImage.value = null; // 템플릿 변경 시 선택한 이미지 초기화
};

const selectImage = (image) => {
    console.log(`이미지 선택됨: ${image}`);
    selectedImage.value = image;
};

// 랜덤으로 배열을 섞는 함수
const shuffleArray = (array) => {
    let shuffledArray = array.slice(); // 원본 배열을 복사
    for (let i = shuffledArray.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffledArray[i], shuffledArray[j]] = [
            shuffledArray[j],
            shuffledArray[i],
        ];
    }
    return shuffledArray;
};

const imagesToShow = computed(() => {
    let images = [];
    if (selectedTemplate.value === "all") {
        images = Object.values(templateImages).flat();
    } else {
        images = templateImages[selectedTemplate.value] || [];
    }
    return shuffleArray(images);
});

// "전체"가 기본 선택되도록 설정
watch(
    selectedTemplate,
    (newVal) => {
        if (newVal === "all") {
            imagesToShow.value = shuffleArray(
                Object.values(templateImages).flat()
            );
        }
    },
    { immediate: true }
);

const goToNext = () => {
    if (selectedImage.value) {
        console.log(
            `다음 화면으로 이동: 템플릿: ${selectedTemplate.value}, 이미지: ${selectedImage.value}`
        );
        router.push({
            name: "insertImg",
            params: {
                templateKey: selectedTemplate.value,
            },
            query: {
                selectedImage: encodeURIComponent(selectedImage.value),
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
    height: 180px;
}

.image-wrapper img {
    max-height: 100%;
    max-width: 100%;
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
