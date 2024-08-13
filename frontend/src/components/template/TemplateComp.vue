<script setup>
const props = defineProps({
	row: Number,
	col: Number,
	photo: Object,
});
</script>

<template>
	<div
		class="template-container"
		:style="{ width: `${props.col * 200}px`, height: `${props.row * 200}px` }"
	>
		<div
			class="template-inner"
			:style="{
				gridTemplateColumns: `repeat(${props.col}, 1fr)`,
			}"
		>
			<div
				v-for="_ in row * col"
				class="template-div"
			>
				<img
					v-if="photo"
					:src="photo"
					class="photo"
				/>
				<span
					v-else
					class="placeholder"
					>여기에 사진을 놓으세요</span
				>
			</div>
		</div>
		<div class="template-logo">picple</div>
	</div>
</template>

<style scoped>
.template-container {
	background-color: black;
	display: flex;
	flex-direction: column;
	height: 100%;
}

.template-inner {
	flex: 1;
	display: grid;
	place-items: center;
	width: 100%;
}

.template-div {
	width: 95%;
	height: 95%;
	border: 1px solid white;
	display: flex;
	justify-content: center;
	align-items: center;
}

.template-logo {
	flex-shrink: 0;
	height: 40px;
	font-size: 20px;
	color: white;
	display: flex;
	justify-content: center;
	align-items: center;
}

.placeholder {
	color: white;
}
</style>
