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
	width: 93%;
	height: 11%;
	border-radius: 20px;
}

.booth-shoot {
	background-color: rgba(124, 124, 124, 0.232);
	width: 98%;
	height: 11%;
	border-radius: 20px;
}

.booth-select-box {
	background-color: rgba(124, 124, 124, 0.232);
	width: 20%;
	height: 95%;

	border-radius: 20px;
}
.booth-camera-box {
	width: 75%;
	height: 95%;

	border-radius: 20px;
}

#app
	> div
	> div
	> div
	> div.booth-content
	> div.booth-content-main
	> div.booth-camera-box
	> div
	> div.booth-shoot
	> div {
	justify-content: center;
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
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: space-between;

	border-radius: 20px;

	height: 100%;
	width: 100%;
}

.booth-create > .booth-background {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;

	border-radius: 20px;

	height: 100%;
	width: 100%;
}

.booth-template-back > .booth-background {
	display: block;
}
</style>
