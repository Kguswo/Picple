<script setup>
import { ref } from 'vue';
import FormIconCancelComp from '@/components/form/FormIconCancelComp.vue';
import FormIconVisibilityComp from '@/components/form/FormIconVisibilityComp.vue';
import FormMessageComp from '@/components/form/FormMessageComp.vue';

const props = defineProps({
	inputParams: Object,
	isSend: Boolean,
});
const message = ref(null);

const inputField = ref(null);
const maxLength =
	props.inputParams.label === '이메일'
		? 254
		: props.inputParams.label === '닉네임'
		? 8
		: props.inputParams.label.includes('비밀번호')
		? 64
		: 10;

const focusInput = () => {
	inputField.value.focus();
};

const toggleVisibility = (newType) => {
	props.inputParams.type = newType;
};

const cancelInput = () => {
	props.inputParams.value = '';
};

defineExpose({
	focusInput,
	message,
});
</script>

<template>
	<div class="form-input-conatiner">
		<div
			class="input-container"
			:class="{
				'input-container-selected': !message || !message.isError,
				'input-container-error': message && message.isError,
				'background-color-disabled': isSend === true,
			}"
			@click="focusInput"
		>
			<input
				:type="inputParams.type"
				v-model="inputParams.value"
				ref="inputField"
				class="form-input"
				:class="{
					'has-content': inputParams.value,
					'background-color-disabled': isSend === true,
				}"
				:maxlength="maxLength"
				:disabled="isSend === true"
				autocomplete="off"
			/>
			<label class="form-label">{{ inputParams.label }}</label>

			<div class="form-input-etc">
				<FormIconCancelComp
					v-if="inputParams.value"
					class="form-icon-cancel"
					@click="cancelInput"
				/>
				<FormIconVisibilityComp
					v-if="inputParams.label.includes('비밀번호') && inputParams.value"
					class="form-icon-visibility"
					:type="inputParams.type"
					@toggle-visibility="toggleVisibility"
				/>
				<slot></slot>
			</div>
		</div>
		<FormMessageComp
			v-if="message"
			:message="message"
		/>
	</div>
</template>

<style scoped></style>
