<script setup></script>

<template>
	<div>
		<div class="booth-background">
			<slot></slot>
		</div>
	</div>
</template>

<style scoped>
.booth-create {
	background-color: rgba(124, 124, 124, 0.232);
	width: 90%;
	height: 10%;
	border-radius: 20px;
}

.booth-select-box {
	background-color: rgba(124, 124, 124, 0.232);
	width: 20%;
	height: 90%;

	border-radius: 20px;
}
.booth-camera-box {
	background-color: rgba(124, 124, 124, 0.232);
	width: 75%;
	height: 90%;

	border-radius: 20px;
}

.booth-temp-box {
	background-color: rgba(124, 124, 124, 0.232);
	width: 62%;
	height: 90%;
	border-radius: 20px;
}

.booth-img-box {
	background-color: rgba(124, 124, 124, 0.232);
	width: 28%;
	height: 90%;
	border-radius: 20px;
}

.booth-background {
	width: 100%;
	height: 100%;
	border-radius: 20px;
}
</style>
