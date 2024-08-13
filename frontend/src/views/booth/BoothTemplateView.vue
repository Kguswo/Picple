<script setup>
import { ref, watch, computed } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import WhiteBoardComp from '@/components/common/WhiteBoardComp.vue';
import BoothBack from '@/components/booth/BoothBackComp.vue';
import TemplateComp from '@/components/template/TemplateComp.vue';
import { usePhotoStore } from '@/stores/photoStore';

const row = ref(0);
const col = ref(0);

const frameSizes = [
	{ row: 1, col: 1 },
	{ row: 2, col: 1 },
	{ row: 3, col: 1 },
	{ row: 4, col: 1 },
	{ row: 2, col: 2 },
];

const selectFrameSize = (item) => {
	row.value = item.row;
	col.value = item.col;
};
</script>

<template>
	<WhiteBoardComp class="whiteboard-area-booth">
		<div class="booth-content">
			<div class="close-btn">
				<button
					class="close"
					@click="navigateTo('main')"
				>
					X
				</button>
			</div>

			<div class="booth-content-main">
				<div class="template-list">
					<div
						v-for="(item, index) in frameSizes"
						:key="index"
						class="template-text"
					>
						<button @click="selectFrameSize(item)">{{ item.row }} x {{ item.col }}</button>
					</div>
				</div>
				<BoothBack class="booth-camera-box">
					<TemplateComp
						v-if="row && col"
						:row="row"
						:col="col"
					/>
				</BoothBack>

				<!-- <BoothBack class="booth-select-box">
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
				</BoothBack> -->
			</div>
		</div>
	</WhiteBoardComp>
</template>

<style scoped>
@import url('@/assets/css/boothsSelectTemp.css');

.template-list {
	width: 75%;
	display: flex;
	justify-content: space-around;
}

.template-text {
	button {
		width: 50px;
		height: 30px;
		border: 2px solid black;
		border-radius: 5px;
	}
}
</style>
