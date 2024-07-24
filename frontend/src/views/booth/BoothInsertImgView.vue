<script setup>
import { ref, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
import WhiteBoardComp from "@/components/common/WhiteBoardComp.vue";
import BoothBack from "@/components/booth/boothBackComp.vue";

// 사용자가 선택한 템플릿과 사진 리스트를 받아오기 위한 setup
const route = useRoute();
const selectedTemplate = ref(route.params.templateKey);
const takenPhotos = ref(route.params.photos); // 여기서 실제로 부모 뷰로부터 전달받은 사진들을 사용합니다

// 기존 템플릿 이미지 데이터
import template1x2 from "@/assets/img/template/80x120.png";
import template1x3 from "@/assets/img/template/80x120_1x3.png";
import template4x4 from "@/assets/img/template/80x120_4x4.png";
import template1x1 from "@/assets/img/template/160x120.png";
import template2x2 from "@/assets/img/template/160x120_2x2.png";

const templateImages = {
    "1x2": [
        template1x2,
        template1x2,
        template1x2,
        template1x2,
        template1x2,
        template1x2,
    ],
    "1x3": [template1x2, template1x2, template1x2],
    "4x4": [template1x2, template1x2, template1x2, template1x2],
    "1x1": [template1x2],
    "2x2": [template1x2, template1x2],
};

const imagesToShow = computed(() => {
    return templateImages[selectedTemplate.value] || [];
});
</script>

<template>
    <WhiteBoardComp class="whiteboard-area-booth">
        <div class="booth-content">
            <div class="booth-content-main">
                <BoothBack class="booth-camera-box">
                    <div class="selected-template-area">
                        <div class="selected-template">
                            <div class="template-images">
                                <div
                                    v-for="image in imagesToShow"
                                    :key="image"
                                    class="image-wrapper"
                                >
                                    <img :src="image" alt="Template Image" />
                                </div>
                            </div>
                        </div>
                    </div>
                </BoothBack>

                <BoothBack class="booth-camera-box">
                    <div class="taken-photos-area">
                        <div
                            class="taken-photo"
                            v-for="(photo, index) in takenPhotos"
                            :key="index"
                        >
                            <img :src="photo" alt="Taken Photo" />
                        </div>
                    </div>
                </BoothBack>
            </div>
        </div>
    </WhiteBoardComp>
</template>

<style scoped>
.image-wrapper {
    margin: 10px;
}

.image-wrapper img {
    max-width: 100%;
    height: auto;
}

.taken-photos-area {
    display: flex;
    flex-wrap: wrap;
}

.taken-photo {
    margin: 10px;
}

.taken-photo img {
    max-width: 100%;
    height: auto;
}
</style>
